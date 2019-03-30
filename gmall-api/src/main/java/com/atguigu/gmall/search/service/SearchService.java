package com.atguigu.gmall.search.service;

import com.atguigu.gmall.to.EsProduct;
import com.atguigu.gmall.to.SearchParam;
import com.atguigu.gmall.to.SearchResponse;

import java.io.IOException;
import java.util.List;

public interface SearchService {



    boolean saveProductInfoToEs(EsProduct esProduct);

    boolean reomve(List<Long> ids);

    SearchResponse getSearch(SearchParam searchParam) throws IOException;
}
