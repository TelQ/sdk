package com.telq.sdk.model.v3.lnt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LntApiSupplierDto {
    private Long supplierId;
    private String supplierName;
    private String routeType;
    private List<RouteAttribute> attributeList;
    private String comment;
    private String serviceType;
    private List<LntApiTlvDto> tlvs;
    private List<LntApiUdhDto> udhs;
    private Long smppSessionId;
    private Long userId;
}
