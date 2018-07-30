package com.limefriends.molde.menu_magazine;

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
import com.limefriends.molde.menu_magazine.cardnews.MagazineCardNewsAdapter;
import com.limefriends.molde.menu_magazine.entity.CardNewsEntity;
import com.limefriends.molde.menu_magazine.magazineReport.MagazineReportLocationDetailActivity;
import com.limefriends.molde.menu_magazine.magazineReport.MagazineReportMolcaDetailActivity;
import com.limefriends.molde.menu_magazine.magazineReport.MagazineReportSpreadDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMagazineFragment extends Fragment implements MoldeMainActivity.onKeyBackPressedListener{

    @BindView(R.id.cardnews_recyclerView)
    RecyclerView cardnews_recyclerView;
    @BindView(R.id.manual_new_molca)
    LinearLayout manual_new_molca;
    @BindView(R.id.manual_by_location)
    LinearLayout manual_by_location;
    @BindView(R.id.manual_for_spreading)
    LinearLayout manual_for_spreading;

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
                intent.setClass(getActivity(), MagazineReportMolcaDetailActivity.class);
                intent.putExtra("title", "최신 몰카 정보");
                startActivity(intent);
            }
        });

        manual_by_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), MagazineReportLocationDetailActivity.class);
                intent.putExtra("title", "장소별 대처법");
                startActivity(intent);
            }
        });

        manual_for_spreading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), MagazineReportSpreadDetailActivity.class);
                intent.putExtra("title", "몰카유포 대처");
                startActivity(intent);
            }
        });


        List<CardNewsEntity> cardnewsDataList = new ArrayList<CardNewsEntity>();
        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스1"));
        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스2"));
        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스3"));
        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스4"));
        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스5"));
        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스6"));
        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스7"));
        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스8"));
        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스9"));
        cardnewsDataList.add(new CardNewsEntity(R.drawable.img_cardnews_dummy, "카드뉴스10"));


        MagazineCardNewsAdapter magazineCardNewsAdapter = new MagazineCardNewsAdapter(getContext(), cardnewsDataList);
        cardnews_recyclerView.setAdapter(magazineCardNewsAdapter);

        return rootView;
    }

    @Override
    public void onBackKey() {

    }
}
