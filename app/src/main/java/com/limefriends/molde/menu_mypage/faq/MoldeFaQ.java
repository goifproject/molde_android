package com.limefriends.molde.menu_mypage.faq;

public class MoldeFaQ {
    private String user_id;
    private String sns_id;
    private String faq_contents;
    private String faq_email;

    public MoldeFaQ(String user_id, String sns_id, String faq_contents, String faq_email) {
        this.user_id = user_id;
        this.sns_id = sns_id;
        this.faq_contents = faq_contents;
        this.faq_email = faq_email;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getSns_id() {
        return sns_id;
    }

    public String getFaq_contents() {
        return faq_contents;
    }

    public String getFaq_email() {
        return faq_email;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setSns_id(String sns_id) {
        this.sns_id = sns_id;
    }

    public void setFaq_contents(String faq_contents) {
        this.faq_contents = faq_contents;
    }

    public void setFaq_email(String faq_email) {
        this.faq_email = faq_email;
    }
}
