package com.limefriends.molde.common.utils.comparator;


import com.limefriends.molde.networking.schema.comment.CommentSchema;

import java.util.Comparator;

public class CommentComparator implements Comparator<CommentSchema> {

    @Override
    public int compare(CommentSchema entity, CommentSchema t1) {
        return Integer.compare(entity.getNewsId(), t1.getNewsId());
    }
}
