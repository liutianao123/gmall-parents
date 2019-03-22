package com.atguigu.gmall.pms.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProductCategoryVo implements Serializable{
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "上机分类的编号：0表示一级分类")
    @TableField("parent_id")
    private Long parentId;

    @TableField("name")
    private String name;

    @ApiModelProperty(value = "分类级别：0->1级；1->2级")
    @TableField("level")
    private Integer level;

    @TableField("product_count")
    private Integer productCount;

    @TableField("product_unit")
    private String productUnit;

    @ApiModelProperty(value = "是否显示在导航栏：0->不显示；1->显示")
    @TableField("nav_status")
    private Integer navStatus;

    @ApiModelProperty(value = "显示状态：0->不显示；1->显示")
    @TableField("show_status")
    private Integer showStatus;

    @TableField("sort")
    private Integer sort;

    @ApiModelProperty(value = "图标")
    @TableField("icon")
    private String icon;

    @TableField("keywords")
    private String keywords;

    @ApiModelProperty(value = "描述")
    @TableField("description")
    private String description;
    List<ProductCategoryVo> children;
}
