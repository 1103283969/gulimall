package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author wangzhiliang
 * @email 1920731406qq.com
 * @date 2022-10-30 13:46:03
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
