package com.atguigu.gmall.pms;

import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.service.ProductService;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class GmallPmsApplicationTests {
    @Autowired
    private ProductService productService;

    @Test
    public void contextLoads() {
        List<Long> list=new ArrayList();
        list.add(1l);
        list.add(2l);
        list.add(3l);
        List<Product> product=productService.selectByIds(list);
        System.out.println(product);
    }

}
