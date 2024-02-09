package com.telq.sdk.model.tests.v3.lnt;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class LntApiSessionsSearchDto {

    private Set<Long> sessionIds;
    private String host;
    private Set<String> logins;
    private List<Long> userIds;
    private Set<Long> supplierIds;
    private Set<String> supplierNames;
    private String routeType;
    private String serviceType;
}
