package com.telq.sdk.model.tests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestIdTextOptions {
    TestIdTextType testIdTextType;
    TestIdTextCase testIdTextCase;
    Integer testIdTextLength;
}
