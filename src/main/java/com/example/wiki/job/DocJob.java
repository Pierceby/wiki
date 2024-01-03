package com.example.wiki.job;

 import com.example.wiki.service.DocService;
 import com.example.wiki.util.SnowFlake;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.slf4j.MDC;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.scheduling.annotation.Scheduled;
 import org.springframework.stereotype.Component;

 import javax.annotation.Resource;
 import java.text.SimpleDateFormat;
 import java.util.Date;

 @Component
 public class DocJob {
     @Resource
     private DocService docService;
     @Autowired
     private SnowFlake snowFlake;

    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

    /**
     * 自定义cron表达式跑批
     * 只有等上一次执行完成，下一次才会在下一个时间点执行，错过就错过
     */
    @Scheduled(cron = "1/5 * * * * ?")
    public void cron() {
        // 增加日志流水号
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("更新电子书下的文档数据开始");
        long start = System.currentTimeMillis();
        docService.updateEbookInfo();
        LOG.info("更新电子书下的文档数据结束，耗时：{}毫秒", System.currentTimeMillis() - start);
    }

 }

