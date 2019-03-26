package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.config.PageBaseConfig;
import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.atguigu.gmall.pms.mapper.ProductAttributeMapper;
import com.atguigu.gmall.pms.service.ProductAttributeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品属性参数表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class ProductAttributeServiceImpl extends ServiceImpl<ProductAttributeMapper, ProductAttribute> implements ProductAttributeService {

    @Override
    public Map<String, Object> select(Long cid,Integer type, Integer pageNum, Integer pageSize) {
        Page<ProductAttribute> productAttributePage=new Page<>(pageNum,pageSize);
        QueryWrapper<ProductAttribute> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("type",type);
        queryWrapper.eq("product_attribute_category_id",cid);
        IPage<ProductAttribute> productAttributeIPage = baseMapper.selectPage(productAttributePage, queryWrapper);
        Map<String, Object> map = PageBaseConfig.pageBase(productAttributeIPage);
        return map;
    }

    @Override
    public boolean add(ProductAttribute productAttribute) {
        int insert = baseMapper.insert(productAttribute);
        return insert>0;
    }

    @Override
    public boolean updateByid(Long id, ProductAttribute productAttribute) {
        productAttribute.setId(id);
        int i = baseMapper.updateById(productAttribute);
        return i>0;
    }

    @Override
    public ProductAttribute selectbyid(Long id) {
        ProductAttribute productAttribute = baseMapper.selectById(id);
        return productAttribute;
    }

    @Override
    public boolean removeByIdls(List<Long> ids) {
        QueryWrapper<ProductAttribute> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",ids);
       Integer i= baseMapper.delete(queryWrapper);
        return i>0;
    }

    @Override
    public List<ProductAttribute> selectByPCId(Long productCategoryId) {

        return null;
    }
}
