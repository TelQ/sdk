package com.telq.sdk.model.v3.lnt;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LntApiCreateSessionDto {
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
