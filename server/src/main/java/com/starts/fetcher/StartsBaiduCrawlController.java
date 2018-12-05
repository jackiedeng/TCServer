package com.starts.fetcher;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectRequest;
import com.google.common.io.Files;
import com.starts.parser.StartCrawlConfig;
import com.starts.storage.FIleOssUploadProgressListener;
import com.starts.storage.FileSaveService;
import com.starts.storage.SleepCatRecordService;
import com.starts.util.LogUtil;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.apache.commons.lang3.StringUtils;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

/**
 * Created by Jackie on 2018/11/5.
 */
public class StartsBaiduCrawlController extends CrawlController implements Runnable{

    //当前任务时间搓 用于校验当日任务是否完成
    private String jobDateStemp = "";
    //是否已经打包 用于每次任务之后打包标记
    private boolean hasPackaged = false;
    //是否在packge中
    private boolean isPackaging = false;

    public StartsBaiduCrawlController(CrawlConfig config, PageFetcher pageFetcher, RobotstxtServer robotstxtServer) throws Exception {
        super(config, pageFetcher, robotstxtServer);

        jobDateStemp = FileSaveService.dateTimeStamp();
    }

    public StartsBaiduCrawlController(CrawlConfig config, StartPageFetcher pageFetcher, Parser parser, RobotstxtServer robotstxtServer) throws Exception {
        super(config,pageFetcher,parser,robotstxtServer);

        jobDateStemp = FileSaveService.dateTimeStamp();
    }


    public synchronized boolean isFinishTodaysJob(){

        if(this.isFinished()){

            if(this.hasPackaged){
                return true;
            }else if(!this.isPackaging){

                //begin package
                this.isPackaging = true;

                Thread thread = new Thread(this);

                thread.start();

                hasPackaged = true;

                //同步数据库
                SleepCatRecordService.DefaultServie.sync();}
        }

        LogUtil.biz.info("baidu status finish:{} shutdown:{} ispackaging:{} packaged:{}",
                this.isFinished(),this.isShuttingDown(),this.isPackaging(),this.isHasPackaged());

        return false;
    }


    public String getJobDateStemp() {
        return jobDateStemp;
    }

    public void setJobDateStemp(String jobDateStemp) {
        this.jobDateStemp = jobDateStemp;
    }

    public boolean isHasPackaged() {
        return hasPackaged;
    }

    public void setHasPackaged(boolean hasPackaged) {
        this.hasPackaged = hasPackaged;
    }

    public boolean isPackaging() {
        return isPackaging;
    }

    public void setIsPackaging(boolean isPackaging) {
        this.isPackaging = isPackaging;
    }

    @Override
    public void run() {

        //package task
        LogUtil.biz.info("baidu task 开始打包");


        String path = FileSaveService.storageFolderName+'/'+this.jobDateStemp;
        String zipFilePath = FileSaveService.PackagePathWithTimestamp(this.jobDateStemp);

        File targetFile = new File(path);

        if(!targetFile.exists() || !targetFile.isDirectory()){

            LogUtil.biz.error("打包文件夹不存在请检查:{} exists:{} isDict:{}"
                    ,path,targetFile.exists(),targetFile.isDirectory());

        }else {

            //写入记录文件
            writeResultFile(targetFile+"/info.txt");

            File zipFile = new File(zipFilePath);

            try {

                //打包
                ZipUtil.pack(targetFile, zipFile);

                LogUtil.biz.info("打包完成:{} 等待1s进行清除任务", zipFilePath);

                LogUtil.biz.info("上传oss..");

                Thread.sleep(1000);

                if(fileClear(targetFile)){
                    LogUtil.biz.info("{}清除成功",path);
                }

                uploadToOss(zipFilePath,this.jobDateStemp+"_crawler_task.zip");

                LogUtil.biz.info("关闭爬虫任务");

                this.shutdown();

                LogUtil.biz.info("{}任务已经完成，总共下载{}张图片，等待下一个工作时间",this.getJobDateStemp(),FileSaveService.imgSaveTotalCount);

            } catch (Exception e) {

                LogUtil.exception(e);

                LogUtil.biz.error("打包任务失败！！");
            }
        }


    }

        private boolean fileClear(File file){

        //删除所有 先删除目录下的内容，才能删除目录
        if (file.exists() && file.isDirectory()) {

            File[] fileArray = file.listFiles();//获取目录下的所有文件

            if(fileArray != null) {
                for (File subFile : fileArray) {
                    if(!fileClear(subFile)){
                        LogUtil.biz.error("文件清除失败",subFile);
                    }
                }
            }
        }

        return file.delete();
    }


    private void writeResultFile(String path){

        File file = new File(path);

        String resultStr = this.getJobDateStemp()+"本次任务一共下载"+FileSaveService.imgSaveTotalCount+"张图片";

        try {
            Files.write(resultStr.getBytes(), file);
        }catch (Exception e){
            LogUtil.exception(e);
        }

    }

    private boolean uploadToOss(String zipFile,String fileName){

        String endpoint = StartCrawlConfig.ossEndpoint;
        String accessKeyId = StartCrawlConfig.ossAccessKeyId;
        String accessKeySecret = StartCrawlConfig.ossAccessKeySecret;
        String bucketName = StartCrawlConfig.ossBucketName;

        if(StringUtils.isAnyBlank(endpoint,accessKeyId,accessKeySecret,bucketName)){
            LogUtil.biz.error("oss配置错误，停止上传");
            return false;
        }

        String objectName = fileName;

        // 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

        boolean result = true;
        try {
            // 带进度条的上传。
            ossClient.putObject(new PutObjectRequest(bucketName, objectName, new FileInputStream(zipFile)).
                    <PutObjectRequest>withProgressListener(new FIleOssUploadProgressListener()));

        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        if(result) {
            LogUtil.biz.info("{},oss上传成功!", zipFile);
        }else{
            LogUtil.biz.error("{},oss上传失败!", zipFile);
        }
        // 关闭OSSClient。
        ossClient.shutdown();

        return false;
    }
}
