package com.telq.sdk.model.v3.lnt;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LntApiTestRequestDto {

    private List<LntApiCreateTestDto> tests;
    private List<LntApiTlvDto> tlv;
    private List<LntApiUdhDto> udh;
    private String dataCoding;
    private String sourceTon;
    private String sourceNpi;
    private Byte priorityFlag;
    private Integer smppValidityPeriod;
    private Integer testTimeToLiveInSeconds;
    private String commentText;
    private String resultsCallbackUrl;
    private Integer maxCallbackRetries;
    private Boolean replaceIfPresentFlag;
    private Boolean sendTextAsMessagePayloadTlv;
    private String scheduledDeliveryTime;
}
