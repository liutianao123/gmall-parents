package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.config.PageBaseConfig;
import com.atguigu.gmall.pms.entity.Brand;
import com.atguigu.gmall.pms.mapper.BrandMapper;
import com.atguigu.gmall.pms.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Lang;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 品牌表 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    @Override
    public Map<String, Object> select(Integer pageNum, Integer pageSize) {
        Page<Brand> page=new Page<>(pageNum,pageSize);
        IPage<Brand> page1 = baseMapper.selectPage(page, null);
        Map<String, Object> map = PageBaseConfig.pageBase(page1);
        return map;
    }

    @Override
    public List<Brand> listAll() {
        List<Brand> brands = baseMapper.selectList(null);
        return brands;
    }

    @Override
    public boolean addNew(Brand brand) {
        int insert = baseMapper.insert(brand);
        return insert>0;
    }

    @Override
    public boolean updateByIdd(Long id, Brand brand) {
        int id1 = baseMapper.update(brand, new QueryWrapper<Brand>().eq("id", id));
        return id1>0;
    }

    @Override
    public boolean updateandRemove(List<Long> ids, Integer showStatus) {
        Brand brand=new Brand();
        brand.setShowStatus(showStatus);
        int id1 = baseMapper.update(brand, new QueryWrapper<Brand>().in("id", ids));
        return id1>0;
    }

    @Override
    public boolean updatefactoryStatus(List<Long> ids, Integer factoryStatus) {
        Brand brand=new Brand();
        brand.setFactoryStatus(factoryStatus);
        int id1 = baseMapper.update(brand, new QueryWrapper<Brand>().in("id", ids));
        return id1>0;
    }

    @Override
    public Brand selectById(Long id) {
        Brand brand = baseMapper.selectById(id);
        return brand;
    }

}
