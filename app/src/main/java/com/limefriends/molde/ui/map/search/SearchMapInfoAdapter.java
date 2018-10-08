package com.limefriends.molde.ui.map.search;

import com.limefriends.molde.R;
import com.limefriends.molde.comm.manager.cache_manager.Cache;
import com.limefriends.molde.entity.map.SearchMapInfoEntity;
import com.limefriends.molde.remote.SearchPoiService;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

// TODO 로컬 데이터베이스로 변경
public class SearchMapInfoAdapter extends RecyclerView.Adapter<SearchMapInfoAdapter.MapInfoViewHolder> {

    private ArrayList<SearchMapInfoEntity> infoList = new ArrayList<>();
    private MapInfoAdapterCallback mapInfoAdapterCallback;
    private String keywordHistory;
    private Cache cache;

    public interface MapInfoAdapterCallback {
        void showToast(String toast);

        void applySearchMapInfo(SearchMapInfoEntity entity);

        void writeSearchMapHistory(SearchMapInfoEntity entity, String historyStr) throws IOException;
    }

    @Override
    public MapInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_info, parent, false);
        return new MapInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MapInfoViewHolder viewHolder, int position) {

        SearchMapInfoEntity info = infoList.get(position);

        viewHolder.map_info_title.setText(info.getName());
        viewHolder.map_info_address.setText(info.getMainAddress());

        double mapLat = info.getMapLat();
        double mapLng = info.getMapLng();
        String name = info.getName();
        String mainAddress = info.getMainAddress();
        String bizName = info.getBizName();
        String telNo = info.getTelNo();
        String streetAddress = info.getStreetAddress();

        checkNull(name);
        checkNull(mainAddress);
        checkNull(bizName);
        checkNull(telNo);
        checkNull(streetAddress);

        viewHolder.mapLat = mapLat;
        viewHolder.mapLng = mapLng;
        viewHolder.name = name;
        viewHolder.mainAddress = mainAddress;
        viewHolder.bizName = bizName;
        viewHolder.telNo = telNo;
        viewHolder.streetAddress = streetAddress;
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public void setData(ArrayList<SearchMapInfoEntity> itemLists) {
        this.infoList = itemLists;
    }

    public void setMapInfoAdapterCallback(MapInfoAdapterCallback mapInfoAdapterCallback) {
        this.mapInfoAdapterCallback = mapInfoAdapterCallback;
    }

    public void filter(String keyword, String keywordHistoryStr, Context context) {
        keywordHistory = keywordHistoryStr;
        if (keyword.length() >= 2) {
            try {
                SearchPoiService parser = new SearchPoiService(this, context);
                infoList.addAll(parser.execute(keyword).get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            mapInfoAdapterCallback.showToast("두 글자 이상 입력해주세요");
        }
    }

    private void checkNull(String str) {
        if (str == null || str.equals("")) {
            str = "정보 없음";
        }
    }

    private void applyCallback(Context context, double mapLat, double mapLng, String name,
                               String mainAddress, String streetAddress,
                               String bizName, String telNo) {
        if (keywordHistory == null || keywordHistory.equals("")) {
            cache = new Cache(context);
            try {
                keywordHistory =
                        mapLat
                                + "|" + mapLng
                                + "|" + name
                                + "|" + mainAddress
                                + "|" + bizName
                                + "|" + telNo + ",";
                cache.write(keywordHistory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String[] keywordHistoryArray = keywordHistory.split(",");
        ArrayList<String> keywordHistoryList = new ArrayList<>(Arrays.asList(keywordHistoryArray));
        for (int i = 0; i < keywordHistoryList.size(); i++) {
            if (keywordHistoryList.get(i).contains(mapLat + "|" + mapLng + "|" + name)) {
                keywordHistoryList.remove(i);
            }
        }
        keywordHistory = "";
        for (int i = 0; i < keywordHistoryList.size(); i++) {
            keywordHistory += keywordHistoryList.get(i) + ",";
        }
        mapInfoAdapterCallback.applySearchMapInfo(
                new SearchMapInfoEntity(
                        mapLat,
                        mapLng,
                        name,
                        mainAddress,
                        streetAddress,
                        bizName,
                        telNo
                )
        );

        try {
            mapInfoAdapterCallback.writeSearchMapHistory(
                    new SearchMapInfoEntity(
                            mapLat,
                            mapLng,
                            name,
                            mainAddress,
                            streetAddress,
                            bizName,
                            telNo
                    ), keywordHistory
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class MapInfoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.map_info_title)
        TextView map_info_title;
        @BindView(R.id.map_info_address)
        TextView map_info_address;
//        @BindView(R.id.check_favorite_toggle)
//        ToggleButton check_favorite_toggle;

        double mapLat;
        double mapLng;
        String name;
        String mainAddress;
        String bizName;
        String telNo;
        String streetAddress;

        MapInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setupListener();
        }

        private void setupListener() {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyCallback(v.getContext(), mapLat, mapLng, name, mainAddress,
                            bizName, telNo, streetAddress);
                }
            });

//            check_favorite_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked) {
//                        check_favorite_toggle.setBackgroundResource(R.drawable.ic_favorite_star_on);
//                    } else {
//                        check_favorite_toggle.setBackgroundResource(R.drawable.ic_favorite_star_off);
//                    }
//                }
//            });
        }
    }

}
