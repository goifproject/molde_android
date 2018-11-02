package com.limefriends.molde.screen.common.recyclerview.itemView;

import com.limefriends.molde.model.entity.molcaInfo.MolcaInfo;
import com.limefriends.molde.screen.common.views.ViewMvc;

public interface MolcaInfoItemView extends ViewMvc {

    void bindInfo(MolcaInfo info);

}
