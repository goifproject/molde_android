package com.limefriends.molde.screen.common.gallery;

public class PhotoVO {

    private String imgPath;
    private boolean selected;
    private int orientation;

    public PhotoVO(String imgPath, boolean selected) {
        this.imgPath = imgPath;
        this.selected = selected;
    }

    public PhotoVO(String imgPath, boolean selected, int orientation) {
        this.imgPath = imgPath;
        this.selected = selected;
        this.orientation = orientation;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
