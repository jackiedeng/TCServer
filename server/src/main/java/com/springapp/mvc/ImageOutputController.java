package com.springapp.mvc;

import com.starts.storage.FileSaveService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;

/**
 * Created by Jackie on 2018/11/10. 图片下载服务
 */
@Controller
@CrossOrigin
public class ImageOutputController {

    private String imageByPath(String path,
                               HttpServletRequest httpServletRequest,
                               HttpServletResponse httpServletResponse) throws IOException {

        File file = new File(path);
        long fileSize = file.length();

        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }

        FileInputStream fi = new FileInputStream(file);

        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length
                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }

        fi.close();

        OutputStream os = httpServletResponse.getOutputStream();
        os.write(buffer,0,numRead);
        os.flush();
        os.close();

        return "success";
    }

    @RequestMapping(value = {"/image/service/**"}, method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    public String execute(HttpServletRequest httpServletRequest,
                          HttpServletResponse httpServletResponse) throws IOException {
        // img为图片的二进制流
//        byte[] img = xxx;
        String filePath = httpServletRequest.getRequestURI();
        String replaceStr = "/image/service";
        filePath = URLDecoder.decode(filePath.replaceAll(replaceStr,""),"UTF-8");
        String path = FileSaveService.ImageFolderPath()+filePath;

       return imageByPath(path,httpServletRequest,httpServletResponse);
    }

    @RequestMapping(value = {"/thumbnailImage/service/**"}, method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    public String thumbnailExcute(HttpServletRequest httpServletRequest,
                                  HttpServletResponse httpServletResponse) throws IOException {

        String filePath = httpServletRequest.getRequestURI();
        String replaceStr = "/thumbnailImage/service";
        filePath = URLDecoder.decode(filePath.replaceAll(replaceStr,""),"UTF-8");
        String path = FileSaveService.PreviewImageFolderPath()+filePath;

        return imageByPath(path,httpServletRequest,httpServletResponse);
    }


    @RequestMapping(value = {"/markedImageFolder/service/**"}, method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    public String marketImageExcute(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse) throws IOException {

        String filePath = httpServletRequest.getRequestURI();
        String replaceStr = "/markedImageFolder/service";
        filePath = URLDecoder.decode(filePath.replaceAll(replaceStr,""),"UTF-8");

        String path = FileSaveService.markedImageFolderPath()+filePath;

        return imageByPath(path,httpServletRequest,httpServletResponse);
    }

}
