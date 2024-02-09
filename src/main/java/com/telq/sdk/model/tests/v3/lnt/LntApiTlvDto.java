package com.telq.sdk.model.tests.v3.lnt;

import com.telq.sdk.model.tests.v3.lnt.TlvDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LntApiTlvDto {

    private String tagHex;
    private String valueHex;

    public TlvDto toDto() {
        TlvDto tlv = new TlvDto();
        tlv.setTagHex(this.tagHex);
        tlv.setValueHex(this.valueHex);
        return tlv;
    }
}
