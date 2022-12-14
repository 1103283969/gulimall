package com.atguigu.gulimall.product;


import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;


   // @Autowired
   // OSSClient ossClient;

    @Autowired
    CategoryService categoryService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Test
    public void testStringRedis() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        //保存
        ops.set("hello","world_" + UUID.randomUUID().toString());

        //查询
        String hello = ops.get("hello");
        System.out.println("之前保存的数据:"+hello);
    }

    @Test
    public void testFindPath(){
        Long[] catelogPath = categoryService.findCatelogPath(255L);

        log.info("完整路径：{}", Arrays.asList(catelogPath));

    }

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
      // ossClient.putObject("gulimall-hellowzl","bug.jpg",inputStream);

     //  ossClient.shutdown();
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
