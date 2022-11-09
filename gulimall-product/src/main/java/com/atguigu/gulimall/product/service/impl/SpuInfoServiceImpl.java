package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.SkuHasStockVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {


    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductAttrValueService  productAttrValueService;
    @Autowired
    AttrService attrService;

    @Autowired
    WareFeignService wareFeignService;



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {



        //1、查出当前spuid对应的所有sku信息，品牌的名字。
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIdList = skus.stream() .map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        // TODo 4、查询当前sku的所有可以被用来检索的规格属性，

        //TODO 4、查出当前sku的所有可以被用来检索的规格属性
        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrListforspu(spuId);

        List<Long> attrIds = baseAttrs.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        List<Long> searchAttrIds = attrService.selectSearchAttrs(attrIds);
        //转换为Set集合
        Set<Long> idSet = searchAttrIds.stream().collect(Collectors.toSet());

        List<SkuEsModel.Attrs> attrsList = baseAttrs.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attrs);
            return attrs;
        }).collect(Collectors.toList());

        //2、封装每个sku的信息

        List<SkuEsModel>  uoProducts = skus.stream( ).map(sku -> {
          //组装需要的数据

            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku , esModel) ;
           // skuPrice, skuImg , hasStock,
            esModel.setSkuPrice( sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());

            // TODo 1、发送远程调用，库存系统查询是否有库存

            Map<Long, Boolean> stockMap = null;
            try {
                R<List<SkuHasStockVo> >  skuHasStock = wareFeignService.getSkusHasStock(skuIdList);
                List<SkuHasStockVo> data =skuHasStock.getData();

                TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {};
                stockMap = skuHasStock.getData().stream()
                        .collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
            } catch (Exception e) {
                log.error("库存服务查询异常：原因{}",e);
            }


            // //ToDo 2、热度评分。0,
            esModel.setHotScore(0L);

            //ToDo 3  查询品牌和分类的名字信息
            BrandEntity brand = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo()) ;
            CategoryEntity category = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName( category.getName());

            //设置检索属性
            esModel.setAttrs(attrsList);

            return esModel;
        }).collect(Collectors.toList( ));


        //TODo 5、将数据发送给es进行保存;gulimalL-search;




    }

}