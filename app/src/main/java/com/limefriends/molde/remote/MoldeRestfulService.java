package com.limefriends.molde.remote;

import com.limefriends.molde.entity.comment.CommentResponseInfoEntityList;
import com.limefriends.molde.entity.comment.ReportedCommentResponseInfoEntityList;
import com.limefriends.molde.entity.faq.FaqResponseInfoEntityList;
import com.limefriends.molde.entity.favorite.FavoriteResponseInfoEntityList;
import com.limefriends.molde.entity.feed.FeedResponseInfoEntity;
import com.limefriends.molde.entity.feed.FeedResponseInfoEntityList;
import com.limefriends.molde.entity.news.CardNewsResponseInfoEntityList;
import com.limefriends.molde.entity.response.Result;
import com.limefriends.molde.entity.scrap.ScrapResponseInfoEntityList;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface MoldeRestfulService {

    interface CardNews {

        @GET(MoldeRestfulApi.CardNews.GET_NEWS_API)
        Call<CardNewsResponseInfoEntityList> getCardNewsList(@Query("perPage") int perPage,
                                                             @Query("page") int page);

        @GET(MoldeRestfulApi.CardNews.GET_NEWS_BY_ID_API)
        Call<CardNewsResponseInfoEntityList> getCardNewsListById(@Query("cardNewsId") int newsId);
    }

    interface Feed {

        // 현재 위치 1.5 반경 피드
        @GET(MoldeRestfulApi.Feed.GET_FEED_API)
        Call<FeedResponseInfoEntityList> getFeedByDistance(@Query("reportLat") double lat,
                                                           @Query("reportLng") double lng);

        // 내가 작성한 피드
        @GET(MoldeRestfulApi.Feed.GET_MY_FEED_API)
        Call<FeedResponseInfoEntityList> getMyFeed(@Query("userId") String userId,
                                                   @Query("perPage") int perPage,
                                                   @Query("page") int page);

        @GET(MoldeRestfulApi.Feed.GET_FEED_BY_DATE_API)
        Call<FeedResponseInfoEntityList> getPagedFeedByDate(@Query("perPage") int perPage,
                                                            @Query("page") int page);

        @GET(MoldeRestfulApi.Feed.GET_FEED_BY_LOCATION_API)
        Call<FeedResponseInfoEntityList> getPagedFeedByDistance(@Query("reportLat") double lat,
                                                                @Query("reportLng") double lng,
                                                                @Query("perPage") int perPage,
                                                                @Query("page") int page);

        @GET(MoldeRestfulApi.Feed.GET_FEED_BY_ID_API)
        Call<FeedResponseInfoEntityList> getFeedById(@Query("reportId") int reportId);

        @Multipart
        @POST(MoldeRestfulApi.Feed.POST_FEED_API)
        Call<Result> reportNewFeed(@Part List<MultipartBody.Part> imageFiles,
                                   @PartMap Map<String, FeedResponseInfoEntity> feed);

//        @Multipart
//        @POST(MoldeRestfulApi.Feed.POST_FEED_API)
//        Call<Result> reportNewFeed2(
//                @Part("userId") String userId,
//                @Part("userName") String userName,
//                @Part("reportState") int reportState,
//                @Part("reportContent") String reportContent,
//                @Part("reportAddress") String reportAddress,
//                @Part("reportDetailAddress") String reportDetailAddress,
//                @Part("userEmail") String userEmail,
//                @Part("reportLat") double reportLat,
//                @Part("reportLng") double reportLng
//               // @Part("reportDate") long reportDate
//                //@Part MultipartBody.Part reportImageList
////                @Part List<MultipartBody.Part> reportImageList
//        );

        @Multipart
        @POST(MoldeRestfulApi.Feed.POST_FEED_API)
        Call<Result> reportNewFeed3(
                @Part("userId") String userId,
                @Part("userName") String userName,
                @Part("userEmail") String userEmail,
                @Part("reportContent") String reportContent,
                @Part("reportLat") double reportLat,
                @Part("reportLng") double reportLng,
                @Part("reportAddress") String reportAddress,
                @Part("reportDetailAddress") String reportDetailAddress,
                @Part("reportState") int reportState,
                @Part("reportDate") long reportDate,
                //@Part MultipartBody.Part reportImageList
                @Part List<MultipartBody.Part> reportImageList
        );

        @FormUrlEncoded
        @PUT(MoldeRestfulApi.Feed.UPDATE_FEED_API)
        Call<Result> updateFeed(@Field("reportId") int reportId,
                                @Field("reportState") int state);

        @FormUrlEncoded
        @HTTP(method = "DELETE", path = MoldeRestfulApi.Feed.DELETE_FEED_API, hasBody = true)
        Call<Result> deleteFeed(@Field("userId") String userId,
                                @Field("reportId") int state);
    }

    interface Comment {

        /**
         * 원래는 news_id 가 있어야 하는데 테스트 결과 어떤 경우에도 모든 데이터를 넘겨줌
         *
         * @return
         */
        @GET(MoldeRestfulApi.Comment.GET_COMMENT_BY_NEWSID_API)
        Call<CommentResponseInfoEntityList> getNewsComment(@Query("cardNewsId") int newsId,
                                                           @Query("perPage") int perPage,
                                                           @Query("page") int page);

        @GET(MoldeRestfulApi.Comment.GET_COMMENT_BY_USERID_API)
        Call<CommentResponseInfoEntityList> getMyComment(@Query("commentUserId") String userId,
                                                         @Query("perPage") int perPage,
                                                         @Query("page") int page);

        @GET(MoldeRestfulApi.Comment.GET_REPORTED_COMMENT_API)
        Call<ReportedCommentResponseInfoEntityList> getReportedComment(
                @Query("perPage") int perPage,
                @Query("page") int page);

        @GET(MoldeRestfulApi.Comment.GET_COMMENT_BY_COMMENTID_API)
        Call<CommentResponseInfoEntityList> getReportedCommentDetail(
                @Query("commentId") int commentId);

        @FormUrlEncoded
        @POST(MoldeRestfulApi.Comment.POST_COMMENT_API)
        Call<Result> createNewComment(@Field("userId") String userId,
                                      @Field("userName") String userName,
                                      @Field("cardNewsId") int newsId,
                                      @Field("commentContent") String content,
                                      @Field("commentRegiDate") String regiDate);

        @FormUrlEncoded
        @PUT(MoldeRestfulApi.Comment.PUT_COMMENT_API)
        Call<Result> reportComment(@Field("commentUserId") String uId,
                                   @Field("commentId") int commentId);

        @FormUrlEncoded
        @HTTP(method = "DELETE", path = MoldeRestfulApi.Comment.DELETE_COMMENT_API, hasBody = true)
        Call<Result> deleteComment(@Field("commentUserId") String commentUserId,
                                   @Field("commentId") int commentId);

        @FormUrlEncoded
        @HTTP(method = "DELETE", path = MoldeRestfulApi.Comment.DELETE_COMMENT_API, hasBody = true)
        Call<Result> deleteReportedComment(@Field("commentId") int commentUserId);
    }

    interface Faq {

        @GET(MoldeRestfulApi.Faq.GET_FAQ_API)
        Call<FaqResponseInfoEntityList> getMyFaqList();

        @FormUrlEncoded
        @POST(MoldeRestfulApi.Faq.POST_FAQ_API)
        Call<Result> createNewFaq(@Field("userId") String userId,
                                  @Field("userName") String userName,
                                  @Field("faqContent") String content,
                                  @Field("userEmail") String email);
    }

    interface Scrap {

        @GET(MoldeRestfulApi.Scrap.GET_SCRAP_API)
        Call<ScrapResponseInfoEntityList> getMyScrapList(@Query("userId") String userId,
                                                         @Query("perPage") int perPage,
                                                         @Query("page") int page);

        @GET(MoldeRestfulApi.Scrap.GET_SCRAP_BY_ID_API)
        Call<Result> getMyScrap(@Query("userId") String userId,
                                @Query("cardNewsId") int cardNewsId);

        @FormUrlEncoded
        @POST(MoldeRestfulApi.Scrap.POST_SCRAP_API)
        Call<Result> addToMyScrap(@Field("userId") String userId,
                                  @Field("cardNewsId") int newsId);

        @FormUrlEncoded
        @HTTP(method = "DELETE", path = MoldeRestfulApi.Scrap.DELETE_SCRAP_API, hasBody = true)
        Call<Result> deleteMyScrap(@Field("userId") String userId,
                                   @Field("cardNewsScrapId") int newsId);
    }


    interface Favorite {

        @GET(MoldeRestfulApi.Favorite.GET_FAVORITE_API)
        Call<FavoriteResponseInfoEntityList> getMyFavorite(@Query("userId") String userId,
                                                           @Query("perPage") int perPage,
                                                           @Query("page") int page);

        @FormUrlEncoded
        @POST(MoldeRestfulApi.Favorite.POST_FAVORITE_API)
        Call<Result> addToMyFavorite(@Field("userId") String userId,
                                     @Field("favoriteName") String name,
                                     @Field("favoriteAddress") String address,
                                     @Field("favoriteLat") double lat,
                                     @Field("favoriteLng") double lng);

        @FormUrlEncoded
        @HTTP(method = "DELETE", path = MoldeRestfulApi.Favorite.DELETE_FAVORITE_API, hasBody = true)
        Call<Result> deleteFavorite(@Field("userId") String userId,
                                    @Field("favoriteId") int favoriteId);
    }

}
