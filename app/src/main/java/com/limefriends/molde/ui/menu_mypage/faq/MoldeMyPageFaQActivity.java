package com.limefriends.molde.ui.menu_mypage.faq;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.entity.faq.FaqEntitiy;
import com.limefriends.molde.entity.faq.FaqResponseInfoEntity;
import com.limefriends.molde.entity.faq.FaqResponseInfoEntityList;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.remote.MoldeNetwork;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoldeMyPageFaQActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.faq1)
    TextView faq1;
    @BindView(R.id.faq2)
    TextView faq2;
    @BindView(R.id.faq3)
    TextView faq3;
    @BindView(R.id.faq4)
    TextView faq4;
    @BindView(R.id.faq5)
    TextView faq5;
    @BindView(R.id.answer1)
    TextView answer1;
    @BindView(R.id.answer2)
    TextView answer2;
    @BindView(R.id.answer3)
    TextView answer3;
    @BindView(R.id.answer4)
    TextView answer4;
    @BindView(R.id.answer5)
    TextView answer5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_activity_faq);

        ButterKnife.bind(this);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText("자주 묻는 질문");

        findViewById(R.id.faq1).setOnClickListener(this);
        findViewById(R.id.faq2).setOnClickListener(this);
        findViewById(R.id.faq3).setOnClickListener(this);
        findViewById(R.id.faq4).setOnClickListener(this);
        findViewById(R.id.faq5).setOnClickListener(this);

        loadFaq();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.faq1:
                answer1.setVisibility(View.VISIBLE);
                answer2.setVisibility(View.GONE);
                answer3.setVisibility(View.GONE);
                answer4.setVisibility(View.GONE);
                answer5.setVisibility(View.GONE);
                break;

            case R.id.faq2:
                answer1.setVisibility(View.GONE);
                answer2.setVisibility(View.VISIBLE);
                answer3.setVisibility(View.GONE);
                answer4.setVisibility(View.GONE);
                answer5.setVisibility(View.GONE);
                break;

            case R.id.faq3:
                answer1.setVisibility(View.GONE);
                answer2.setVisibility(View.GONE);
                answer3.setVisibility(View.VISIBLE);
                answer4.setVisibility(View.GONE);
                answer5.setVisibility(View.GONE);
                break;

            case R.id.faq4:
                answer1.setVisibility(View.GONE);
                answer2.setVisibility(View.GONE);
                answer3.setVisibility(View.GONE);
                answer4.setVisibility(View.VISIBLE);
                answer5.setVisibility(View.GONE);
                break;

            case R.id.faq5:
                answer1.setVisibility(View.GONE);
                answer2.setVisibility(View.GONE);
                answer3.setVisibility(View.GONE);
                answer4.setVisibility(View.GONE);
                answer5.setVisibility(View.VISIBLE);
                break;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    private void loadFaq() {

        MoldeRestfulService.Faq faqService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Faq.class);

        // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.
        Call<FaqResponseInfoEntityList> call = faqService.getMyFaqList();

        call.enqueue(new Callback<FaqResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<FaqResponseInfoEntityList> call, Response<FaqResponseInfoEntityList> response) {
                if (response.isSuccessful()) {
                    List<FaqEntitiy> entitiys = fromSchemaToLocalEntity(response.body().getData());

                }
            }

            @Override
            public void onFailure(Call<FaqResponseInfoEntityList> call, Throwable t) {

            }
        });

    }

    private List<FaqEntitiy> fromSchemaToLocalEntity(List<FaqResponseInfoEntity> schemas) {
        List<FaqEntitiy> entities = new ArrayList<>();
        for (FaqResponseInfoEntity schema : schemas) {
            entities.add(new FaqEntitiy(
                    schema.getFaqId(),
                    schema.getUserId(),
                    schema.getUserName(),
                    schema.getFaqContents(),
                    schema.getFaqEmail()
            ));
        }
        return entities;
    }

}
