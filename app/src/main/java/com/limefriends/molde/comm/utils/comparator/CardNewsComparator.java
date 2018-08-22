package com.limefriends.molde.comm.utils.comparator;


import com.limefriends.molde.entity.news.CardNewsEntity;

import java.util.Comparator;

public class CardNewsComparator implements Comparator<CardNewsEntity> {

    @Override
    public int compare(CardNewsEntity entity, CardNewsEntity t1) {
        return Integer.compare(entity.getNewsId(), t1.getNewsId());
    }
}
