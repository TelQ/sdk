package com.telq.sdk.service.rest;

import com.google.gson.Gson;
import com.telq.sdk.exceptions.httpExceptions.clientSide.ApiCredentialsException;
import com.telq.sdk.exceptions.httpExceptions.clientSide.AuthorizationServiceException;
import com.telq.sdk.model.TelQUrls;
import com.telq.sdk.model.authorization.TokenBearer;
import com.telq.sdk.model.network.DestinationNetwork;
import com.telq.sdk.model.network.Network;
import com.telq.sdk.model.tests.RequestTestDto;
import com.telq.sdk.model.tests.Result;
import com.telq.sdk.model.tests.Test;
import com.telq.sdk.model.tests.TestIdTextOptions;
import com.telq.sdk.model.token.TokenResponseDto;
import com.telq.sdk.service.authorization.AuthorizationService;
import com.telq.sdk.utils.HttpCodeStatusChecker;
import com.telq.sdk.utils.JsonMapper;
import lombok.NonNull;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class RestV2ApiConnectorService implements ApiConnectorService {

    private final CloseableHttpClient httpClient;

    public RestV2ApiConnectorService(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Here using the api credentials we request a new token
     * @param request
     * @return {@link TokenBearer} generated token
     * @throws ApiCredentialsException if api credentials not given or instanced
     * @throws IOException if request to server fails
     */
    @Override
    public TokenBearer getToken(HttpUriRequestBase request)
            throws Exception {

        try (CloseableHttpResponse response = httpRequestWrapper(null, request, true)) {

            String content = extractResponseContent(response.getEntity());
            response.close();
            TokenResponseDto tokenResponseDto = JsonMapper.getInstance().getMapper().fromJson(content, TokenResponseDto.class);

            return TokenBearer.builder().token(tokenResponseDto.getValue()).build();
        }
    }

    /**
     * Here we get the list of all available networks
     * @return A list of networks received from the server
     * @throws AuthorizationServiceException If token is aren't valid
     * @throws IOException if request to server fails
     */
    @Override
    public List<Network> getNetworks(@NonNull AuthorizationService authorizationService, @NonNull HttpUriRequestBase request) throws Exception {
        Network[] networks;

        try (CloseableHttpResponse response = httpRequestWrapper(authorizationService, request, false)) {
            networks = JsonMapper.getInstance().getMapper().fromJson(extractResponseContent(response.getEntity()), Network[].class);
            response.close();

            return Arrays.asList(networks);
        }

    }

    /**
     * Here we make new tests, sending them in one array
     * @return List of tests as a response after test creation
     * @throws AuthorizationServiceException If token is aren't valid
     * @throws IOException if request to server fails
     * @param authorizationService
     * @param request
     */
    @Override
    public List<Test> sendTests(@NonNull AuthorizationService authorizationService, @NonNull HttpUriRequestBase request)
            throws Exception {

        Test[] testResponse;

        try (CloseableHttpResponse response = httpRequestWrapper(authorizationService, request, false)) {
            testResponse = JsonMapper.getInstance().getMapper().fromJson(extractResponseContent(response.getEntity()), Test[].class);
            response.close();

            return Arrays.asList(testResponse);
        }


    }

    /**
     * This method aims to fetch the results of a single tests with its id given.
     *
     *
     * @param authorizationService
     * @param request @return {@link Result} response of the test status
     * @throws Exception in case of authorization of http code failures
     */
    @Override
    public Result getTestResult(@NonNull AuthorizationService authorizationService, @NonNull HttpUriRequestBase request) throws Exception {
        try (CloseableHttpResponse response = httpRequestWrapper(authorizationService, request, false)) {
            String resultResponse = extractResponseContent(response.getEntity());
            response.close();
            return JsonMapper.getInstance().getMapper().fromJson(resultResponse, Result.class);
        }
    }

    private CloseableHttpResponse httpRequestWrapper(
            AuthorizationService authorizationService,
            HttpUriRequestBase request,
            boolean requestingToken) throws Exception {

        if(request instanceof HttpPost) {
            request.setHeader("Content-Type", "application/json");
        }

        if(!requestingToken) {
            TokenBearer tokenBearer = authorizationService.checkAndGetToken();
            request.setHeader("Authorization", "Bearer " + tokenBearer.getToken());
        }

        CloseableHttpResponse response = httpClient.execute(request);

        HttpCodeStatusChecker.statusCheck(response.getCode());

        return response;
    }

    public String extractResponseContent(HttpEntity httpEntity) throws Exception {
        return EntityUtils.toString(httpEntity);
    }

    @Override
    public HttpPost buildHttpPostRequest(@NonNull List<DestinationNetwork> destinationNetworks,
                                         int maxCallbackRetries,
                                         String callbackUrl,
                                         int testTimeToLiveInSeconds,
                                         String callBackToken,
                                         TestIdTextOptions testIdTextOptions) throws UnsupportedEncodingException {

        RequestTestDto requestTestDto = RequestTestDto.builder()
                .destinationNetworks(destinationNetworks)
                .testIdTextType(testIdTextOptions.getTestIdTextType())
                .testIdTextCase(testIdTextOptions.getTestIdTextCase())
                .testIdTextLength(testIdTextOptions.getTestIdTextLength())
                .build();

        if(maxCallbackRetries >= 0)
            requestTestDto.setMaxCallbackRetries(maxCallbackRetries);
        if(callbackUrl != null)
            requestTestDto.setResultsCallbackUrl(callbackUrl);
        if(testTimeToLiveInSeconds >= 0)
            requestTestDto.setTestTimeToLiveInSeconds(testTimeToLiveInSeconds);


        HttpPost request = new HttpPost(TelQUrls.getTestsUrl());

        if(callBackToken != null)
            request.setHeader("results-callback-token", callBackToken);

        Gson mapper = JsonMapper.getInstance().getMapper();
        StringEntity bodyEntity = new StringEntity(mapper.toJson(requestTestDto),
                ContentType.create("text/plain", "UTF-8"));
        request.setEntity(bodyEntity);

        return request;
    }

}
