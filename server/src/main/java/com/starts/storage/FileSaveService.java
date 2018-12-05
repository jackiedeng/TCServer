package com.starts.storage;

import com.google.common.io.Files;
import com.starts.parser.StartCrawlConfig;
import com.starts.util.LogUtil;
import edu.uci.ics.crawler4j.crawler.Page;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jackie on 2018/11/4.
 */

public class FileSaveService {

    private File storageFolder;
    //存储路径
    public static String storageFolderName = "/Users/Jackie/Desktop/crawFiles";
    //日期
    public static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    //整体图片计数
    public static volatile int imgSaveTotalCount = 0;
    //status key
    public static final String SyncKey = "fileKey";


    public static String dateTimeStamp(){
        return format.format(new Date());
    }

    public FileSaveService(){

        storageFolder = new File(storageFolderName);

        if (!storageFolder.exists()) {
            storageFolder.mkdirs();
        }
    }

    public static String PackagePathWithTimestamp(String timestamp){
        return storageFolderName+'/'+timestamp+".zip";
    }

    private void checkFolder(String path){

        File file = new File(path);
        if(!file.exists()){
            if(!file.mkdir()){
                LogUtil.debug.error("目录创建失败"+path);
            }
        }
    }

    public static String ImageFolderPath(){

        return  storageFolderName+"/imageFolder";
    }

    public static String PreviewImageFolderPath(){

        return  storageFolderName+"/previewImageFolder";
    }

    public static String markedImageFolderPath(){

        return storageFolderName+"/markedImageFolder";
    }

    public static String OriginFileToThumbnail(String orginUrl){

        String findStr = "/image/service";
        String targetStr = "/thumbnailImage/service";
        return orginUrl.replaceAll(findStr,targetStr);
    }

    private String crawlerKeyFromUrl(String originUrl) throws URISyntaxException {
        return StartCrawlConfig.findPraramInUrl(StartCrawlConfig.CrawlKey, originUrl);
    }

    private String checkFolderAndReturnPath(String originUrl,String crawlerKey,String fileName) throws URISyntaxException, UnsupportedEncodingException {

        crawlerKey = URLDecoder.decode(crawlerKey,"UTF-8");

        //原图路径检查
        String imagePath = FileSaveService.ImageFolderPath();
        String fullPath = imagePath+"/"+crawlerKey;
        checkFolder(imagePath);
        checkFolder(fullPath);


        //任务记录

        String dateKey = format.format(new Date());

        String timeFullPath = storageFolderName+"/"+dateKey;
        checkFolder(timeFullPath);

        return fullPath+"/" + fileName;
    }

    private String checkPreviewFolderAndReturnPath(String originUrl,String crawlerKey) throws UnsupportedEncodingException {

        crawlerKey = URLDecoder.decode(crawlerKey,"UTF-8");

        //缩略图路径检查
        String previewImagePath = FileSaveService.PreviewImageFolderPath();
        String previewFullPath = previewImagePath+"/"+crawlerKey;
        checkFolder(previewImagePath);
        checkFolder(previewFullPath);

        return  previewFullPath;
    }

   public boolean saveToFile(Page page,String fileName,String originUrl) throws URISyntaxException, UnsupportedEncodingException {

       boolean result = true;

        try {

            String crawlerKey = crawlerKeyFromUrl(originUrl);

//            LogUtil.debug.info(">>>>{}", originUrl);
            // store image
            String saveFilePath = checkFolderAndReturnPath(originUrl,crawlerKey,fileName);

            String previewFilePath = checkPreviewFolderAndReturnPath(originUrl,crawlerKey);


            File newFile = new File(saveFilePath);

            if(!newFile.exists()){
                Files.write(page.getContentData(), new File(saveFilePath));
            }

            //save preview img
            Thumbnails.of(newFile)
                    .size(200, 200)
                    .outputQuality(0.8)
                    .toFiles(new File(previewFilePath), Rename.NO_CHANGE);

            //计数加一
            synchronized (SyncKey) {
                imgSaveTotalCount++;
            }

        } catch (IOException iox) {
            LogUtil.debug.error("Failed to write file: " + fileName, iox);
            result = false;
//            LogUtil.exception(e);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.exception(e);
        }

       return result;
    }


}
