package com.limefriends.molde.menu_map.entity;

public class MoldeMyFavoriteEntity {
    private String myFavoriteTitle;
    private String myFavoriteInfo;
    private boolean myFavoriteToggle;

    public MoldeMyFavoriteEntity(String myFavoriteTitle, String myFavoriteInfo, boolean myFavoriteToggle) {
        this.myFavoriteTitle = myFavoriteTitle;
        this.myFavoriteInfo = myFavoriteInfo;
        this.myFavoriteToggle = myFavoriteToggle;
    }

    public String getMyFavoriteTitle() {
        return myFavoriteTitle;
    }

    public void setMyFavoriteTitle(String myFavoriteTitle) {
        this.myFavoriteTitle = myFavoriteTitle;
    }

    public String getMyFavoriteInfo() {
        return myFavoriteInfo;
    }

    public void setMyFavoriteInfo(String myFavoriteInfo) {
        this.myFavoriteInfo = myFavoriteInfo;
    }

    public boolean isMyFavoriteToggle() {
        return myFavoriteToggle;
    }

    public void setMyFavoriteToggle(boolean myFavoriteToggle) {
        this.myFavoriteToggle = myFavoriteToggle;
    }
}
