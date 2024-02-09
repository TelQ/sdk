package com.telq.sdk.model.tests.v3.lnt;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LntApiSmscInfoDto {
    private String smscNumber;
    private String countryName;
    private String countryCode;
    private String mcc;
    private String mnc;
    private String providerName;
}

