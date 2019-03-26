package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.Brand;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;
/**
 * <p>
 * 品牌表 服务类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
public interface BrandService extends IService<Brand> {

    Map<String,Object> select(Integer pageNum, Integer pageSize);

    List<Brand> listAll();

    boolean addNew(Brand brand);


    boolean updateByIdd(Long id, Brand brand);


    Brand selectById(Long id);

    boolean updateandRemove(List<Long> ids, Integer showStatus);

    boolean updatefactoryStatus(List<Long> ids, Integer factoryStatus);
}
