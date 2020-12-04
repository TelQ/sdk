package com.telq.sdk.service.rest;

import com.telq.sdk.exceptions.httpExceptions.clientSide.ApiCredentialsException;
import com.telq.sdk.exceptions.httpExceptions.clientSide.AuthorizationServiceException;
import com.telq.sdk.model.TelQUrls;
import com.telq.sdk.model.authorization.TokenBearer;
import com.telq.sdk.model.network.Network;
import com.telq.sdk.model.tests.RequestTestDto;
import com.telq.sdk.model.tests.Result;
import com.telq.sdk.model.tests.Test;
import com.telq.sdk.model.token.TokenResponseDto;
import com.telq.sdk.service.authorization.AuthorizationService;
import com.telq.sdk.utils.HttpCodeStatusChecker;
import com.telq.sdk.utils.JsonMapper;
import lombok.NonNull;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.http.util.EntityUtils;

import java.io.IOException;
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
    public TokenBearer getToken(HttpRequestBase request)
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
    public List<Network> getNetworks(@NonNull AuthorizationService authorizationService, @NonNull HttpRequestBase request) throws Exception {
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
    public List<Test> sendTests(@NonNull AuthorizationService authorizationService, @NonNull HttpRequestBase request)
            throws Exception {

        Test[] testResponse;

        try (CloseableHttpResponse response = httpRequestWrapper(authorizationService, request, false)) {
            testResponse = JsonMapper.getInstance().getMapper().fromJson(extractResponseContent(response.getEntity()), Test[].class);
            response.close();

            return Arrays.asList(testResponse);
        }


    }

    /**
     * This method aims to fetch the results of a single tests with it's id given.
     *
     *
     * @param authorizationService
     * @param request @return {@link Result} response of the test status
     * @throws Exception in case of authorization of http code failures
     */
    @Override
    public Result getTestResult(@NonNull AuthorizationService authorizationService, @NonNull HttpRequestBase request) throws Exception {
        try (CloseableHttpResponse response = httpRequestWrapper(authorizationService, request, false)) {
            String resultResponse = extractResponseContent(response.getEntity());
            response.close();
            return JsonMapper.getInstance().getMapper().fromJson(resultResponse, Result.class);
        }
    }

    private CloseableHttpResponse httpRequestWrapper(
            AuthorizationService authorizationService,
            HttpRequestBase request,
            boolean requestingToken) throws Exception {

        if(request instanceof HttpPost) {
            request.setHeader("Content-Type", "application/json");
        }

        if(!requestingToken) {
            TokenBearer tokenBearer = authorizationService.checkAndGetToken();
            request.setHeader("Authorization", "Bearer " + tokenBearer.getToken());
        }

        CloseableHttpResponse response = httpClient.execute(request);

        HttpCodeStatusChecker.statusCheck(response.getStatusLine().getStatusCode());

        return response;
    }

    public String extractResponseContent(HttpEntity httpEntity) throws Exception {
        return EntityUtils.toString(httpEntity);
    }

}
