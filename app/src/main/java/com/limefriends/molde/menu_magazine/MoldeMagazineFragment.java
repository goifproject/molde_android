package com.limefriends.molde.menu_magazine;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.limefriends.molde.MoldeMainActivity;
import com.limefriends.molde.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMagazineFragment extends Fragment implements MoldeMainActivity.onKeyBackPressedListener{

    @BindView(R.id.cardnews_recyclerView)
    RecyclerView cardnews_recyclerView;

    @BindView(R.id.manual_molca)
    ImageView manual_molca;

    @BindView(R.id.manual_hotel)
    ImageView manual_hotel;

    @BindView(R.id.manual_toilet)
    ImageView manual_toilet;

    @BindView(R.id.manual_transportation)
    ImageView manual_transportation;



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


        // TODO: 중복된 코드 제거?!
        manual_hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "hotel clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(getActivity(), MagazineReportDetailActivity.class);
                intent.putExtra("title", "숙박업소에 몰카가 있는 것 같아요!");
                startActivity(intent);
            }
        });

        manual_toilet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "toilet clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(getActivity(), MagazineReportDetailActivity.class);
                intent.putExtra("title", "화장실에 몰카가 있는 것 같아요!");
                startActivity(intent);
            }
        });

        manual_transportation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "trasportation clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(getActivity(), MagazineReportDetailActivity.class);
                intent.putExtra("title", "대중교통 이용 중 몰카가 의심된다면");
                startActivity(intent);
            }
        });

        manual_molca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "molca clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(getActivity(), MagazineReportDetailActivity.class);
                intent.putExtra("title", "몰카 유포 시 대처법");
                startActivity(intent);
            }
        });


        cardnewsDataList = new ArrayList<CardnewsListElement>();
        cardnewsDataList.add(new CardnewsListElement(R.drawable.square_dummy, "카드뉴스1"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.square_dummy, "카드뉴스2"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.square_dummy, "카드뉴스3"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.square_dummy, "카드뉴스4"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.square_dummy, "카드뉴스5"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.square_dummy, "카드뉴스6"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.square_dummy, "카드뉴스7"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.square_dummy, "카드뉴스8"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.square_dummy, "카드뉴스9"));
        cardnewsDataList.add(new CardnewsListElement(R.drawable.square_dummy, "카드뉴스10"));

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
