package com.limefriends.molde.comm.manager.gallery_manager;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.limefriends.molde.R;
import com.limefriends.molde.ui.map.report.MoldeReportActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeReportGalleryActivity extends AppCompatActivity {
    @BindView(R.id.recyclerGallery)
    RecyclerView recyclerGallery;
    TextView gallery_toolbar_title;
    Button gallery_done_button;

    private GalleryAdapter galleryAdapter;

    public final int allImageSize = 5;
    public int lastImageSize = 0;
    public GalleryManager mGalleryManager;
    public int imageArraySize;
    public int imageSeq = 0;

    private String reportContent;
    private String reportAddress;
    private String reportDetailAddress;
    private String reportEmailName;
    private String reportEmailDomainPosition;
    private String reportLat;
    private String reportLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_molde_report_gallery);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        gallery_done_button = getSupportActionBar().getCustomView().findViewById(R.id.done_button);
        gallery_done_button.setVisibility(View.VISIBLE);
        gallery_done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> pathList = selectDone();
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MoldeReportActivity.class);
                intent.putStringArrayListExtra("imagePathList", pathList);

                intent.putExtra("reportContent", reportContent);
                intent.putExtra("reportAddress", reportAddress);
                intent.putExtra("reportDetailAddress", reportDetailAddress);
                intent.putExtra("reportEmailName", reportEmailName);
                intent.putExtra("reportEmailDomainPosition", reportEmailDomainPosition);
                intent.putExtra("reportLat", reportLat);
                intent.putExtra("reportLng", reportLng);

                setResult(RESULT_OK, intent);
                finish();

//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
            }
        });
        gallery_toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);

        //gallery_toolbar_title.setText("갤러리");

        Intent intent = getIntent();
        imageSeq = intent.getIntExtra("imageSeq", 1);
        imageArraySize = intent.getIntExtra("imageArraySize", 1);
        lastImageSize = allImageSize - imageArraySize;
        gallery_toolbar_title.setText(lastImageSize + "장 추가 가능");
        reportContent = intent.getStringExtra("reportContent");
        reportAddress = intent.getStringExtra("reportAddress");
        reportDetailAddress = intent.getStringExtra("reportDetailAddress");
        reportEmailName = intent.getStringExtra("reportEmailName");
        reportEmailDomainPosition = intent.getStringExtra("reportEmailDomainPosition");
        reportLat = intent.getStringExtra("reportLat");
        reportLng = intent.getStringExtra("reportLng");
        initRecyclerGallery();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    /**
     * 갤러리 이미지 데이터 초기화
     */
    private List<PhotoVO> initGalleryPathList() {
        mGalleryManager = new GalleryManager(getApplicationContext());
        List<PhotoVO> galleryList = mGalleryManager.getAllPhotoPathList();
        Collections.reverse(galleryList);
        return galleryList;
    }

    /**
     * 확인 버튼 선택 시
     */
    private ArrayList<String> selectDone() {
        List<PhotoVO> selectedPhotoList = galleryAdapter.getSelectedPhotoList();
        ArrayList<String> pathList = new ArrayList<String>();
        for (int i = 0; i < selectedPhotoList.size(); i++) {
            Log.i("selectedPhotoList", selectedPhotoList.get(i).getImgPath());
            pathList.add(selectedPhotoList.get(i).getImgPath());
        }
        return pathList;
    }


    /**
     * 갤러리 리사이클러뷰 초기화
     */
    private void initRecyclerGallery() {
        galleryAdapter = new GalleryAdapter(MoldeReportGalleryActivity.this, initGalleryPathList(), R.layout.map_gallery_item);
        galleryAdapter.setOnItemClickListener(mOnItemClickListener);
        recyclerGallery.setAdapter(galleryAdapter);
        recyclerGallery.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerGallery.setItemAnimator(new DefaultItemAnimator());
        recyclerGallery.addItemDecoration(new GridDividerDecoration(getResources(), R.drawable.frame_gallery));
    }


    /**
     * 리사이클러뷰 아이템 선택시 호출 되는 리스너
     */
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void OnItemClick(GalleryAdapter.PhotoViewHolder photoViewHolder, int position) {
            PhotoVO photoVO = galleryAdapter.getmPhotoList().get(position);
            if (photoVO.isSelected()) {
                photoVO.setSelected(false);
                lastImageSize++;
            } else {
                if (lastImageSize == 0) {
                    Toast.makeText(getApplicationContext(), "이미 선택할 수 있는 이미지 수를 넘었습니다.", Toast.LENGTH_SHORT).show();
                    lastImageSize = 0;
                    gallery_toolbar_title.setText(lastImageSize + "장 추가 가능");
                    return;
                }
                photoVO.setSelected(true);
                lastImageSize--;
            }
            gallery_toolbar_title.setText(lastImageSize + "장 추가 가능");
            galleryAdapter.getmPhotoList().set(position, photoVO);
            galleryAdapter.notifyDataSetChanged();
        }
    };

}

