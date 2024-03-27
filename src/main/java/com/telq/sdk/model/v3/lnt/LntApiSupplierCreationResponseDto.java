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
public class LntApiSupplierCreationResponseDto {
    private Long supplierId;
    private Long smppSessionId;
    private String supplierName;
    private String routeType;
    private List<RouteAttribute> attributeList;
    private String comment;
    private String serviceType;
}