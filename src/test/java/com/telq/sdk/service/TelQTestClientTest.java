package com.telq.sdk.service;

import com.telq.sdk.BaseTest;
import com.telq.sdk.clients.TelQTestClient;
import com.telq.sdk.model.network.DestinationNetwork;
import com.telq.sdk.model.network.Network;
import com.telq.sdk.model.tests.*;
import com.telq.sdk.model.v3.mt.MtApiTestResultDto;
import com.telq.sdk.service.authorization.AuthorizationService;
import com.telq.sdk.service.rest.ApiConnectorService;
import com.telq.sdk.utils.JsonMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;


@ExtendWith(
        MockitoExtension.class
)
public class TelQTestClientTest extends BaseTest {

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private ApiConnectorService apiConnectorService;


    @Captor
    ArgumentCaptor<List<DestinationNetwork>> networksCaptor;
    @Captor
    ArgumentCaptor<Integer> maxCallbackRetriesCaptor;
    @Captor
    ArgumentCaptor<String> callbackUrlCaptor;
    @Captor
    ArgumentCaptor<Integer> testTimeToLiveCaptor;
    @Captor
    ArgumentCaptor<String> callbackTokenCaptor;
    @Captor
    ArgumentCaptor<TestIdTextOptions> testIdTextOptionsCaptor;

    private TelQTestClient testClient;

    private Field authorizationServiceField;

    private Field apiConnectorServiceField;

    private List<Network> mockNetworks = Arrays.asList(
            Network.builder().countryName("Serbia").providerName("Telenor").mcc("111").mnc("222").build(),
            Network.builder().countryName("Mexico").providerName("MexicoProvider").mcc("333").mnc("444").build()
    );

    private List<DestinationNetwork> mockDestinations = Arrays.asList(
            DestinationNetwork.builder().mcc(mockNetworks.get(0).getMcc()).mnc(mockNetworks.get(0).getMnc()).build(),
            DestinationNetwork.builder().mcc(mockNetworks.get(1).getMcc()).mnc(mockNetworks.get(1).getMnc()).build()
    );

    private List<com.telq.sdk.model.tests.Test> returnTests = Arrays.asList(
            com.telq.sdk.model.tests.Test.builder()
                    .id(1L)
                    .destinationNetwork(mockDestinations.get(0))
                    .phoneNumber("38112311231")
                    .testIdText("testThisIs")
                    .build(),
            com.telq.sdk.model.tests.Test.builder()
                    .id(2L)
                    .destinationNetwork(mockDestinations.get(1))
                    .phoneNumber("381999999")
                    .testIdText("testThisIs2")
                    .build()
    );

    @BeforeEach
    public void setup() throws Exception {
        testClient = TelQTestClient.getInstance(appKey, appId);

        //GetNetworks
        Mockito.lenient().when(apiConnectorService.getNetworks(any(), any())).thenReturn(mockNetworks);

        //InitiateTests
        Mockito.lenient().when(apiConnectorService.sendTests(
                        eq(authorizationService),
                        any()))
                .thenReturn(returnTests);

        Mockito.lenient().when(apiConnectorService.getTestResult(
                        eq(authorizationService),
                        any()))
                .thenReturn(Result.builder().id(1L).build());

        authorizationServiceField = TelQTestClient.class.getDeclaredField("authorizationService");
        authorizationServiceField.setAccessible(true);
        authorizationServiceField.set(testClient, authorizationService);

        apiConnectorServiceField = TelQTestClient.class.getDeclaredField("apiConnectorService");
        apiConnectorServiceField.setAccessible(true);
        apiConnectorServiceField.set(testClient, apiConnectorService);

    }

    @AfterEach
    public void restore() {
        authorizationServiceField.setAccessible(false);
        apiConnectorServiceField.setAccessible(false);
    }


    @Test
    public void getNetworksTest_pass() throws Exception {
        List<Network> networks = testClient.getNetworks();

        assertEquals(mockNetworks.size(), networks.size());
        assertEquals(mockNetworks.get(0).getCountryName(), networks.get(0).getCountryName());
    }

    @Test
    public void initiateNewTests_onlyNetworks_pass() throws Exception {
        List<Network> networks = testClient.getNetworks();

        List<com.telq.sdk.model.tests.Test> tests = testClient.initiateNewTests(networks);

        assertEquals(returnTests.size(), tests.size());
        assertEquals(returnTests.get(0).getId(), tests.get(0).getId());
    }

    @Test
    public void initiateNewTests_onlyNetworks_plusTestIdTextOptions_pass() throws Exception {
        List<Network> networks = testClient.getNetworks();

        List<com.telq.sdk.model.tests.Test> tests = testClient.initiateNewTests(networks,
                TestIdTextOptions.builder()
                        .testIdTextType(TestIdTextType.ALPHA_NUMERIC)
                        .testIdTextCase(TestIdTextCase.MIXED)
                        .testIdTextLength(6)
                        .build());

        assertEquals(returnTests.size(), tests.size());
        assertEquals(returnTests.get(0).getId(), tests.get(0).getId());
    }

    @Test
    public void initiateNewTests_invalidNetworks_pass() throws Exception {
        List<Network> networks = testClient.getNetworks();
        networks.get(0).setMcc("");
        try {
            List<com.telq.sdk.model.tests.Test> tests = testClient.initiateNewTests(networks);
        } catch (Exception e) {
            assertEquals("Incorrect data passed in networks.", e.getMessage());
        }
    }


    @Test
    public void initiateNewTests_withTtl_pass() throws Exception {
        List<Network> networks = testClient.getNetworks();

        List<com.telq.sdk.model.tests.Test> tests = testClient.initiateNewTests(networks, 5, TimeUnit.SECONDS);

        assertEquals(returnTests.size(), tests.size());
        assertEquals(returnTests.get(0).getId(), tests.get(0).getId());
    }

    @Test
    public void initiateNewTests_withTtlAsMinute_pass() throws Exception {
        List<Network> networks = testClient.getNetworks();

        List<com.telq.sdk.model.tests.Test> tests = testClient.initiateNewTests(networks, 1, TimeUnit.MINUTES);

        assertEquals(returnTests.size(), tests.size());
        assertEquals(returnTests.get(0).getId(), tests.get(0).getId());
    }

    @Test
    public void initiateNewTests_allParamsPresent_pass() throws Exception {
        List<Network> networks = testClient.getNetworks();

        List<com.telq.sdk.model.tests.Test> tests = testClient.initiateNewTests(networks, 3, "callbackurl", "callbacktoken", 5, TimeUnit.SECONDS);

        assertEquals(returnTests.size(), tests.size());
        assertEquals(returnTests.get(0).getId(), tests.get(0).getId());
    }

    @Test
    public void initiateNewTests_allParamsPresent_plusTestIdTextOptions_pass() throws Exception {
        List<Network> networks = testClient.getNetworks();

        List<com.telq.sdk.model.tests.Test> tests = testClient.initiateNewTests(networks, 3, "callbackurl",
                "callbacktoken", 5, TimeUnit.SECONDS,
                TestIdTextOptions.builder()
                        .testIdTextType(TestIdTextType.ALPHA_NUMERIC)
                        .testIdTextCase(TestIdTextCase.MIXED)
                        .testIdTextLength(6)
                        .build());

        assertEquals(returnTests.size(), tests.size());
        assertEquals(returnTests.get(0).getId(), tests.get(0).getId());
    }

    @Test
    public void initiateNewTests_TestRequestObject_allParamsPresent_pass() throws Exception {
        // given
        TestRequest testRequest = TestRequest.builder()
                .networks(testClient.getNetworks())
                .maxCallbackRetries(3)
                .callbackUrl("callbackurl")
                .callbackToken("callbacktoken")
                .testTimeToLive(5)
                .timeUnit(TimeUnit.SECONDS)
                .testIdTextOptions(
                        TestIdTextOptions.builder()
                                .testIdTextType(TestIdTextType.ALPHA_NUMERIC)
                                .testIdTextCase(TestIdTextCase.MIXED)
                                .testIdTextLength(6)
                                .build())
                .build();

        // when
        List<com.telq.sdk.model.tests.Test> tests = testClient.createTests(testRequest);

        // then
        assertEquals(returnTests.size(), tests.size());
        assertEquals(returnTests.get(0).getId(), tests.get(0).getId());
        assertThatAllParamsAreMappedCorrectly(testRequest);
    }


    @Test
    public void initiateNewTests_TestRequestObject_onlyNetworks_pass() throws Exception {
        TestRequest testRequest = TestRequest.builder()
                .networks(testClient.getNetworks())
                .build();

        List<com.telq.sdk.model.tests.Test> tests = testClient.createTests(testRequest);

        assertEquals(returnTests.size(), tests.size());
        assertEquals(returnTests.get(0).getId(), tests.get(0).getId());
        assertThatAllParamsAreMappedCorrectly(testRequest);
    }


    private void assertThatAllParamsAreMappedCorrectly(TestRequest testRequest) throws UnsupportedEncodingException {
        verify(apiConnectorService).buildHttpPostRequest(
                networksCaptor.capture(),
                maxCallbackRetriesCaptor.capture(),
                callbackUrlCaptor.capture(),
                testTimeToLiveCaptor.capture(),
                callbackTokenCaptor.capture(),
                testIdTextOptionsCaptor.capture()
        );

        int capturedMaxCallbackRetries = maxCallbackRetriesCaptor.getValue();
        String capturedCallbackUrl = callbackUrlCaptor.getValue();
        int capturedTestTimeToLive = testTimeToLiveCaptor.getValue();
        String capturedCallbackToken = callbackTokenCaptor.getValue();

        assertThat(capturedMaxCallbackRetries).isEqualTo(testRequest.getMaxCallbackRetries());
        assertThat(capturedCallbackUrl).isEqualTo(testRequest.getCallbackUrl());
        assertThat(capturedTestTimeToLive).isEqualTo(testRequest.getTestTimeToLive());
        assertThat(capturedCallbackToken).isEqualTo(testRequest.getCallbackToken());
    }

    @Test
    public void initiateNewTests_TestRequestObject_invalidNetworks_pass() throws Exception {
        List<Network> networks = testClient.getNetworks();
        networks.get(0).setMcc("");
        TestRequest testRequest = TestRequest.builder()
                .networks(networks)
                .build();

        try {
            List<com.telq.sdk.model.tests.Test> tests = testClient.createTests(testRequest);
        } catch (Exception e) {
            assertEquals("Incorrect data passed in networks.", e.getMessage());
        }
    }

    @Test
    public void getTestResult_validId_pass() throws Exception {
        MtApiTestResultDto result = testClient.getTestById(1L);
        assertEquals(1L, (long) result.getId());
    }

    @Test
    public void getTestResult_invalidId_pass() {
        try {
            MtApiTestResultDto result = testClient.getTestById(-1L);
        } catch (Exception e) {
            assertEquals("Invalid id passed", e.getMessage());
        }

    }

    @SneakyThrows
    @Test
    public void jsonMapperLoadTest_pass() {
        CyclicBarrier barrier = new CyclicBarrier(700);

        AtomicBoolean nullCaught = new AtomicBoolean(false);

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 700; i++) {
            threads.add(new Thread(() -> {
                try {
                    barrier.await();
                    if (JsonMapper.getInstance().getMapper() == null) {
                        throw new NullPointerException("JSON MAPPER NULL");
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                } catch (NullPointerException nullPointerException) {
                    nullCaught.set(true);
                }
            }));
        }

        threads.forEach(Thread::start);
        Thread.sleep(5000);
        if (nullCaught.get()) {
            fail();
        }
    }


}
