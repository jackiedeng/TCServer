package com.springapp.mvc.outputDO;

import com.starts.storage.ImgRecord;
import com.starts.storage.entity.MarkImgRecord;

/**
 * Created by Jackie on 2018/11/11.
 */
public class ImageRecordDO extends ImgRecord{

    private String folderName;
    private String info;
    private String thumbnail;
    private String imageName;
    private boolean isFile;
    private boolean isMarkByTag;
    private MarkImgRecord markImgRecord;

    public ImageRecordDO(){
        super();
    }

    public ImageRecordDO(ImgRecord imgRecord){

        super();

        this.setFileName(imgRecord.getFileName());
        this.setImgFilePath(imgRecord.getImgFilePath());
        this.setTag(imgRecord.getTag());
    }

    public boolean isFile() {
        return isFile;
    }

    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isMarkByTag() {
        return isMarkByTag;
    }

    public void setIsMarkByTag(boolean isMarkByTag) {
        this.isMarkByTag = isMarkByTag;
    }


    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public MarkImgRecord getMarkImgRecord() {
        return markImgRecord;
    }

    public void setMarkImgRecord(MarkImgRecord markImgRecord) {
        this.markImgRecord = markImgRecord;
    }
}
