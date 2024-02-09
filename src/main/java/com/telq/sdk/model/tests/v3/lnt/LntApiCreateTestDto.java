package com.telq.sdk.model.tests.v3.lnt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LntApiCreateTestDto {

    private String sender;
    private String text;
    private Long supplierId;
    private String mnc;
    private String mcc;
    private String portedFromMnc;
    private String testIdTextType = "ALPHA_NUMERIC";
    private String testIdTextCase = "MIXED";
    private int testIdTextLength = 6;
}
