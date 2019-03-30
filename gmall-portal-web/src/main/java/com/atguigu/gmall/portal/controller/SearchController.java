package com.atguigu.gmall.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.to.SearchParam;
import com.atguigu.gmall.to.SearchResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.atguigu.gmall.search.service.SearchService;

import java.io.IOException;

@CrossOrigin
@RestController
public class SearchController {
    @Reference
    private SearchService searchService;
    @GetMapping("/search")
    public SearchResponse search(SearchParam searchParam){
        SearchResponse search = null;
        try {
            search = searchService.getSearch(searchParam);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return search;
    }
}
