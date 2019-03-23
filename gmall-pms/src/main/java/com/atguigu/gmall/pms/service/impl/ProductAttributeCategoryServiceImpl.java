package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.config.PageBaseConfig;
import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.atguigu.gmall.pms.entity.ProductAttributeCategory;
import com.atguigu.gmall.pms.mapper.ProductAttributeCategoryMapper;
import com.atguigu.gmall.pms.service.ProductAttributeCategoryService;
import com.atguigu.gmall.pms.vo.PmsProductAttributeCategoryItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 产品属性分类表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class ProductAttributeCategoryServiceImpl extends ServiceImpl<ProductAttributeCategoryMapper, ProductAttributeCategory> implements ProductAttributeCategoryService {

    @Override
    public Map<String, Object> select(Integer pageSize, Integer pageNum) {
        Page<ProductAttributeCategory> pages=new Page<>(pageNum,pageSize);
        IPage<ProductAttributeCategory> page = baseMapper.selectPage(pages, null);
        Map<String, Object> map = PageBaseConfig.pageBase(page);
        return map;
    }

    @Override
    public List<PmsProductAttributeCategoryItem> selectAll() {
        List<PmsProductAttributeCategoryItem> list = baseMapper.selectAll();
        System.out.println(list);
        return list;
    }
}
