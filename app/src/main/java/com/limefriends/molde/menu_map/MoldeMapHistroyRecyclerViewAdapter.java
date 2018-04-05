package com.limefriends.molde.menu_map;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.menu_map.cacheManager.Cache;
import java.io.IOException;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MoldeMapHistroyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<MoldeSearchMapHistoryEntity> historyList;
    private MoldeMapHistoryRecyclerViewAdapterCallback callback;
    private Context context;
    private Cache cache;

    public MoldeMapHistroyRecyclerViewAdapter() {
    }

    public MoldeMapHistroyRecyclerViewAdapter(ArrayList<MoldeSearchMapHistoryEntity> historyList,
                                              Context context) {
        this.historyList = historyList;
        this.context = context;
    }

    public static class MapHistoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.history_title)
        TextView history_title;
        @BindView(R.id.history_delete_button)
        ImageButton history_delete_button;

        private String mapLat;
        private String mapLng;
        private String name;
        private String mainAddress;
        private String bizName;
        private String telNo;


        public MapHistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_map_info_list_item, parent, false);
        return new MapHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MapHistoryViewHolder) {
            final MapHistoryViewHolder viewHolder = (MapHistoryViewHolder) holder;
            cache = new Cache(context);
            viewHolder.history_title.setText(historyList.get(position).getName());
            viewHolder.history_delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String keywordHistoryStr = "";
                    if(historyList.size() <= 1){
                        historyList.remove(position);
                        notifyItemRemoved(position);
                        try {
                            cache.Write(keywordHistoryStr);
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        notifyItemRemoved(position);
                        historyList.remove(position);
                        for(int i = 0; i < historyList.size(); i++){
                            keywordHistoryStr =
                                            historyList.get(i).getMapLat()
                                    + "|" + historyList.get(i).getMapLng()
                                    + "|" + historyList.get(i).getName()
                                    + "|" + historyList.get(i).getMainAddress()
                                    + "|" + historyList.get(i).getBizName()
                                    + "|" + historyList.get(i).getTelNo() + ",";

                        }
                        cache.Write(keywordHistoryStr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        /*Log.e("맵 정보",
                                historyList.get(position).getName() + ", " +
                                        historyList.get(position).getMapLat() + ", " +
                                        historyList.get(position).getMapLng() + ", " +
                                        historyList.get(position).getBizName() + ", " +
                                        historyList.get(position).getMainAddress() + ", " +
                                        historyList.get(position).getTelNo());*/
                        callback.applyHistoryMapInfo(new MoldeSearchMapHistoryEntity(
                                        historyList.get(position).getMapLat(),
                                        historyList.get(position).getMapLng(),
                                        historyList.get(position).getName(),
                                        historyList.get(position).getMainAddress(),
                                        historyList.get(position).getBizName(),
                                        historyList.get(position).getTelNo()
                                )
                        );
                    }catch (ArrayIndexOutOfBoundsException e){
                        Log.e("맵 정보",
                                historyList.get(0).getName() + ", " +
                                        historyList.get(0).getMapLat() + ", " +
                                        historyList.get(0).getMapLng() + ", " +
                                        historyList.get(0).getBizName() + ", " +
                                        historyList.get(0).getMainAddress() + ", " +
                                        historyList.get(0).getTelNo());
                        callback.applyHistoryMapInfo(new MoldeSearchMapHistoryEntity(
                                        historyList.get(0).getMapLat(),
                                        historyList.get(0).getMapLng(),
                                        historyList.get(0).getName(),
                                        historyList.get(0).getMainAddress(),
                                        historyList.get(0).getBizName(),
                                        historyList.get(0).getTelNo()
                                )
                        );
                    }finally {
                        Log.e("맵 정보",
                                historyList.get(0).getName() + ", " +
                                        historyList.get(0).getMapLat() + ", " +
                                        historyList.get(0).getMapLng() + ", " +
                                        historyList.get(0).getBizName() + ", " +
                                        historyList.get(0).getMainAddress() + ", " +
                                        historyList.get(0).getTelNo());
                        callback.applyHistoryMapInfo(new MoldeSearchMapHistoryEntity(
                                        historyList.get(0).getMapLat(),
                                        historyList.get(0).getMapLng(),
                                        historyList.get(0).getName(),
                                        historyList.get(0).getMainAddress(),
                                        historyList.get(0).getBizName(),
                                        historyList.get(0).getTelNo()
                                )
                        );
                    }
                    MoldeSearchMapInfoActivity.checkBackPressed = false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void setCallback(MoldeMapHistoryRecyclerViewAdapterCallback callback) {
        this.callback = callback;
    }

}
