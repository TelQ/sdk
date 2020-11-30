package com.telq.sdk.model.tests;

import com.telq.sdk.model.network.DestinationNetwork;
import com.telq.sdk.model.network.DestinationNetworkDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * {@link Result} represents a single test result received from the result API.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Result {

    private Long id;
    private Instant testCreatedAt;
    private Instant smsReceivedAt;
    private String testStatus;
    private Integer receiptDelay;

    private DestinationNetworkDetails destinationNetworkDetails;

    private String senderDelivered;
    private String textDelivered;
    private String testIdText;

    private SmscInfo smscInfo;

    private List<String> pdusDelivered;

}
