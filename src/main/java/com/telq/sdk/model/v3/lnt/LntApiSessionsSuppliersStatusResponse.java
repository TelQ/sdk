package com.telq.sdk.model.v3.lnt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LntApiSessionsSuppliersStatusResponse {
    private Long smppSessionId;
    private Long supplierId;
    private String supplierName;
    private String routeType;
    private Boolean online;
    private List<String> attributeList;
}
