package com.limefriends.molde.menu_map.callbackMethod;

import com.limefriends.molde.menu_map.entity.MoldeSearchMapHistoryEntity;

public interface MoldeMapHistoryRecyclerViewAdapterCallback {
    void applyHistoryMapInfo(MoldeSearchMapHistoryEntity historyEntity, String cmd);
}
