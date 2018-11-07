package com.limefriends.molde.model.common;

import com.limefriends.molde.model.database.schema.SearchHistorySchema;
import com.limefriends.molde.model.entity.cardNews.CardNewsImageEntity;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.model.entity.feed.FeedImageEntity;
import com.limefriends.molde.model.entity.search.SearchInfoEntity;
import com.limefriends.molde.networking.schema.cardNews.CardNewsImageSchema;
import com.limefriends.molde.networking.schema.comment.CommentSchema;
import com.limefriends.molde.model.entity.comment.ReportedCommentEntity;
import com.limefriends.molde.networking.schema.comment.reported.ReportedCommentSchema;
import com.limefriends.molde.model.entity.faq.FaqEntity;
import com.limefriends.molde.networking.schema.faq.FaqSchema;
import com.limefriends.molde.model.entity.favorite.FavoriteEntity;
import com.limefriends.molde.networking.schema.favorite.FavoriteSchema;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.networking.schema.feed.FeedImageSchema;
import com.limefriends.molde.networking.schema.feed.FeedSchema;
import com.limefriends.molde.model.entity.feedResult.FeedResultEntity;
import com.limefriends.molde.networking.schema.feedResult.FeedResultSchema;
import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.networking.schema.cardNews.CardNewsSchema;
import com.limefriends.molde.model.entity.safehouse.SafehouseEntity;
import com.limefriends.molde.networking.schema.safehouse.SafehouseSchema;
import com.limefriends.molde.model.entity.scrap.ScrapEntity;
import com.limefriends.molde.networking.schema.scrap.ScrapSchema;
import com.limefriends.molde.networking.schema.search.SearchSchema;

import java.util.ArrayList;
import java.util.List;

public class FromSchemaToEntity {


    /**
     * 카드뉴스 리스트
     */

    public List<CardNewsEntity> cardNewsNS(List<CardNewsSchema> schemas) {

        if (schemas == null || schemas.size() == 0) return null;

        List<CardNewsEntity> entities = new ArrayList<>();
        for (CardNewsSchema schema : schemas) {
            entities.add(new CardNewsEntity(
                    schema.getNewsId(),
                    schema.getPostId(),
                    schema.getDescription(),
                    schema.getDate(),
                    cardNewsImage(schema.getNewsImg())
            ));
        }
        return entities;
    }

    public CardNewsEntity cardNewsNS(CardNewsSchema schema) {
        return new CardNewsEntity(
                schema.getNewsId(),
                schema.getPostId(),
                schema.getDescription(),
                schema.getDate(),
                cardNewsImage(schema.getNewsImg()),
                new ArrayList<>());
    }

    private List<CardNewsImageEntity> cardNewsImage(List<CardNewsImageSchema> schemas) {

        List<CardNewsImageEntity> entities = new ArrayList<>();
        for (CardNewsImageSchema schema : schemas) {
            entities.add(new CardNewsImageEntity(
                    schema.getPageNum(),
                    schema.getUrl()
            ));
        }
        return entities;
    }

    /**
     * 댓글
     */

    public List<CommentEntity> commentNS(List<CommentSchema> schemas) {

        if (schemas == null || schemas.size() == 0) return null;

        List<CommentEntity> entities = new ArrayList<>();
        for (CommentSchema schema : schemas) {
            entities.add(new CommentEntity(
                    schema.getCommId(),
                    schema.getUserId(),
                    schema.getUserName(),
                    schema.getNewsId(),
                    schema.getComment(),
                    schema.getCommDate()
            ));
        }
        return entities;
    }

    public CommentEntity commentNS(CommentSchema entity) {
        return new CommentEntity(
                entity.getCommId(),
                entity.getUserId(),
                entity.getUserName(),
                entity.getNewsId(),
                entity.getComment(),
                entity.getCommDate());
    }

    public List<ReportedCommentEntity> reportedCommentNS(List<ReportedCommentSchema> schemas) {

        if (schemas == null || schemas.size() == 0) return null;

        List<ReportedCommentEntity> entities = new ArrayList<>();
        for (ReportedCommentSchema schema : schemas) {
            entities.add(new ReportedCommentEntity(
                    schema.getCommRepId(),
                    schema.getCommId(),
                    schema.getUserId(),
                    schema.getCommRepDate()
            ));
        }
        return entities;
    }

    /**
     * 피드
     */

    public List<FeedEntity> feedNS(List<FeedSchema> schemas) {

        if (schemas == null || schemas.size() == 0) return null;

        List<FeedEntity> data = new ArrayList<>();
        for (FeedSchema entity : schemas) {
            data.add(new FeedEntity(
                    entity.getRepId(),
                    entity.getUserName(),
                    entity.getUserEmail(),
                    entity.getUserId(),
                    entity.getRepContents(),
                    entity.getRepLat(),
                    entity.getRepLon(),
                    entity.getRepAddr(),
                    entity.getRepDetailAddr(),
                    entity.getRepDate(),
                    feedImage(entity.getRepImg()),
                    entity.getRepState()
            ));
        }
        return data;
    }

    private List<FeedImageEntity> feedImage(List<FeedImageSchema> schemas) {

        List<FeedImageEntity> entities = new ArrayList<>();
        for (FeedImageSchema schema : schemas) {
            entities.add(new FeedImageEntity(
                    schema.getFilename(),
                    schema.getFilepath(),
                    schema.getFilesize()
            ));
        }
        return entities;
    }

    /**
     * 스크랩
     */

    public List<ScrapEntity> scrapNS(List<ScrapSchema> schemas) {

        if (schemas == null || schemas.size() == 0) return null;

        List<ScrapEntity> entities = new ArrayList<>();
        for (ScrapSchema schema : schemas) {
            entities.add(new ScrapEntity(
                    schema.getScrapId(),
                    schema.getUserId(),
                    schema.getNewsId()
            ));
        }
        return entities;
    }


    /**
     * faq
     */

    public List<FaqEntity> faqNS(List<FaqSchema> schemas) {

        if (schemas == null || schemas.size() == 0) return null;

        List<FaqEntity> entities = new ArrayList<>();
        for (FaqSchema schema : schemas) {
            entities.add(new FaqEntity(
                    schema.getFaqId(),
                    schema.getUserId(),
                    schema.getUserName(),
                    schema.getFaqContents(),
                    schema.getFaqEmail()
            ));
        }
        return entities;
    }


    /**
     * favorite
     */

    public List<FavoriteEntity> favoriteNS(List<FavoriteSchema> schemas) {

        if (schemas == null || schemas.size() == 0) return null;

        List<FavoriteEntity> entities = new ArrayList<>();
        for (FavoriteSchema schema : schemas) {
            entities.add(new FavoriteEntity(
                    schema.getFavId(),
                    schema.getUserId(),
                    schema.getFavName(),
                    schema.getFavAddr(),
                    schema.getFavLat(),
                    schema.getFavLon(),
                    true
            ));
        }
        return entities;
    }


    /**
     * safehouse
     */

    public List<SafehouseEntity> safehouseNS(List<SafehouseSchema> schemas) {

        if (schemas == null || schemas.size() == 0) return null;

        List<SafehouseEntity> entities = new ArrayList<>();
        for (SafehouseSchema schema : schemas) {
            entities.add(new SafehouseEntity(
                    schema.getSafeId(),
                    schema.getSafeName(),
                    schema.getSafeAddress(),
                    schema.getSafePhone(),
                    schema.getSafeLat(),
                    schema.getSafeLon(),
                    schema.getDistance()
            ));
        }
        return entities;
    }


    /**
     * feedResult
     */

    public static List<FeedResultEntity> feedResult(List<FeedResultSchema> schemas) {

        List<FeedResultEntity> entities = new ArrayList<>();
        for (FeedResultSchema schema : schemas) {
            entities.add(new FeedResultEntity(
                    schema.getResultId(),
                    schema.getRepId(),
                    schema.getResultDate(),
                    schema.getResultImg()
            ));
        }
        return entities;
    }

    /**
     * feedResult
     */

    public List<SearchInfoEntity> searchHistory(List<SearchHistorySchema> schemas) {

        if (schemas == null || schemas.size() == 0) return null;

        List<SearchInfoEntity> entities = new ArrayList<>();
        for (SearchHistorySchema schema : schemas) {
            entities.add(new SearchInfoEntity(
                    schema.getUId(),
                    schema.getName(),
                    schema.getBizName(),
                    schema.getMainAddress(),
                    schema.getStreetAddress(),
                    schema.getLat(),
                    schema.getLng(),
                    true
            ));
        }
        return entities;
    }

    public SearchHistorySchema searchHistoryReverse(SearchInfoEntity entity) {

       return new SearchHistorySchema(
               entity.getId(),
               entity.getName(),
               entity.getBizName(),
               entity.getMainAddress(),
               entity.getStreetAddress(),
               entity.getMapLat(),
               entity.getMapLng());
    }

    public List<SearchHistorySchema> searchHistoryReverse(List<SearchInfoEntity> entities) {

        List<SearchHistorySchema> schemas = new ArrayList<>();
        for (SearchInfoEntity entity : entities) {
            schemas.add(new SearchHistorySchema(
                    entity.getId(),
                    entity.getName(),
                    entity.getBizName(),
                    entity.getMainAddress(),
                    entity.getStreetAddress(),
                    entity.getMapLat(),
                    entity.getMapLng())
            );
        }
        return schemas;
    }

    public List<SearchInfoEntity> searchInfo(List<SearchSchema> schemas) {

        if (schemas == null || schemas.size() == 0) return null;

        List<SearchInfoEntity> entities = new ArrayList<>();
        for (SearchSchema schema : schemas) {
            entities.add(convertSearchSchema(schema));
        }
        return entities;
    }

    private SearchInfoEntity convertSearchSchema(SearchSchema searchSchema) {

        String mainAddress = "";
        if (searchSchema.getSecondNo() == null || searchSchema.getSecondNo().trim().equals("")) {
            mainAddress =
                    searchSchema.getUpperAddrName().trim() + " " +
                    searchSchema.getMiddleAddrName().trim() + " " +
                    searchSchema.getLowerAddrName().trim() + " " +
                    searchSchema.getDetailAddrName().trim() + " " +
                    searchSchema.getFirstNo().trim();
        } else {
            mainAddress =
                    searchSchema.getUpperAddrName().trim() + " " +
                    searchSchema.getMiddleAddrName().trim() + " " +
                    searchSchema.getLowerAddrName().trim() + " " +
                    searchSchema.getDetailAddrName().trim() + " " +
                    searchSchema.getFirstNo().trim() + "-" +
                    searchSchema.getSecondNo().trim();
        }
        String streetAddress =
                searchSchema.getRoadName().trim() + " " +
                searchSchema.getFirstBuildNo().trim();

        return new SearchInfoEntity(
                    searchSchema.getName(),
                    searchSchema.getBizName(),
                    mainAddress,
                    streetAddress,
                    searchSchema.getNoorLat(),
                    searchSchema.getNoorLon(),
                    false);
    }

}
