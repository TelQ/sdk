package com.telq.sdk.model.tests.v3.lnt;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LntApiDestinationDto {

    private String mcc;
    private String mnc;
    private String portedFromMnc;
    private String countryName;
    private String providerName;
    private String portedFromProviderName;
    private String phoneNumber;
    private Boolean manualNumber;
}
