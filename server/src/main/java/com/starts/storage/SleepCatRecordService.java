package com.starts.storage;

import com.sleepycat.je.*;
import com.sleepycat.persist.*;
import com.starts.storage.entity.MarkImgRecord;
import com.starts.storage.entity.MarkTagRecord;
import com.starts.util.LogUtil;
import com.starts.util.SleepCatSaveHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Created by Jackie on 2018/11/8.
 */
@Service
public class SleepCatRecordService {

    private Environment env;
    private Database db;
    private EntityStore entityStore;

    //图片记录
    private SleepCatSaveHelper imgRecordSaver;
    //清洗池记录
    private SleepCatSaveHelper resourcePoolRecordSaver;
    //标注标签管理
    private SleepCatSaveHelper markTagRecordSaver;
    //标注图片管理
    private SleepCatSaveHelper markImgRecordSaver;

    public static SleepCatRecordService DefaultServie = null;

    static final String dbName = "starsdb";

    public SleepCatRecordService() {

        SleepCatRecordService.DefaultServie = this;
    }

    public void setUp(String path, long cacheSize) {

        if (entityStore != null) {
            entityStore.close();
        }

        if (env != null) {
            env.close();
        }

        File filePath = new File(path);
        if (!filePath.exists()) {
            filePath.mkdir();
        }

        //set up env
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        envConfig.setCacheSize(cacheSize);

        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setAllowCreate(true);
        storeConfig.setDeferredWrite(true);

        try {

            env = new Environment(new File(path), envConfig);

            entityStore = new EntityStore(env, "EntityStore", storeConfig);

            //init img save
            PrimaryIndex<String, ImgRecord> primaryIndex = entityStore.getPrimaryIndex(String.class, ImgRecord.class);

            SecondaryIndex<String, String, ImgRecord> secondaryIndex =
                    entityStore.getSecondaryIndex(primaryIndex, String.class, "tag");

            imgRecordSaver = new SleepCatSaveHelper<String,ImgRecord>(primaryIndex, secondaryIndex);

            //init resource pool save
            PrimaryIndex<String, ResourcePool> resourcePrimaryIndex = entityStore.getPrimaryIndex(String.class, ResourcePool.class);

            resourcePoolRecordSaver = new SleepCatSaveHelper<String,ResourcePool>(resourcePrimaryIndex);

            //init mark tag save
            PrimaryIndex<String, MarkTagRecord> markTagPrimaryIndex = entityStore.getPrimaryIndex(String.class, MarkTagRecord.class);

            markTagRecordSaver = new SleepCatSaveHelper<String,MarkTagRecord>(markTagPrimaryIndex);

            //init markImageRecord
            PrimaryIndex<String, MarkImgRecord> markImgPrimaryIndex = entityStore.getPrimaryIndex(String.class, MarkImgRecord.class);

            SecondaryIndex<String, String, MarkImgRecord> markImgSecondaryIndex =
                    entityStore.getSecondaryIndex(markImgPrimaryIndex, String.class, "tag");

            markImgRecordSaver = new SleepCatSaveHelper<String,MarkImgRecord>(markImgPrimaryIndex, markImgSecondaryIndex);


        } catch (DatabaseException e) {
            e.printStackTrace();
            LogUtil.exception(e);
        }

    }

    public void sync(){
        entityStore.sync();
    }

    public void setUp(long cacheSize) {

        String path = FileSaveService.storageFolderName + "/metadatas";

        setUp(path, cacheSize);

    }


    public void close() {
        try {
            if (db != null) {
                db.close();
            }
            if (entityStore != null) {
                entityStore.close();
            }
            if (env != null) {
                env.close();
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {


        System.out.print("hello");

        String path = "/Users/Jackie/Desktop/Playground/metadatas";
//
        SleepCatRecordService sleepCatRecordService = new SleepCatRecordService();
//
        sleepCatRecordService.setUp(path, 100000);

            //add rescoure pool
        for(int i = 0; i < 10; i++){

            ResourcePool pool = new ResourcePool();

            pool.setPoolKey("poolkey"+i);
            pool.setPoolName("poolname"+i);

            sleepCatRecordService.getResourcePoolRecordSaver().put(pool);
        }


        long count = sleepCatRecordService.getResourcePoolRecordSaver().getCount();
        List<ResourcePool> all = sleepCatRecordService.getResourcePoolRecordSaver().getListByIndex(0, (int) count);

        for(int i = 0; i < all.size(); i++){

            ResourcePool pool = all.get(i);

            System.out.print("\n name="+pool.getPoolName()+" key="+pool.getPoolKey());

        }

        System.out.print("total count=" + sleepCatRecordService.getResourcePoolRecordSaver().getTotalCount());

//        System.out.print("b count=" + sleepCatRecordService.getResourcePoolRecordSaver().getSecondaryIndexByKey("image.baidu.com_家具"));

        System.out.print("\nc count=" + sleepCatRecordService.getResourcePoolRecordSaver().getListByIndex(0, -1));
//
//        sleepCatRecordService.getByTagKey("a",8,5);
        System.out.print(">>>>>>>>");
//        sleepCatRecordService.getByTagKey("b",6,5);

        sleepCatRecordService.close();
    }


    public SleepCatSaveHelper getImgRecordSaver() {
        return imgRecordSaver;
    }

    public void setImgRecordSaver(SleepCatSaveHelper imgRecordSaver) {
        this.imgRecordSaver = imgRecordSaver;
    }

    public SleepCatSaveHelper getResourcePoolRecordSaver() {
        return resourcePoolRecordSaver;
    }

    public void setResourcePoolRecordSaver(SleepCatSaveHelper resourcePoolRecordSaver) {
        this.resourcePoolRecordSaver = resourcePoolRecordSaver;
    }

    public SleepCatSaveHelper getMarkImgRecordSaver() {
        return markImgRecordSaver;
    }

    public void setMarkImgRecordSaver(SleepCatSaveHelper markImgRecordSaver) {
        this.markImgRecordSaver = markImgRecordSaver;
    }

    public SleepCatSaveHelper getMarkTagRecordSaver() {
        return markTagRecordSaver;
    }

    public void setMarkTagRecordSaver(SleepCatSaveHelper markTagRecordSaver) {
        this.markTagRecordSaver = markTagRecordSaver;
    }
}
