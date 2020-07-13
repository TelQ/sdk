package com.telq.sdk.model.network;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DestinationNetwork for an API v2 Manual Test.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DestinationNetwork {
    private String mcc;
    private String mnc;
    private String portedFromMnc;
}
