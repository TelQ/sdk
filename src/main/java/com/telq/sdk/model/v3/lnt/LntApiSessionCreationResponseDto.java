package com.telq.sdk.model.v3.lnt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LntApiSessionCreationResponseDto {
    private Long smppSessionId;
    private String hostIp;
    private Integer hostPort;
    private String systemId;
    private String systemType;
    private Boolean enabled;
}