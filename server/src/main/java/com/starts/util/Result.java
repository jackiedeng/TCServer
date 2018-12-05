package com.starts.util;

import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * Created by jackiedeng on 2018/11/10.
 */
public class Result<T> {

    private T data;
    private boolean success = false;
    private long total;
    private String info;

    private Map<String,Object> ext;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Map<String, Object> getExt() {
        return ext;
    }

    public void setExt(Map<String, Object> ext) {
        this.ext = ext;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}
