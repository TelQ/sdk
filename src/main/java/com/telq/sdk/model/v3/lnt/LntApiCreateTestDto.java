package com.telq.sdk.model.v3.lnt;

import com.telq.sdk.model.tests.TestIdTextCase;
import com.telq.sdk.model.tests.TestIdTextType;
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
    private TestIdTextType testIdTextType = TestIdTextType.NUMERIC;
    private TestIdTextCase testIdTextCase = TestIdTextCase.MIXED;
    private int testIdTextLength = 6;
}
