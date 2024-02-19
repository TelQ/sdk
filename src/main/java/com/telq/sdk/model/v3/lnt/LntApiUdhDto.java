package com.telq.sdk.model.v3.lnt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class LntApiUdhDto {
    private String tagHex;
    private String valueHex;
}
