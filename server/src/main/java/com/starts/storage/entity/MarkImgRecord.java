package com.starts.storage.entity;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chaodeng on 2018/11/17.
 */
@Entity(version = 1)
public class MarkImgRecord {

    @PrimaryKey
    private String fileName;

    @SecondaryKey(relate = Relationship.MANY_TO_MANY)
    private Set<String> tag = new HashSet<>();

    //全路径
    private String fileFullPath;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Set<String> getTag() {
        return tag;
    }

    public void setTag(Set<String> tag) {
        this.tag = tag;
    }


    public String getFileFullPath() {
        return fileFullPath;
    }

    public void setFileFullPath(String fileFullPath) {
        this.fileFullPath = fileFullPath;
    }
}
