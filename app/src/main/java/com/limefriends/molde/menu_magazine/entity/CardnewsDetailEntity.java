package com.limefriends.molde.menu_magazine.entity;

/**
 * Created by joo on 2018. 4. 19..
 */

public class CardnewsDetailEntity {
    private int image;
    private String description;

    public CardnewsDetailEntity(int image, String description) {
        this.image = image;
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
