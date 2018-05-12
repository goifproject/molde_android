package com.limefriends.molde.menu_magazine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.limefriends.molde.MoldeMainActivity;
import com.limefriends.molde.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMagazineFragment extends Fragment implements MoldeMainActivity.onKeyBackPressedListener{

    @BindView(R.id.cardnews_recyclerView)
    RecyclerView cardnews_recyclerView;
    @BindView(R.id.manual_for_spreading)
    LinearLayout manual_for_spreading;
    @BindView(R.id.manual_new_molca)
    LinearLayout manual_new_molca;
    @BindView(R.id.manual_by_location)
    LinearLayout manual_by_location;



    private RecyclerAdapter recyclerAdapter;
    private List<CardnewsListElement> cardnewsDataList;


    public MoldeMagazineFragment(){}


    public static MoldeMagazineFragment newInstance() {
        MoldeMagazineFragment fragment = new MoldeMagazineFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.magazine_fragment, container, false);
        ButterKnife.bind(this, rootView);

        manual_new_molca.setElevation(8);
        manual_for_spreading.setElevation(8);
        manual_by_location.setElevation(8);


        manual_new_molca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), MagazineReportDetail01Activity.class);
                intent.putExtra("title", "최신 몰카 정보");
                startActivity(intent);
            }
        });

        manual_by_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), MagazineReportDetail02Activity.class);
                intent.putExtra("title", "장소별 대처법");
                startActivity(intent);
            }
        });

        manual_for_spreading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), MagazineReportDetail03Activity.class);
                intent.putExtra("title", "몰카유포 대처");
                startActivity(intent);
            }
        });


        cardnewsDataList = new ArrayList<CardnewsListElement>();
        cardnewsDataList.add(new CardnewsListElement(R.drawable.img_cardnews_dummy, "카드뉴스1"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.img_cardnews_dummy, "카드뉴스2"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.img_cardnews_dummy, "카드뉴스3"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.img_cardnews_dummy, "카드뉴스4"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.img_cardnews_dummy, "카드뉴스5"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.img_cardnews_dummy, "카드뉴스6"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.img_cardnews_dummy, "카드뉴스7"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.img_cardnews_dummy, "카드뉴스8"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.img_cardnews_dummy, "카드뉴스9"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.img_cardnews_dummy, "카드뉴스10"));


        recyclerAdapter = new RecyclerAdapter(getContext(), R.layout.magazine_item_cardnews_recycler, cardnewsDataList);
        cardnews_recyclerView.setAdapter(recyclerAdapter);

        return rootView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onBackKey() {
    }
}
