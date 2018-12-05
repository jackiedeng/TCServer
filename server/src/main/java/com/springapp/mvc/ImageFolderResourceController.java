package com.springapp.mvc;

import com.alibaba.fastjson.JSON;
import com.springapp.mvc.outputDO.ImageRecordDO;
import com.starts.storage.FileSaveService;
import com.starts.storage.ResourcePool;
import com.starts.storage.SleepCatRecordService;
import com.starts.util.LogUtil;
import com.starts.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Jackie on 2018/11/11. //文件查询服务
 */
@Controller
@CrossOrigin
public class ImageFolderResourceController {

    @Autowired
    ImageTagServiceController imageTagServiceController;

    @RequestMapping(value = "/imageFolder/query.do", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String query(String path, int index, int size,String tag) {

        path = path == null?"/":path;

        size =index+size;

        String rootPath = FileSaveService.ImageFolderPath()+path;

        File pathFolder = new File(rootPath);

        Result<List<ImageRecordDO>> result = new Result<>();

        List<ImageRecordDO> resultList = new ArrayList<>();

        try {

            if (pathFolder.exists() && pathFolder.isDirectory()) {

                File[] files = pathFolder.listFiles();

                //按日期排序
                Arrays.sort(files, new Comparator<File>() {

                    public int compare(File f1, File f2) {
                        long diff = f2.lastModified() - f1.lastModified();
                        if (diff > 0)
                            return 1;
                        else if (diff == 0)
                            return 0;
                        else
                            return -1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
                    }

                    public boolean equals(Object obj) {
                        return true;
                    }

                });

                result.setTotal(files.length);

                if(size < 0 || size > files.length){
                    size = files.length;
                }

                if (index > size) {
                    index = size;
                }

                for (int i = index; i < size; i++) {

                    File subFile = files[i];

                    ImageRecordDO imageFolderDO = new ImageRecordDO();

                    if (subFile.isDirectory()) {
                        imageFolderDO.setIsFile(false);
                        imageFolderDO.setInfo(path+subFile.getName()+"/");
                    } else if (subFile.isFile()) {
                        imageFolderDO.setIsFile(true);
                        imageFolderDO.setInfo("/image/service"+path+subFile.getName());
                        imageFolderDO.setThumbnail("/thumbnailImage/service"+path+subFile.getName());
                        imageFolderDO.setImageName(subFile.getName());

                        if(tag!= null){

                            imageFolderDO.getTag().add(tag);
                            imageFolderDO.setIsMarkByTag(imageTagServiceController.isImageWithTag(subFile.getName(),tag));
                        }

                    } else {
                        continue;
                    }

                    imageFolderDO.setFolderName(subFile.getName());

                    resultList.add(imageFolderDO);
                }

            }

            result.setData(resultList);
            result.setSuccess(true);

        } catch (Exception e) {

            result.setSuccess(false);
            LogUtil.exception(e);
        }

        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/imageFolder/queryFolder.do", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String queryList() {

        return this.query(null,0,-1,null);
    }


    @RequestMapping(value="/imageFolder/queryTagDetail.do", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String queryTagDetail(String tagId) throws Exception {

        ResourcePool pool = null;

        Result<ResourcePool> result = new Result<>();

        try{

            pool = (ResourcePool) SleepCatRecordService.DefaultServie.getResourcePoolRecordSaver().get(tagId);

            if(pool != null) {
                result.setData(pool);

                result.setSuccess(true);
            }

        }catch (Exception e){

            LogUtil.exception(e);
        }

        return JSON.toJSONString(result);

    }
}
