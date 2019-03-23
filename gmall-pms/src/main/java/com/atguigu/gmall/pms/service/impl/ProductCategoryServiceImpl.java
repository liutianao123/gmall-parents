package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.config.PageBaseConfig;
import com.atguigu.gmall.config.RediesConfig;
import com.atguigu.gmall.pms.entity.ProductCategory;
import com.atguigu.gmall.pms.mapper.ProductCategoryMapper;
import com.atguigu.gmall.pms.service.ProductCategoryService;
import com.atguigu.gmall.pms.vo.ProductCategoryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Override
    public List<ProductCategoryVo> select() {
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String catchs = operations.get(RediesConfig.REDIES_ELEMENT);
        if(!StringUtils.isEmpty(catchs)){
            List<ProductCategoryVo> list = JSON.parseArray(catchs, ProductCategoryVo.class);
            return list;
        }
        List<ProductCategoryVo> list = baseMapper.selectLists(0);
        String s = JSON.toJSONString(list);
        operations.set(RediesConfig.REDIES_ELEMENT,s,3, TimeUnit.DAYS);
        return list;
    }

    @Override
    public Map<String, Object> selects(Integer pageNum, Integer pageSize, Long parentId) {
        QueryWrapper<ProductCategory> queryWrapper=new QueryWrapper<>();
        Page<ProductCategory> page=new Page<>(pageNum,pageSize);
        queryWrapper.eq("parent_id",parentId);
        IPage<ProductCategory> productCategoryIPage = baseMapper.selectPage(page, queryWrapper);
        Map<String, Object> map = PageBaseConfig.pageBase(productCategoryIPage);
        return map;
    }

    @Override
    public boolean add(ProductCategory  productCategoryParam) {

        int insert =baseMapper.insert(productCategoryParam);
        return insert>0;
    }
}
