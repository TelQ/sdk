package com.telq.sdk.model.tests.v3.lnt;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class LntApiTestResultDto {

    private Long id;
    private String testIdText;
    private String senderSent;
    private String senderDelivered;
    private String textSent;
    private String textDelivered;
    private Instant testCreatedAt;
    private Long smppSessionId;

    private LntApiSupplierDto supplier;

    private Instant smsReceivedAt;
    private String receiptStatus;
    private String dlrStatus;
    private Integer receiptDelay;
    private Integer dlrDelay;
    private Long scheduledTaskId;

    private LntApiDestinationDto destinationNetworkDetails;

    private LntApiSmscInfoDto smscInfo;

    private Boolean retry;
    private List<String> pdusDelivered;
    private LntApiUserDto user;
}
