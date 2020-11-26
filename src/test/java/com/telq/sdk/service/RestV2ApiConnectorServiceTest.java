package com.telq.sdk.service;

import com.telq.sdk.BaseTest;
import com.telq.sdk.exceptions.httpExceptions.clientSide.BadRequest;
import com.telq.sdk.exceptions.httpExceptions.serverSide.InternalServerError;
import com.telq.sdk.model.MockResponses;
import com.telq.sdk.model.TelQUrls;
import com.telq.sdk.model.authorization.TokenBearer;
import com.telq.sdk.model.network.DestinationNetwork;
import com.telq.sdk.model.network.Network;
import com.telq.sdk.model.tests.RequestTestDto;
import com.telq.sdk.model.token.TokenRequestDto;
import com.telq.sdk.service.authorization.AuthorizationService;
import com.telq.sdk.service.rest.RestV2ApiConnectorService;
import com.telq.sdk.utils.JsonMapper;
import lombok.NonNull;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class RestV2ApiConnectorServiceTest extends BaseTest {


    @Mock
    private CloseableHttpClient mockClient;
    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private CloseableHttpResponse tokenResponse;
    @Mock
    private CloseableHttpResponse networksResponse;
    @Mock
    private CloseableHttpResponse sendTestsResponse;
    @Mock
    private CloseableHttpResponse testResultResponse;
    @Mock
    private StatusLine statusLine200;
    @Mock
    private StatusLine statusLine400;
    @Mock
    private StatusLine statusLine500;

    private RestV2ApiConnectorService connectorService;

    private HttpPost requestPost;

    @Before
    public void setup() throws Exception {
        connectorService = new RestV2ApiConnectorService(mockClient);

        when(statusLine200.getStatusCode()).thenReturn(200);
        when(statusLine400.getStatusCode()).thenReturn(400);
        when(statusLine500.getStatusCode()).thenReturn(500);

        when(authorizationService.checkAndGetToken()).thenReturn(TokenBearer.builder().token(token).build());
    }

    @Test
    public void getToken_correctData_pass() throws Exception {
        TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                .appId(appId)
                .appKey(appKey)
                .build();

        requestPost = new HttpPost(TelQUrls.getTokenUrl());
        requestPost.setEntity(new StringEntity(JsonMapper.getInstance().getMapper().toJson(tokenRequestDto)));
        when(this.tokenResponse.getStatusLine()).thenReturn(statusLine200);

        JSONObject tokenResponse = new JSONObject();
        tokenResponse.put("ttl", ttl);
        tokenResponse.put("value", token);
        when(this.tokenResponse.getEntity()).thenReturn(EntityBuilder.create().setText(tokenResponse.toString()).build());
        when(mockClient.execute(requestPost)).thenReturn(this.tokenResponse);

        TokenBearer token = connectorService.getToken(requestPost);

        assertEquals(this.token, token.getToken());
    }

    @Test
    public void getToken_incorrectAppKey_exceptionThrown() throws Exception {
        TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                .appId(appId)
                .appKey(appKey)
                .build();

        requestPost = new HttpPost(TelQUrls.getTokenUrl());
        requestPost.setEntity(new StringEntity(JsonMapper.getInstance().getMapper().toJson(tokenRequestDto)));
        when(this.tokenResponse.getStatusLine()).thenReturn(statusLine400);

        JSONObject tokenResponse = new JSONObject();
        tokenResponse.put("ttl", ttl);
        tokenResponse.put("value", token);
        when(this.tokenResponse.getEntity()).thenReturn(EntityBuilder.create().setText(tokenResponse.toString()).build());
        when(mockClient.execute(requestPost)).thenReturn(this.tokenResponse);

        try {
           connectorService.getToken(requestPost);
        } catch (Exception e) {
            assertTrue(e instanceof BadRequest);
        }
    }

    @Test
    public void getNetworks_pass() throws Exception {
        HttpGet networksGet = new HttpGet(TelQUrls.getNetworksUrl());
        when(this.networksResponse.getStatusLine()).thenReturn(statusLine200);

        Network[] networks = new Network[3];
        networks[0] = Network.builder().mcc("289").mnc("88").build();
        networks[1] = Network.builder().mcc("289").mnc("87").build();
        networks[2] = Network.builder().mcc("289").mnc("86").build();

        when(this.networksResponse.getEntity()).thenReturn(
                EntityBuilder
                        .create()
                        .setText(JsonMapper.getInstance().getMapper().toJson(networks)).build());
        when(mockClient.execute(networksGet)).thenReturn(networksResponse);

        List<Network> responseNetworks = connectorService.getNetworks(authorizationService, networksGet);

        assertEquals(3, responseNetworks.size());
    }

    @Test
    public void getNetworks_detailedResponse_pass() throws Exception {
        HttpGet networksGet = new HttpGet(TelQUrls.getNetworksUrl());
        when(this.networksResponse.getStatusLine()).thenReturn(statusLine200);

        when(this.networksResponse.getEntity()).thenReturn(
                EntityBuilder
                        .create()
                        .setText(MockResponses.getNetworksResponse).build());
        when(mockClient.execute(networksGet)).thenReturn(networksResponse);

        List<Network> responseNetworks = connectorService.getNetworks(authorizationService, networksGet);

        assertEquals(3, responseNetworks.size());

        List<Network> expectedResponse = new ArrayList<>();
        expectedResponse.add(Network.builder()
                .mcc("289")
                .mnc("88")
                .providerName("A-Mobile")
                .countryName("Abkhazia")
                .portedFromMnc("99")
                .build());
        expectedResponse.add(Network.builder()
                .mcc("289")
                .mnc("67")
                .providerName("Aquafon")
                .countryName("Abkhazia")
                .portedFromProviderName("PortedProviderName")
                .build());
        expectedResponse.add(Network.builder()
                .mcc("412")
                .mnc("01")
                .providerName("AWCC")
                .countryName("Afghanistan")
                .build());


        assertEquals(expectedResponse, responseNetworks);
    }


    @Test
    public void sendTests_basicParams_pass() throws Exception {
        List<Network> networks = new ArrayList<>();
        networks.add(Network.builder().mcc("289").mnc("88").build());
        networks.add(Network.builder().mcc("289").mnc("87").build());
        networks.add(Network.builder().mcc("289").mnc("86").build());
        List<DestinationNetwork> destinationNetworks = convertToDestinationNetwork(networks);

        requestPost = formTestInitiationRequest(destinationNetworks,
                -1,
                null,
                3600,
                null);

        com.telq.sdk.model.tests.Test[] tests = new com.telq.sdk.model.tests.Test[3];
        tests[0] = com.telq.sdk.model.tests.Test.builder()
                .id(6783L)
                .phoneNumber("79407280661")
                .testIdText("nkxofabewq")
                .build();
        tests[1] = com.telq.sdk.model.tests.Test.builder()
                .id(6784L)
                .phoneNumber("79407280661")
                .testIdText("nkxofabewq")
                .build();
        tests[2] = com.telq.sdk.model.tests.Test.builder()
                .id(6785L)
                .phoneNumber("79407280661")
                .testIdText("nkxofabewq")
                .build();

        when(this.sendTestsResponse.getStatusLine()).thenReturn(statusLine200);
        when(this.sendTestsResponse.getEntity()).thenReturn(
                EntityBuilder
                        .create()
                        .setText(JsonMapper.getInstance().getMapper().toJson(tests)).build());

        when(mockClient.execute(requestPost)).thenReturn(sendTestsResponse);

        List<com.telq.sdk.model.tests.Test> testsResponse = connectorService.sendTests(
                authorizationService,
                requestPost);

        assertEquals(3, testsResponse.size());


    }

    @Test
    public void sendTests_basicParams_exceptionThrown500() throws Exception {
        List<Network> networks = new ArrayList<>();
        networks.add(Network.builder().mcc("289").mnc("88").build());
        networks.add(Network.builder().mcc("289").mnc("87").build());
        networks.add(Network.builder().mcc("289").mnc("86").build());
        List<DestinationNetwork> destinationNetworks = convertToDestinationNetwork(networks);

        requestPost = formTestInitiationRequest(destinationNetworks,
                -1,
                null,
                3600,
                null);

        com.telq.sdk.model.tests.Test[] tests = new com.telq.sdk.model.tests.Test[3];
        tests[0] = com.telq.sdk.model.tests.Test.builder()
                .id(6783L)
                .phoneNumber("79407280661")
                .testIdText("nkxofabewq")
                .build();
        tests[1] = com.telq.sdk.model.tests.Test.builder()
                .id(6784L)
                .phoneNumber("79407280661")
                .testIdText("nkxofabewq")
                .build();
        tests[2] = com.telq.sdk.model.tests.Test.builder()
                .id(6785L)
                .phoneNumber("79407280661")
                .testIdText("nkxofabewq")
                .build();

        when(this.sendTestsResponse.getStatusLine()).thenReturn(statusLine500);
        when(this.sendTestsResponse.getEntity()).thenReturn(
                EntityBuilder
                        .create()
                        .setText(JsonMapper.getInstance().getMapper().toJson(tests)).build());

        when(mockClient.execute(requestPost)).thenReturn(sendTestsResponse);

        try {
            connectorService.sendTests(
                    authorizationService,
                    requestPost);
        } catch (Exception e) {
            assertTrue(e instanceof InternalServerError);
        }



    }

    @Test
    public void sendTests_basicParams_exceptionThrown400() throws Exception {
        List<Network> networks = new ArrayList<>();
        networks.add(Network.builder().mcc("289").mnc("88").build());
        networks.add(Network.builder().mcc("289").mnc("87").build());
        networks.add(Network.builder().mcc("289").mnc("86").build());
        List<DestinationNetwork> destinationNetworks = convertToDestinationNetwork(networks);

        requestPost = formTestInitiationRequest(destinationNetworks,
                -1,
                null,
                3600,
                null);

        com.telq.sdk.model.tests.Test[] tests = new com.telq.sdk.model.tests.Test[3];
        tests[0] = com.telq.sdk.model.tests.Test.builder()
                .id(6783L)
                .phoneNumber("79407280661")
                .testIdText("nkxofabewq")
                .build();
        tests[1] = com.telq.sdk.model.tests.Test.builder()
                .id(6784L)
                .phoneNumber("79407280661")
                .testIdText("nkxofabewq")
                .build();
        tests[2] = com.telq.sdk.model.tests.Test.builder()
                .id(6785L)
                .phoneNumber("79407280661")
                .testIdText("nkxofabewq")
                .build();

        when(this.sendTestsResponse.getStatusLine()).thenReturn(statusLine400);
        when(this.sendTestsResponse.getEntity()).thenReturn(
                EntityBuilder
                        .create()
                        .setText(JsonMapper.getInstance().getMapper().toJson(tests)).build());

        when(mockClient.execute(requestPost)).thenReturn(sendTestsResponse);

        try {
            connectorService.sendTests(
                    authorizationService,
                    requestPost);
        } catch (Exception e) {
            assertTrue(e instanceof BadRequest);
        }



    }

    private List<DestinationNetwork> convertToDestinationNetwork(List<Network> networks) {
        List<DestinationNetwork> destinationNetworks = new ArrayList<>();

        networks.forEach(network ->
                destinationNetworks.add(DestinationNetwork.builder()
                        .mcc(network.getMcc())
                        .mnc(network.getMnc())
                        .portedFromMnc(network.getPortedFromMnc())
                        .build())
        );

        return destinationNetworks;
    }

    private HttpPost formTestInitiationRequest(@NonNull List<DestinationNetwork> destinationNetworks,
                                               int maxCallbackRetries,
                                               String resultsCallbackUrl,
                                               int testTimeToLiveInSeconds, String resultsCallBackToken) throws Exception {

        RequestTestDto requestTestDto = RequestTestDto.builder()
                .destinationNetworks(destinationNetworks)
                .build();

        if(maxCallbackRetries >= 0)
            requestTestDto.setMaxCallbackRetries(maxCallbackRetries);
        if(resultsCallbackUrl != null)
            requestTestDto.setResultsCallbackUrl(resultsCallbackUrl);
        if(testTimeToLiveInSeconds >= 0)
            requestTestDto.setTestTimeToLiveInSeconds(testTimeToLiveInSeconds);


        HttpPost request = new HttpPost(TelQUrls.getTestsUrl());

        if(resultsCallBackToken != null)
            request.setHeader("results-callback-token", resultsCallBackToken);

        request.setEntity(new StringEntity(JsonMapper.getInstance().getMapper().toJson(requestTestDto)));

        return request;
    }

}
