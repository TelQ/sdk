package com.telq.sdk.model.tests;

import com.telq.sdk.model.network.DestinationNetwork;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestTestDto {

    private List<DestinationNetwork> destinationNetworks;

    private int maxCallbackRetries;

    private String resultsCallbackUrl;

    private int testTimeToLiveInSeconds;

}
