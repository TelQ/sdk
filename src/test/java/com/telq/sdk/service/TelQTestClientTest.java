package com.telq.sdk.service;

import com.telq.sdk.BaseTest;
import com.telq.sdk.TelQTestClient;
import com.telq.sdk.model.network.DestinationNetwork;
import com.telq.sdk.model.network.Network;
import com.telq.sdk.model.tests.Result;
import com.telq.sdk.service.authorization.AuthorizationService;
import com.telq.sdk.service.rest.ApiConnectorService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class TelQTestClientTest extends BaseTest {

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private ApiConnectorService apiConnectorService;

    private TelQTestClient testClient;

    private Field authorizationServiceField;

    private Field apiConnectorServiceField;

    private List<Network> mockNetworks = Arrays.asList(Network.builder().country("Serbia").provider("Telenor").mcc("111").mnc("222").build(),
            Network.builder().country("Mexico").provider("MexicoProvider").mcc("333").mnc("444").build());

    private List<DestinationNetwork> mockDestinations = Arrays.asList(
                DestinationNetwork.builder().mcc(mockNetworks.get(0).getMcc()).mnc(mockNetworks.get(0).getMnc()).build(),
                DestinationNetwork.builder().mcc(mockNetworks.get(1).getMcc()).mnc(mockNetworks.get(1).getMnc()).build()
    );

    private List<com.telq.sdk.model.tests.Test> returnTests = Arrays.asList(
      com.telq.sdk.model.tests.Test.builder()
              .id(1L)
              .destinationNetworks(mockDestinations.get(0))
              .phoneNumber("38112311231")
              .testIdText("testThisIs")
              .build(),
        com.telq.sdk.model.tests.Test.builder()
                .id(2L)
                .destinationNetworks(mockDestinations.get(1))
                .phoneNumber("381999999")
                .testIdText("testThisIs2")
                .build()
    );

    @Before
    public void setup() throws Exception {
        testClient = TelQTestClient.getInstance(appKey, appId);

        //GetNetworks
        when(apiConnectorService.getNetworks(any(), any())).thenReturn(mockNetworks);

        //InitiateTests
        when(apiConnectorService.sendTests(
                eq(authorizationService),
                any()))
                .thenReturn(returnTests);

        when(apiConnectorService.getTestResult(eq(authorizationService), any())).thenReturn(Result.builder().id(1L).build());

        authorizationServiceField = TelQTestClient.class.getDeclaredField("authorizationService");
        authorizationServiceField.setAccessible(true);
        authorizationServiceField.set(testClient, authorizationService);

        apiConnectorServiceField = TelQTestClient.class.getDeclaredField("apiConnectorService");
        apiConnectorServiceField.setAccessible(true);
        apiConnectorServiceField.set(testClient, apiConnectorService);

    }

    @After
    public void restore() {
        authorizationServiceField.setAccessible(false);
        apiConnectorServiceField.setAccessible(false);
    }


    @Test
    public void getNetworksTest_pass() throws Exception {
        List<Network> networks = testClient.getNetworks();

        assertEquals(mockNetworks.size(), networks.size());
        assertEquals(mockNetworks.get(0).getCountry(), networks.get(0).getCountry());
    }

    @Test
    public void initiateNewTests_onlyNetworks_pass() throws Exception {
        List<Network> networks = testClient.getNetworks();

        List<com.telq.sdk.model.tests.Test> tests = testClient.initiateNewTests(networks);

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
    public void  initiateNewTests_withTtlAsMinute_pass() throws Exception {
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
    public void getTestResult_validId_pass() throws Exception {
        Result result = testClient.getTestResult(1L);

        assertEquals(1L, (long) result.getId());
    }

    @Test
    public void getTestResult_invalidId_pass()  {
        try {
            Result result = testClient.getTestResult(-1L);
        } catch (Exception e) {
            assertEquals("Invalid id passed", e.getMessage());
        }

    }




}
