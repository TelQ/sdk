package com.telq.sdk.model.v3.mt;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MtApiSmscInfoDto {
    private String smscNumber;
    private String countryName;
    private String countryCode;
    private String mcc;
    private String mnc;
    private String providerName;
}
