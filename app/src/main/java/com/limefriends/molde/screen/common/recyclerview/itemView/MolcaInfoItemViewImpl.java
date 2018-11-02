package com.limefriends.molde.screen.common.recyclerview.itemView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.model.entity.molcaInfo.MolcaInfo;
import com.limefriends.molde.screen.common.imageLoader.ImageLoader;
import com.limefriends.molde.screen.common.views.BaseView;
import com.limefriends.molde.screen.common.recyclerview.itemView.MolcaInfoItemView;

public class MolcaInfoItemViewImpl extends BaseView implements MolcaInfoItemView {

    private final ImageLoader mImageLoader;

    private ImageView img_molca_by_location;
    private TextView txt_molca_by_location, txt_molca_by_location_title;

    public MolcaInfoItemViewImpl(LayoutInflater inflater,
                                 ViewGroup parent,
                                 ImageLoader imageLoader) {

        setRootView(inflater.inflate(R.layout.item_cardnews_info, parent, false));

        this.mImageLoader = imageLoader;

        img_molca_by_location = findViewById(R.id.img_molca_by_location);
        txt_molca_by_location = findViewById(R.id.txt_molca_by_location);
        txt_molca_by_location_title = findViewById(R.id.txt_molca_by_location_title);
    }

    @Override
    public void bindInfo(MolcaInfo info) {
        mImageLoader.load(info.getImage(), img_molca_by_location);
        txt_molca_by_location_title.setText(info.getTitle());
        txt_molca_by_location.setText(info.getContent());
    }
}
