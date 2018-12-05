package com.springapp.mvc;

import com.alibaba.fastjson.JSON;
import com.starts.storage.FileSaveService;
import com.starts.storage.entity.MarkImgRecord;
import com.starts.util.LogUtil;
import com.starts.util.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.IOException;
import com.google.common.io.Files;

import javax.annotation.Resource;
import java.nio.file.Path;

@Controller
@CrossOrigin
@RequestMapping(produces = "text/html;charset=UTF-8")
public class ImageUploadController {

    @Resource
    private MarkedImgController markedImgController;

    @RequestMapping(value="/imageUpload.do",method = RequestMethod.POST)
    @ResponseBody
    public String uploadImageByFileName(@RequestParam("data") MultipartFile[] files,String fileName,String tags) throws IOException {


        String folderPath = FileSaveService.markedImageFolderPath();

        File fileFolder = new File(folderPath);
        if(!fileFolder.exists()){
            fileFolder.mkdir();
        }
        //file path
        String filePath = folderPath + "/" + fileName;

        Result<MarkImgRecord> result = new Result<>();

        try {

            if(files.length > 0){

                //save image
                MultipartFile file = files[0];
                file.transferTo(new File(filePath));
                //update record
                String fullPath = "/markedImageFolder/service/"+fileName;

                result.setData(markedImgController.updateMarkImgRecord(fileName, fullPath, tags));

                if(result.getData() != null){
                    result.setSuccess(true);
                }

            }

        }catch (Exception e){
            LogUtil.exception(e);
        }

        return JSON.toJSONString(result);
    }

}