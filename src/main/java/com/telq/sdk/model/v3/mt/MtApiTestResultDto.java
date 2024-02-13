package com.telq.sdk.model.v3.mt;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class MtApiTestResultDto {

    private Long id;

    private String testIdText;

    private String senderDelivered;
    private String textDelivered;

    private Instant testCreatedAt;

    private Instant smsReceivedAt;

    private Integer receiptDelay;

    private String receiptStatus;

    private MtApiDestinationNetworkDetailsDto destinationNetworkDetails;
    private MtApiSmscInfoDto smscInfo;

    private List<String> pdusDelivered;
}
