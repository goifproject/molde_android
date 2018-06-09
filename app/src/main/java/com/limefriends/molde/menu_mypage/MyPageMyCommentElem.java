package com.limefriends.molde.menu_mypage;

/**
 * Created by user on 2018-05-19.
 */

public class MyPageMyCommentElem {
    String myComment_title;
    int myComment_image;
    String myComment_date;
    String myComment_comment;

    public MyPageMyCommentElem(String myComment_title, int myComment_image, String myComment_date, String myComment_comment) {
        this.myComment_title = myComment_title;
        this.myComment_image = myComment_image;
        this.myComment_date = myComment_date;
        this.myComment_comment = myComment_comment;
    }

    public String getMyComment_title() {
        return myComment_title;
    }

    public void setMyComment_title(String myComment_title) {
        this.myComment_title = myComment_title;
    }

    public int getMyComment_image() {
        return myComment_image;
    }

    public void setMyComment_image(int myComment_image) {
        this.myComment_image = myComment_image;
    }

    public String getMyComment_date() {
        return myComment_date;
    }

    public void setMyComment_date(String myComment_date) {
        this.myComment_date = myComment_date;
    }

    public String getMyComment_comment() {
        return myComment_comment;
    }

    public void setMyComment_comment(String myComment_comment) {
        this.myComment_comment = myComment_comment;
    }
}
