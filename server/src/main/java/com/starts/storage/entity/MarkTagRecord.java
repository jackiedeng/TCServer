package com.starts.storage.entity;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

/**
 * Created by chaodeng on 2018/11/17.
 */
@Entity(version=3)
public class MarkTagRecord {

    @PrimaryKey
    private String tagKey;

    private String tagName;

    @SecondaryKey(relate = Relationship.ONE_TO_ONE)
    private String color;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private String tagColor;

    private String ext;

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagColor() {
        return tagColor;
    }

    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
