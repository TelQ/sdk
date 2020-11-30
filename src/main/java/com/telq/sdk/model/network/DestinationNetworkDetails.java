package com.telq.sdk.model.network;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DestinationNetworkDetails for an API v2 Manual Test.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DestinationNetworkDetails {
    private String mcc;
    private String mnc;
    private String portedFromMnc;
    private String countryName;
    private String providerName;
    private String portedFromProviderName;
}
