package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.BrandEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 品牌
 *
 * @author wangzhiliang
 * @email sunlightcs@gmail.com
 * @date 2022-10-29 21:51:50
 */

public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

