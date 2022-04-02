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
    private List<Network> networks = new ArrayList<>(); // mandatory, can't be null
    @Builder.Default
    private int maxCallBackRetries = 3; // optional, default 3
    private String resultsCallbackUrl; // optional, default null
    private String callBackToken; // optional, default null
    @Builder.Default
    private int testTimeToLive = 3600; // optional, default 3600
    @Builder.Default
    private TimeUnit timeUnit = TimeUnit.SECONDS; // optional, default TimeUnit.SECONDS
    @Builder.Default
    private TestIdTextOptions testIdTextOptions = TestIdTextOptions.builder().build(); // optional
}
