package com.limefriends.molde.comm.utils.comparator;


import com.limefriends.molde.entity.news.MoldeCardNewsEntity;

import java.util.Comparator;

public class CardNewsComparator implements Comparator<MoldeCardNewsEntity> {

    @Override
    public int compare(MoldeCardNewsEntity entity, MoldeCardNewsEntity t1) {
        return Integer.compare(entity.getNewsId(), t1.getNewsId());
    }
}
