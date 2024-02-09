package com.telq.sdk.model.tests.v3.lnt;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LntApiTestDto {

    private Long id;
    private String testIdText;
    private String phoneNumber;
    private LntApiDestinationNetworkDto destinationNetwork;
    private String errorMessage;
}

