package com.telq.sdk.model.v3.lnt;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LntApiTlvDto {
    private String tagHex;
    private String valueHex;
}
