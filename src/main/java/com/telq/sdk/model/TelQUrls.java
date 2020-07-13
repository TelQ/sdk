package com.telq.sdk.model;


import lombok.Getter;
import lombok.Setter;

/**
 * Collection of urls needed to contact our API services
 */
public class TelQUrls {

    @Getter
    private static String baseUrl = "https://api.telqtele.com/v2/client";

    @Getter
    private static String tokenUrl = baseUrl + "/token";

    @Getter
    private static String networksUrl = baseUrl + "/networks";

    @Getter
    private static String testsUrl = baseUrl + "/tests";

    @Getter
    private static String resultsUrl = baseUrl + "/results";

}
