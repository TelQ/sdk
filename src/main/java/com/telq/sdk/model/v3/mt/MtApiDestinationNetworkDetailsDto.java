package com.telq.sdk.model.v3.mt;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MtApiDestinationNetworkDetailsDto {
    private String mcc;
    private String mnc;
    private String portedFromMnc;
    private String countryName;
    private String providerName;
    private String portedFromProviderName;
    private String phoneNumber;
}
