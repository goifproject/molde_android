package com.limefriends.molde.model.repository;

import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.model.entity.faq.FaqEntity;
import com.limefriends.molde.model.entity.favorite.FavoriteEntity;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.model.entity.safehouse.SafehouseEntity;
import com.limefriends.molde.model.entity.scrap.ScrapEntity;
import com.limefriends.molde.networking.schema.response.Result;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.Query;


/**
 * 리스트를 반환받는 것보다 데이터의 흐름을 정의하고 받는 곳에서 어떻게 사용할지 결정하면 되는 것 같다
 */

public interface Repository {


    /**
     * 카드뉴스
     */

    interface CardNews {

        // 매거진 카드뉴스 목록 가져오기
        Observable<List<CardNewsEntity>> getCardNewsList(int perPage, int page);

        // 매거진 카드뉴스 디테일 가져오기
        Observable<List<CardNewsEntity>> getCardNewsListById(int newsId);
    }


    /**
     * 스크랩
     */

    interface Scrap {

        // 스크랩 한 개 가져오기
        Observable<ScrapEntity> getScrap(String userId, int cardNewsId);

        // 내 스크랩 목록 가져오기
        Observable<CardNewsEntity> getScrapList(String userId, int perPage, int currentPage);

        // 스크랩 추가하기
        Observable<Result> addToMyScrap(String userId, int newsId);

        // 스크랩 삭제하기
        Observable<Result> deleteMyScrap(String userId, int newsId);
    }


    /**
     * 댓글
     */

    interface Comment {

        // 뉴스에 달린 댓글 목록 가져오기
        Observable<List<CommentEntity>> getNewsComment(int newsId, int perPage, int page);

        // 내가 생성한 댓글 목록 가져오기
        Observable<CardNewsEntity> getMyComment(List<CardNewsEntity> lastEntities,
                                                String userId, int perPage, int page);

        // 신고된 댓글 목록 가져오기
        Observable<CommentEntity> getReportedComment(int perPage, int page);

        // 댓글 작성하기
        Observable<Result> createNewComment(String userId, String userName, int newsId, String content);

        // 댓글 신고하기
        Observable<Result> reportComment(String uId, int commentId);

        // 댓글 삭제하기
        Observable<Result> deleteComment(int commentId);

        // 신고된 댓글 삭제하기
        Observable<Result> deleteReportedComment(int commentUserId);
    }


    /**
     * 피드
     */

    interface Feed {

        // 내가 신고한 피드 목록 가져오기
        Observable<List<FeedEntity>> getMyFeed(String userId, int perPage, int page);

        // 날짜순으로 피드 목록 가져오기
        Observable<List<FeedEntity>> getPagedFeedByDate(int perPage, int page);

        // 거리순으로 피드 목록 가져오기
        Observable<List<FeedEntity>> getPagedFeedByDistance(double lat, double lng, int perPage, int page);

        // 피드 한 개 가져오기
        Observable<List<FeedEntity>> getFeedById(int reportId);

        // 신고하기(피드 생성)
        Observable<Result> reportNewFeed(String userId, String userName, String userEmail,
                                         String reportContent, double reportLat, double reportLng,
                                         String reportAddress, String reportDetailAddress,
                                         int reportState, long reportDate, List<MultipartBody.Part> reportImageList);

        // 피드 신고상태 변경하기
        Observable<Result> updateFeed(int reportId, int state);

        // 피드 삭제하기
        Observable<Result> deleteFeed(String userId, int state);
    }


    /**
     * 안심지역 신고
     */

    interface FeedResult {

        // 안심지역 신고하기
        Observable<Result> reportFeedResult(int reportId, List<MultipartBody.Part> reportImageList);
    }


    /**
     * 자주 묻는 질문
     */

    interface Faq {

        // 문의 목록 받아보기
        Observable<List<FaqEntity>> getFaqList();

        // 문의하기
        Observable<Result> createNewFaq(String userId, String userName, String content, String email);
    }


    /**
     * 즐겨찾기
     */

    interface Favorite {

        // 내 즐겨찾기 목록 가져오기
        Observable<List<FavoriteEntity>> getMyFavorite(@Query("userId") String userId,
                                                                 @Query("perPage") int perPage,
                                                                 @Query("page") int page);

        // 거리순으로 내 즐겨찾기 목록 가져오기
        Observable<FavoriteEntity> getMyFavoriteByDistance(@Query("userId") String userId,
                                                                           @Query("favoriteLat") double lat,
                                                                           @Query("favoriteLng") double lng);

        // 즐겨찾기 추가하기
        Observable<Result> addToMyFavorite(@Field("userId") String userId,
                                                     @Field("favoriteName") String name,
                                                     @Field("favoriteAddress") String address,
                                                     @Field("favoriteLat") double lat,
                                                     @Field("favoriteLng") double lng);

        // 즐겨찾기 삭제하기
        Observable<Result> deleteFavorite(@Field("userId") String userId,
                                                    @Field("favoriteId") int favoriteId);
    }


    /**
     * 안심지역
     */

    interface Safehouse {

        // 현재 위치에서 안심지역 리스트 가져오기
        Observable<SafehouseEntity> getSafehouse(double safeLat, double safeLng, int perPage, int page);
    }

}
