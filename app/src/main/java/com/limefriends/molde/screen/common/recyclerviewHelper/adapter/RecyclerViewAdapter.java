package com.limefriends.molde.screen.common.recyclerviewHelper.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.limefriends.molde.model.entity.Data;
import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.model.entity.faq.FaqEntity;
import com.limefriends.molde.model.entity.favorite.FavoriteEntity;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.model.entity.molcaInfo.MolcaInfo;
import com.limefriends.molde.model.entity.search.SearchInfoEntity;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.ItemViewType;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.myFeed.MyFeedItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.reportCard.ReportCardItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.searchLocation.SearchLocationItemView;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.common.view.ViewMvc;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.molcaInfo.MolcaInfoItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.cardNews.CardNewsItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.cardNewsComment.CardNewsCommentItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.feed.FeedItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.favorite.FavoriteItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.inquiry.InquiryItemView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.reportedComment.ReportedCommentItemView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter<T extends Data>
        extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
        implements
        CardNewsItemView.Listener, CardNewsCommentItemView.Listener, FeedItemView.Listener,
        FavoriteItemView.Listener, SearchLocationItemView.Listener, InquiryItemView.Listener,
        ReportedCommentItemView.Listener, MyFeedItemView.Listener, ReportCardItemView.Listener {

    public interface OnItemClickListener {

        void onItemClicked(int itemId);
    }

    public interface OnItem2ClickListener {

        void onItem2Clicked(int itemId);
    }

    private static final int
            VIEW_TYPE_CARD_NEWS = 0,
            VIEW_TYPE_COMMENT = 1,
            VIEW_TYPE_FAVORITE = 2,
            VIEW_TYPE_FEED = 3,
            VIEW_TYPE_FAQ = 4,
            VIEW_TYPE_MOLCA_INFO = 5,
            VIEW_TYPE_MY_FEED = 6,
            VIEW_TYPE_REPORTED_COMMENT = 7,
            VIEW_TYPE_SEARCH_INFO = 8,
            VIEW_TYPE_REPORT_CARD_LIST = 9,
            VIEW_TYPE_SAFEHOUSE = 10;


    private List<T> dataList = new ArrayList<>();
    private ViewFactory mViewFactory;
    private OnItemClickListener mOnItemClickListener;
    private OnItem2ClickListener mOnItem2ClickListener;

    public RecyclerViewAdapter(ViewFactory mViewFactory, ItemViewType itemViewType) {
        this.mViewFactory = mViewFactory;
        this.itemViewType = itemViewType;
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

    public void removeData(int position) {
        dataList.remove(position);
        notifyDataSetChanged();
    }

    public void updateData(int position, T data) {
        dataList.set(position, data);
        notifyItemChanged(position);
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

    private ItemViewType itemViewType;

    @Override
    public int getItemViewType(int position) {

        switch (itemViewType) {
            case CARDNEWS:
                return VIEW_TYPE_CARD_NEWS;
            case CARDNEWS_COMMENT:
                return VIEW_TYPE_COMMENT;
            case FAVORITE:
                return VIEW_TYPE_FAVORITE;
            case FEED:
                return VIEW_TYPE_FEED;
            case MY_FEED:
                return VIEW_TYPE_MY_FEED;
            case INQUIRY:
                return VIEW_TYPE_FAQ;
            case MOLCA_INFO:
                return VIEW_TYPE_MOLCA_INFO;
            case REPORTED_COMMENT:
                return VIEW_TYPE_REPORTED_COMMENT;
            case SEARCH_LOCATION:
                return VIEW_TYPE_SEARCH_INFO;
            case REPORT_CARD:
                return VIEW_TYPE_REPORT_CARD_LIST;
            default:
                return -1;
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewMvc viewMvc = null;

        switch (viewType) {
            case VIEW_TYPE_CARD_NEWS:
                viewMvc = mViewFactory.newInstance(CardNewsItemView.class, parent);
                break;
            case VIEW_TYPE_COMMENT:
                viewMvc = mViewFactory.newInstance(CardNewsCommentItemView.class, parent);
                break;
            case VIEW_TYPE_FAVORITE:
                viewMvc = mViewFactory.newInstance(FavoriteItemView.class, parent);
                break;
            case VIEW_TYPE_FEED:
                viewMvc = mViewFactory.newInstance(FeedItemView.class, parent);
                break;
            case VIEW_TYPE_FAQ:
                viewMvc = mViewFactory.newInstance(InquiryItemView.class, parent);
                break;
            case VIEW_TYPE_MOLCA_INFO:
                viewMvc = mViewFactory.newInstance(MolcaInfoItemView.class, parent);
                break;
            case VIEW_TYPE_MY_FEED:
                viewMvc = mViewFactory.newInstance(MyFeedItemView.class, parent);
                break;
            case VIEW_TYPE_SEARCH_INFO:
                viewMvc = mViewFactory.newInstance(SearchLocationItemView.class, parent);
                break;
            case VIEW_TYPE_REPORTED_COMMENT:
                viewMvc = mViewFactory.newInstance(ReportedCommentItemView.class, parent);
                break;
            case VIEW_TYPE_REPORT_CARD_LIST:
                viewMvc = mViewFactory.newInstance(ReportCardItemView.class, parent);
                break;
//            case VIEW_TYPE_SAFEHOUSE:
//                return ;
        }
        return new ViewHolder(viewMvc);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.position = position;
        holder.bindItem(itemViewType, dataList.get(position));
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

    class ViewHolder extends RecyclerView.ViewHolder {

        ViewMvc mViewMvc;
        int position;

        ViewHolder(ViewMvc view) {
            super(view.getRootView());
            this.mViewMvc = view;
        }

        void bindItem(ItemViewType itemViewType, T data) {
            switch (itemViewType) {

                case CARDNEWS:
                    ((CardNewsItemView) mViewMvc).bindCardNews((CardNewsEntity) data);
                    ((CardNewsItemView) mViewMvc).registerListener(RecyclerViewAdapter.this);
                    break;
                case CARDNEWS_COMMENT:
                    ((CardNewsCommentItemView) mViewMvc).bindComment((CommentEntity) data);
                    ((CardNewsCommentItemView) mViewMvc).registerListener(RecyclerViewAdapter.this);
                    break;
                case FAVORITE:
                    ((FavoriteItemView) mViewMvc).bindFavorite((FavoriteEntity) data);
                    ((FavoriteItemView) mViewMvc).registerListener(RecyclerViewAdapter.this);
                    break;
                case FEED:
                    ((FeedItemView) mViewMvc).bindFeed((FeedEntity) data);
                    ((FeedItemView) mViewMvc).registerListener(RecyclerViewAdapter.this);
                    break;
                case INQUIRY:
                    ((InquiryItemView) mViewMvc).bindInquiry((FaqEntity) data);
                    ((InquiryItemView) mViewMvc).registerListener(RecyclerViewAdapter.this);
                    break;
                case MOLCA_INFO:
                    ((MolcaInfoItemView) mViewMvc).bindInfo((MolcaInfo) data);
                    break;
                case MY_FEED:
                    ((MyFeedItemView) mViewMvc).bindFeed((FeedEntity) data, position);
                    ((MyFeedItemView) mViewMvc).registerListener(RecyclerViewAdapter.this);
                    break;
                case REPORTED_COMMENT:
                    ((ReportedCommentItemView) mViewMvc).bindComment((CommentEntity) data, position);
                    ((ReportedCommentItemView) mViewMvc).registerListener(RecyclerViewAdapter.this);
                    break;
                case SEARCH_LOCATION:
                    ((SearchLocationItemView) mViewMvc).bindSearchInfo((SearchInfoEntity) data, position);
                    ((SearchLocationItemView) mViewMvc).registerListener(RecyclerViewAdapter.this);
                    break;
                case REPORT_CARD:
                    ((ReportCardItemView) mViewMvc).bindFeed((FeedEntity) data);
                    ((ReportCardItemView) mViewMvc).registerListener(RecyclerViewAdapter.this);
                    break;
//                case SAFE_HOUSE:
//                    break;
            }
        }
    }

}
