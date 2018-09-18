package com.limefriends.molde.entity.faq;

public class FaqEntity {

    private int faqId;
    private String userId;
    private String userName;
    private String faqContents;
    private String faqEmail;

    public FaqEntity(int faqId, String userId, String userName, String faqContents, String faqEmail) {
        this.faqId = faqId;
        this.userId = userId;
        this.userName = userName;
        this.faqContents = faqContents;
        this.faqEmail = faqEmail;
    }

    public int getFaqId() {
        return faqId;
    }

    public void setFaqId(int faqId) {
        this.faqId = faqId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFaqContents() {
        return faqContents;
    }

    public void setFaqContents(String faqContents) {
        this.faqContents = faqContents;
    }

    public String getFaqEmail() {
        return faqEmail;
    }

    public void setFaqEmail(String faqEmail) {
        this.faqEmail = faqEmail;
    }
}
