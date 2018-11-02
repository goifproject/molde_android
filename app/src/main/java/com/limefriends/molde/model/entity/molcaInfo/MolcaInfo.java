package com.limefriends.molde.model.entity.molcaInfo;

import com.limefriends.molde.model.entity.Data;
import com.limefriends.molde.model.entity.DataType;

public class MolcaInfo implements Data {

    private String title;
    private String content;
    private int image;

    public MolcaInfo(String title, String content, int image) {
        this.title = title;
        this.content = content;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Override
    public DataType getType() {
        return DataType.MOLCA_INFO;
    }
}
