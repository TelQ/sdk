package com.telq.sdk.model;

public class MockResponses {

    /**
     * This is used to mock the exact response from the networks endpoint. In case mapping goes wrong, a test will fail
     */
    public static final String getNetworksResponse =
            "[{\"mcc\":\"289\",\"countryName\":\"Abkhazia\",\"mnc\":\"88\",\"providerName\":\"A-Mobile\",\"portedFromMnc\":\"99\",\"portedFromProviderName\":null},{\"mcc\":\"289\",\"countryName\":\"Abkhazia\",\"mnc\":\"67\",\"providerName\":\"Aquafon\",\"portedFromMnc\":null,\"portedFromProviderName\":\"PortedProviderName\"},{\"mcc\":\"412\",\"countryName\":\"Afghanistan\",\"mnc\":\"01\",\"providerName\":\"AWCC\",\"portedFromMnc\":null,\"portedFromProviderName\":null}]";

}
