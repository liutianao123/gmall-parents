package com.atguigu.gmall.to;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SearchResponseAttrVo implements Serializable{
    private Long productAttributeId;
    private List<String> value=new ArrayList<>();
    private String name;
}
