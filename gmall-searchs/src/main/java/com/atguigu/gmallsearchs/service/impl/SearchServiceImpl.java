package com.atguigu.gmallsearchs.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.constant.EsConstant;
import com.atguigu.gmall.pms.entity.ProductAttributeValue;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.search.service.SearchService;
import com.atguigu.gmall.to.*;
import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import io.searchbox.core.search.aggregation.ChildrenAggregation;
import io.searchbox.core.search.aggregation.FilterAggregation;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Component
public class SearchServiceImpl implements SearchService {
    @Autowired
    JestClient jestClient;
    @Reference
    private ProductService productService;



    @Override
    public boolean saveProductInfoToEs(EsProduct esProduct) {
        Index index = new Index.Builder(esProduct)
                .index(EsConstant.ES_PRODUCT_INDEX)
                .type(EsConstant.ES_PRODUCT_TYPE)
                .id(esProduct.getId().toString()).build();
        DocumentResult execute = null;
        try {
            execute = jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return execute.isSucceeded();
    }

    @Override
    public boolean reomve(List<Long> ids) {
        AtomicReference<Integer> count=new AtomicReference<>(0);
        ids.forEach(id->{
            DocumentResult execute = null;

            Delete build = new Delete.Builder(EsConstant.ES_PRODUCT_INDEX + "/" + EsConstant.ES_PRODUCT_TYPE + "/" + id).build();
            try {
                 execute = jestClient.execute(build);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (execute.isSucceeded()){
                count.set(count.get()+1);
            }
        });
        if(ids.size()==count.get()){
            return true;
        }
        return false;
    }

    @Override
    public SearchResponse getSearch(SearchParam searchParam) throws IOException {
        //构造检索dsl语句
        String dsl=bulidSearchDsl(searchParam);
        Search build = new Search.Builder(dsl).build();
        //执行查询
        SearchResult execute = jestClient.execute(build);
        //封装和分析查询结果
        SearchResponse response=bulidSearchResult(execute);

        return response;
    }

    private SearchResponse bulidSearchResult(SearchResult execute) {
        SearchResponse searchResponse=new SearchResponse();
        SearchResponseAttrVo searchResponses = new SearchResponseAttrVo();
        MetricAggregation aggregations = execute.getAggregations();
        List<SearchResult.Hit<EsProduct, Void>> hits = execute.getHits(EsProduct.class);
        hits.forEach(his->{
            System.out.println(his);
            EsProduct source = his.source;
            System.out.println(source);
            searchResponse.getProducts().add(source);
        });
        searchResponses.setName("品牌");
        List<TermsAggregation.Entry> brandidagg = aggregations.getTermsAggregation("brandidagg").getBuckets();
        brandidagg.forEach(termAgg->{
            Long key1 = Long.valueOf(termAgg.getKey());
            searchResponses.setProductAttributeId(key1);
            termAgg.getTermsAggregation("brandNameAgg").getBuckets().forEach(brandname->{
                String key = brandname.getKey();
                searchResponses.getValue().add(key);
            });
        });
        searchResponse.setBrid(searchResponses);
        SearchResponseAttrVo searchResponsef = new SearchResponseAttrVo();
        searchResponsef.setName("分类");
        aggregations.getTermsAggregation("categoryId").getBuckets().forEach(cateId->{
            Long key1 = Long.valueOf(cateId.getKey());
            searchResponses.setProductAttributeId(key1);
            cateId.getTermsAggregation("productCategoryAgg").getBuckets().forEach(productCAgg->{
                String key = productCAgg.getKey();
                searchResponsef.getValue().add(key);
            });
        });
        searchResponse.setCatelog(searchResponsef);

        ChildrenAggregation productAttrAgg = aggregations.getChildrenAggregation("productAttrAgg");
         TermsAggregation termsAggregation = productAttrAgg.getTermsAggregation("productAttrIdAgg");

        termsAggregation.getBuckets().forEach(productid->{
            Long key1 = Long.parseLong(productid.getKey());
           SearchResponseAttrVo searchResponseAttrVo=new SearchResponseAttrVo();
            searchResponseAttrVo.setProductAttributeId(key1);
            productid.getTermsAggregation("productAttrNameAgg").getBuckets().forEach(productName->{
                String key = productName.getKey();
                searchResponseAttrVo.setName(key);
                productName.getTermsAggregation("productAttrvalue").getBuckets().forEach(productvalue->{
                    String key2 = productvalue.getKey();
                    searchResponseAttrVo.getValue().add(key2);
                });
            });
            searchResponse.getAttrs().add(searchResponseAttrVo);
        });
        searchResponse.setTotal(execute.getTotal());
        return searchResponse;
    }

    private String bulidSearchDsl(SearchParam searchParam) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if(StringUtils.isEmpty(searchParam.getKeyword())){
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        }else {
            boolQueryBuilder.must(QueryBuilders.matchQuery("name",searchParam.getKeyword()));
            boolQueryBuilder.should(QueryBuilders.matchQuery("subTitle",searchParam.getKeyword()));
            boolQueryBuilder.should(QueryBuilders.matchQuery("keywords",searchParam.getKeyword()));
        }
        if(!StringUtils.isEmpty(searchParam.getCatelogId())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("productCategoryId",searchParam.getCatelogId()));
        }
        if(!StringUtils.isEmpty(searchParam.getBrandId())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("brandId",searchParam.getBrandId()));
        }
        if(!StringUtils.isEmpty(searchParam.getProps())&&searchParam.getProps().length>0){
            for (String s : searchParam.getProps()) {
                String split = s.split(":")[0];//属性对应的id
                String split1 = s.split(":")[1];//属性对应的名字
               boolQueryBuilder.filter(QueryBuilders.nestedQuery("attrValueList",QueryBuilders.boolQuery()
                       .must(QueryBuilders.termQuery("attrValueList.productAttributeId",split))
                       .must(QueryBuilders.termQuery("attrValueList.value",split1)),ScoreMode.None));
            }
        }
        if(!StringUtils.isEmpty(searchParam.getOrder())){
            String order = searchParam.getOrder();
            String split = order.split(":")[0];
            String asc = order.split(":")[1];
            switch (split){
                case "0":
                    searchSourceBuilder.sort(SortBuilders.scoreSort().order( SortOrder.fromString(asc)));
                    break;
                case  "1":
                    searchSourceBuilder.sort(SortBuilders.fieldSort("sale").order(SortOrder.fromString(asc)));
                    break;
                case "2":
                    searchSourceBuilder.sort(SortBuilders.fieldSort("price").order(SortOrder.fromString(asc)));
                    break;
                case "3":
                    String s = asc.split("-")[0];
                    String s1 = asc.split("-")[1];
                    boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(s).lte(s1));
                    break;
            }
        }
        //聚合品牌信息
        TermsAggregationBuilder brandidagg = AggregationBuilders.terms("brandidagg").field("brandId").size(100)
                .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName").size(100));
        searchSourceBuilder.aggregation(brandidagg);
        //聚合分类信息
        TermsAggregationBuilder categoryId = AggregationBuilders.terms("categoryId").field("productCategoryId").size(100)
                .subAggregation(AggregationBuilders.terms("productCategoryAgg").field("productCategoryName").size(100));
        searchSourceBuilder.aggregation(categoryId);
        //聚合属性信息
        NestedAggregationBuilder field = AggregationBuilders.nested("productAttrAgg", "attrValueList").
                subAggregation(AggregationBuilders.filter("productAttrTypeAgg", QueryBuilders.termQuery("attrValueList.type", 1)))
               .subAggregation(AggregationBuilders.terms("productAttrIdAgg").field("attrValueList.productAttributeId").size(100)
                .subAggregation(AggregationBuilders.terms("productAttrNameAgg").field("attrValueList.name").size(100)
                .subAggregation(AggregationBuilders.terms("productAttrvalue").field("attrValueList.value").size(100))));

        searchSourceBuilder.aggregation(field);
        HighlightBuilder highlighter = new HighlightBuilder();
        highlighter.field("name").preTags("<b style='color red'>").postTags("</b>");
        searchSourceBuilder.highlighter(highlighter);
        searchSourceBuilder.from((searchParam.getPageNum()-1)*searchParam.getPageNum());
        searchSourceBuilder.size(searchParam.getPageSize());
        searchSourceBuilder.query(boolQueryBuilder);
        String s = searchSourceBuilder.toString();

        System.out.println(s);
        return s;
    }


}
