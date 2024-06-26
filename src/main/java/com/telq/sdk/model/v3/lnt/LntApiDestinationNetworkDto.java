package com.telq.sdk.model.v3.lnt;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LntApiDestinationNetworkDto {

    private String mcc;
    private String mnc;
    private String portedFromMnc;
}

