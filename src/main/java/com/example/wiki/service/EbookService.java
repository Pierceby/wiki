package com.example.wiki.service;

import com.example.wiki.domain.Demo;
import com.example.wiki.domain.Ebook;
import com.example.wiki.domain.EbookExample;
import com.example.wiki.mapper.DemoMapper;
import com.example.wiki.mapper.EbookMapper;
import com.example.wiki.req.EbookReq;
import com.example.wiki.resp.EbookResp;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {
    @Autowired
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req){
        //模糊查询
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        criteria.andNameLike("%"+req.getName()+"%");

        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
        ArrayList<EbookResp> ebookReqList = new ArrayList<>();
        for(Ebook ebook:ebookList){
            EbookResp ebookResp = new EbookResp();
            BeanUtils.copyProperties(ebook,ebookResp);
            ebookReqList.add(ebookResp);
        }
        return ebookReqList;
    }
}
