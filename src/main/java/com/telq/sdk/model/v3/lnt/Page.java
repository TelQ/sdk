package com.telq.sdk.model.v3.lnt;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Page<T> {
    private List<T> content;
    private Integer page;
    private Integer size;
    private String order;
    private String error;
}
