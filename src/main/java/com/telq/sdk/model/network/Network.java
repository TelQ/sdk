package com.telq.sdk.model.network;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@link Network} will usually be an item of a list received from the API endpoint.
 * It represents a single network that is online at request time.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Network {

    private String country;

    private String provider;

    private String mcc;

    private String mnc;

    private String portedFromProvider;

    private String portedFromMnc;


}
