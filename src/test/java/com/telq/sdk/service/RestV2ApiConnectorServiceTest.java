package com.telq.sdk.service;

import com.telq.sdk.BaseTest;
import com.telq.sdk.exceptions.httpExceptions.clientSide.BadRequest;
import com.telq.sdk.exceptions.httpExceptions.clientSide.Unauthorized;
import com.telq.sdk.exceptions.httpExceptions.serverSide.InternalServerError;
import com.telq.sdk.model.MockResponses;
import com.telq.sdk.model.TelQUrls;
import com.telq.sdk.model.authorization.TokenBearer;
import com.telq.sdk.model.network.DestinationNetwork;
import com.telq.sdk.model.network.Network;
import com.telq.sdk.model.tests.RequestTestDto;
import com.telq.sdk.model.tests.Result;
import com.telq.sdk.model.token.TokenRequestDto;
import com.telq.sdk.service.authorization.AuthorizationService;
import com.telq.sdk.service.rest.RestV2ApiConnectorService;
import com.telq.sdk.utils.JsonMapper;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.StatusLine;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(
        MockitoExtension.class
)
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

    @BeforeEach
    public void setup() throws Exception {
        connectorService = new RestV2ApiConnectorService(mockClient);

        Mockito.lenient().when(statusLine200.getStatusCode()).thenReturn(200);
        Mockito.lenient().when(statusLine400.getStatusCode()).thenReturn(400);
        Mockito.lenient().when(statusLine500.getStatusCode()).thenReturn(500);

        Mockito.lenient().when(authorizationService.checkAndGetToken()).thenReturn(TokenBearer.builder().token(token).build());
    }

    @Test
    public void getToken_correctData_pass() throws Exception {
        TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                .appId(appId)
                .appKey(appKey)
                .build();

        requestPost = new HttpPost(TelQUrls.getTokenUrl());
        requestPost.setEntity(new StringEntity(JsonMapper.getInstance().getMapper().toJson(tokenRequestDto)));
        Mockito.lenient().when(this.tokenResponse.getCode()).thenReturn(200);

        JSONObject tokenResponse = new JSONObject();
        tokenResponse.put("ttl", ttl);
        tokenResponse.put("value", token);
        Mockito.lenient().when(this.tokenResponse.getEntity()).thenReturn(EntityBuilder.create().setText(tokenResponse.toString()).build());
        Mockito.lenient().when(mockClient.execute(requestPost)).thenReturn(this.tokenResponse);

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
        Mockito.lenient().when(this.tokenResponse.getCode()).thenReturn(400);

        JSONObject tokenResponse = new JSONObject();
        tokenResponse.put("ttl", ttl);
        tokenResponse.put("value", token);
        Mockito.lenient().when(this.tokenResponse.getEntity()).thenReturn(EntityBuilder.create().setText(tokenResponse.toString()).build());
        Mockito.lenient().when(mockClient.execute(requestPost)).thenReturn(this.tokenResponse);

        try {
           connectorService.getToken(requestPost);
        } catch (Exception e) {
            assertTrue(e instanceof BadRequest);
        }
    }

    @Test
    @SneakyThrows
    public void getToken_validCredentialsValidResponse_pass() {
        TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                .appId(appId)
                .appKey(appKey)
                .build();

        requestPost = new HttpPost(TelQUrls.getTokenUrl());
        requestPost.setEntity(new StringEntity(JsonMapper.getInstance().getMapper().toJson(tokenRequestDto)));
        Mockito.lenient().when(this.tokenResponse.getCode()).thenReturn(200);

        Mockito.lenient().when(this.tokenResponse.getEntity()).thenReturn(EntityBuilder.create().setText(MockResponses.getTokenValid).build());
        Mockito.lenient().when(mockClient.execute(requestPost)).thenReturn(this.tokenResponse);

        TokenBearer tokenBearer = connectorService.getToken(requestPost);
        assertEquals(MockResponses.token, tokenBearer.getToken());
    }

    @Test
    @SneakyThrows
    public void getToken_invalidCredentials_badRequestExpected() {
        TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                .appId(appId)
                .appKey(appKey)
                .build();

        requestPost = new HttpPost(TelQUrls.getTokenUrl());
        requestPost.setEntity(new StringEntity(JsonMapper.getInstance().getMapper().toJson(tokenRequestDto)));
        Mockito.lenient().when(this.tokenResponse.getCode()).thenReturn(400);

        Mockito.lenient().when(this.tokenResponse.getEntity()).thenReturn(EntityBuilder.create().setText(MockResponses.getTokenInvalid).build());
        Mockito.lenient().when(mockClient.execute(requestPost)).thenReturn(this.tokenResponse);

        assertThrows(BadRequest.class, () -> connectorService.getToken(requestPost));
    }

    @Test
    public void getNetworks_pass() throws Exception {
        HttpGet networksGet = new HttpGet(TelQUrls.getNetworksUrl());
        Mockito.lenient().when(this.networksResponse.getCode()).thenReturn(200);

        Network[] networks = new Network[3];
        networks[0] = Network.builder().mcc("289").mnc("88").build();
        networks[1] = Network.builder().mcc("289").mnc("87").build();
        networks[2] = Network.builder().mcc("289").mnc("86").build();

        Mockito.lenient().when(this.networksResponse.getEntity()).thenReturn(
                EntityBuilder
                        .create()
                        .setText(JsonMapper.getInstance().getMapper().toJson(networks)).build());
        Mockito.lenient().when(mockClient.execute(networksGet)).thenReturn(networksResponse);

        List<Network> responseNetworks = connectorService.getNetworks(authorizationService, networksGet);

        assertEquals(3, responseNetworks.size());
    }

    @Test
    public void getNetworks_detailedResponse_pass() throws Exception {
        HttpGet networksGet = new HttpGet(TelQUrls.getNetworksUrl());
        Mockito.lenient().when(this.networksResponse.getCode()).thenReturn(200);

        Mockito.lenient().when(this.networksResponse.getEntity()).thenReturn(
                EntityBuilder
                        .create()
                        .setText(MockResponses.getNetworksResponseDetailedValid).build());
        Mockito.lenient().when(mockClient.execute(networksGet)).thenReturn(networksResponse);

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
    public void sendTests_unauthorizedRefreshesTokenAndRetriesOnce() throws Exception {
        TokenBearer rejectedToken = TokenBearer.builder().token("rejected-token").build();
        TokenBearer refreshedToken = TokenBearer.builder().token("refreshed-token").build();
        CloseableHttpResponse unauthorizedResponse = mockResponse(401, null);
        CloseableHttpResponse successfulResponse = mockResponse(200, "[]");
        List<String> authorizationHeaders = new ArrayList<>();
        AtomicInteger executions = new AtomicInteger();
        HttpPost request = new HttpPost(TelQUrls.getTestsUrl());
        StringEntity requestEntity = new StringEntity("request-body");
        request.setEntity(requestEntity);

        Mockito.when(authorizationService.checkAndGetToken()).thenReturn(rejectedToken, rejectedToken);
        Mockito.when(authorizationService.requestToken()).thenReturn(refreshedToken);
        Mockito.when(mockClient.execute(Mockito.any(HttpUriRequestBase.class))).thenAnswer(invocation -> {
            HttpUriRequestBase executedRequest = invocation.getArgument(0);
            authorizationHeaders.add(executedRequest.getFirstHeader("Authorization").getValue());
            return executions.getAndIncrement() == 0 ? unauthorizedResponse : successfulResponse;
        });

        List<com.telq.sdk.model.tests.Test> response = connectorService.sendTests(authorizationService, request);

        assertTrue(response.isEmpty());
        assertSame(requestEntity, request.getEntity());
        assertTrue(request.getEntity().isRepeatable());
        assertEquals(2, executions.get());
        assertEquals("Bearer rejected-token", authorizationHeaders.get(0));
        assertEquals("Bearer refreshed-token", authorizationHeaders.get(1));
        Mockito.verify(authorizationService, Mockito.times(1)).requestToken();
        Mockito.verify(unauthorizedResponse, Mockito.times(1)).close();
        Mockito.verify(successfulResponse, Mockito.times(1)).close();
    }

    @Test
    public void getNetworks_secondUnauthorizedThrowsAfterSingleRetry() throws Exception {
        TokenBearer rejectedToken = TokenBearer.builder().token("rejected-token").build();
        TokenBearer refreshedToken = TokenBearer.builder().token("refreshed-token").build();
        CloseableHttpResponse firstUnauthorizedResponse = mockResponse(401, null);
        CloseableHttpResponse secondUnauthorizedResponse = mockResponse(401, null);
        HttpGet request = new HttpGet(TelQUrls.getNetworksUrl());

        Mockito.when(authorizationService.checkAndGetToken()).thenReturn(rejectedToken, rejectedToken);
        Mockito.when(authorizationService.requestToken()).thenReturn(refreshedToken);
        Mockito.when(mockClient.execute(request)).thenReturn(firstUnauthorizedResponse, secondUnauthorizedResponse);

        assertThrows(Unauthorized.class, () -> connectorService.getNetworks(authorizationService, request));

        assertEquals("Bearer refreshed-token", request.getFirstHeader("Authorization").getValue());
        Mockito.verify(mockClient, Mockito.times(2)).execute(request);
        Mockito.verify(authorizationService, Mockito.times(1)).requestToken();
        Mockito.verify(firstUnauthorizedResponse, Mockito.times(1)).close();
        Mockito.verify(secondUnauthorizedResponse, Mockito.times(1)).close();
    }

    @Test
    public void sendTests_tokenRefreshFailureDoesNotRetryAndClosesResponse() throws Exception {
        TokenBearer rejectedToken = TokenBearer.builder().token("rejected-token").build();
        CloseableHttpResponse unauthorizedResponse = mockResponse(401, null);
        HttpPost request = new HttpPost(TelQUrls.getTestsUrl());

        Mockito.when(authorizationService.checkAndGetToken()).thenReturn(rejectedToken, rejectedToken);
        Mockito.when(authorizationService.requestToken()).thenThrow(new IllegalStateException("refresh failed"));
        Mockito.when(mockClient.execute(request)).thenReturn(unauthorizedResponse);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> connectorService.sendTests(authorizationService, request)
        );

        assertEquals("refresh failed", exception.getMessage());
        Mockito.verify(mockClient, Mockito.times(1)).execute(request);
        Mockito.verify(authorizationService, Mockito.times(1)).requestToken();
        Mockito.verify(unauthorizedResponse, Mockito.times(1)).close();
    }

    @Test
    public void sendTests_unauthorizedNonRepeatableBodyDoesNotRetry() throws Exception {
        TokenBearer rejectedToken = TokenBearer.builder().token("rejected-token").build();
        CloseableHttpResponse unauthorizedResponse = mockResponse(401, null);
        HttpPost request = new HttpPost(TelQUrls.getTestsUrl());
        byte[] requestBody = "request-body".getBytes(StandardCharsets.UTF_8);
        request.setEntity(new InputStreamEntity(
                new ByteArrayInputStream(requestBody),
                requestBody.length,
                ContentType.APPLICATION_JSON
        ));

        Mockito.when(authorizationService.checkAndGetToken()).thenReturn(rejectedToken);
        Mockito.when(mockClient.execute(request)).thenReturn(unauthorizedResponse);

        assertThrows(Unauthorized.class, () -> connectorService.sendTests(authorizationService, request));

        assertFalse(request.getEntity().isRepeatable());
        Mockito.verify(mockClient, Mockito.times(1)).execute(request);
        Mockito.verify(authorizationService, Mockito.never()).requestToken();
        Mockito.verify(unauthorizedResponse, Mockito.times(1)).close();
    }

    @Test
    public void getToken_unauthorizedDoesNotRefreshOrRetry() throws Exception {
        HttpPost request = new HttpPost(TelQUrls.getTokenUrl());
        CloseableHttpResponse unauthorizedResponse = mockResponse(401, null);
        Mockito.when(mockClient.execute(request)).thenReturn(unauthorizedResponse);

        assertThrows(Unauthorized.class, () -> connectorService.getToken(request));

        Mockito.verify(mockClient, Mockito.times(1)).execute(request);
        Mockito.verifyNoInteractions(authorizationService);
        Mockito.verify(unauthorizedResponse, Mockito.times(1)).close();
    }

    @Test
    public void getTestResult_badRequestDoesNotRefreshOrRetry() throws Exception {
        HttpGet request = new HttpGet(TelQUrls.getResultsUrl() + "/123");
        CloseableHttpResponse badRequestResponse = mockResponse(400, null);
        Mockito.when(mockClient.execute(request)).thenReturn(badRequestResponse);

        assertThrows(BadRequest.class, () -> connectorService.getTestResult(authorizationService, request));

        Mockito.verify(mockClient, Mockito.times(1)).execute(request);
        Mockito.verify(authorizationService, Mockito.times(1)).checkAndGetToken();
        Mockito.verify(authorizationService, Mockito.never()).requestToken();
        Mockito.verify(badRequestResponse, Mockito.times(1)).close();
    }

    @Test
    public void concurrentUnauthorizedResponsesShareOneTokenRefresh() throws Exception {
        TokenBearer rejectedToken = TokenBearer.builder().token("rejected-token").build();
        TokenBearer refreshedToken = TokenBearer.builder().token("refreshed-token").build();
        AtomicReference<TokenBearer> currentToken = new AtomicReference<>(rejectedToken);
        AtomicInteger refreshCount = new AtomicInteger();
        CountDownLatch rejectedRequestsStarted = new CountDownLatch(2);
        List<CloseableHttpResponse> responses = Collections.synchronizedList(new ArrayList<>());
        List<String> authorizationHeaders = new CopyOnWriteArrayList<>();

        Mockito.when(authorizationService.checkAndGetToken()).thenAnswer(invocation -> currentToken.get());
        Mockito.when(authorizationService.requestToken()).thenAnswer(invocation -> {
            refreshCount.incrementAndGet();
            currentToken.set(refreshedToken);
            return refreshedToken;
        });
        Mockito.when(mockClient.execute(Mockito.any(HttpUriRequestBase.class))).thenAnswer(invocation -> {
            HttpUriRequestBase request = invocation.getArgument(0);
            String authorizationHeader = request.getFirstHeader("Authorization").getValue();
            authorizationHeaders.add(authorizationHeader);

            CloseableHttpResponse response;
            if("Bearer rejected-token".equals(authorizationHeader)) {
                rejectedRequestsStarted.countDown();
                assertTrue(rejectedRequestsStarted.await(5, TimeUnit.SECONDS));
                response = mockResponse(401, null);
            } else if("Bearer refreshed-token".equals(authorizationHeader)) {
                response = mockResponse(200, "[]");
            } else {
                throw new AssertionError("Unexpected authorization header: " + authorizationHeader);
            }

            responses.add(response);
            return response;
        });

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            Future<List<Network>> firstRequest = executorService.submit(() -> connectorService.getNetworks(
                    authorizationService,
                    new HttpGet(TelQUrls.getNetworksUrl())
            ));
            Future<List<Network>> secondRequest = executorService.submit(() -> connectorService.getNetworks(
                    authorizationService,
                    new HttpGet(TelQUrls.getNetworksUrl())
            ));

            assertTrue(firstRequest.get(5, TimeUnit.SECONDS).isEmpty());
            assertTrue(secondRequest.get(5, TimeUnit.SECONDS).isEmpty());
        } finally {
            executorService.shutdownNow();
        }

        assertEquals(1, refreshCount.get());
        assertEquals(2, Collections.frequency(authorizationHeaders, "Bearer rejected-token"));
        assertEquals(2, Collections.frequency(authorizationHeaders, "Bearer refreshed-token"));
        assertEquals(4, responses.size());
        Mockito.verify(authorizationService, Mockito.times(1)).requestToken();
        Mockito.verify(mockClient, Mockito.times(4)).execute(Mockito.any(HttpUriRequestBase.class));
        for(CloseableHttpResponse response : responses) {
            Mockito.verify(response, Mockito.times(1)).close();
        }
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

        Mockito.lenient().when(this.sendTestsResponse.getCode()).thenReturn(200);
        Mockito.lenient().when(this.sendTestsResponse.getEntity()).thenReturn(
                EntityBuilder
                        .create()
                        .setText(JsonMapper.getInstance().getMapper().toJson(tests)).build());

        Mockito.lenient().when(mockClient.execute(requestPost)).thenReturn(sendTestsResponse);

        List<com.telq.sdk.model.tests.Test> testsResponse = connectorService.sendTests(
                authorizationService,
                requestPost);

        assertEquals(3, testsResponse.size());
    }

    @Test
    @SneakyThrows
    public void sendTests_basicParamsSingleTest_pass() {
        List<Network> networks = new ArrayList<>();
        networks.add(Network.builder().mcc("208").mnc("10").build());
        List<DestinationNetwork> destinationNetworks = convertToDestinationNetwork(networks);
        requestPost = formTestInitiationRequest(destinationNetworks,
                -1,
                null,
                3600,
                null);

        Mockito.lenient().when(this.sendTestsResponse.getCode()).thenReturn(200);
        Mockito.lenient().when(this.sendTestsResponse.getEntity()).thenReturn(EntityBuilder.create().setText(MockResponses.requestNewTestValid).build());
        Mockito.lenient().when(mockClient.execute(requestPost)).thenReturn(sendTestsResponse);

        List<com.telq.sdk.model.tests.Test> testsResponse = connectorService.sendTests(
                authorizationService,
                requestPost);

        assertEquals(1, testsResponse.size());
        assertEquals(Long.parseLong(MockResponses.requestTestId), (long) testsResponse.get(0).getId());
        assertEquals(MockResponses.testIdText, testsResponse.get(0).getTestIdText());
        assertEquals(MockResponses.requestTestPhoneNumber, testsResponse.get(0).getPhoneNumber());
        assertNull(testsResponse.get(0).getErrorMessage());
        assertEquals(MockResponses.requestTestMcc, testsResponse.get(0).getDestinationNetwork().getMcc());
        assertEquals(MockResponses.requestTestMnc, testsResponse.get(0).getDestinationNetwork().getMnc());
        assertEquals(MockResponses.requestTestPortedFromMnc, testsResponse.get(0).getDestinationNetwork().getPortedFromMnc());
    }

    @Test
    @SneakyThrows
    public void sendTests_basicParamsInvalidNetwork_pass() {
        List<Network> networks = new ArrayList<>();
        networks.add(Network.builder().mcc("208").mnc("99").build());
        List<DestinationNetwork> destinationNetworks = convertToDestinationNetwork(networks);
        requestPost = formTestInitiationRequest(destinationNetworks,
                -1,
                null,
                3600,
                null);

        Mockito.lenient().when(this.sendTestsResponse.getCode()).thenReturn(200);
        Mockito.lenient().when(this.sendTestsResponse.getEntity()).thenReturn(EntityBuilder.create().setText(MockResponses.requestNewTestWrongParamsInvalid).build());
        Mockito.lenient().when(mockClient.execute(requestPost)).thenReturn(sendTestsResponse);

        List<com.telq.sdk.model.tests.Test> testsResponse = connectorService.sendTests(
                authorizationService,
                requestPost);

        assertEquals(1, testsResponse.size());
        assertEquals(Long.parseLong(MockResponses.requestTestId), (long) testsResponse.get(0).getId());
        assertEquals(MockResponses.testIdText, testsResponse.get(0).getTestIdText());
        assertNull(testsResponse.get(0).getPhoneNumber());
        assertEquals("NETWORK_OFFLINE", testsResponse.get(0).getErrorMessage());
        assertEquals(MockResponses.requestTestMcc, testsResponse.get(0).getDestinationNetwork().getMcc());
        assertEquals("99", testsResponse.get(0).getDestinationNetwork().getMnc());
        assertEquals(MockResponses.requestTestPortedFromMnc, testsResponse.get(0).getDestinationNetwork().getPortedFromMnc());
    }

    @Test
    @SneakyThrows
    public void sendTests_missingParams_badRequestExpected() {
        List<Network> networks = new ArrayList<>();
        networks.add(Network.builder().mcc("208").build());
        List<DestinationNetwork> destinationNetworks = convertToDestinationNetwork(networks);
        requestPost = formTestInitiationRequest(destinationNetworks,
                -1,
                null,
                3600,
                null);

        Mockito.lenient().when(this.sendTestsResponse.getCode()).thenReturn(400);
        Mockito.lenient().when(this.sendTestsResponse.getEntity()).thenReturn(EntityBuilder.create().setText(MockResponses.requestNewTestMissingParamsInvalid).build());
        Mockito.lenient().when(mockClient.execute(requestPost)).thenReturn(sendTestsResponse);

        assertThrows(BadRequest.class, () -> connectorService.sendTests(
                authorizationService,
                requestPost));
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

        Mockito.lenient().when(this.sendTestsResponse.getCode()).thenReturn(500);
        Mockito.lenient().when(this.sendTestsResponse.getEntity()).thenReturn(
                EntityBuilder
                        .create()
                        .setText(JsonMapper.getInstance().getMapper().toJson(tests)).build());

        Mockito.lenient().when(mockClient.execute(requestPost)).thenReturn(sendTestsResponse);

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

        Mockito.lenient().when(this.sendTestsResponse.getCode()).thenReturn(400);
        Mockito.lenient().when(this.sendTestsResponse.getEntity()).thenReturn(
                EntityBuilder
                        .create()
                        .setText(JsonMapper.getInstance().getMapper().toJson(tests)).build());

        Mockito.lenient().when(mockClient.execute(requestPost)).thenReturn(sendTestsResponse);

        try {
            connectorService.sendTests(
                    authorizationService,
                    requestPost);
        } catch (Exception e) {
            assertTrue(e instanceof BadRequest);
        }
    }

    @Test
    @SneakyThrows
    public void getTestResult_validParams_pass() {
        HttpGet requestGet = new HttpGet(TelQUrls.getResultsUrl() + "/" + MockResponses.requestTestId);
        Mockito.lenient().when(this.testResultResponse.getCode()).thenReturn(200);
        Mockito.lenient().when(this.testResultResponse.getEntity()).thenReturn(EntityBuilder.create().setText(MockResponses.requestResultValid).build());
        Mockito.lenient().when(mockClient.execute(requestGet)).thenReturn(testResultResponse);

        Result result = connectorService.getTestResult(
                authorizationService,
                requestGet
        );

        assertEquals(Long.parseLong(MockResponses.requestResultId), (long) result.getId());
        assertEquals(MockResponses.testIdText, result.getTestIdText());
        assertNull(result.getSenderDelivered());
        assertNull(result.getTextDelivered());
        assertEquals(Instant.parse("2020-11-30T11:26:43Z"), result.getTestCreatedAt());
        assertNull(result.getSmsReceivedAt());
        assertNull(result.getReceiptDelay());
        assertEquals("WAIT", result.getTestStatus());
        assertEquals(MockResponses.requestResultMcc, result.getDestinationNetworkDetails().getMcc());
        assertEquals(MockResponses.requestResultMnc, result.getDestinationNetworkDetails().getMnc());
        assertEquals(MockResponses.requestResultPortedFromMnc, result.getDestinationNetworkDetails().getPortedFromMnc());
        assertEquals(MockResponses.requestResultCountryName, result.getDestinationNetworkDetails().getCountryName());
        assertEquals(MockResponses.requestResultProviderName, result.getDestinationNetworkDetails().getProviderName());
        assertEquals(MockResponses.requestResultProviderName, result.getDestinationNetworkDetails().getPortedFromProviderName());
        assertNull(result.getSmscInfo());
        assertTrue(result.getPdusDelivered().isEmpty());
    }

    @Test
    @SneakyThrows
    public void getTestResult_testDeliveredAndDetailed_pass() {
        HttpGet requestGet = new HttpGet(TelQUrls.getResultsUrl() + "/" + MockResponses.requestTestId);
        Mockito.lenient().when(this.testResultResponse.getCode()).thenReturn(200);
        Mockito.lenient().when(this.testResultResponse.getEntity()).thenReturn(EntityBuilder.create().setText(MockResponses.requestResultDetailedValid).build());
        Mockito.lenient().when(mockClient.execute(requestGet)).thenReturn(testResultResponse);

        Result result = connectorService.getTestResult(
                authorizationService,
                requestGet
        );

        assertEquals(Long.parseLong(MockResponses.requestResultId), (long) result.getId());
        assertEquals(MockResponses.testIdText, result.getTestIdText());
        assertEquals("8001", result.getSenderDelivered());
        assertEquals(MockResponses.testIdText + " is your code", result.getTextDelivered());
        assertEquals(Instant.parse("2020-11-30T11:53:31Z"), result.getTestCreatedAt());
        assertEquals(Instant.parse("2020-11-30T11:54:09Z"), result.getSmsReceivedAt());
        assertEquals(38, (long) result.getReceiptDelay());
        assertEquals("POSITIVE", result.getTestStatus());
        assertEquals(MockResponses.requestResultMcc, result.getDestinationNetworkDetails().getMcc());
        assertEquals(MockResponses.requestResultMnc, result.getDestinationNetworkDetails().getMnc());
        assertEquals(MockResponses.requestResultPortedFromMnc, result.getDestinationNetworkDetails().getPortedFromMnc());
        assertEquals(MockResponses.requestResultCountryName, result.getDestinationNetworkDetails().getCountryName());
        assertEquals(MockResponses.requestResultProviderName, result.getDestinationNetworkDetails().getProviderName());
        assertEquals(MockResponses.requestResultProviderName, result.getDestinationNetworkDetails().getPortedFromProviderName());
        assertEquals("32495002530", result.getSmscInfo().getSmscNumber());
        assertEquals("Belgium", result.getSmscInfo().getCountryName());
        assertEquals("BE", result.getSmscInfo().getCountryCode());
        assertEquals("206", result.getSmscInfo().getMcc());
        assertEquals("10", result.getSmscInfo().getMnc());
        assertEquals("Mobistar", result.getSmscInfo().getProviderName());
        assertEquals(1, result.getPdusDelivered().size());
        assertEquals("07912394052035F0040481081000000211032145704017593BF9EE565A9F792B283D07E5DF753968FC269701", result.getPdusDelivered().get(0));
    }

    @Test
    @SneakyThrows
    public void getTestResult_invalidId_badRequest() {
        HttpGet requestGet = new HttpGet(TelQUrls.getResultsUrl() + "/" + MockResponses.requestTestId);
        Mockito.lenient().when(this.testResultResponse.getCode()).thenReturn(400);
        Mockito.lenient().when(this.testResultResponse.getEntity()).thenReturn(EntityBuilder.create().setText(MockResponses.requestResultInvalidIdInvalid).build());
        Mockito.lenient().when(mockClient.execute(requestGet)).thenReturn(testResultResponse);

        assertThrows(BadRequest.class, () -> connectorService.getTestResult(
                authorizationService,
                requestGet
        ));
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

    private CloseableHttpResponse mockResponse(int statusCode, String content) throws Exception {
        CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(response.getCode()).thenReturn(statusCode);
        if(content != null) {
            Mockito.when(response.getEntity()).thenReturn(EntityBuilder.create().setText(content).build());
        }
        return response;
    }

    private HttpPost formTestInitiationRequest(@NonNull List<DestinationNetwork> destinationNetworks,
                                               int maxCallbackRetries,
                                               String callbackUrl,
                                               int testTimeToLiveInSeconds, String callBackToken) throws Exception {

        RequestTestDto requestTestDto = RequestTestDto.builder()
                .destinationNetworks(destinationNetworks)
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

        request.setEntity(new StringEntity(JsonMapper.getInstance().getMapper().toJson(requestTestDto)));

        return request;
    }

}
