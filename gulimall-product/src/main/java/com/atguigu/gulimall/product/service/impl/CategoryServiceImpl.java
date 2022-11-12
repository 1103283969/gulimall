package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

/*    @Autowired
    CategoryDao categoryDao;*/

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {

        //1、查询出所有分类
        List<CategoryEntity> entities = super.baseMapper.selectList(null);

        //2、组装成父子的树形结构

        //2.1)、找到所有一级分类
        List<CategoryEntity> levelMenus = entities.stream()
                .filter(e -> e.getParentCid() == 0)
                .map((menu) -> {
                    menu.setChildren(getChildrens(menu, entities));
                    return menu;
                })
                .sorted((menu, menu2) -> {
                    return (menu.getSort() == null ? 0 : menu.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                })
                .collect(Collectors.toList());

        return levelMenus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {

        //1.检查当前删除的菜单，是否被别的地方引用
        //逻辑删除
        baseMapper.deleteBatchIds(asList);


    }


    /*

    找到catelogId的完整路径
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long>  paths =new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId,paths);

        Collections.reverse(parentPath);
        return  parentPath.toArray(new Long[parentPath.size()]);
    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {

        List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return entities;
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        System.out.println("查询了数据库");

        //将数据库的多次查询变为一次
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);

        //1、查出所有分类
        //1、1）查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //封装数据
        Map<String, List<Catelog2Vo>> parentCid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个的一级分类,查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());

            //2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName().toString());

                    //1、找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());

                    if (level3Catelog != null) {
                        List<Catelog2Vo.Category3Vo> category3Vos = level3Catelog.stream().map(l3 -> {
                            //2、封装成指定格式
                            Catelog2Vo.Category3Vo category3Vo = new Catelog2Vo.Category3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());

                            return category3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(category3Vos);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));

        return parentCid;
    }


    private List<Long> findParentPath(Long catelogId,List<Long> paths){
        //收集当前节点
        paths.add(catelogId);
        CategoryEntity byId  =this.getById(catelogId);
        if(byId.getParentCid()!=0){
            findParentPath(byId.getParentCid(),paths);
        }
        return paths;
    }


    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            //1、找到子菜单(递归)
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu, menu2) -> {
            //2、菜单的排序
            return (menu.getSort() == null ? 0 : menu.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;

    }


    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList,Long parentCid) {
        List<CategoryEntity> categoryEntities = selectList.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
        return categoryEntities;
        // return this.baseMapper.selectList(
        //         new QueryWrapper<CategoryEntity>().eq("parent_cid", parentCid));
    }

}