package com.limefriends.molde.screen.common.recyclerview.itemView.searchLocation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.model.entity.search.SearchInfoEntity;
import com.limefriends.molde.screen.common.view.BaseObservableView;

public class SearchLocationItemViewImpl
        extends BaseObservableView<SearchLocationItemView.Listener> implements SearchLocationItemView {

    private TextView search_info_title;
    private TextView search_info_address;
    private ImageView search_info_delete_button;
    private int position;

    public SearchLocationItemViewImpl(LayoutInflater inflater, ViewGroup parent) {

        setRootView(inflater.inflate(R.layout.item_search_info, parent, false));

        setupViews();

        setupListener();
    }

    private void setupViews() {
        search_info_title = findViewById(R.id.search_info_title);
        search_info_address = findViewById(R.id.search_info_address);
        search_info_delete_button = findViewById(R.id.search_info_delete_button);
    }

    private void setupListener() {

        getRootView().setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onItemClicked(position);
            }
        });

        search_info_delete_button.setOnClickListener(view -> {
            for (Listener listener : getListeners()) {
                listener.onItem2Clicked(position);
            }
        });
    }

    @Override
    public void bindSearchInfo(SearchInfoEntity entity, int position) {
        this.position = position;
        if (entity.isFromHistory()) {
            search_info_delete_button.setVisibility(View.VISIBLE);
        } else {
            search_info_delete_button.setVisibility(View.GONE);
        }
        search_info_title.setText(entity.getName());
        search_info_address.setText(entity.getMainAddress());
    }

}
