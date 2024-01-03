package com.example.wiki.job;

 import com.example.wiki.service.DocService;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.scheduling.annotation.Scheduled;
 import org.springframework.stereotype.Component;

 import javax.annotation.Resource;
 import java.text.SimpleDateFormat;
 import java.util.Date;

 @Component
 public class DocJob {
     @Resource
     private DocService docService;

    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

    /**
     * 自定义cron表达式跑批
     * 只有等上一次执行完成，下一次才会在下一个时间点执行，错过就错过
     */
    @Scheduled(cron = "1/5 * * * * ?")
    public void cron() {
        docService.updateEbookInfo();
    }

 }

