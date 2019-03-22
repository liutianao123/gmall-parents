package com.atguigu.gmall.config;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.metadata.IPage;

public class PageBaseConfig {
    public static Map<String,Object> pageBase(IPage page){
        Map<String,Object> map=new HashMap<>();
        map.put("totalPage",page.getPages());
        map.put("total",page.getTotal());
        map.put("PageNum",page.getCurrent());
        map.put("list",page.getRecords());
        map.put("pageSize",page.getSize());

        return map;
    }
}
