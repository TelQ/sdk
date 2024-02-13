package com.telq.sdk;

import com.telq.sdk.model.v3.lnt.*;
import com.telq.sdk.model.v3.mt.MtApiTestResultDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collections;

@Disabled
class TelqApiTest {
    String testApiKey = "your-api-key-here";
    String appId = "your-app-id-here";

    //    for local testing
    @Test
    void liveTestingApiCalls_allSucceed() {
        TelqApi telqApi = new TelqApi(testApiKey, appId);

        Page<LntApiTestResultDto> tests = telqApi.lnt.getTestResults(null, null, null);
        System.out.println(tests);

        LntApiTestResultDto test = telqApi.lnt.getTestResultById("1155470");
        System.out.println(test);

        LntApiTestRequestDto testRequestDto = LntApiTestRequestDto.builder()
                .tests(Collections.singletonList(
                        LntApiCreateTestDto.builder()
                                .sender("123456789")
                                .text("Hello, World!")
                                .supplierId(6L)
                                .mcc("412")
                                .mnc("88")
                                .build()
                )).build();

        System.out.println(testRequestDto);

        Page<MtApiTestResultDto> mtTests = telqApi.mt.getTestResults(null, null, null);
        System.out.println(mtTests);
    }
}