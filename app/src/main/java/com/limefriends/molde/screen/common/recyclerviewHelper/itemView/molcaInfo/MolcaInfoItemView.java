package com.limefriends.molde.screen.common.recyclerviewHelper.itemView.molcaInfo;

import com.limefriends.molde.model.entity.molcaInfo.MolcaInfo;
import com.limefriends.molde.screen.common.view.ViewMvc;

public interface MolcaInfoItemView extends ViewMvc {

    void bindInfo(MolcaInfo info);

}
