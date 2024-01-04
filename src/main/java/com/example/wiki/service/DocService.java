package com.example.wiki.service;

import com.example.wiki.domain.CategoryExample;
import com.example.wiki.domain.Content;
import com.example.wiki.domain.Doc;
import com.example.wiki.domain.DocExample;
import com.example.wiki.exception.BusinessException;
import com.example.wiki.exception.BusinessExceptionCode;
import com.example.wiki.mapper.ContentMapper;
import com.example.wiki.mapper.DocMapper;
import com.example.wiki.mapper.DocMapperCust;
import com.example.wiki.req.DocQueryReq;
import com.example.wiki.req.DocSaveReq;
import com.example.wiki.resp.DocQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.util.CopyUtil;
import com.example.wiki.util.RedisUtil;
import com.example.wiki.util.RequestContext;
import com.example.wiki.util.SnowFlake;
import com.example.wiki.websocket.WebSocketServer;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.cj.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class DocService {
    @Autowired
    private DocMapper docMapper;
    @Autowired
    private SnowFlake snowFlake;
    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private DocMapperCust docMapperCust;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WebSocketServer webSocketServer;

    private static final Logger log = LoggerFactory.getLogger(DocService.class);

    public List<DocQueryResp> all(Long ebookId) {

        //模糊查询
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);

        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
        return list;
    }
    public PageResp<DocQueryResp> list(DocQueryReq req) {

        //模糊查询
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
        //与想查询的操作放在一起，以免中间有其他查询导致分页失败
        /**
         * 1.page=0 size=0要想查询全部数据，设置reasonable=true以及pageSizezero=true
         * 2.page!=0 size=0要想查询全部数据，设置pageSizezero=true
         */
        PageHelper.startPage(req.getPage(), req.getSize(),true,null,true);
        List<Doc> docList = docMapper.selectByExample(docExample);
        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        log.info("总行数：{}", pageInfo.getTotal());
        log.info("总页数：{}", pageInfo.getPages());

//        ArrayList<DocResp> docReqList = new ArrayList<>();
//        for(Doc doc:docList){
//            DocResp docResp = new DocResp();
//            BeanUtils.copyProperties(doc,docResp);
//            docReqList.add(docResp);
//        }
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }


    /**
     * 保存
     * @param req
     */
    public void save(DocSaveReq req) {
        Doc doc = new Doc();
        doc = CopyUtil.copy(req, Doc.class);
        Content content=CopyUtil.copy(req,Content.class);
        //根据id是否有值判断是新增还是更新
        if(ObjectUtils.isEmpty(req.getId())){
            //新增
            doc.setId(snowFlake.nextId());

            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);
            content.setId(doc.getId());
            contentMapper.insert(content);
        }
        else {
            //更新
            docMapper.updateByPrimaryKey(doc);
            //大字段更新
            int count=contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if(count==0){
                contentMapper.insert(content);
            }
        }
    }

    /**
     * 删除
     * @param id
     */
    public void delete(Long id) {
        int i = docMapper.deleteByPrimaryKey(id);
        log.info("删除行数{}",i);
    }
    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }
    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        //文档阅读书加一
        docMapperCust.increaseViewCount(id);
        if(ObjectUtils.isEmpty(content)){
            return "";
        }
        return content.getContent();
    }

    /**
     * 点赞
     */
    public void vote(Long id) {
        // docMapperCust.increaseVoteCount(id);
        // 远程IP+doc.id作为key，24小时内不能重复
        String ip = RequestContext.getRemoteAddr();

        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 5000)) {
            docMapperCust.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }
        // 推送消息
        Doc docDb = docMapper.selectByPrimaryKey(id);
        String logId = MDC.get("LOG_ID");
        webSocketServer.sendInfo("【" + docDb.getName() + "】被点赞！");
        // rocketMQTemplate.convertAndSend("VOTE_TOPIC", "【" + docDb.getName() + "】被点赞！");
    }
    public void updateEbookInfo() {
        docMapperCust.updateEbookInfo();
    }
}
