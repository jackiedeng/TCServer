package com.springapp.mvc;

import com.alibaba.fastjson.JSON;
import com.sleepycat.persist.model.Entity;
import com.starts.storage.SleepCatRecordService;
import com.starts.storage.entity.MarkTagRecord;
import com.starts.util.LogUtil;
import com.starts.util.Result;
import com.starts.util.SleepCatSaveHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by chaodeng on 2018/11/17.
 */
@Controller
@CrossOrigin
@RequestMapping(produces = "text/html;charset=UTF-8")
public class CommonQueryController {


    //增删改查
    private SleepCatSaveHelper getSleepCatHelperByType(String type) {

        if(StringUtils.equals(type,"markTag")){
            return SleepCatRecordService.DefaultServie.getMarkTagRecordSaver();
        }else if(StringUtils.equals(type,"markImg")){
            return SleepCatRecordService.DefaultServie.getMarkImgRecordSaver();
        }

        return null;
    }


    @RequestMapping(value="/request/common/queryAll.do")
    @ResponseBody
    public String queryAll(String type) {

        Result<List<Object>> result = new Result<>();

        try {

            SleepCatSaveHelper commonSaver = this.getSleepCatHelperByType(type);

            if(commonSaver != null) {

                List<Object> resultList = commonSaver.getListByIndex(0, -1);

                result.setData(resultList);

                result.setSuccess(true);
            }

        }catch (Exception e){
            result.setSuccess(false);
        }

        return JSON.toJSONString(result);
    }

    @RequestMapping(value="/request/markTag/update.do")
    @ResponseBody
    public String updateMarkTagRecord(String tagKey,String tagName,String tagColor,String ext) {

        Result<MarkTagRecord> result = new Result<>();

        try {

            MarkTagRecord markTagRecord = (MarkTagRecord) SleepCatRecordService.DefaultServie.getMarkTagRecordSaver().get(tagKey);

            if(markTagRecord == null){
                markTagRecord = new MarkTagRecord();
            }

            if(tagKey !=null) {
                markTagRecord.setTagKey(tagKey);
            }

            if(tagName != null){
                markTagRecord.setTagName(tagName);
            }

            if(tagColor !=null){
                markTagRecord.setTagColor(tagColor);
            }

            if(ext != null){
                markTagRecord.setExt(ext);
            }

            if(SleepCatRecordService.DefaultServie.getMarkTagRecordSaver().put(markTagRecord)){
                result.setData(markTagRecord);
                result.setSuccess(true);
            }

        }catch (Exception e){

            result.setSuccess(false);
            LogUtil.exception(e);
        }


        return JSON.toJSONString(result);
    }


    @RequestMapping(value="/request/markTag/delete.do")
    @ResponseBody
    public String deleteMarkTag(String tagName){

        Result<String> result = new Result<>();

        try{

            if(SleepCatRecordService.DefaultServie.getMarkTagRecordSaver().delete(tagName)){
                result.setSuccess(true);
            }

            result.setData(tagName);

        }catch (Exception e){
            LogUtil.exception(e);
        }

        return JSON.toJSONString(result);
    }



}
