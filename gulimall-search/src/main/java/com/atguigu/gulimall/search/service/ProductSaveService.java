package com.atguigu.gulimall.search.service;


import com.atguigu.common.to.es.SkuEsModel;
import org.springframework.stereotype.Service;

import java.util.List;



public interface ProductSaveService {

    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws Exception;
}
