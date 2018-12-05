package com.springapp.mvc;

import com.alibaba.fastjson.JSON;
import com.springapp.mvc.outputDO.ImageRecordDO;
import com.starts.storage.FileSaveService;
import com.starts.storage.ImgRecord;
import com.starts.storage.SleepCatRecordService;
import com.starts.storage.entity.MarkImgRecord;
import com.starts.storage.entity.MarkTagRecord;
import com.starts.util.LogUtil;
import com.starts.util.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Jackie on 2018/11/11.  图片打表服务
 */
@Controller
@CrossOrigin
@RequestMapping(produces = "text/html;charset=UTF-8")
public class ImageTagServiceController {


    @Resource
    private MarkedImgController markedImgController;

    public ImgRecord queryImageWithKey(String imageName) throws Exception {
        return (ImgRecord) SleepCatRecordService.DefaultServie.getImgRecordSaver().get(imageName);
    }

    public boolean isImageWithTag(String imageName,String tag){

        try {
            ImgRecord image = queryImageWithKey(imageName);

            if (image != null) {
                return image.getTag().contains(tag);
            }
        }catch (Exception e){
            LogUtil.exception(e);
        }

        return false;
    }

    //查询单张图片详情
    @RequestMapping(value = {"/tag/queryDetail.do"}, method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    public String queryByKey(String imageName) throws Exception {

        Result<ImageRecordDO> result = new Result<>();
        try {

            ImgRecord imgRecord = queryImageWithKey(imageName);

            ImageRecordDO resultDo = new ImageRecordDO(imgRecord);

            //查询标注信息
            resultDo.setMarkImgRecord(markedImgController.queryMarketRecordByFileName(imageName));

            if (resultDo != null) {
                result.setData(resultDo);
            }

            result.setSuccess(true);

        } catch (Exception e) {
            LogUtil.exception(e);
        }

        return JSON.toJSONString(result);
    }

    @RequestMapping(value = {"/tag/insertTagToImage.do"}, method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    public String addTagToImage(String imageName, String filePath, String tag) throws Exception {

        Result<ImgRecord> result = insertTagToImage(imageName, filePath, tag);

        return JSON.toJSONString(result);
    }

    public Result<ImgRecord> insertTagToImage(String fileName,
                                              String filePath,
                                              String tag) throws Exception {

        ImgRecord imageRecrod = queryImageWithKey(fileName);

        if (imageRecrod == null) {

            imageRecrod = new ImgRecord();
            imageRecrod.setFileName(fileName);
            imageRecrod.setImgFilePath(filePath);
            imageRecrod.getTag().add(tag);

        } else {

            imageRecrod.setImgFilePath(filePath);
            imageRecrod.getTag().add(tag);
        }

        Result<ImgRecord> result = new Result<>();

        result.setData(imageRecrod);

        result.setSuccess(SleepCatRecordService.DefaultServie.getImgRecordSaver().put(imageRecrod));

        return result;
    }

    @RequestMapping(value = {"/tag/removeTagToImage.do"}, method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    public String removeTagToImage(String imageName,String tag) throws Exception {

        ImgRecord imageRecrod = queryImageWithKey(imageName);


        Result<ImgRecord> result = new Result<>();


        if (imageRecrod != null) {

            imageRecrod.getTag().remove(tag);

            result.setData(imageRecrod);

            result.setSuccess(SleepCatRecordService.DefaultServie.getImgRecordSaver().put(imageRecrod));

        }else{
            result.setInfo("没有找到记录:"+imageName);
        }

        return JSON.toJSONString(result);
    }




    @RequestMapping(value = {"/tag/queryList.do"}, method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @ResponseBody
    public String queryList(String tag,int index,int size) throws Exception {

        Result<List<ImageRecordDO>> result = new Result<>();

        List<ImgRecord> imgRecordList = SleepCatRecordService.DefaultServie.getImgRecordSaver().getSecondaryIndexByKey(tag,index,size);

        List<ImageRecordDO> resultList = new ArrayList<>();

        if(imgRecordList!=null){

//            result.setData(imgRecordList);
            for(ImgRecord imgRecord:imgRecordList){

                ImageRecordDO imageRecordDO = new ImageRecordDO(imgRecord);

                imageRecordDO.getTag().add(tag);
                imageRecordDO.setIsMarkByTag(true);
                imageRecordDO.setFolderName(imgRecord.getFileName());
                imageRecordDO.setIsFile(true);
                imageRecordDO.setImageName(imgRecord.getFileName());
                imageRecordDO.setInfo(imgRecord.getImgFilePath());
                imageRecordDO.setThumbnail(FileSaveService.OriginFileToThumbnail(imgRecord.getImgFilePath()));
                imageRecordDO.setMarkImgRecord(markedImgController.queryMarketRecordByFileName(imgRecord.getFileName()));
                resultList.add(imageRecordDO);
            }

            result.setData(resultList);

            result.setTotal(SleepCatRecordService.DefaultServie.getImgRecordSaver().getSecondaryIndexTotalCountByKey(tag));
        }

        return JSON.toJSONString(result);
    }
}
