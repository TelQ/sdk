package com.telq.sdk.functional;

import com.telq.sdk.TelQTestClient;
import com.telq.sdk.model.network.Network;
import com.telq.sdk.model.tests.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Functional {

    private static final String appKey = "someappkeyakjdkdajlDFASFDA";
    private static final String appId = "1234";


    public static void main(String[] args) throws Exception {
        TelQTestClient testClient = TelQTestClient.getInstance(appKey, appId);

        Long timeA = System.currentTimeMillis();
        List<Network> networks = testClient.getNetworks();

        System.out.print("\n>> Networks - Response in " + (System.currentTimeMillis()-timeA) +" ms <<\n" +  Arrays.toString(networks.toArray()));

        List<Network> requestNetworks = new ArrayList<>();

        Network network_1 = Network.builder()
                .mcc("206")
                .mnc("10")
                .portedFromMnc("20")
                .build();

        Network network_2 = Network.builder()
                .mcc("716")
                .mnc("06")
                .build();

        requestNetworks.add(network_1);
        requestNetworks.add(network_2);

        int maxCallBackRetries = 1;
        String callbackUrl = "https://some-callback-url-abcdadsa22.com/some-path";
        int testTimeToLive = 200;
        String callBackToken = "peHWFdAXikjzmMgqPTwhpeHWFdAXikjzmMgqPTwhpeHWFdAXikjzmMgqPTwh";

        TestIdTextOptions testIdTextOptions = TestIdTextOptions.builder()
                .testIdTextType(TestIdTextType.WHATSAPP_CODE)
                .testIdTextCase(TestIdTextCase.MIXED)
                .testIdTextLength(6)
                .build();

        timeA = System.currentTimeMillis();
        List<Test> requestedTests = testClient.initiateNewTests(requestNetworks, maxCallBackRetries, callbackUrl, callBackToken, testTimeToLive, TimeUnit.SECONDS, testIdTextOptions);

        System.out.print("\n\n>> Requested Tests - Response in " + (System.currentTimeMillis()-timeA) +" ms <<\n" +  Arrays.toString(requestedTests.toArray()));

        System.out.println("\n\n>> Test Results:");

        for(Test test: requestedTests){
           timeA = System.currentTimeMillis();
            Result result = testClient.getTestResult(test.getId());
            System.out.println(" - Response in " + (System.currentTimeMillis()-timeA) +" ms - " + result);
        }

    }

}
