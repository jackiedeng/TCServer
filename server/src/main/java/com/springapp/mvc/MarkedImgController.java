package com.springapp.mvc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starts.storage.FileSaveService;
import com.starts.storage.SleepCatRecordService;
import com.starts.storage.entity.MarkImgRecord;
import com.starts.util.LogUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Created by chaodeng on 2018/11/18.
 */
//标记图片存储
@Controller
public class MarkedImgController {


    public MarkImgRecord queryMarketRecordByFileName(String fileName) throws Exception {

        try {
            return (MarkImgRecord)SleepCatRecordService.DefaultServie.getMarkImgRecordSaver().get(fileName);
        }catch (Exception e){
            LogUtil.exception(e);
        }

        return null;
    }


    public MarkImgRecord updateMarkImgRecord(String fileName,String fullPath,String tags){

        if(fileName == null)return null;

        try {

            MarkImgRecord imgRecord = this.queryMarketRecordByFileName(fileName);

            if(imgRecord == null){

                imgRecord = new MarkImgRecord();
            }

            imgRecord.setFileName(fileName);

            imgRecord.setFileFullPath(fullPath);

            if(tags != null){



                    JSONArray array = JSON.parseArray(tags);

                    Iterator<Object> iterator = array.iterator();

                    while(iterator.hasNext()){

                        JSONObject tagObject = (JSONObject)iterator.next();

                        if(tagObject.getString("tagKey") != null) {
                            imgRecord.getTag().add(tagObject.getString("tagKey"));
                        }
                    }
            }

            if(SleepCatRecordService.DefaultServie.getMarkImgRecordSaver().put(imgRecord)){
                return imgRecord;
            }

            return null;

        }catch (Exception e){
            LogUtil.exception(e);
        }

        return null;
    }


}
