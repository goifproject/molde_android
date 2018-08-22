package com.limefriends.molde.comm.utils.comparator;


import com.limefriends.molde.entity.comment.CommentResponseInfoEntity;

import java.util.Comparator;

public class CommentComparator implements Comparator<CommentResponseInfoEntity> {

    @Override
    public int compare(CommentResponseInfoEntity entity, CommentResponseInfoEntity t1) {
        return Integer.compare(entity.getNewsId(), t1.getNewsId());
    }
}
