package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.ProductCategory;
import com.atguigu.gmall.pms.vo.ProductCategoryVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 产品分类 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    List<ProductCategoryVo> select();

    Map<String,Object> selects(Integer pageNum, Integer pageSize, Long parentId);

    boolean add(ProductCategory  productCategoryParam);

    List<ProductCategoryVo> selectById(Long id);
}
