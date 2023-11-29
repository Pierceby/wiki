package com.example.wiki.service;

import com.example.wiki.domain.Category;
import com.example.wiki.domain.CategoryExample;
import com.example.wiki.mapper.CategoryMapper;
import com.example.wiki.req.CategoryQueryReq;
import com.example.wiki.req.CategorySaveReq;
import com.example.wiki.resp.CategoryQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.util.CopyUtil;
import com.example.wiki.util.SnowFlake;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private SnowFlake snowFlake;

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    public PageResp<CategoryQueryResp> list(CategoryQueryReq req) {

        //模糊查询
        CategoryExample categoryExample = new CategoryExample();
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
        //与想查询的操作放在一起，以免中间有其他查询导致分页失败
        /**
         * 1.page=0 size=0要想查询全部数据，设置reasonable=true以及pageSizezero=true
         * 2.page!=0 size=0要想查询全部数据，设置pageSizezero=true
         */
        PageHelper.startPage(req.getPage(), req.getSize(),true,null,true);
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);
        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

//        ArrayList<CategoryResp> categoryReqList = new ArrayList<>();
//        for(Category category:categoryList){
//            CategoryResp categoryResp = new CategoryResp();
//            BeanUtils.copyProperties(category,categoryResp);
//            categoryReqList.add(categoryResp);
//        }
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);
        PageResp<CategoryQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }


    /**
     * 保存
     * @param req
     */
    public void save(CategorySaveReq req) {
        Category category = new Category();
        category = CopyUtil.copy(req, Category.class);
        //根据id是否有值判断是新增还是更新
        if(ObjectUtils.isEmpty(req.getId())){
            //新增
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        }
        else {
            //更新
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    /**
     * 删除
     * @param id
     */
    public void delete(Long id) {
        int i = categoryMapper.deleteByPrimaryKey(id);
        log.info("删除行数{}",i);
    }
}
