package com.telq.sdk.model.tests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the details for the Network of the phone number.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SmscInfo {
    private String smscNumber;
    private String countryName;
    private String countryCode;
    private String mcc;
    private String mnc;
    private String providerName;
}