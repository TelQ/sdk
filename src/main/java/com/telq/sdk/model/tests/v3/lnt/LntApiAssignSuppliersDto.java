package com.telq.sdk.model.tests.v3.lnt;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@NoArgsConstructor
public class LntApiAssignSuppliersDto {

    private Set<Long> supplierIds;
    private Long sessionId;
}

