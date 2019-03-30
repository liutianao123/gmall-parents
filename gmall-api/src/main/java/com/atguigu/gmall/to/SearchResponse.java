package com.atguigu.gmall.to;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SearchResponse implements Serializable{
    private List<SearchResponseAttrVo> attrs=new ArrayList<>();//所有商品的筛选属性
    private List<EsProduct> products=new ArrayList<>();//检索出的商品信息
    private SearchResponseAttrVo brid;//所有品牌
    private SearchResponseAttrVo catelog;
    private Long total;//总记录数
    private Long pageSize;//每页显示的内容
    private Long pageNum;//当前页码
}
