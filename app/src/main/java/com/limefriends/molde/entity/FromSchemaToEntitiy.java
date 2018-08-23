package com.limefriends.molde.entity;

import com.limefriends.molde.entity.comment.CommentEntity;
import com.limefriends.molde.entity.comment.CommentResponseInfoEntity;
import com.limefriends.molde.entity.faq.FaqEntitiy;
import com.limefriends.molde.entity.faq.FaqResponseInfoEntity;
import com.limefriends.molde.entity.feed.FeedEntity;
import com.limefriends.molde.entity.feed.FeedResponseInfoEntity;
import com.limefriends.molde.entity.news.CardNewsEntity;
import com.limefriends.molde.entity.news.CardNewsResponseInfoEntity;
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
    public static List<FaqEntitiy> faq(List<FaqResponseInfoEntity> schemas) {
        List<FaqEntitiy> entities = new ArrayList<>();
        for (FaqResponseInfoEntity schema : schemas) {
            entities.add(new FaqEntitiy(
                    schema.getFaqId(),
                    schema.getUserId(),
                    schema.getUserName(),
                    schema.getFaqContents(),
                    schema.getFaqEmail()
            ));
        }
        return entities;
    }

}
