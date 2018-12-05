package com.springapp.mvc;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

/**
 * Created by chaodeng on 2018/11/9.
 */
@Controller
public class UrlController {

    @RequestMapping(value = "/*", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public String getHtmlBody(HttpServletRequest req) throws IOException {

        String path = req.getSession().getServletContext().getRealPath("/website/index.html");

        File filePath = new File(path);

        StringBuffer fileData = new StringBuffer();
        //
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        //缓冲区使用完必须关掉
        reader.close();
        return fileData.toString();
    }

//    @RequestMapping(value = "/static/**", produces = MediaType.ALL_VALUE, method = RequestMethod.GET)
//    @ResponseBody
//    public String getResourceFile(HttpServletRequest req) throws IOException {
//
//        String path = req.getSession().getServletContext().getRealPath("/website/"+req.getContextPath());
//
//        File filePath = new File(path);
//
//        StringBuffer fileData = new StringBuffer();
//        //
//        BufferedReader reader = new BufferedReader(new FileReader(filePath));
//        char[] buf = new char[1024];
//        int numRead = 0;
//        while ((numRead = reader.read(buf)) != -1) {
//            String readData = String.valueOf(buf, 0, numRead);
//            fileData.append(readData);
//        }
//        //缓冲区使用完必须关掉
//        reader.close();
//
//        return fileData.toString();
//    }

}