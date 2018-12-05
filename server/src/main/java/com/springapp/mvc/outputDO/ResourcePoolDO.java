package com.springapp.mvc.outputDO;

import com.starts.storage.ResourcePool;

/**
 * Created by Jackie on 2018/11/10.
 */
public class ResourcePoolDO extends ResourcePool {


    public ResourcePoolDO(ResourcePool pool){
        super();
        this.setPoolKey(pool.getPoolKey());
        this.setPoolName(pool.getPoolName());
    }

    //图片总数
    private Long imgCount;

    public Long getImgCount() {
        return imgCount;
    }

    public void setImgCount(Long imgCount) {
        this.imgCount = imgCount;
    }

}
