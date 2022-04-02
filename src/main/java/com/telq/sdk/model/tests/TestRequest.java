package com.telq.sdk.model.tests;

import com.telq.sdk.model.network.Network;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestRequest {
    List<Network> networks = new ArrayList<>(); // mandatory, can't be null
    @Builder.Default
    int maxCallBackRetries = 3; // optional, default 3
    String resultsCallbackUrl; // optional, default null
    String callBackToken; // optional, default null
    @Builder.Default
    int testTimeToLive = 3600; // optional, default 3600
    @Builder.Default
    TimeUnit timeUnit = TimeUnit.SECONDS; // optional, default TimeUnit.SECONDS
    @Builder.Default
    TestIdTextOptions testIdTextOptions = TestIdTextOptions.builder().build(); // optional
}
