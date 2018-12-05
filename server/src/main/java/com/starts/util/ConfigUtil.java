package com.starts.util;

import com.starts.parser.StartCrawlConfig;
import com.starts.servers.StartCrawlService;
import com.starts.storage.FileSaveService;
import com.starts.storage.SleepCatRecordService;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Jackie on 2018/11/6.
 */
@Component
public class ConfigUtil {

    public void getConfigFromFileAndSet() throws IOException {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.json");

        String result = new BufferedReader(new InputStreamReader(inputStream))
                .lines().parallel().collect(Collectors.joining(System.lineSeparator()));


        JSONObject json = new JSONObject(result);

        FileSaveService.storageFolderName = json.getString("storageFolderName");
        //文件后缀
        StartCrawlConfig.pattern = ConfigUtil.toStringArray(json.getJSONArray("pattern"));
//        //最大的翻页数
        StartCrawlConfig.maxPageCount = json.getInt("maxPageCount");
//        //最大搜索关键字的数量
        StartCrawlConfig.maxWordCount = json.getInt("maxWordCount");
//        //查找的seed
        StartCrawlConfig.seeds = ConfigUtil.toStringArray(json.getJSONArray("seeds"));
        //连接数 最大200个线程
        StartCrawlConfig.maxTotalConnections = json.getInt("maxTotalConnections");
        //单host最大链接数
        StartCrawlConfig.maxConnectionsPerHost = json.getInt("maxConnectionsPerHost");
        //每个抓取之后延时时间
        StartCrawlConfig.politenessDelay = json.getInt("politenessDelay");
        //连接的超时时间
        StartCrawlConfig.connectionTimeout = json.getInt("connectionTimeout");
        //爬虫数量
        StartCrawlConfig.numberOfcrawler = json.getInt("numberOfcrawler");

        //是否启用
        StartCrawlConfig.enable = json.getBoolean("enable");

        SleepCatRecordService.DefaultServie.setUp(100000);

//        public static  String [] seeds = {"https://image.baidu.com/search/flip?tn=baiduimage&ie=utf-8&word=家具&pn=0&gsm=0&ct=&ic=0&lm=-1&width=0&height=0"};

    }


    public static String[] toStringArray(JSONArray array) {
        if(array==null)
            return null;

        String[] arr=new String[array.length()];
        for(int i=0; i<arr.length; i++) {
            arr[i]=array.optString(i);
        }
        return arr;
    }
}


