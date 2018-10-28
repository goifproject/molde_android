package com.limefriends.molde.screen.magazine.main;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.screen.common.views.ViewFactory;
import com.limefriends.molde.screen.magazine.main.view.CardNewsItemView;
import java.util.ArrayList;
import java.util.List;


public class CardNewsAdapter
        extends RecyclerView.Adapter<CardNewsAdapter.CardNewsViewHolder>
        implements CardNewsItemView.Listener {

    public interface Listener {
        void onCardNewsClicked(int cardNewsId);
    }

    private Listener mListener;
    private ViewFactory mViewFactory;
    private List<CardNewsEntity> entities = new ArrayList<>();

    public CardNewsAdapter(ViewFactory viewFactory, Listener listener) {
        this.mViewFactory = viewFactory;
        this.mListener = listener;
    }

    @Override
    public CardNewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CardNewsViewHolder(mViewFactory.newInstance(CardNewsItemView.class, parent));
    }

    @Override
    public void onBindViewHolder(CardNewsViewHolder holder, int position) {
        holder.mCardNewsItemView.bindCardNews(entities.get(position));
        holder.mCardNewsItemView.registerListener(this);
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    @Override
    public void onCardNewsClicked(int cardNewsId) {
        mListener.onCardNewsClicked(cardNewsId);
    }

    public void addData(List<CardNewsEntity> newEntities) {
        entities.addAll(newEntities);
        notifyDataSetChanged();
    }

    class CardNewsViewHolder extends RecyclerView.ViewHolder {

        private final CardNewsItemView mCardNewsItemView;

        CardNewsViewHolder(CardNewsItemView view) {
            super(view.getRootView());
            this.mCardNewsItemView = view;
        }
    }
}
