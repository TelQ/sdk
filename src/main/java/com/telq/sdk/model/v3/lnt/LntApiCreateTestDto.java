package com.telq.sdk.model.v3.lnt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
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