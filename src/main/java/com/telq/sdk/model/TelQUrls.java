package com.telq.sdk.model;


import lombok.Getter;
import lombok.Setter;

/**
 * Collection of urls needed to contact our API services
 */
public class TelQUrls {

    @Getter
    private static String baseUrl = "https://api.dev.telqtele.com/v2.1/client";

    @Getter
    private static String tokenUrl = baseUrl + "/token";

    @Getter
    private static String networksUrl = baseUrl + "/networks";

    @Getter
    private static String testsUrl = baseUrl + "/tests";

    @Getter
    private static String resultsUrl = baseUrl + "/results";

    public static final String baseUrlV3 = "https://api.dev.telqtele.com/v3/client";
    public static final String baseUrlLnt = baseUrlV3 + "/lnt";
    public static final String lntTestsUrl = baseUrlLnt + "/tests";
    public static final String mtTestsUrl = baseUrlV3 + "/tests";
}
