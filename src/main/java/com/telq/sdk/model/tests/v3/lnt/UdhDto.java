package com.telq.sdk.model.tests.v3.lnt;

import com.telq.sdk.utils.Hex;
import lombok.*;



/**
 * Represents UDH parameter.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class UdhDto {
    public static final int MAX_UDH_LENGTH = 255;
    
    private Long id;
    private Byte tag;
    private byte[] value;

    /**
     * This allows to get HEX representation of 'tag' and also serialize it as additional field 'tagHex'.
     */
    @ToString.Include
    public String getTagHex() {
        if (tag == null) return null;
        return Hex.bytesToHex(new byte[]{tag});
    }

    /**
     * This allows to set 'tag' thru HEX representation.
     */
    public void setTagHex(String tagHex) {
        if (tagHex == null) return; // Despite of @NotNull constraint still can be null in templates.
        if (tagHex.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex UDH tag length must be even! Tag: " + tagHex);
        }
        byte[] tBytes = Hex.hexToBytes(tagHex);
        tag = tBytes[0];
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
            throw new IllegalArgumentException("Hex UDH value length must be even! Value: " + valueHex);
        }
        byte[] bytes = Hex.hexToBytes(valueHex);
        if (bytes.length > MAX_UDH_LENGTH) {
            throw new IllegalArgumentException(String.format(
                    "UDH value length must be less than %s. Current: %s", MAX_UDH_LENGTH, bytes.length
            ));
        }
        value = bytes;
    }
}
