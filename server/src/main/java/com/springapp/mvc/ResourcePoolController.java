package com.springapp.mvc;

import com.alibaba.fastjson.JSON;
import com.springapp.mvc.outputDO.ResourcePoolDO;
import com.starts.storage.ResourcePool;
import com.starts.storage.SleepCatRecordService;
import com.starts.util.LogUtil;
import com.starts.util.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackiedeng on 2018/11/10.
 */
@Controller
@CrossOrigin
@RequestMapping(produces = "text/html;charset=UTF-8")
public class ResourcePoolController {

    @RequestMapping(value="/request/pool/queryAll.do")
    @ResponseBody
    public String queryAll() {

        Result<List<ResourcePoolDO>> result = new Result<>();

        try {

            List<ResourcePool> resultList = SleepCatRecordService.DefaultServie.getResourcePoolRecordSaver().getListByIndex(0, -1);

            List<ResourcePoolDO> outputList = new ArrayList<>();

            for(ResourcePool pool:resultList){

                ResourcePoolDO poolDO = new ResourcePoolDO(pool);

                poolDO.setImgCount(SleepCatRecordService.DefaultServie
                        .getImgRecordSaver()
                        .getSecondaryIndexTotalCountByKey(pool.getPoolKey()));

                outputList.add(poolDO);
            }

            result.setData(outputList);
            result.setSuccess(true);

        }catch (Exception e){
            result.setSuccess(false);
        }


        return JSON.toJSONString(result);
    }


    @RequestMapping(value="/request/pool/add.do")
    @ResponseBody
    public String add(String poolKey,String poolName) throws Exception {

        Result<ResourcePool> result = new Result<>();

        ResourcePool newPool = new ResourcePool();
        newPool.setPoolKey(poolKey);
        newPool.setPoolName(poolName);

        result.setData(newPool);

        result.setSuccess(SleepCatRecordService.DefaultServie.getResourcePoolRecordSaver().put(newPool));

        SleepCatRecordService.DefaultServie.sync();

        return JSON.toJSONString(result);
    }

    @RequestMapping(value="/request/pool/delete.do")
    @ResponseBody
    public String del(String poolKey) throws Exception {

        Result<String > result = new Result<>();

        try {

            result.setData("delte poolkey:"+poolKey);

            result.setSuccess(SleepCatRecordService.DefaultServie.getResourcePoolRecordSaver().delete(poolKey));

            SleepCatRecordService.DefaultServie.sync();

        }catch (Exception e){
            LogUtil.exception(e);
        }

        return JSON.toJSONString(result);
    }
}
