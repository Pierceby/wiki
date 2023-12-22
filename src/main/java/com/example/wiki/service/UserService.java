package com.example.wiki.service;

import com.example.wiki.domain.User;
import com.example.wiki.domain.UserExample;
import com.example.wiki.mapper.UserMapper;
import com.example.wiki.req.UserQueryReq;
import com.example.wiki.req.UserSaveReq;
import com.example.wiki.resp.UserQueryResp;
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
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SnowFlake snowFlake;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public PageResp<UserQueryResp> list(UserQueryReq req) {

        //模糊查询
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameEqualTo(req.getLoginName());
        }
        //与想查询的操作放在一起，以免中间有其他查询导致分页失败
        /**
         * 1.page=0 size=0要想查询全部数据，设置reasonable=true以及pageSizezero=true
         * 2.page!=0 size=0要想查询全部数据，设置pageSizezero=true
         */
        PageHelper.startPage(req.getPage(), req.getSize(),true,null,true);
        List<User> userList = userMapper.selectByExample(userExample);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

//        ArrayList<UserResp> userReqList = new ArrayList<>();
//        for(User user:userList){
//            UserResp userResp = new UserResp();
//            BeanUtils.copyProperties(user,userResp);
//            userReqList.add(userResp);
//        }
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);
        PageResp<UserQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }


    /**
     * 保存
     * @param req
     */
    public void save(UserSaveReq req) {
        User user = new User();
        user = CopyUtil.copy(req, User.class);
        //根据id是否有值判断是新增还是更新
        if(ObjectUtils.isEmpty(req.getId())){
            //新增
            user.setId(snowFlake.nextId());
            userMapper.insert(user);
        }
        else {
            //更新
            userMapper.updateByPrimaryKey(user);
        }
    }

    /**
     * 删除
     * @param id
     */
    public void delete(Long id) {
        int i = userMapper.deleteByPrimaryKey(id);
        log.info("删除行数{}",i);
    }
}
