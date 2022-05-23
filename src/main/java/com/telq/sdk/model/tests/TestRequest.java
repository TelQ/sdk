package com.telq.sdk.model.tests;

import com.telq.sdk.TelQTestClient;
import com.telq.sdk.model.network.Network;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
@Builder
public class TestRequest {
    private List<Network> networks;
    @Builder.Default private int maxCallbackRetries = TelQTestClient.DEFAULT_MAX_CALLBACK_RETRIES;
    @Builder.Default private String callbackUrl = TelQTestClient.DEFAULT_CALLBACK_URL;
    @Builder.Default private String callbackToken = TelQTestClient.DEFAULT_CALLBACK_TOKEN;
    @Builder.Default private int testTimeToLive = TelQTestClient.DEFAULT_TTL;
    @Builder.Default private TimeUnit timeUnit = TelQTestClient.DEFAULT_TIME_UNIT;
    @Builder.Default private TestIdTextOptions testIdTextOptions = TelQTestClient.DEFAULT_TEST_ID_TEXT_OPTIONS;
}