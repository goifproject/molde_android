package com.limefriends.molde.networking.schema.faq;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaqSchema {

    @SerializedName("faq_id")
    @Expose(deserialize = false)
    private int faqId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("faq_contents")
    @Expose
    private String faqContents;
    @SerializedName("faq_email")
    @Expose
    private String faqEmail;

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

//    @Override
//    public String toString() {
//        return new ToStringBuilder(this).append("faqId", faqId).append("userId", userId).append("userName", userName).append("faqContents", faqContents).append("faqEmail", faqEmail).toString();
//    }
//
//    @Override
//    public int hashCode() {
//        return new HashCodeBuilder().append(faqContents).append(userId).append(faqId).append(userName).append(faqEmail).toHashCode();
//    }
//
//    @Override
//    public boolean equals(Object other) {
//        if (other == this) {
//            return true;
//        }
//        if ((other instanceof FaqSchema) == false) {
//            return false;
//        }
//        FaqSchema rhs = ((FaqSchema) other);
//        return new EqualsBuilder().append(faqContents, rhs.faqContents).append(userId, rhs.userId).append(faqId, rhs.faqId).append(userName, rhs.userName).append(faqEmail, rhs.faqEmail).isEquals();
//    }


}
