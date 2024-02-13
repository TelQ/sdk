package com.telq.sdk.model.v3.lnt;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LntApiSessionDto {

    private Long smppSessionId;
    private String hostIp;
    private Integer hostPort;
    private String systemId;
    private String systemType;
    private Integer throughput;
    private Byte destinationTon;
    private Byte destinationNpi;
    private Boolean enabled;
    private Long userId;
    private Boolean online;
    private String lastError;
    private Integer windowSize;
    private Boolean useSSL;
    private Long windowWaitTimeout;
    private Integer supplierCount;
}
