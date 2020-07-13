package com.telq.sdk;


import com.telq.sdk.model.network.Network;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
//        TelQTestClient testClient = TelQTestClient.getInstance("VP1XD0R*P2GH9Hefd5o@ZmD%ND#u73LiGPy", "1515");
        TelQTestClient testClient = TelQTestClient.getInstance("o-/PTc)^5d+z/hLWK=#bs07y^wp+5yg%$C9", "1447");
//        List<Network> networks = testClient.getNetworks();

//        System.out.println(networks);
//
//        List<Network> shortenedList = new ArrayList<>();
//
//        for(int i = 0; i < 5; i++) {
//            shortenedList.add(networks.get(i));
//        }
//
//        for (Network network: shortenedList) {
//            System.out.println(network);
//        }
//
//
//        System.out.println(testClient.initiateNewTests(shortenedList));
//        System.out.println(testClient.getTestResult(6758L));

        List<Network> networks = new ArrayList<>();
        networks.add(Network.builder().mcc("231").mnc("01").portedFromMnc("06").build());

        testClient.initiateNewTests(networks);

    }

}
