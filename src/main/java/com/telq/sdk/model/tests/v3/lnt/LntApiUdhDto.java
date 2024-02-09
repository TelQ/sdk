package com.telq.sdk.model.tests.v3.lnt;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LntApiUdhDto {

    private String tagHex;
    private String valueHex;

    public UdhDto toDto() {
        UdhDto udh = new UdhDto();
        udh.setTagHex(this.tagHex);
        udh.setValueHex(this.valueHex);
        return udh;
    }
}
