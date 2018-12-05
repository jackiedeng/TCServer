package com.starts.servers;

import com.starts.crawlers.StartsBaiduImgCrawler;
import com.starts.fetcher.StartPageFetcher;
import com.starts.fetcher.StartsBaiduCrawlController;
import com.starts.parser.StartCrawlConfig;
import com.starts.parser.StartsPraser;
import com.starts.storage.FileSaveService;
import com.starts.storage.SleepCatRecordService;
import com.starts.util.ConfigUtil;
import com.starts.util.LogUtil;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Jackie on 2018/11/4.
 */
public class StartCrawlService implements InitializingBean,Runnable {

    private StartsBaiduCrawlController startsBaiduCrawlController;

    private boolean setUpPrintMark = false; //用于启动日志输出 标记

    private boolean hasBeginJob = false;

    private static final int checkTime = 10*1000; //轮训检查时间间隔

    @Resource
    private ConfigUtil configUtil;

    @Resource
    private SleepCatRecordService sleepCatRecordService;

    public void init(){

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        LogUtil.biz.info("*************************************************");
        LogUtil.biz.info("*************************************************");
        LogUtil.biz.info("***************全自动爬虫服务online!***************");
        LogUtil.biz.info("*************************************************");
        LogUtil.biz.info("*************************************************");
        try {


//            //开启线程
            Thread thread = new Thread(this,"mainServiceThread");

            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.exception(e);
        }
    }

    @Override
    public void run() {

        //读取文件配置
        try {
            configUtil.getConfigFromFileAndSet();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        常驻
        while (true){

            try {


                try {
                    taskStatusCheck();
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.exception(e);
                }

                 Thread.sleep(checkTime);

            } catch (InterruptedException e) {
                e.printStackTrace();
                LogUtil.exception(e);
            }

        }

    }


    synchronized private void taskStatusCheck() throws Exception {

        if(!StartCrawlConfig.enable){
            LogUtil.biz.warn("爬虫关闭！！！");
            return;
        }

        LogUtil.debug.info("{}开始日常任务状态检查",Thread.currentThread().getName());

        if(this.getStartsBaiduCrawlController() == null && !hasBeginJob){

            if(!checkTodaysTaskIsDone()){

                LogUtil.biz.info("开始新的爬取任务");
                //记录任务已经开始
                hasBeginJob = true;
                //直接开始新的任务
                beginCrawTask();
            }

        }else{

            //status check
            if(startsBaiduCrawlController.isFinishTodaysJob()){

                //时间搓对比
                if(!StringUtils.equals(startsBaiduCrawlController.getJobDateStemp(),
                        FileSaveService.dateTimeStamp())){
                //模拟已经过期了
//                if(true){
                    //任务已经过期 开始新的任务
                    LogUtil.biz.info("任务过期，准备开始新的任务");
                    //清理
                    clearForRestart();
                }else{
                    LogUtil.debug.info("{} all job is done!waiting for next work day", startsBaiduCrawlController.getJobDateStemp());
                }

            }else{

                LogUtil.biz.info("运行中，已经获取{}个",FileSaveService.imgSaveTotalCount);
            }
        }
    }

    private boolean checkTodaysTaskIsDone(){

//        return false;

        String packagePath = FileSaveService.PackagePathWithTimestamp(FileSaveService.dateTimeStamp());

        File zipFile = new File(packagePath);

        if(zipFile.exists() && !zipFile.isDirectory()){

            LogUtil.debug.info("{} all job is done!waiting for next work day",FileSaveService.dateTimeStamp());

            if(!setUpPrintMark){

                setUpPrintMark = true;

                LogUtil.biz.info("看起来今天的任务已经完成了,等待下一个工作时间:{}",packagePath);
            }


            return  true;
        }

        return false;
    }

    private void clearForRestart() throws IOException {

        //记录任务已经结束
        hasBeginJob = false;
        //置空
        startsBaiduCrawlController = null;
        //重制图片数量
        FileSaveService.imgSaveTotalCount = 0;
        //读取文件配置
        configUtil.getConfigFromFileAndSet();

    }


    private void beginCrawTask() throws Exception {

        Thread thread = new Thread(new Runnable(){

            @Override public void run(){

                try {

                    LogUtil.biz.info("创建爬虫工作线程:{}", Thread.currentThread().getName());

                    beginBaiduImgCrawlTask();
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.exception(e);
                }
            }
        },"wordThread"+ Math.random()%100);

        thread.start();
    }

//    private void htmlunitTest(){
//
//        try (final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_52)) {
//
//            webClient.getOptions().setThrowExceptionOnScriptError(false);
//            webClient.getOptions().setCssEnabled(false);
//            webClient.getOptions().setJavaScriptEnabled(true);
//            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
//            webClient.getOptions().setTimeout(30000);
//
//            final HtmlPage page = webClient.getPage("https://www.baidu.com/s?rtt=1&bsst=1&cl=2&tn=news&word=1");
//
//            final String pageAsXml = page.asXml();
//            final String pageAsText = page.asText();
//
//            System.out.print(pageAsXml+pageAsText);
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }


    public void beginBaiduImgCrawlTask() throws Exception {
//
        //爬虫数量
        int numberOfCrawlers = StartCrawlConfig.numberOfcrawler;
        //爬取的网页信息的Seed
        String [] seeds = StartCrawlConfig.seeds;
        //爬取网页的domain
        String [] domains = {"https://image.baidu.com/search/flip"};


        CrawlConfig config = new CrawlConfig();
        //设置下载二进制文件
        config.setIncludeBinaryContentInCrawling(true);
        //最多三层
        config.setMaxDepthOfCrawling(3);
        //连接数 最大200个线程
        config.setMaxTotalConnections(StartCrawlConfig.maxTotalConnections);
        config.setMaxConnectionsPerHost(StartCrawlConfig.maxConnectionsPerHost);
        config.setPolitenessDelay(StartCrawlConfig.politenessDelay);
        config.setResumableCrawling(true);

        config.setSocketTimeout(StartCrawlConfig.connectionTimeout);
        config.setConnectionTimeout(StartCrawlConfig.connectionTimeout);

        config.setCrawlStorageFolder(FileSaveService.storageFolderName);

        StartCrawlConfig.domains = domains;

        StartPageFetcher pageFetcher = new StartPageFetcher(config);

        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        robotstxtConfig.setUserAgentName("Googlebot");
        robotstxtConfig.setIgnoreUADiscrimination(true);
        robotstxtConfig.setEnabled(false);

        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

        startsBaiduCrawlController = new StartsBaiduCrawlController(config,pageFetcher,
                new Parser(config,new StartsPraser(config)),
                robotstxtServer);

        for(String seed : seeds) {

            String seedUrl = "https://image.baidu.com/search/flip?tn=baiduimage&ie=utf-8&word="+seed+"&pn=0&gsm=0&ct=&ic=0&lm=-1&width=0&height=0";

            startsBaiduCrawlController.addSeed(seedUrl);

        }


        StartsBaiduImgCrawler.configure(domains);

        LogUtil.biz.info("百度图片爬取任务启动！");

        startsBaiduCrawlController.start(StartsBaiduImgCrawler.class,numberOfCrawlers);

    }

    public StartsBaiduCrawlController getStartsBaiduCrawlController() {
        return startsBaiduCrawlController;
    }

    public void setStartsBaiduCrawlController(StartsBaiduCrawlController startsBaiduCrawlController) {
        this.startsBaiduCrawlController = startsBaiduCrawlController;
    }
}
