package com.telq.sdk.model.v3.lnt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LntApiCreateOrUpdateSessionDto {
    private Long smppSessionId;
    private String hostIp;
    private Integer hostPort;
    private String systemId;
    private String password;
    private String systemType;
    private Integer throughput;
    private Byte destinationTon;
    private Byte destinationNpi;
    private Boolean enabled;
    private Integer windowSize;
    private Boolean useSSL;
    private Long windowWaitTimeout;
}
