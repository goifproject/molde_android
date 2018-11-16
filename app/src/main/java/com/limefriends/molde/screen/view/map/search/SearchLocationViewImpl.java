package com.limefriends.molde.screen.view.map.search;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.limefriends.molde.R;
import com.limefriends.molde.model.entity.search.SearchInfoEntity;
import com.limefriends.molde.screen.common.recyclerviewHelper.adapter.RecyclerViewAdapter;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.ItemViewType;
import com.limefriends.molde.screen.common.view.BaseObservableView;
import com.limefriends.molde.screen.common.view.ViewFactory;

import java.util.List;

public class SearchLocationViewImpl
        extends BaseObservableView<SearchLocationView.Listener>
        implements SearchLocationView, RecyclerViewAdapter.OnItemClickListener, RecyclerViewAdapter.OnItem2ClickListener {

    private EditText loc_map_info_search_input;
    private ImageView loc_map_info_search_button;
    private ImageView delete_all_button;
    private Button delete_search_history_button;
    private RecyclerView search_info_list;
    
    private RecyclerViewAdapter<SearchInfoEntity> mSearchInfoAdapter;
    private ViewFactory mViewFactory;

    public SearchLocationViewImpl(LayoutInflater inflater,
                                  ViewGroup parent,
                                  ViewFactory viewFactory) {
        this.mViewFactory = viewFactory;

        setRootView(inflater.inflate(R.layout.activity_search_location, parent, false));

        setupViews();

        setMapInfoRecyclerView();

        setListeners();
    }

    private void setupViews() {

        LinearLayout loc_map_info_search_bar = findViewById(R.id.loc_map_info_search_bar);
        loc_map_info_search_bar.setElevation(12);
        
        loc_map_info_search_input = findViewById(R.id.loc_map_info_search_input);
        loc_map_info_search_button = findViewById(R.id.loc_map_info_search_button);
        search_info_list = findViewById(R.id.loc_map_info_list);
        delete_all_button = findViewById(R.id.delete_all_button);
        delete_search_history_button = findViewById(R.id.delete_search_history_button);
    }

    private void setMapInfoRecyclerView() {

        mSearchInfoAdapter = new RecyclerViewAdapter<>(mViewFactory, ItemViewType.SEARCH_LOCATION);
        search_info_list.setLayoutManager(new LinearLayoutManager(getContext()));
        search_info_list.setAdapter(mSearchInfoAdapter);
        mSearchInfoAdapter.setOnItemClickListener(this);
        mSearchInfoAdapter.setOnItem2ClickListener(this);
    }

    private void setListeners() {

        loc_map_info_search_input.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                loc_map_info_search_button.performClick();
                return true;
            } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                loc_map_info_search_button.performClick();
                return true;
            }
            return false;
        });

        loc_map_info_search_button.setOnClickListener(view -> {
            String keyword = loc_map_info_search_input.getText().toString();
            if (keyword.equals("")) return;
            for (Listener listener : getListeners()) {
                listener.onSearchLocationClicked(keyword);
            }
        });

        delete_all_button.setOnClickListener(view -> {
            loc_map_info_search_input.setText("");
            delete_all_button.setVisibility(View.INVISIBLE);
        });

        delete_search_history_button.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onDeleteHistoryClicked();
            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        for (Listener listener : getListeners()) {
            listener.onLocationItemClicked(position);
        }
    }

    @Override
    public void onItem2Clicked(int position) {
        for (Listener listener : getListeners()) {
            listener.onDeleteHistoryClicked(position);
        }
    }

    @Override
    public void bindSearchInfo(List<SearchInfoEntity> entities) {
        mSearchInfoAdapter.clearData();
        mSearchInfoAdapter.addData(entities);
    }

    @Override
    public void deleteHistoryItem(int position) {
        mSearchInfoAdapter.removeData(position);
    }

    @Override
    public void clearHistory() {
        mSearchInfoAdapter.clearData();
    }
}