package com.limefriends.molde.ui.menu_map.callback_method;



import com.limefriends.molde.ui.menu_map.entity.MoldeSearchMapInfoEntity;

import java.io.IOException;

public interface MapInfoAdapterCallback {
    void showToast(String toast);
    void applySearchMapInfo(MoldeSearchMapInfoEntity entity, String cmd);
    void writeSearchMapHistory(MoldeSearchMapInfoEntity entity, String historyStr) throws IOException;
}
