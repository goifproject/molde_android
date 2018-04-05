package com.limefriends.molde.menu_map;

import com.limefriends.molde.R;
import com.limefriends.molde.menu_map.autosearch.SearchPoiParse;
import com.limefriends.molde.menu_map.cacheManager.Cache;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MoldeMapInfoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MoldeSearchMapInfoEntity> itemLists = new ArrayList<>();
    private MoldeMapInfoRecyclerViewAdapterCallback callback;
    private String keywordHistory;
    private Context context;
    private Cache cache;

    public MoldeMapInfoRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public static class MapInfoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.map_info_title)
        TextView map_info_title;
        @BindView(R.id.map_info_address)
        TextView map_info_address;

        private String name;
        private String mainAddress;
        private String streetAddress;
        private String mapLat;
        private String mapLng;
        private String bizName;
        private String telNo;

        public MapInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loc_map_info_list_item, parent, false);
        return new MapInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //final int ItemPosition = position;

        if (holder instanceof MapInfoViewHolder) {
            final MapInfoViewHolder viewHolder = (MapInfoViewHolder) holder;
            viewHolder.map_info_title.setText(itemLists.get(position).getName());
            viewHolder.map_info_address.setText(itemLists.get(position).getMainAddress());

            viewHolder.mapLat = itemLists.get(position).getMapLat();
            viewHolder.mapLng = itemLists.get(position).getMapLng();
            viewHolder.name = itemLists.get(position).getName();
            viewHolder.mainAddress = itemLists.get(position).getMainAddress();
            if(viewHolder.mainAddress == null || viewHolder.mainAddress.equals("")){
                viewHolder.mainAddress = "정보 없음";
            }
            viewHolder.bizName = itemLists.get(position).getBizName();
            if(viewHolder.bizName == null || viewHolder.bizName.equals("")){
                viewHolder.bizName = "정보 없음";
            }
            viewHolder.telNo = itemLists.get(position).getTelNo();
            if(viewHolder.telNo == null || viewHolder.telNo.equals("")){
                viewHolder.telNo = "정보 없음";
            }
            viewHolder.streetAddress = itemLists.get(position).getStreetAddress();
            if(viewHolder.streetAddress == null || viewHolder.streetAddress.equals("")){
                viewHolder.streetAddress = "정보 없음";
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(keywordHistory == null || keywordHistory.equals("")){
                        cache = new Cache(context);
                        try {
                            keywordHistory =
                                    viewHolder.mapLat
                            + "|" + viewHolder.mapLng
                            + "|" + viewHolder.name
                            + "|" + viewHolder.mainAddress
                            + "|" + viewHolder.bizName
                            + "|" + viewHolder.telNo + ",";
                            cache.Write(keywordHistory);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //callback.showToast(viewHolder.mapLat + ", " + viewHolder.mapLng + " : " + viewHolder.bizName + " - " + viewHolder.telNo);
                    String[] keywordHistoryArray = keywordHistory.split(",");
                    ArrayList<String> keywordHistoryList = new ArrayList<String>();
                    for (int i = 0; i < keywordHistoryArray.length; i++) {
                        keywordHistoryList.add(keywordHistoryArray[i]);
                    }
                    for (int i = 0; i < keywordHistoryList.size(); i++) {
                        if (keywordHistoryList.get(i).contains(viewHolder.mapLat + "|" + viewHolder.mapLng + "|" + viewHolder.name)) {
                            keywordHistoryList.remove(i);
                        }
                    }
                    keywordHistory = "";
                    for (int i = 0; i < keywordHistoryList.size(); i++) {
                        keywordHistory += keywordHistoryList.get(i) + ",";
                    }
                    callback.applyMapInfo(
                            new MoldeSearchMapInfoEntity(
                                    viewHolder.mapLat,
                                    viewHolder.mapLng,
                                    viewHolder.name,
                                    viewHolder.mainAddress,
                                    viewHolder.streetAddress,
                                    viewHolder.bizName,
                                    viewHolder.telNo
                            )
                    );
                    MoldeSearchMapInfoActivity.checkBackPressed = false;
                    try {
                        callback.writeSearchMapHistory(
                                new MoldeSearchMapInfoEntity(
                                        viewHolder.mapLat,
                                        viewHolder.mapLng,
                                        viewHolder.name,
                                        viewHolder.mainAddress,
                                        viewHolder.streetAddress,
                                        viewHolder.bizName,
                                        viewHolder.telNo
                                ), keywordHistory
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemLists.size();
    }

    public void setData(ArrayList<MoldeSearchMapInfoEntity> itemLists) {
        this.itemLists = itemLists;
    }

    public void setCallback(MoldeMapInfoRecyclerViewAdapterCallback callback) {
        this.callback = callback;
    }

    public void filter(String keyword, String keywordHistoryStr) {
        keywordHistory = keywordHistoryStr;
        if (keyword.length() >= 2) {
            try {
                SearchPoiParse parser = new SearchPoiParse(this);
                itemLists.addAll(parser.execute(keyword).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }


}
