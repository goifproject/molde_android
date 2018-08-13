package com.limefriends.molde.comm.utils.comparator;


import com.limefriends.molde.entity.comment.MoldeCommentResponseInfoEntity;

import java.util.Comparator;

public class CommentComparator implements Comparator<MoldeCommentResponseInfoEntity> {

    @Override
    public int compare(MoldeCommentResponseInfoEntity entity, MoldeCommentResponseInfoEntity t1) {
        return Integer.compare(entity.getNewsId(), t1.getNewsId());
    }
}
