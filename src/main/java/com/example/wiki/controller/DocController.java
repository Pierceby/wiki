package com.example.wiki.controller;

import com.example.wiki.req.DocQueryReq;
import com.example.wiki.req.DocSaveReq;
import com.example.wiki.resp.DocQueryResp;
import com.example.wiki.resp.CommonResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@RestController
public class DocController {
    @Autowired
    private DocService docService;
    @GetMapping("/doc/all/{ebookId}")
    public CommonResp all(@PathVariable Long ebookId){
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list = docService.all(ebookId);
        resp.setContent(list);
        return resp;
    }
    @GetMapping("/doc/list")
    public CommonResp list(@Valid DocQueryReq req){
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list = docService.list(req);
        resp.setContent(list);
        return resp;
    }
    @PostMapping("/doc/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq req){
        CommonResp resp = new CommonResp<>();
        docService.save(req);
        return resp;
    }
    @DeleteMapping("/doc/delete/{idStr}")
    public CommonResp delete(@PathVariable String idStr){
        CommonResp resp = new CommonResp<>();
        List<String> list = Arrays.asList(idStr.split(","));
        docService.delete(list);
        return resp;
    }
    @GetMapping("/doc/find-content/{id}")
    public CommonResp findContent(@PathVariable Long id){
        CommonResp<String> resp = new CommonResp<>();
        String content = docService.findContent(id);
        resp.setContent(content);
        return resp;
    }

    @GetMapping("/vote/{id}")
    public CommonResp vote(@PathVariable Long id) {
        CommonResp commonResp = new CommonResp();
        docService.vote(id);
        return commonResp;
    }
}
