package com.limefriends.molde.entity;

import com.limefriends.molde.comm.Constant;
import com.limefriends.molde.entity.comment.CommentEntity;
import com.limefriends.molde.entity.comment.CommentResponseInfoEntity;
import com.limefriends.molde.entity.comment.ReportedCommentEntity;
import com.limefriends.molde.entity.comment.ReportedCommentResponseInfoEntity;
import com.limefriends.molde.entity.faq.FaqEntity;
import com.limefriends.molde.entity.faq.FaqResponseInfoEntity;
import com.limefriends.molde.entity.favorite.FavoriteEntity;
import com.limefriends.molde.entity.favorite.FavoriteResponseInfoEntity;
import com.limefriends.molde.entity.feed.FeedEntity;
import com.limefriends.molde.entity.feed.FeedResponseInfoEntity;
import com.limefriends.molde.entity.feedResult.FeedResultEntity;
import com.limefriends.molde.entity.feedResult.FeedResultResponseInfoEntity;
import com.limefriends.molde.entity.news.CardNewsEntity;
import com.limefriends.molde.entity.news.CardNewsResponseInfoEntity;
import com.limefriends.molde.entity.safehouse.SafehouseEntity;
import com.limefriends.molde.entity.safehouse.SafehouseResponseInfoEntity;
import com.limefriends.molde.entity.scrap.ScrapEntity;
import com.limefriends.molde.entity.scrap.ScrapResponseInfoEntity;

import java.util.ArrayList;
import java.util.List;

public class FromSchemaToEntitiy {


    /**
     * 카드뉴스 리스트
     */
    public static List<CardNewsEntity> cardNews(List<CardNewsResponseInfoEntity> schemas) {
        List<CardNewsEntity> entities = new ArrayList<>();
        for (CardNewsResponseInfoEntity schema : schemas) {
            entities.add(new CardNewsEntity(
                    schema.getNewsId(),
                    schema.getPostId(),
                    schema.getDescription(),
                    schema.getDate(),
                    schema.getNewsImg()
            ));
        }
        return entities;
    }


    /**
     * 카드뉴스
     */
    public static CardNewsEntity cardNews(CardNewsResponseInfoEntity schema) {
        return new CardNewsEntity(
                schema.getNewsId(),
                schema.getPostId(),
                schema.getDescription(),
                schema.getDate(),
                schema.getNewsImg());
    }


    /**
     * 댓글
     */
    public static List<CommentEntity> comment(List<CommentResponseInfoEntity> schemas) {
        List<CommentEntity> entities = new ArrayList<>();
        for (CommentResponseInfoEntity schema : schemas) {
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

    public static CommentEntity comment(CommentResponseInfoEntity entity) {
        return new CommentEntity(
                entity.getCommId(),
                entity.getUserId(),
                entity.getUserName(),
                entity.getNewsId(),
                entity.getComment(),
                entity.getCommDate());
    }

    public static List<ReportedCommentEntity> reportedComment(List<ReportedCommentResponseInfoEntity> schemas) {
        List<ReportedCommentEntity> entities = new ArrayList<>();
        for (ReportedCommentResponseInfoEntity schema : schemas) {
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
    public static List<FeedEntity> feed(List<FeedResponseInfoEntity> entities) {
        List<FeedEntity> data = new ArrayList<>();
        for (FeedResponseInfoEntity entity : entities) {
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
                    entity.getRepImg(),
                    entity.getRepState()
            ));
        }
        return data;
    }

    public static List<FeedEntity> feed2(List<FeedResponseInfoEntity> entities) {
        List<FeedEntity> data = new ArrayList<>();
        for (FeedResponseInfoEntity entity : entities) {
            if (entity.getRepState() == Constant.ReportState.RECEIVING) continue;
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
                    entity.getRepImg(),
                    entity.getRepState()
            ));
        }
        return data;
    }


    /**
     * 스크랩
     */
    public static List<ScrapEntity> scrap(List<ScrapResponseInfoEntity> schemas) {
        List<ScrapEntity> entities = new ArrayList<>();
        for (ScrapResponseInfoEntity schema : schemas) {
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
    public static List<FaqEntity> faq(List<FaqResponseInfoEntity> schemas) {
        List<FaqEntity> entities = new ArrayList<>();
        for (FaqResponseInfoEntity schema : schemas) {
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
    public static List<FavoriteEntity> favorite(List<FavoriteResponseInfoEntity> schemas) {
        List<FavoriteEntity> entities = new ArrayList<>();
        for (FavoriteResponseInfoEntity schema : schemas) {
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
    public static List<SafehouseEntity> safehouse(List<SafehouseResponseInfoEntity> schemas) {
        List<SafehouseEntity> entities = new ArrayList<>();
        for (SafehouseResponseInfoEntity schema : schemas) {
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
    public static List<FeedResultEntity> feedResult(List<FeedResultResponseInfoEntity> schemas) {
        List<FeedResultEntity> entities = new ArrayList<>();
        for (FeedResultResponseInfoEntity schema : schemas) {
            entities.add(new FeedResultEntity(
                    schema.getResultId(),
                    schema.getRepId(),
                    schema.getResultDate(),
                    schema.getResultImg()
            ));
        }
        return entities;
    }
}
