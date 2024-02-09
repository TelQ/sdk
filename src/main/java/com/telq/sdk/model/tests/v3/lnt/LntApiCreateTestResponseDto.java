package com.telq.sdk.model.tests.v3.lnt;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LntApiCreateTestResponseDto {

    private List<LntApiTestDto> tests;
    private String error;
}
