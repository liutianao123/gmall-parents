package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.cms.entity.PrefrenceAreaProductRelation;
import com.atguigu.gmall.cms.entity.SubjectProductRelation;
import com.atguigu.gmall.config.PageBaseConfig;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.mapper.*;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.pms.vo.PmsProductParam;
import com.atguigu.gmall.pms.vo.PmsProductQueryParam;
import com.atguigu.gmall.search.service.SearchService;
import com.atguigu.gmall.to.EsProduct;
import com.atguigu.gmall.to.EsProductAttributeValue;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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

    @Autowired
    private MemberPriceMapper memberPriceMapper;
    @Autowired
    private ProductAttributeValueMapper productAttributeValueMapper;
    @Autowired
    private ProductFullReductionMapper productFullReductionMapper;
    @Autowired
    private ProductLadderMapper productLadderMapper;
    @Autowired
    private SkuStockMapper skuStockMapper;
    private ThreadLocal<PmsProductParam> local = new ThreadLocal();
    @Reference
    private SearchService searchService;

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

    @Override
    public Product selectById(Long id) {
        Product product = baseMapper.selectById(id);
        return product;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void Insert(PmsProductParam productParam) {

        insertSkuAndPro(productParam);

        insertmember();
        insertpav();
        insertpfrl();
        insertpll();
    }

    @Override
    public void updateByIds(List<Long> ids, Integer publishStatus) {
        Product product = new Product();
        product.setPublishStatus(publishStatus);
        baseMapper.update(product, new QueryWrapper<Product>().in("id", ids));
    }

    @Override
    public List<Product> selectByIds(List<Long> list) {
        List<Product> products = baseMapper.selectList(new QueryWrapper<Product>().in("id", list));
        return products;
    }

    @Override
    public void ublishStatus(List<Long> ids, Integer publishStatus) {
        if (publishStatus == 1) {
            publish(ids);
        } else {
            remove(ids);
        }
    }

    private void publish(List<Long> ids) {

        ids.forEach(id -> {
            Product product = baseMapper.selectById(id);
            List<SkuStock> skuStocks = skuStockMapper.selectList(new QueryWrapper<SkuStock>().eq("product_id", id));
            List<EsProductAttributeValue> list = productAttributeValueMapper.selectProductAttrvalues(id);
            AtomicReference<Integer> count = new AtomicReference<>(0);
            skuStocks.forEach(skuStock -> {
                EsProduct esProduct = new EsProduct();
                BeanUtils.copyProperties(product, esProduct);
                //改写商品的标题，加上sku的销售属性
                esProduct.setName(product.getName() + "   " + skuStock.getSp1() + " " + skuStock.getSp2() + " " + skuStock.getSp3());
                esProduct.setPrice(skuStock.getPrice());
                esProduct.setStock(skuStock.getStock());
                esProduct.setSale(skuStock.getSale());
                esProduct.setAttrValueList(list);
                esProduct.setId(skuStock.getId());
                System.out.println(skuStock.getId());
                boolean b = searchService.saveProductInfoToEs(esProduct);
                if (b) {
                    count.set(count.get() + 1);
                }
            });
            if (count.get() == skuStocks.size()) {
                Product product1 = new Product();
                product1.setId(id);
                product1.setPublishStatus(1);
                baseMapper.updateById(product1);
            }
        });
    }

    private void remove(List<Long> ids) {
        boolean b = searchService.reomve(ids);
        if (b) {
            Product product = new Product();
            product.setPublishStatus(0);
            baseMapper.update(product, new QueryWrapper<Product>().in("id", ids));
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public Long insertProduct(PmsProductParam productParam) {
        Product product = new Product();
        BeanUtils.copyProperties(productParam, product);
        baseMapper.insert(product);
        return product.getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertpav() {
        PmsProductParam pmsProductParam = local.get();
        List<ProductAttributeValue> productAttributeValueList = pmsProductParam.getProductAttributeValueList();
        productAttributeValueList.forEach(productAttributeValue -> {
            productAttributeValue.setProductId(pmsProductParam.getId());
            productAttributeValueMapper.insert(productAttributeValue);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertmember() {
        PmsProductParam pmsProductParam = local.get();
        List<MemberPrice> memberPriceList = pmsProductParam.getMemberPriceList();
        memberPriceList.forEach(memberPrice -> {
            memberPrice.setProductId(pmsProductParam.getId());
            memberPriceMapper.insert(memberPrice);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertpfrl() {
        PmsProductParam pmsProductParam = local.get();
        List<ProductFullReduction> productFullReductionList = pmsProductParam.getProductFullReductionList();
        productFullReductionList.forEach(productFullReduction -> {
            productFullReduction.setProductId(pmsProductParam.getId());
            productFullReductionMapper.insert(productFullReduction);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertpll() {
        PmsProductParam pmsProductParam = local.get();
        List<ProductLadder> productLadderList = pmsProductParam.getProductLadderList();
        productLadderList.forEach(productLadder -> {
            productLadder.setProductId(pmsProductParam.getId());
            productLadderMapper.insert(productLadder);
        });
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void insertSku() {
        PmsProductParam pmsProductParam = local.get();
        List<SkuStock> skuStockList = pmsProductParam.getSkuStockList();
        skuStockList.forEach(skuStock -> {
            skuStock.setProductId(pmsProductParam.getId());
            skuStockMapper.insert(skuStock);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertSkuAndPro(PmsProductParam productParam) {
        Long id = insertProduct(productParam);
        productParam.setId(id);
        local.set(productParam);
        insertSku();

    }


}
