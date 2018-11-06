package com.limefriends.molde.screen.common.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.limefriends.molde.model.entity.Data;
import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.model.entity.favorite.FavoriteEntity;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.model.entity.molcaInfo.MolcaInfo;
import com.limefriends.molde.screen.common.views.ViewFactory;
import com.limefriends.molde.screen.common.views.ViewMvc;
import com.limefriends.molde.screen.common.recyclerview.itemView.MolcaInfoItemView;
import com.limefriends.molde.screen.common.recyclerview.itemView.CardNewsItemView;
import com.limefriends.molde.screen.common.recyclerview.itemView.CardNewsCommentItemView;
import com.limefriends.molde.screen.common.recyclerview.itemView.FeedItemView;
import com.limefriends.molde.screen.common.recyclerview.itemView.FavoriteItemView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter<T extends Data>
        extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
        implements CardNewsItemView.Listener, CardNewsCommentItemView.Listener, FeedItemView.Listener, FavoriteItemView.Listener {

    public interface OnItemClickListener {

        void onItemClicked(int itemId);
    }

    public interface OnItem2ClickListener {

        void onItem2Clicked(int itemId);
    }

    private static final int 
            VIEW_TYPE_CARD_NEWS = 0,
            VIEW_TYPE_COMMENT = 1,
            VIEW_TYPE_REPORTED_COMMENT = 2,
            VIEW_TYPE_FAQ = 3,
            VIEW_TYPE_FAVORITE = 4,
            VIEW_TYPE_FEED = 5,
            VIEW_TYPE_SAFEHOUSE = 6,
            VIEW_TYPE_SCRAP = 7,
            VIEW_TYPE_SEARCH_INFO = 8,
            VIEW_TYPE_SEARCH_HISTORY = 9,
            VIEW_TYPE_MOLCA_INFO = 10;
    
    private List<T> dataList = new ArrayList<>();
    private ViewFactory mViewFactory;
    private OnItemClickListener mOnItemClickListener;
    private OnItem2ClickListener mOnItem2ClickListener;

    public RecyclerViewAdapter(ViewFactory mViewFactory) {
        this.mViewFactory = mViewFactory;
    }

    public void setData(List<T> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void addData(List<T> dataList) {
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public void addData(T data) {
        this.dataList.add(data);
        notifyDataSetChanged();
    }

    public void removeData(T data) {
        dataList.remove(data);
        notifyDataSetChanged();
    }

    public void clearData() {
        dataList.clear();
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItem2ClickListener(OnItem2ClickListener listener) {
        this.mOnItem2ClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {

        switch (dataList.get(position).getType()) {
            case CARD_NEWS:
                return VIEW_TYPE_CARD_NEWS;
            case COMMENT:
                return VIEW_TYPE_COMMENT;
            case REPORTED_COMMENT:
                return VIEW_TYPE_REPORTED_COMMENT;
            case FAQ:
                return VIEW_TYPE_FAQ;
            case FAVORITE:
                return VIEW_TYPE_FAVORITE;
            case FEED:
                return VIEW_TYPE_FEED;
            case SAFE_HOUSE:
                return VIEW_TYPE_SAFEHOUSE;
            case SCRAP:
                return VIEW_TYPE_SCRAP;
            case SEARCH_INFO:
                return VIEW_TYPE_SEARCH_INFO;
            case SEARCH_HISTORY:
                return VIEW_TYPE_SEARCH_HISTORY;
            case MOLCA_INFO:
                return VIEW_TYPE_MOLCA_INFO;
            default:
                return -1;
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewMvc viewMvc = null;

        switch (viewType) {
            case VIEW_TYPE_MOLCA_INFO:
                viewMvc = mViewFactory.newInstance(MolcaInfoItemView.class, parent);
                break;
            case VIEW_TYPE_CARD_NEWS:
                viewMvc = mViewFactory.newInstance(CardNewsItemView.class, parent);
                break;
            case VIEW_TYPE_COMMENT:
                viewMvc = mViewFactory.newInstance(CardNewsCommentItemView.class, parent);
                break;
            case VIEW_TYPE_FEED:
                viewMvc = mViewFactory.newInstance(FeedItemView.class, parent);
                break;
            case VIEW_TYPE_FAVORITE:
                viewMvc = mViewFactory.newInstance(FavoriteItemView.class, parent);
                break;
//            case VIEW_TYPE_REPORTED_COMMENT:
//                return ;
//            case VIEW_TYPE_FAQ:
//                return ;
//            case VIEW_TYPE_SAFEHOUSE:
//                return ;
//            case VIEW_TYPE_SCRAP:
//                return ;
//            case VIEW_TYPE_SEARCH_INFO:
//                return ;
//            case VIEW_TYPE_SEARCH_HISTORY:
//                return ;
        }
        return new ViewHolder(viewMvc);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.bindItem(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onItemClicked(int itemId) {
        mOnItemClickListener.onItemClicked(itemId);
    }

    @Override
    public void onItem2Clicked(int itemId) {
        mOnItem2ClickListener.onItem2Clicked(itemId);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewMvc mViewMvc;

        public ViewHolder(ViewMvc view) {
            super(view.getRootView());
            this.mViewMvc = view;
        }

        void bindItem(T data) {
            switch (data.getType()) {
                case MOLCA_INFO:
                    ((MolcaInfoItemView) mViewMvc).bindInfo((MolcaInfo) data);
                case CARD_NEWS:
                    ((CardNewsItemView) mViewMvc).bindCardNews((CardNewsEntity) data);
                    ((CardNewsItemView) mViewMvc).registerListener(RecyclerViewAdapter.this);
                    break;
                case COMMENT:
                    ((CardNewsCommentItemView) mViewMvc).bindComment((CommentEntity) data);
                    ((CardNewsCommentItemView) mViewMvc).registerListener(RecyclerViewAdapter.this);
                    break;
                case FEED:
                    ((FeedItemView) mViewMvc).bindFeed((FeedEntity) data);
                    ((FeedItemView) mViewMvc).registerListener(RecyclerViewAdapter.this);
                    break;
                case FAVORITE:
                    ((FavoriteItemView) mViewMvc).bindFavorite((FavoriteEntity) data);
                    ((FavoriteItemView) mViewMvc).registerListener(RecyclerViewAdapter.this);
                    break;
//                case CARD_NEWS_IMAGE:
//                    break;
//                case REPORTED_COMMENT:
//                    break;
//                case FAQ:
//                    break;
//                case FEED_RESULT:
//                    break;
//                case SAFE_HOUSE:
//                    break;
//                case SCRAP:
//                    break;
//                case SEARCH_INFO:
//                    break;
//                case SEARCH_HISTORY:
//                    break;
//                default:
            }

        }


    }

}
