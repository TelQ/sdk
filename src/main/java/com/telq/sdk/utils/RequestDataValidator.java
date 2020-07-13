package com.telq.sdk.utils;

import com.telq.sdk.model.network.DestinationNetwork;

import java.util.List;

public class RequestDataValidator {

    public static boolean validateNetworks(List<DestinationNetwork> networks) {

        for(DestinationNetwork network: networks) {
            if (isNetworkPropertyInvalid(network.getMcc()) || isNetworkPropertyInvalid(network.getMnc())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNetworkPropertyInvalid(String prop) {
        return prop == null || prop.isEmpty() || prop.length() > 3;
    }

}
