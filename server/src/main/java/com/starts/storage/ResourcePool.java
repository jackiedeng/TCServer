package com.starts.storage;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * Created by jackiedeng on 2018/11/10.
 */
@Entity
public class ResourcePool {

    //资源池id
    @PrimaryKey
    private String poolKey;
    //池子名称
    private String poolName;

    public String getPoolKey() {
        return poolKey;
    }

    public void setPoolKey(String poolKey) {
        this.poolKey = poolKey;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

}
