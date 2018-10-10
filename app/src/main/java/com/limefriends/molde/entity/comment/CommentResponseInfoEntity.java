package com.limefriends.molde.entity.comment;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentResponseInfoEntity {

    @SerializedName("comm_id")
    @Expose(deserialize = false)
    private int commId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("news_id")
    @Expose
    private int newsId;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("comm_date")
    @Expose
    private String commDate;

    /**
     * POST 용
     */
    @SerializedName("card_news_id")
    @Expose(serialize = false)
    private String cardNewsId;
    @SerializedName("comment_content")
    @Expose(serialize = false)
    private String commentContent;
    @SerializedName("comment_regi_date")
    @Expose(serialize = false)
    private String commentRegiDate;

    /**
     * DELETE 용
     */
    @SerializedName("comment_user_id")
    @Expose(serialize = false)
    private String commentUserId;
    @SerializedName("comment_id")
    @Expose(serialize = false)
    private String commentId;

    public int getCommId() {
        return commId;
    }

    public void setCommId(int commId) {
        this.commId = commId;
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

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommDate() {
        return commDate;
    }

    public void setCommDate(String commDate) {
        this.commDate = commDate;
    }



//    @Override
//    public String toString() {
//        return new ToStringBuilder(this).append("commId", commId).append("userId", userId).append("userName", userName).append("newsId", newsId).append("comment", comment).append("commDate", commDate).toString();
//    }
//
//    @Override
//    public int hashCode() {
//        return new HashCodeBuilder().append(newsId).append(userId).append(userName).append(commDate).append(commId).append(comment).toHashCode();
//    }
//
//    @Override
//    public boolean equals(Object other) {
//        if (other == this) {
//            return true;
//        }
//        if ((other instanceof CommentEntity) == false) {
//            return false;
//        }
//        CommentResponseInfoEntity rhs = ((CommentResponseInfoEntity) other);
//        return new EqualsBuilder().append(newsId, rhs.newsId).append(userId, rhs.userId).append(userName, rhs.userName).append(commDate, rhs.commDate).append(commId, rhs.commId).append(comment, rhs.comment).isEquals();
//    }

}
