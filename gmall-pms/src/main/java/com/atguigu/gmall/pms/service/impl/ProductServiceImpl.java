package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.config.PageBaseConfig;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.mapper.ProductMapper;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.pms.vo.PmsProductQueryParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Service
@Component
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public Map<String, Object> pageselect(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Product> queryParamQueryWrapper = null;
        if (!StringUtils.isEmpty(productQueryParam)) {
            queryParamQueryWrapper = new QueryWrapper<>();
            if (!StringUtils.isEmpty(productQueryParam.getBrandId())) {
                queryParamQueryWrapper.eq("brand_id", productQueryParam.getBrandId());
            }
            if (!StringUtils.isEmpty(productQueryParam.getVerifyStatus())) {
                queryParamQueryWrapper.eq("verify_status", productQueryParam.getVerifyStatus());
            }
            if (!StringUtils.isEmpty(productQueryParam.getKeyword())) {
                queryParamQueryWrapper.eq("keywords", productQueryParam.getKeyword());
            }
            if (!StringUtils.isEmpty(productQueryParam.getProductCategoryId())) {
                queryParamQueryWrapper.eq("product_category_id", productQueryParam.getProductCategoryId());
            }
            if (!StringUtils.isEmpty(productQueryParam.getProductSn())) {
                queryParamQueryWrapper.eq("product_sn", productQueryParam.getProductSn());
            }
            if (!StringUtils.isEmpty(productQueryParam.getPublishStatus())) {
                queryParamQueryWrapper.eq("publish_status", productQueryParam.getPublishStatus());
            }
        }
        IPage<Product> page1 = baseMapper.selectPage(page, queryParamQueryWrapper);
        Map<String, Object> map = PageBaseConfig.pageBase(page1);
        return map;
    }
}
