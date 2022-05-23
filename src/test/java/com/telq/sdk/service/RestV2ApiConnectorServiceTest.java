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
import com.telq.sdk.model.tests.Result;
import com.telq.sdk.model.token.TokenRequestDto;
import com.telq.sdk.service.authorization.AuthorizationService;
import com.telq.sdk.service.rest.RestV2ApiConnectorService;
import com.telq.sdk.utils.JsonMapper;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
        Mockito.lenient().when(this.tokenResponse.getStatusLine()).thenReturn(statusLine200);

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
        Mockito.lenient().when(this.tokenResponse.getStatusLine()).thenReturn(statusLine400);

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
        Mockito.lenient().when(this.tokenResponse.getStatusLine()).thenReturn(statusLine200);

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
        Mockito.lenient().when(this.tokenResponse.getStatusLine()).thenReturn(statusLine400);

        Mockito.lenient().when(this.tokenResponse.getEntity()).thenReturn(EntityBuilder.create().setText(MockResponses.getTokenInvalid).build());
        Mockito.lenient().when(mockClient.execute(requestPost)).thenReturn(this.tokenResponse);

        assertThrows(BadRequest.class, () -> connectorService.getToken(requestPost));
    }

    @Test
    public void getNetworks_pass() throws Exception {
        HttpGet networksGet = new HttpGet(TelQUrls.getNetworksUrl());
        Mockito.lenient().when(this.networksResponse.getStatusLine()).thenReturn(statusLine200);

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
        Mockito.lenient().when(this.networksResponse.getStatusLine()).thenReturn(statusLine200);

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

//    @Test TODO
//    public void getNetworks_invalidToken_badRequestExpected() {
//
//    }


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

        Mockito.lenient().when(this.sendTestsResponse.getStatusLine()).thenReturn(statusLine200);
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

        Mockito.lenient().when(this.sendTestsResponse.getStatusLine()).thenReturn(statusLine200);
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

//    @Test TODO
//    @SneakyThrows
//    public void sendTests_invalidToken_badRequestExpected() {
//
//    }

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

        Mockito.lenient().when(this.sendTestsResponse.getStatusLine()).thenReturn(statusLine200);
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

        Mockito.lenient().when(this.sendTestsResponse.getStatusLine()).thenReturn(statusLine400);
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

        Mockito.lenient().when(this.sendTestsResponse.getStatusLine()).thenReturn(statusLine500);
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

        Mockito.lenient().when(this.sendTestsResponse.getStatusLine()).thenReturn(statusLine400);
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
        Mockito.lenient().when(this.testResultResponse.getStatusLine()).thenReturn(statusLine200);
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
        Mockito.lenient().when(this.testResultResponse.getStatusLine()).thenReturn(statusLine200);
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
        Mockito.lenient().when(this.testResultResponse.getStatusLine()).thenReturn(statusLine400);
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
            requestTestDto.setCallbackUrl(callbackUrl);
        if(testTimeToLiveInSeconds >= 0)
            requestTestDto.setTestTimeToLiveInSeconds(testTimeToLiveInSeconds);


        HttpPost request = new HttpPost(TelQUrls.getTestsUrl());

        if(callBackToken != null)
            request.setHeader("results-callback-token", callBackToken);

        request.setEntity(new StringEntity(JsonMapper.getInstance().getMapper().toJson(requestTestDto)));

        return request;
    }

}
