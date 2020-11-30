package com.telq.sdk.model;

public class MockResponses {

    /**
     * This is used to mock the exact response from the networks endpoint. In case mapping goes wrong, a test will fail
     */

    //TOKEN
    public static final String token = "ZXlKMGVYQWlPaUtokenDSmhiR2NpT2lKdWIyNWxJbjtokenPakUyTURZM016RTJNelFzSW01aVppStokenek1UWTVOQ3dpWlhod0lqb3hOakEyT0RFNE1ETTBMQ0prWVhSaElqb2lNVFZsTVRCatokenQTNMVGt6TTJRdE5ESmpPV1JsTtokenYVdWdWRDSjku";
    public static final String getTokenValid = "{\"ttl\":86400,\"value\":\"" + token + "\"}";
    public static final String getTokenInvalid = "{\"name\":\"Bad Request\",\"message\":\"Bad credentials exception\",\"code\":0,\"status\":400}";

    //GET_NETWORKS
    public static final String getNetworksResponseDetailedValid =
            "[{\"mcc\":\"289\",\"countryName\":\"Abkhazia\",\"mnc\":\"88\",\"providerName\":\"A-Mobile\",\"portedFromMnc\":\"99\",\"portedFromProviderName\":null},{\"mcc\":\"289\",\"countryName\":\"Abkhazia\",\"mnc\":\"67\",\"providerName\":\"Aquafon\",\"portedFromMnc\":null,\"portedFromProviderName\":\"PortedProviderName\"},{\"mcc\":\"412\",\"countryName\":\"Afghanistan\",\"mnc\":\"01\",\"providerName\":\"AWCC\",\"portedFromMnc\":null,\"portedFromProviderName\":null}]";
    public static final String getNetworksResponseAuthInvalid = "{\"name\":\"Internal Server Error\",\"message\":\"An internal server error occurred.\",\"code\":0,\"status\":500}";

    //REQUEST_TESTS
    public static final String requestTestId = "1";
    public static final String requestTestMcc = "208";
    public static final String requestTestMnc = "10";
    public static final String requestTestPortedFromMnc = "20";
    public static final String requestTestPhoneNumber = "33651111111";
    public static final String testIdText = "someCoolTestIdText";
    public static final String requestNewTestValid = "[{\"id\":" + requestTestId + ",\"testIdText\":\"" + testIdText + "\",\"phoneNumber\":\"" + requestTestPhoneNumber + "\",\"errorMessage\":null,\"destinationNetwork\":{\"mcc\":\"" + requestTestMcc + "\",\"mnc\":\""+ requestTestMnc + "\",\"portedFromMnc\":\"" + requestTestPortedFromMnc + "\"}}]";
    public static final String requestNewTestAuthInvalid = "{\"name\":\"Internal Server Error\",\"message\":\"An internal server error occurred.\",\"code\":0,\"status\":500}";
    public static final String requestNewTestWrongParamsInvalid = "[{\"id\":" + requestTestId + ",\"testIdText\":\"" + testIdText + "\",\"phoneNumber\":null,\"errorMessage\":\"NETWORK_OFFLINE\",\"destinationNetwork\":{\"mcc\":\"" + requestTestMcc + "\",\"mnc\":\"99\",\"portedFromMnc\":\"" + requestTestPortedFromMnc + "\"}}]";
    public static final String requestNewTestMissingParamsInvalid = "{\"timestamp\":\"2020-11-30T11:25:37.375+0000\",\"status\":400,\"error\":\"Bad Request\",\"errors\":[{\"codes\":[\"NotNull.testRequest.destinationNetworks[0].mnc\",\"NotNull.testRequest.destinationNetworks.mnc\",\"NotNull.destinationNetworks[0].mnc\",\"NotNull.destinationNetworks.mnc\",\"NotNull.mnc\",\"NotNull.java.lang.String\",\"NotNull\"],\"arguments\":[{\"codes\":[\"testRequest.destinationNetworks[0].mnc\",\"destinationNetworks[0].mnc\"],\"arguments\":null,\"defaultMessage\":\"destinationNetworks[0].mnc\",\"code\":\"destinationNetworks[0].mnc\"}],\"defaultMessage\":\"must not be null\",\"objectName\":\"testRequest\",\"field\":\"destinationNetworks[0].mnc\",\"rejectedValue\":null,\"bindingFailure\":false,\"code\":\"NotNull\"},{\"codes\":[\"NotEmpty.testRequest.destinationNetworks[0].mnc\",\"NotEmpty.testRequest.destinationNetworks.mnc\",\"NotEmpty.destinationNetworks[0].mnc\",\"NotEmpty.destinationNetworks.mnc\",\"NotEmpty.mnc\",\"NotEmpty.java.lang.String\",\"NotEmpty\"],\"arguments\":[{\"codes\":[\"testRequest.destinationNetworks[0].mnc\",\"destinationNetworks[0].mnc\"],\"arguments\":null,\"defaultMessage\":\"destinationNetworks[0].mnc\",\"code\":\"destinationNetworks[0].mnc\"}],\"defaultMessage\":\"must not be empty\",\"objectName\":\"testRequest\",\"field\":\"destinationNetworks[0].mnc\",\"rejectedValue\":null,\"bindingFailure\":false,\"code\":\"NotEmpty\"}],\"message\":\"Validation failed for object='testRequest'. Error count: 2\",\"path\":\"/api/v2/tests\"}";

    //REQUEST_RESULT
    public static final String requestResultId = "1";
    public static final String requestResultMcc = "208";
    public static final String requestResultMnc = "10";
    public static final String requestResultPortedFromMnc = "20";
    public static final String requestResultPhoneNumber = "33651111111";
    public static final String requestResultCountryName = "Belgium";
    public static final String requestResultProviderName = "Mobistar";
    public static final String requestResultValid = "{\"id\":" + requestResultId + ",\"testIdText\":\"" + testIdText + "\",\"senderDelivered\":null,\"textDelivered\":null,\"testCreatedAt\":\"2020-11-30T11:26:43Z\",\"smsReceivedAt\":null,\"receiptDelay\":null,\"testStatus\":\"WAIT\",\"destinationNetworkDetails\":{\"mcc\":\"" + requestResultMcc + "\",\"mnc\":\"" + requestResultMnc + "\",\"portedFromMnc\":\"" + requestResultPortedFromMnc + "\",\"countryName\":\"" + requestResultCountryName + "\",\"providerName\":\"" + requestResultProviderName + "\",\"portedFromProviderName\": " + requestResultProviderName + "},\"smscInfo\":null,\"pdusDelivered\":[]}";
    public static final String requestResultDetailedValid = "{\"id\":" + requestTestId + ",\"testIdText\":\"" + testIdText + "\",\"senderDelivered\":\"8001\",\"textDelivered\":\"" + testIdText + " is your code\",\"testCreatedAt\":\"2020-11-30T11:53:31Z\",\"smsReceivedAt\":\"2020-11-30T11:54:09Z\",\"receiptDelay\":38,\"testStatus\":\"POSITIVE\",\"destinationNetworkDetails\":{\"mcc\":\"" + requestResultMcc + "\",\"mnc\":\"" + requestResultMnc + "\",\"portedFromMnc\":\"" + requestResultPortedFromMnc + "\",\"countryName\":\"" + requestResultCountryName + "\",\"providerName\":\"" + requestResultProviderName + "\",\"portedFromProviderName\": " + requestResultProviderName + "},\"smscInfo\":{\"smscNumber\":\"32495002530\",\"countryName\":\"Belgium\",\"countryCode\":\"BE\",\"mcc\":\"206\",\"mnc\":\"10\",\"providerName\":\"Mobistar\"},\"pdusDelivered\":[\"07912394052035F0040481081000000211032145704017593BF9EE565A9F792B283D07E5DF753968FC269701\"]}";
    public static final String requestResultInvalidIdInvalid = "{\"timestamp\":\"2020-11-30T11:28:31.570+0000\",\"status\":404,\"error\":\"Not Found\",\"message\":\"Id Not Found\",\"path\":\"/api/v2/results/9999\"}";

}
