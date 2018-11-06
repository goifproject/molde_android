package com.limefriends.molde.networking.service;


import com.limefriends.molde.networking.schema.comment.CommentResponseSchema;
import com.limefriends.molde.networking.schema.comment.reported.ReportedCommentResponseSchema;
import com.limefriends.molde.networking.schema.faq.FaqResponseSchema;
import com.limefriends.molde.networking.schema.favorite.FavoriteResponseSchema;
import com.limefriends.molde.networking.schema.feed.FeedResponseSchema;
import com.limefriends.molde.networking.schema.cardNews.CardNewsResponseSchema;
import com.limefriends.molde.networking.schema.response.Result;
import com.limefriends.molde.networking.schema.safehouse.SafehouseResponseSchema;
import com.limefriends.molde.networking.schema.scrap.ScrapResponseSchema;

import java.util.List;

import io.reactivex.Observable;
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
import retrofit2.http.Query;

public interface MoldeRestfulService {


    /**
     * CardNews
     */

    interface CardNews {

        @GET(MoldeRestfulApi.CardNews.GET_NEWS_API)
        Observable<CardNewsResponseSchema> getCardNewsListObservable(@Query("perPage") int perPage,
                                                                     @Query("page") int page);

        @GET(MoldeRestfulApi.CardNews.GET_NEWS_BY_ID_API)
        Observable<CardNewsResponseSchema> getCardNewsByIdObservable(@Query("cardNewsId") int newsId);
    }

    /**
     * Feed
     */

    interface Feed {

        @GET(MoldeRestfulApi.Feed.GET_MY_FEED_API)
        Observable<FeedResponseSchema> getMyFeedObservable(@Query("userId") String userId,
                                                           @Query("perPage") int perPage,
                                                           @Query("page") int page);

        @GET(MoldeRestfulApi.Feed.GET_FEED_BY_DATE_API)
        Observable<FeedResponseSchema> getPagedFeedByDateObservable(@Query("perPage") int perPage,
                                                                    @Query("page") int page);

        @GET(MoldeRestfulApi.Feed.GET_FEED_BY_LOCATION_API)
        Observable<FeedResponseSchema> getPagedFeedByDistanceObservable(@Query("reportLat") double lat,
                                                                        @Query("reportLng") double lng,
                                                                        @Query("perPage") int perPage,
                                                                        @Query("page") int page);

        @GET(MoldeRestfulApi.Feed.GET_FEED_BY_ID_API)
        Call<FeedResponseSchema> getFeedById(@Query("reportId") int reportId);

        @GET(MoldeRestfulApi.Feed.GET_FEED_BY_ID_API)
        Observable<FeedResponseSchema> getFeedByIdObservable(@Query("reportId") int reportId);

        @Multipart
        @POST(MoldeRestfulApi.Feed.POST_FEED_API)
        Observable<Result> reportNewFeedObservable(
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
                @Part List<MultipartBody.Part> reportImageList
        );

        @FormUrlEncoded
        @PUT(MoldeRestfulApi.Feed.UPDATE_FEED_API)
        Observable<Result> updateFeedObservable(@Field("reportId") int reportId,
                                                @Field("reportState") int state);

        @FormUrlEncoded
        @HTTP(method = "DELETE", path = MoldeRestfulApi.Feed.DELETE_FEED_API, hasBody = true)
        Observable<Result> deleteFeedObservable(@Field("userId") String userId,
                                                @Field("reportId") int state);
    }


    /**
     * FeedResult
     */

    interface FeedResult {

        @Multipart
        @POST(MoldeRestfulApi.FeedResult.POST_FEED_RESULT_API)
        Observable<Result> reportFeedResultObservable(@Part("reportId") int reportId,
                                                      @Part List<MultipartBody.Part> reportImageList
        );
    }


    /**
     * Comment
     */

    interface Comment {

        /**
         * 원래는 news_id 가 있어야 하는데 테스트 결과 어떤 경우에도 모든 데이터를 넘겨줌
         */
        @GET(MoldeRestfulApi.Comment.GET_COMMENT_BY_NEWSID_API)
        Observable<CommentResponseSchema> getNewsCommentObservable(@Query("cardNewsId") int newsId,
                                                                   @Query("perPage") int perPage,
                                                                   @Query("page") int page);

        @GET(MoldeRestfulApi.Comment.GET_COMMENT_BY_USERID_API)
        Observable<CommentResponseSchema> getMyCommentObservable(@Query("commentUserId") String userId,
                                                                 @Query("perPage") int perPage,
                                                                 @Query("page") int page);

        @GET(MoldeRestfulApi.Comment.GET_REPORTED_COMMENT_API)
        Observable<ReportedCommentResponseSchema> getReportedCommentObservable(@Query("perPage") int perPage,
                                                                               @Query("page") int page);

        @GET(MoldeRestfulApi.Comment.GET_COMMENT_BY_COMMENTID_API)
        Observable<CommentResponseSchema> getReportedCommentDetailObservable(@Query("commentId") int commentId);

        @FormUrlEncoded
        @POST(MoldeRestfulApi.Comment.POST_COMMENT_API)
        Observable<Result> createNewCommentObservable(@Field("userId") String userId,
                                                      @Field("userName") String userName,
                                                      @Field("cardNewsId") int newsId,
                                                      @Field("commentContent") String content);

        @FormUrlEncoded
        @PUT(MoldeRestfulApi.Comment.PUT_COMMENT_API)
        Observable<Result> reportCommentObservable(@Field("commentUserId") String uId,
                                                   @Field("commentId") int commentId);

        @FormUrlEncoded
        @HTTP(method = "DELETE", path = MoldeRestfulApi.Comment.DELETE_COMMENT_API, hasBody = true)
        Observable<Result> deleteCommentObservable(@Field("commentId") int commentId);

        @FormUrlEncoded
        @HTTP(method = "DELETE", path = MoldeRestfulApi.Comment.DELETE_REPORTED_COMMENT_API, hasBody = true)
        Observable<Result> deleteReportedCommentObservable(@Field("commentId") int commentUserId);
    }


    /**
     * Faq
     */

    interface Faq {

        @GET(MoldeRestfulApi.Faq.GET_FAQ_API)
        Observable<FaqResponseSchema> getMyFaqListObservable();

        @FormUrlEncoded
        @POST(MoldeRestfulApi.Faq.POST_FAQ_API)
        Observable<Result> createNewFaqObservable(@Field("userId") String userId,
                                                  @Field("userName") String userName,
                                                  @Field("faqContent") String content,
                                                  @Field("userEmail") String email);
    }


    /**
     * Scrap
     */

    interface Scrap {

        @GET(MoldeRestfulApi.Scrap.GET_SCRAP_BY_ID_API)
        Observable<ScrapResponseSchema> getMyScrapObservable(@Query("userId") String userId,
                                                             @Query("cardNewsId") int cardNewsId);

        @GET(MoldeRestfulApi.Scrap.GET_SCRAP_API)
        Observable<ScrapResponseSchema> getMyScrapListObservable(@Query("userId") String userId,
                                                                 @Query("perPage") int perPage,
                                                                 @Query("page") int page);

        @FormUrlEncoded
        @POST(MoldeRestfulApi.Scrap.POST_SCRAP_API)
        Observable<Result> addToMyScrapObservable(@Field("userId") String userId,
                                                  @Field("cardNewsId") int newsId);

        @FormUrlEncoded
        @HTTP(method = "DELETE", path = MoldeRestfulApi.Scrap.DELETE_SCRAP_API, hasBody = true)
        Observable<Result> deleteMyScrapObservable(@Field("userId") String userId,
                                                   @Field("cardNewsScrapId") int newsId);
    }


    /**
     * Favorite
     */

    interface Favorite {

        @GET(MoldeRestfulApi.Favorite.GET_FAVORITE_API)
        Observable<FavoriteResponseSchema> getMyFavoriteObservable(@Query("userId") String userId,
                                                                   @Query("perPage") int perPage,
                                                                   @Query("page") int page);

        @GET(MoldeRestfulApi.Favorite.GET_FAVORITE_BY_LOCATION_API)
        Observable<FavoriteResponseSchema> getMyFavoriteByDistanceObservable(@Query("userId") String userId,
                                                                             @Query("favoriteLat") double lat,
                                                                             @Query("favoriteLng") double lng);

        @FormUrlEncoded
        @POST(MoldeRestfulApi.Favorite.POST_FAVORITE_API)
        Observable<Result> addToMyFavoriteObservable(@Field("userId") String userId,
                                                     @Field("favoriteName") String name,
                                                     @Field("favoriteAddress") String address,
                                                     @Field("favoriteLat") double lat,
                                                     @Field("favoriteLng") double lng);

        @FormUrlEncoded
        @HTTP(method = "DELETE", path = MoldeRestfulApi.Favorite.DELETE_FAVORITE_API, hasBody = true)
        Observable<Result> deleteFavoriteObservable(@Field("userId") String userId,
                                                    @Field("favoriteId") int favoriteId);
    }


    /**
     * Safehouse
     */

    interface Safehouse {

        @GET(MoldeRestfulApi.Safehouse.GET_SAFEHOUSE)
        Observable<SafehouseResponseSchema> getSafehouseObservable(@Query("safeLat") double safeLat,
                                                                   @Query("safeLng") double safeLng,
                                                                   @Query("perPage") int perPage,
                                                                   @Query("page") int page);
    }

}
