package com.limefriends.molde.entity;

import com.limefriends.molde.entity.comment.CommentEntity;
import com.limefriends.molde.entity.comment.CommentResponseInfoEntity;
import com.limefriends.molde.entity.news.CardNewsEntity;
import com.limefriends.molde.entity.news.CardNewsResponseInfoEntity;

import java.util.ArrayList;
import java.util.List;

public class FromSchemaToEntitiy {

    /**
     * 카드뉴스
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

}
