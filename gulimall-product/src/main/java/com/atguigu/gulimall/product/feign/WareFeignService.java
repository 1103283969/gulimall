package com.atguigu.gulimall.product.feign;


import com.atguigu.gulimall.product.vo.SkuHasStockVo;
import com.baomidou.mybatisplus.extension.api.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeignService {
    //直接返回我们想要的结果

    @PostMapping("/ware/waresku/hasstock")
    R<List<SkuHasStockVo> > getSkusHasStock(@RequestBody List<Long> skuIds);

}
