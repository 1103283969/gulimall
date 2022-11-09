package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 商品属性
 * 
 * @author wangzhiliang
 * @email sunlightcs@gmail.com
 * @date 2022-10-29 21:51:50
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> selectSearchAttrIds(@Param("attrIds") List<Long> attrIds);


}
