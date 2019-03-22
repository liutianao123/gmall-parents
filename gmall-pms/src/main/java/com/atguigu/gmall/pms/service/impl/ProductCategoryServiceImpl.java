package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.ProductCategory;
import com.atguigu.gmall.pms.mapper.ProductCategoryMapper;
import com.atguigu.gmall.pms.service.ProductCategoryService;
import com.atguigu.gmall.pms.vo.ProductCategoryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 产品分类 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    @Override
    public List<ProductCategoryVo> select() {
        QueryWrapper<ProductCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", 0);
        queryWrapper.orderByAsc("id");
        List<ProductCategory> productCategories = baseMapper.selectList(queryWrapper);
        List<ProductCategoryVo> list = new ArrayList<>();
        for (int i = 0; i < productCategories.size(); i++) {
            ProductCategory productCategory = productCategories.get(i);
            ProductCategoryVo productCategoryVo = new ProductCategoryVo();
            BeanUtils.copyProperties(productCategory, productCategoryVo);
            list.add(productCategoryVo);
        }
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("level", 1);
        queryWrapper.orderByAsc("parent_id");
        List<ProductCategory> productCategories1 = baseMapper.selectList(queryWrapper);
        int k = 0;
        for (int i = 0; i < productCategories1.size(); i++) {

            ProductCategory productCategory = productCategories1.get(i);
            ProductCategoryVo productCategoryVo = new ProductCategoryVo();
            BeanUtils.copyProperties(productCategory, productCategoryVo);
            Long id = list.get(k).getId();
            if (id == productCategory.getParentId()) {
                list.get(k).getList().add(productCategoryVo);
            } else {
                k++;
            }
        }
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("level", 2);
        queryWrapper.orderByAsc("parent_id");
        List<ProductCategory> productCategories2 = baseMapper.selectList(queryWrapper);
        k = 0;
        for (int i = 0; i < productCategories2.size(); i++) {
            ProductCategory productCategory = productCategories2.get(i);
            ProductCategoryVo productCategoryVo = new ProductCategoryVo();
            BeanUtils.copyProperties(productCategory, productCategoryVo);
            List<ProductCategoryVo> list2 = list.get(i).getList();
            if (list2.get(k).getId() == productCategoryVo.getParentId()) {
                list2.get(k).getList().add(productCategoryVo);
            } else {
                k++;
            }
        }
        return list;
    }
}
