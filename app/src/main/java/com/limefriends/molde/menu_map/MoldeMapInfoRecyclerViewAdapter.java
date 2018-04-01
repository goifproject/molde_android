package com.limefriends.molde.menu_map;

import com.limefriends.molde.R;
import com.limefriends.molde.menu_map.autosearch.SearchPoiParse;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MoldeMapInfoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<MoldeSearchMapInfoEntity> itemLists = new ArrayList<>();
    private MoldeMapInfoRecyclerViewAdapterCallback callback;

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView address;
        public String mapLat;
        public String mapLng;
        public CustomViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.map_info_title);
            address = (TextView) itemView.findViewById(R.id.map_info_address);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_info_list_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int ItemPosition = position;

        if( holder instanceof CustomViewHolder) {
            final CustomViewHolder viewHolder = (CustomViewHolder)holder;

            viewHolder.title.setText(itemLists.get(position).getTitle());
            viewHolder.address.setText(itemLists.get(position).getAddress());

            viewHolder.mapLat = itemLists.get(position).getMapLat();
            viewHolder.mapLng = itemLists.get(position).getMapLng();

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.showToast(viewHolder.mapLat + ", " + viewHolder.mapLng);
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

    public void filter(String keyword) {
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
