package com.telq.sdk.model.tests.v3.lnt;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LntApiTestResultPage {

    private List<LntApiTestResultDto> content;
    private Integer page;
    private Integer size;
    private String order;
    private String error;
}
