package com.limefriends.molde.ui.map.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.comm.manager.cache_manager.Cache;
import com.limefriends.molde.entity.map.SearchMapHistoryEntity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// TODO 로컬 데이터베이스로 변경
public class SearchMapHistoryAdapter extends RecyclerView.Adapter<SearchMapHistoryAdapter.MapHistoryViewHolder> {

    private List<SearchMapHistoryEntity> historyList;
    private MapHistoryAdapterCallback mapHistoryAdapterCallback;
    private Context context;
    private Cache cache;

    public interface MapHistoryAdapterCallback {
        void applyHistoryMapInfo(SearchMapHistoryEntity historyEntity);
    }

    public SearchMapHistoryAdapter(List<SearchMapHistoryEntity> historyList,
                                   Context context) {
        this.historyList = historyList;
        this.context = context;
    }

    public void clear() {
        historyList.clear();
        notifyDataSetChanged();
    }

    @Override
    public MapHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_history, parent, false);
        return new MapHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MapHistoryViewHolder viewHolder, final int position) {


        viewHolder.history_title.setText(historyList.get(position).getName());
        viewHolder.history_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cache = new Cache(context);
                String keywordHistoryStr = "";
                if (historyList.size() <= 1) {
                    historyList.remove(position);
                    notifyItemRemoved(position);
                    try {
                        cache.write(keywordHistoryStr);
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    notifyItemRemoved(position);
                    historyList.remove(position);
                    Collections.reverse(historyList);
                    for (int i = 0; i < historyList.size(); i++) {
                        keywordHistoryStr +=
                                historyList.get(i).getMapLat()
                                        + "|" + historyList.get(i).getMapLng()
                                        + "|" + historyList.get(i).getName()
                                        + "|" + historyList.get(i).getMainAddress()
                                        + "|" + historyList.get(i).getBizName()
                                        + "|" + historyList.get(i).getTelNo() + ",";

                    }
                    cache.write(keywordHistoryStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mapHistoryAdapterCallback.applyHistoryMapInfo(new SearchMapHistoryEntity(
                                    historyList.get(position).getMapLat(),
                                    historyList.get(position).getMapLng(),
                                    historyList.get(position).getName(),
                                    historyList.get(position).getMainAddress(),
                                    historyList.get(position).getBizName(),
                                    historyList.get(position).getTelNo()
                            )
                    );
                } catch (IndexOutOfBoundsException e) {

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void setMapHistoryAdapterCallback(MapHistoryAdapterCallback mapHistoryAdapterCallback) {
        this.mapHistoryAdapterCallback = mapHistoryAdapterCallback;
    }

    class MapHistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.history_title)
        TextView history_title;
        @BindView(R.id.history_delete_button)
        ImageButton history_delete_button;

        MapHistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
