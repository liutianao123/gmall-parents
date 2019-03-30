package com.atguigu.gmall.to;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class EsProductAttributeValue implements Serializable{
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long productAttributeId;
    //属性值
    private List<String> value=new ArrayList<>();//3G
    //属性参数：0->规格；1->参数
    private Integer type;//规格，销售属性；参数，筛选参数

    //属性名称
    private String name;//网络制式
}
