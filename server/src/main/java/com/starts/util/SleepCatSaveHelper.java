package com.starts.util;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.starts.storage.SleepCatRecordService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackiedeng on 2018/11/10.
 */
public class SleepCatSaveHelper<PK,E> {

    private PrimaryIndex<PK, E> primaryIndex;

    private SecondaryIndex<PK,PK,E> secondaryIndex;

    public SleepCatSaveHelper(PrimaryIndex<PK, E> primaryIndex){
        this.primaryIndex = primaryIndex;
    }

    public SleepCatSaveHelper(PrimaryIndex<PK, E> primaryIndex, SecondaryIndex<PK, PK, E> secondaryIndex){
        this.primaryIndex = primaryIndex;
        this.secondaryIndex = secondaryIndex;
    }


    public E get(PK key) throws Exception {


        return primaryIndex.get(key);
    }

    public boolean delete(PK key){

        boolean result =  primaryIndex.delete(key);

        if(result){
            SleepCatRecordService.DefaultServie.sync();
        }


        return result;
    }

    public long getTotalCount(){

        return primaryIndex.count();
    }

    public List<E> getListByIndex(int index,int size){

        EntityCursor<E> sec_curor = primaryIndex.entities();

        long totalCount = primaryIndex.count();

        List<E> result = new ArrayList<>();

        try {
            int count = 0;
            long from = index;
            long to = size==-1?totalCount:(from+size);

            from = from > totalCount?totalCount:from;

            to = to > totalCount?totalCount:to;

            for (E seci : sec_curor) {

                if(count >= from && count < to){
//                    System.out.print("\nfind"+seci.getTag()+" "+seci.getFileName());
                    result.add(seci);
                }

                count++;
            }
        } finally {
            sec_curor.close(); }

        return result;
    }

    public long getSecondaryIndexTotalCountByKey(PK tag){

        long count = secondaryIndex.subIndex(tag).count();

        return count;
    }

    public List<E> getSecondaryIndexByKey(PK tag,int index,int size){

        EntityCursor<E> sec_curor = secondaryIndex.subIndex(tag).entities();

        long totalCount = secondaryIndex.subIndex(tag).count();

        List<E> result = new ArrayList<>();

        try {
            int count = 0;
            long from = index;
            long to = from+size;

            from = from > totalCount?totalCount:from;

            to = to > totalCount?totalCount:to;

            for (E seci : sec_curor) {

                if(count >= from && count < to){
//                    System.out.print("\nfind"+seci.getTag()+" "+seci.getFileName());
                    result.add(seci);
                }

                count++;
            }
        } finally {
            sec_curor.close(); }

        return result;
    }

    public long getCount(){

        return primaryIndex.count();
    }

    public boolean put(E value) throws Exception {

        try {
            primaryIndex.put(value);

            SleepCatRecordService.DefaultServie.sync();

            return  true;
        }catch (Exception e){
            LogUtil.exception(e);
            return  false;
        }

    }
}
