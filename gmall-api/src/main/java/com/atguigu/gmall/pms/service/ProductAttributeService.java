package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品属性参数表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductAttributeService extends IService<ProductAttribute> {

    Map<String,Object> select(Long cid,Integer type, Integer pageNum, Integer pageSize);

    boolean add(ProductAttribute productAttribute);

    boolean updateByid(Long id, ProductAttribute productAttribute);

    ProductAttribute selectbyid(Long id);

    boolean removeByIdls(List<Long> ids);

    List<ProductAttribute> selectByPCId(Long productCategoryId);
}
