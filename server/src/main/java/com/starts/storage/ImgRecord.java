package com.starts.storage;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Jackie on 2018/11/8.
 */
@Entity(version = 1)
public class ImgRecord {

    @PrimaryKey
    private String fileName;

    private String imgFilePath;

    @SecondaryKey(relate = Relationship.MANY_TO_MANY)
    private Set<String> tag = new HashSet<>();

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getImgFilePath() {
        return imgFilePath;
    }

    public void setImgFilePath(String imgFilePath) {
        this.imgFilePath = imgFilePath;
    }

    public Set<String> getTag() {
        return tag;
    }

    public void setTag(Set<String> tag) {
        this.tag = tag;
    }
}
