package com.starts.parser;

import com.starts.util.LogUtil;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;


import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Jackie on 2018/11/5.
 */
public class StartCrawlConfig {

    //是否启用
    public static boolean enable = true;

    //缩略图 测试使用
//    private final String tagHead ="thumbURL";
    //真实图片
    private final String tagHead ="objURL";
    //百度关键字
    private static String BaiduWordKey ="word";
    //标记图片的字段
    public static String CrawlKey = "crawlerTag";
    //查找后缀
    public static String [] pattern = {".jpg",".png",".JPG",".PNG"};
    //用来判断url是否需要记录之前是否访问，图片记录但是查找的种子页面记录
    public static String [] domains;
    //最大的翻页数
    public static int maxPageCount = 1;
    //百度每页包含的图片数 无需修改
    public static int npStep = 60;
    //最大搜索关键字的数量
    public static int maxWordCount = 0;
    //全局的搜索key word
    private static Set<String > keyWord = new HashSet<>();
    //查找的seed
    public static  String [] seeds = {"https://image.baidu.com/search/flip?tn=baiduimage&ie=utf-8&word=家具&pn=0&gsm=0&ct=&ic=0&lm=-1&width=0&height=0"};
    //连接数 最大200个线程
    public static int maxTotalConnections = 100;
    //单host最大链接数
    public static int maxConnectionsPerHost = 100;
    //每个抓取之后延时时间
    public static int politenessDelay = 500;
    //连接的超时时间
    public static int connectionTimeout = 10000;
    //爬虫的数量
    public static int numberOfcrawler = 100;
    //oss 配置
    public static String ossEndpoint = null;
    public static String ossAccessKeyId = null;
    public static String ossAccessKeySecret = null;
    public static String ossBucketName = null;

    public static WebURL WebUrlWithUrl(String url){

        WebURL webURL = new WebURL();

        webURL.setURL(url);

        return  webURL;

    }

    public static String findHostInUrl(String contentURL) throws URISyntaxException {

        URIBuilder builder = new URIBuilder(contentURL);

        builder.setCharset(Charset.forName("UTF-8"));

        return builder.getHost();
    }

    public static String findPathInUrl(String contentURL) throws URISyntaxException {

        URIBuilder builder = new URIBuilder(contentURL);

        builder.setCharset(Charset.forName("UTF-8"));

        return builder.getPath();
    }

    public static String findPraramInUrl(String paramKey,String contentURL) throws URISyntaxException {

        URIBuilder builder = new URIBuilder(contentURL);

        builder.setCharset(Charset.forName("UTF-8"));

        List<NameValuePair> paramsList = builder.getQueryParams();

        String tag = null;

        for (NameValuePair pair : paramsList) {
            if(StringUtils.equals(pair.getName(),paramKey)){
                tag = pair.getValue();
                break;
            }
        }

        return tag;
    }

    static public String addParamToUrl(String url,String paramKey,String value) throws URISyntaxException {

        URIBuilder builder = new URIBuilder(url);

        builder.setCharset(Charset.forName("UTF-8"));

        builder.setParameter(paramKey,value);

        return  builder.build().toString();
    }

    static public boolean urlNeedSaveDoc(String url){


        //如果是seed的domain直接就不记录类 因为要增量跑
        for(String domain :domains){

            if(url.contains(domain)){
                return false;
            }

        }

        return true;
    }

    private String getSubStringFromIndex(int index,String string){

        for(int i = index;i>=0;i--) {

            String searchStr = string.substring(i - 1, i);

            if (StringUtils.equals(searchStr, "\"")
                    || StringUtils.equals(searchStr, "\'")) {

                try {
                    String picHead = string.substring(i - 1 - tagHead.length() - 4,i - 1);

                    if (picHead.contains(tagHead)) {

                        return processUrl(string.substring(i, index));
                    } else {
                        break;
                    }
                }catch (Exception e){
                    break;
                }
            }
        }

//        LogUtil.debug.error("unfind img!",string.substring(index-1000,index));

        return null;
    }

    private String processUrl(String  url){


        url = url.replaceAll("\\\\","");

        return  url;

    }


    public Set<WebURL> beginImageSerch(String content,String tagMark) throws URISyntaxException {

        Set<WebURL> urlSet = new HashSet<WebURL>();

        for(String imgPattern :pattern){

            int index = 0;

            while (index < content.length() && index!=-1) {

                index = content.indexOf(imgPattern,index);

                if(index > 0){

                    String imgUrl = getSubStringFromIndex(index+imgPattern.length(),content);

                    if(imgUrl!=null){

                        //加上tag
                        imgUrl = StartCrawlConfig.addParamToUrl(imgUrl, StartCrawlConfig.CrawlKey, tagMark);

                        urlSet.add(StartCrawlConfig.WebUrlWithUrl(imgUrl));
                    }
                }else{

                    break;
                }

                index+=imgPattern.length();
            }
        }

        return urlSet;

    }

    public static List<String > findKeyWordInContentAndSignOriginWord(String content,String originUrl) throws URISyntaxException {


        List<String > useableKeyWords = new ArrayList<>();

        if(keyWord.size() > maxWordCount){
            return useableKeyWords;
        }

        String word = StartCrawlConfig.findPraramInUrl(StartCrawlConfig.BaiduWordKey, originUrl);

        synchronized (keyWord){

            if(word != null){

                keyWord.add(word);
            }
        }

        //search for word
        int searchStart = content.indexOf("相关搜索");

        if(searchStart != -1){

            //取后面5500个字符串
            String wordContent = content.substring(searchStart,5500+searchStart);
            String searchWord = "sensitive=0\">";
            String searchWordEnd = "</a>";

            int searchIndex = 0;

            while (searchIndex < wordContent.length()){

                searchIndex = wordContent.indexOf(searchWord,searchIndex);

                if(searchIndex == -1){
                    break;
                }else{

                    int searchEnd = wordContent.indexOf(searchWordEnd,searchIndex);

                    if(searchEnd == -1){
                        break;
                    }else{

                        String newWord = wordContent.substring(searchIndex+searchWord.length(),searchEnd-1);

                        if(newWord != null) {

                            synchronized (keyWord) {

                                if(keyWord.size() > maxWordCount){
                                    break;
                                }

                                if (!keyWord.contains(newWord)) {
                                    useableKeyWords.add(newWord);
                                    keyWord.add(newWord);
                                }
                            }
                        }
                    }

                    searchIndex += searchWord.length();
                }
            }

        }

        return useableKeyWords;
    }

}
