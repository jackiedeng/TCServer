package com.starts.crawlers;

import com.starts.parser.StartCrawlConfig;
import com.starts.storage.FileSaveService;
import com.starts.util.LogUtil;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.UUID;

/**
 * Created by Jackie on 2018/11/4.
 */
public class StartsBaiduImgCrawler extends WebCrawler {


    FileSaveService fileSaveService;

//    private static final Pattern filters = Pattern.compile(
//            ".*(\\.(css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf" +
//                    "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
//
//    private static final Pattern imgPatterns = Pattern.compile(".*(\\.(bmp|gif|jpe?g|png|tiff?))$");


    private static String[] crawlDomains;

    public static void configure(String[] domain) {

        crawlDomains = domain;
    }

    public StartsBaiduImgCrawler(){

        fileSaveService = new FileSaveService();
    }

    @Override
    protected int maxUrlCarryAtOneTime(){
        return  4;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        return true;
    }

    @Override
    public void visit(Page page) {

        try {

            // We are only interested in processing images which are bigger than 100k
            if (((page.getParseData() instanceof BinaryParseData) &&
                            (page.getContentData().length > (5 * 1024)))) {

                String url = page.getWebURL().getURL();

                // get a unique name for storing this image
                String path = StartCrawlConfig.findPathInUrl(url);
                String extension = path.substring(path.lastIndexOf('.'));

                String lock = "";
                String hashedName = "";

                synchronized (lock) {
                    hashedName = UUID.randomUUID() + extension;
                }

                fileSaveService.saveToFile(page, hashedName,url);
            }

        }catch (Exception e){
            LogUtil.exception(e);
        }

    }



}
