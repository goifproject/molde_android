package com.limefriends.molde.ui.menu_map.search;

import com.limefriends.molde.R;
import com.limefriends.molde.comm.manager.cache_manager.Cache;
import com.limefriends.molde.entity.map.MoldeSearchMapInfoEntity;
import com.limefriends.molde.remote.SearchPoiService;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MoldeMapInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MoldeSearchMapInfoEntity> infoList = new ArrayList<>();
    private MapInfoAdapterCallback mapInfoAdapterCallback;
    private String keywordHistory;
    private Context context;
    private Cache cache;
    private String cmd;

    public interface MapInfoAdapterCallback {
        void showToast(String toast);

        void applySearchMapInfo(MoldeSearchMapInfoEntity entity, String cmd);

        void writeSearchMapHistory(MoldeSearchMapInfoEntity entity, String historyStr) throws IOException;
    }

    public MoldeMapInfoAdapter(Context context, String cmd) {
        this.context = context;
        this.cmd = cmd;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_search_info_item, parent, false);
        return new MapInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //final int ItemPosition = position;
        final Resources res = holder.itemView.getContext().getResources();
        // if (holder instanceof MapInfoViewHolder) {
        final MapInfoViewHolder viewHolder = (MapInfoViewHolder) holder;
        viewHolder.map_info_title.setText(infoList.get(position).getName());
        viewHolder.map_info_address.setText(infoList.get(position).getMainAddress());

        viewHolder.mapLat = infoList.get(position).getMapLat();
        viewHolder.mapLng = infoList.get(position).getMapLng();
        viewHolder.name = infoList.get(position).getName();
        viewHolder.mainAddress = infoList.get(position).getMainAddress();

        if (viewHolder.mainAddress == null || viewHolder.mainAddress.equals("")) {
            viewHolder.mainAddress = "정보 없음";
        }
        viewHolder.bizName = infoList.get(position).getBizName();
        if (viewHolder.bizName == null || viewHolder.bizName.equals("")) {
            viewHolder.bizName = "정보 없음";
        }
        viewHolder.telNo = infoList.get(position).getTelNo();
        if (viewHolder.telNo == null || viewHolder.telNo.equals("")) {
            viewHolder.telNo = "정보 없음";
        }
        viewHolder.streetAddress = infoList.get(position).getStreetAddress();
        if (viewHolder.streetAddress == null || viewHolder.streetAddress.equals("")) {
            viewHolder.streetAddress = "정보 없음";
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (keywordHistory == null || keywordHistory.equals("")) {
                    cache = new Cache(context);
                    try {
                        keywordHistory =
                                viewHolder.mapLat
                                        + "|" + viewHolder.mapLng
                                        + "|" + viewHolder.name
                                        + "|" + viewHolder.mainAddress
                                        + "|" + viewHolder.bizName
                                        + "|" + viewHolder.telNo + ",";
                        cache.write(keywordHistory);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //mapInfoAdapterCallback.showToast(viewHolder.mapLat + ", " + viewHolder.mapLng + " : " + viewHolder.bizName + " - " + viewHolder.telNo);
                String[] keywordHistoryArray = keywordHistory.split(",");
                ArrayList<String> keywordHistoryList = new ArrayList<String>(Arrays.asList(keywordHistoryArray));
                for (int i = 0; i < keywordHistoryList.size(); i++) {
                    if (keywordHistoryList.get(i).contains(viewHolder.mapLat + "|" + viewHolder.mapLng + "|" + viewHolder.name)) {
                        keywordHistoryList.remove(i);
                    }
                }
                keywordHistory = "";
                for (int i = 0; i < keywordHistoryList.size(); i++) {
                    keywordHistory += keywordHistoryList.get(i) + ",";
                }
                mapInfoAdapterCallback.applySearchMapInfo(
                        new MoldeSearchMapInfoEntity(
                                viewHolder.mapLat,
                                viewHolder.mapLng,
                                viewHolder.name,
                                viewHolder.mainAddress,
                                viewHolder.streetAddress,
                                viewHolder.bizName,
                                viewHolder.telNo
                        ), cmd
                );
                SearchMapInfoActivity.isCheckBackPressed = false;
                try {
                    mapInfoAdapterCallback.writeSearchMapHistory(
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

        viewHolder.check_favorite_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.check_favorite_toggle.isChecked()) {
                    viewHolder.check_favorite_toggle.setBackgroundDrawable(
                            res.getDrawable(R.drawable.ic_star_on)
                    );
                } else {
                    viewHolder.check_favorite_toggle.setBackgroundDrawable(
                            res.getDrawable(R.drawable.ic_star_off)
                    );
                }
            }
        });
//        }
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public void setData(ArrayList<MoldeSearchMapInfoEntity> itemLists) {
        this.infoList = itemLists;
    }

    public void setMapInfoAdapterCallback(MapInfoAdapterCallback mapInfoAdapterCallback) {
        this.mapInfoAdapterCallback = mapInfoAdapterCallback;
    }

    public void filter(String keyword, String keywordHistoryStr) {
        keywordHistory = keywordHistoryStr;
        if (keyword.length() >= 2) {
            try {
                SearchPoiService parser = new SearchPoiService(this);
                infoList.addAll(parser.execute(keyword).get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            mapInfoAdapterCallback.showToast("두 글자 이상 입력해주세요");
        }
    }


    public static class MapInfoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.map_info_title)
        TextView map_info_title;
        @BindView(R.id.map_info_address)
        TextView map_info_address;
        @BindView(R.id.check_favorite_toggle)
        ToggleButton check_favorite_toggle;

        private String name;
        private String mainAddress;
        private String streetAddress;
        private double mapLat;
        private double mapLng;
        private String bizName;
        private String telNo;

        public MapInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
