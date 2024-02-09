package com.telq.sdk.model.tests.v3.lnt;

import com.telq.sdk.utils.Hex;
import lombok.*;

import java.nio.ByteBuffer;

/**
 * Optional TLV parameter.
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
@Builder
public class TlvDto {
    public static final int MAX_TLV_LENGTH = 2550;
    
    private Long id;
    private Short tag;
    private byte[] value;

    /**
     * This allows to get HEX representation of 'tag' and also serialize it as additional field 'tagHex'.
     */
    @ToString.Include
    public String getTagHex() {
        if (tag == null) return null;
        return Hex.bytesToHex(ByteBuffer.allocate(2).putShort(tag).array());
    }

    /**
     * This allows to set 'tag' thru HEX representation.
     */
    public void setTagHex(String tagHex) {
        if (tagHex == null) return; // Despite of @NotNull constraint still can be null in templates.
        if (tagHex.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex TLV tag length must be even! Tag: " + tagHex);
        }
        byte[] tBytes = Hex.hexToBytes(tagHex);
        tag = ByteBuffer.wrap(tBytes).getShort();
    }

    /**
     * This allows to get HEX representation of 'value' and also serialize it as additional field 'valueHex'.
     */
    @ToString.Include
    public String getValueHex() {
        if (value == null) return null;
        return Hex.bytesToHex(value);
    }

    /**
     * This allows to set 'value' thru HEX representation.
     */
    public void setValueHex(String valueHex) {
        if (valueHex == null) return;
        if (valueHex.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex TLV value length must be even! Value: " + valueHex);
        }
        byte[] bytes = Hex.hexToBytes(valueHex);
        if (bytes.length > MAX_TLV_LENGTH) {
            throw new IllegalArgumentException(String.format(
                    "TLV value length must be less than %s. Current: %s", MAX_TLV_LENGTH, bytes.length
            ));
        }
        value = bytes;
    }
}
