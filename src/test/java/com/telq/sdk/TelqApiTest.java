package com.telq.sdk;

import com.telq.sdk.model.tests.TestIdTextCase;
import com.telq.sdk.model.tests.TestIdTextType;
import com.telq.sdk.model.v3.lnt.*;
import com.telq.sdk.model.v3.mt.MtApiTestResultDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;

@Disabled
class TelqApiTest {
    String testApiKey = "your-api-key-here";
    String appId = "your-app-id-here";

    @Test
    void liveTestingApiCalls_allSucceed() {
        TelqApi telqApi = new TelqApi(testApiKey, appId);

        Page<LntApiTestResultDto> tests = telqApi.lnt.getTestPage(null, null, null);
        System.out.println(tests);

        LntApiTestResultDto test = telqApi.lnt.getTestById(1155470L);
        System.out.println(test);

        LntApiTestRequestDto testRequestDto = LntApiTestRequestDto.builder()
                .maxCallbackRetries(3)
                .dataCoding("01")
                .commentText("my comment")
                .priorityFlag(Byte.valueOf("1"))
                .testTimeToLiveInSeconds(360)
                .sourceNpi("02")
                .sourceTon("01")
                .smppValidityPeriod(360)
                .resultsCallbackUrl("https://example.com/callback")
                .sendTextAsMessagePayloadTlv(false)
                .scheduledDeliveryTime("2209131247000000")
                .replaceIfPresentFlag(false)
                .udh(Collections.singletonList(LntApiUdhDto.builder()
                        .tagHex("1F")
                        .valueHex("11BB")
                        .build()
                ))
                .tlv(Collections.singletonList(LntApiTlvDto.builder()
                        .tagHex("1B1A")
                        .valueHex("1AAF")
                        .build()
                ))
                .tests(Collections.singletonList(
                        LntApiCreateTestDto.builder()
                                .sender("123456789")
                                .text("Hello, World!")
                                .testIdTextLength(6)
                                .testIdTextCase(TestIdTextCase.MIXED)
                                .testIdTextType(TestIdTextType.NUMERIC)
                                .portedFromMnc(null)
                                .supplierId(6L)
                                .mcc("412")
                                .mnc("88")
                                .build()
                )).build();

        System.out.println(testRequestDto);

        LntApiCreateTestResponseDto testsCreated = telqApi.lnt.createTests(testRequestDto);
        System.out.println(testsCreated);

        Page<MtApiTestResultDto> mtTests = telqApi.mt.getTestPage(null, null, null);
        System.out.println(mtTests);

        System.out.println(telqApi.mt.getNetworks("631",null));
        System.out.println(telqApi.mt.getNetworks(null,"02"));

        PageConf pageConf = PageConf.builder()
                .page(1)
                .size(10)
                .build();
        Instant from = Instant.parse("2023-09-13T12:47:00Z");
        Instant to = Instant.parse("2023-10-13T12:47:00Z");
        Page<MtApiTestResultDto> page = telqApi.mt.getTestPage(pageConf, from, to);
    }
}