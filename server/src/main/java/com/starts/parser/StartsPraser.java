package com.starts.parser;

import com.starts.util.LogUtil;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.exceptions.ParseException;
import edu.uci.ics.crawler4j.parser.AllTagMapper;
import edu.uci.ics.crawler4j.parser.HtmlContentHandler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.HtmlParser;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlMapper;


import java.io.*;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

/**
 * Created by Jackie on 2018/11/4.
 */
public class StartsPraser implements HtmlParser {

    private final org.apache.tika.parser.html.HtmlParser htmlParser;
    private final ParseContext parseContext;


    private StartCrawlConfig startCrawlConfig;

    public StartsPraser(CrawlConfig config) throws InstantiationException, IllegalAccessException {
//        this.config = config;
        htmlParser = new org.apache.tika.parser.html.HtmlParser();

        parseContext = new ParseContext();
        parseContext.set(HtmlMapper.class, AllTagMapper.class.newInstance());

        startCrawlConfig = new StartCrawlConfig();
    }

    @Override
    public HtmlParseData parse(Page page, String contextURL) throws ParseException {

        HtmlParseData htmlParseData = new HtmlParseData();

        HtmlContentHandler contentHandler = new HtmlContentHandler();
        Metadata metadata = new Metadata();
        InputStream inputStream = null;
        try{
            inputStream = new ByteArrayInputStream(page.getContentData());
            htmlParser.parse(inputStream, contentHandler, metadata, parseContext);
        } catch (Exception e) {
            LogUtil.exception(e);
            throw new ParseException();
        }

        String contentCharset = chooseEncoding(page, metadata);
        htmlParseData.setContentCharset(contentCharset);

        htmlParseData.setText(contentHandler.getBodyText().trim());
        htmlParseData.setTitle(metadata.get(DublinCore.TITLE));
        htmlParseData.setMetaTags(contentHandler.getMetaTags());

        Set<WebURL> outgoingUrls = null;

        try {

            ByteBuffer buf = ByteBuffer.wrap(page.getContentData());

            Charset charset = Charset.forName(contentCharset);

            CharBuffer cBuf = charset.decode(buf);

            String content = cBuf.toString();

            String crawkey = StartCrawlConfig.findHostInUrl(contextURL)+'_'+ StartCrawlConfig.findPraramInUrl("word", contextURL);

            //解析图片
            outgoingUrls = doPrase(content,crawkey);

            LogUtil.biz.warn("+img url:{}个",outgoingUrls.size());

            //查找更多页面
            doFindSeed(outgoingUrls,contextURL);
            //查找更多keywork
            doFindMoreWord(outgoingUrls,contextURL,content);

        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.exception(e);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            LogUtil.exception(e);
        }


        htmlParseData.setOutgoingUrls(outgoingUrls);

        return htmlParseData;
    }

    private String chooseEncoding(Page page, Metadata metadata) {
        String pageCharset = page.getContentCharset();
        if (pageCharset == null || pageCharset.isEmpty()) {
            return metadata.get("Content-Encoding");
        }
        return pageCharset;
    }


    private Set<WebURL> doPrase(String htmlBody,String tagMark) throws IOException, URISyntaxException {

        return startCrawlConfig.beginImageSerch(htmlBody,tagMark);
    }


    //search for more
    private void doFindSeed(Set<WebURL> outgoingUrls,String contentURL) throws URISyntaxException {


        //查找跟多页面
        URIBuilder builder = new URIBuilder(contentURL);
        builder.setCharset(Charset.forName("UTF-8"));

        List<NameValuePair> paramsList = builder.getQueryParams();

        for(NameValuePair pair:paramsList){

            if(StringUtils.equals(pair.getName(),"pn")
                    && NumberUtils.toInt(pair.getValue(),0) == 0){

                int step = 1;

                while(step <= StartCrawlConfig.maxPageCount) {

                    //make new
                    builder.setParameter("pn", String.valueOf(step * StartCrawlConfig.npStep));

                    String newurl = builder.build().toString();

                    LogUtil.seed.info("insert seed url:{}",newurl);

                    outgoingUrls.add(StartCrawlConfig.WebUrlWithUrl(newurl));

                    step++;
                }

            }
        }
    }

    //search for more key word
    private void doFindMoreWord(Set<WebURL> outgoingUrls,String contentURL,String content) throws URISyntaxException, UnsupportedEncodingException {

        List<String > newWords = StartCrawlConfig.findKeyWordInContentAndSignOriginWord(content, contentURL);

        if(newWords.size() > 0) {
            LogUtil.biz.warn("新增关联关键字:{}", newWords);
        }

        for(String word:newWords){

            String urlTemplate = "https://image.baidu.com/search/flip?tn=baiduimage&ie=utf-8&word="+ URLEncoder.encode(word, "UTF-8")+"&pn=0&gsm=0&ct=&ic=0&lm=-1&width=0&height=0";

            outgoingUrls.add(StartCrawlConfig.WebUrlWithUrl(urlTemplate));
        }
    }
}
