package com.atguigu.eduservice.entity.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OneSubjectVo {
    private String id;
    private String title;
    private List<TwoSubjectVo> children = new ArrayList<>();
}
