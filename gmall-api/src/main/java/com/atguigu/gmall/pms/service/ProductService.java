package com.atguigu.gmall.pms.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.vo.PmsProductParam;
import com.atguigu.gmall.pms.vo.PmsProductQueryParam;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品信息 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */

public interface ProductService extends IService<Product> {


    Map<String,Object> pageselect(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum);

    Product selectById(Long id);

    void Insert(PmsProductParam productParam);
    void updateByIds(List<Long> ids, Integer publishStatus);

    List<Product> selectByIds(List<Long> list);

    void ublishStatus(List<Long> ids, Integer publishStatus);
}
