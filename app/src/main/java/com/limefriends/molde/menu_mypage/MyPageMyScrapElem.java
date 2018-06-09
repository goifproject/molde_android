package com.limefriends.molde.menu_mypage;

/**
 * Created by user on 2018-05-19.
 */

public class MyPageMyScrapElem {
    int myScrap_image;
    String myScrap_text;

    public MyPageMyScrapElem(int myScrap_image, String myScrap_text) {
        this.myScrap_image = myScrap_image;
        this.myScrap_text = myScrap_text;
    }

    public int getMyScrap_image() {
        return myScrap_image;
    }

    public void setMyScrap_image(int myScrap_image) {
        this.myScrap_image = myScrap_image;
    }

    public String getMyScrap_text() {
        return myScrap_text;
    }

    public void setMyScrap_text(String myScrap_text) {
        this.myScrap_text = myScrap_text;
    }
}
