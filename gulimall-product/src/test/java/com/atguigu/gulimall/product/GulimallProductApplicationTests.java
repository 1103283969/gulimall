package com.atguigu.gulimall.product;


import com.aliyun.oss.*;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.QueryChainWrapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;


@SpringBootTest
@RunWith(SpringRunner.class)
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;


    @Autowired
    OSSClient ossClient;

    @Test
    public void testUpload() throws FileNotFoundException {
/*//        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
//        String endpoint = "oss-cn-beijing.aliyuncs.com";
//        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
//        String accessKeyId = "LTAI5t9HsdbWiGnPsJ2sqEt4";
//        String accessKeySecret = "CajfY46SGJ2GNxkVK6nL6Gundlu2et";
//        // 填写Bucket名称，例如examplebucket。*/
        String bucketName = "gulimall-hellowzl";
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "bug.jpg";
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        String filePath= "C:\\Users\\wangzhiliang\\Pictures\\bug.jpg";

        InputStream inputStream = new FileInputStream("C:\\Users\\wangzhiliang\\Pictures\\bug.jpg");


        // 创建OSSClient实例。
       ossClient.putObject("gulimall-hellowzl","bug.jpg",inputStream);

       ossClient.shutdown();
       System.out.println("上传完成。。。。");


    }

    @Test
    void contextLoads() {

        /*BrandEntity brandEntity=new BrandEntity();
        brandEntity.setName("华为");
        brandEntity.setBrandId(1L);
      //  brandService.save(brandEntity);

        brandService.updateById(brandEntity);
        System.out.println("保存成功");*/

      List<BrandEntity> list= brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id",1L));

      list.forEach((item)->{
          System.out.println(item);
      });

    }

}
