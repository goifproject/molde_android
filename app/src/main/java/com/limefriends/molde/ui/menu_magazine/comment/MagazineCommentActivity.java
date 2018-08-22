package com.limefriends.molde.ui.menu_magazine.comment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.R;
import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.comm.custom.recyclerview.AddOnScrollRecyclerView;
import com.limefriends.molde.entity.comment.MoldeCommentEntity;
import com.limefriends.molde.entity.comment.MoldeCommentResponseInfoEntity;
import com.limefriends.molde.entity.comment.MoldeCommentResponseInfoEntityList;
import com.limefriends.molde.entity.response.Result;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.remote.MoldeRestfulService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MagazineCommentActivity extends AppCompatActivity {

    @BindView(R.id.comment_layout)
    RelativeLayout comment_layout;
    @BindView(R.id.comment_swipe_layout)
    SwipeRefreshLayout comment_swipe_layout;
    @BindView(R.id.comment_list_view)
    AddOnScrollRecyclerView comment_list_view;
    @BindView(R.id.comment_input)
    EditText comment_input;
    @BindView(R.id.comment_send_button)
    Button comment_send_button;

    private TextView toolbar_title;

    MagazineCommentRecyclerAdapter magazineCommentRecyclerAdapter;
    List<MoldeCommentEntity> commentDataList;
    InputMethodManager inputMethodManager;

    private static final int PER_PAGE = 20;
    private static final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;
    private boolean isLoggedIn = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magazine_activity_comment);
        ButterKnife.bind(this);

        final int cardNewsId = getIntent().getIntExtra("cardNewsId", 0);
        String description = getIntent().getStringExtra("description");


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText(description);


        /* set dummy datalist */
        // 실제로는 서버에서 데이터 받아오기
        commentDataList = new ArrayList<>();
        commentDataList.add(new MoldeCommentEntity(0, "userId", "lkj", 1, "세상에 이런 일이 다 있네요ㅠㅠ", "2018.04.11 11:14"));
        commentDataList.add(new MoldeCommentEntity(0, "userId", "lkj", 1, "세상에 이런 일이 다 있네요ㅠㅠ", "2018.04.11 11:14"));
        commentDataList.add(new MoldeCommentEntity(0, "userId", "lkj", 1, "세상에 이런 일이 다 있네요ㅠㅠ", "2018.04.11 11:14"));
        commentDataList.add(new MoldeCommentEntity(0, "userId", "lkj", 1, "세상에 이런 일이 다 있네요ㅠㅠ", "2018.04.11 11:14"));
        commentDataList.add(new MoldeCommentEntity(0, "userId", "lkj", 1, "세상에 이런 일이 다 있네요ㅠㅠ", "2018.04.11 11:14"));


        magazineCommentRecyclerAdapter = new MagazineCommentRecyclerAdapter(this, this);

        comment_list_view.setAdapter(magazineCommentRecyclerAdapter);

        comment_list_view.setLayoutManager(new LinearLayoutManager(this), false);

        comment_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(comment_input.getWindowToken(), 0);
            }
        });


        /* 댓글 게시 */
        comment_send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 실제 로그인 상태 확인 구현
                // 로그인 된 상태라면 -> 댓글 등록
                FirebaseAuth auth = ((MoldeApplication)getApplication()).getFireBaseAuth();
                if (auth != null && auth.getUid() != null) {
                    addToComment("lkj", "이기정", cardNewsId, comment_input.getText().toString(), String.valueOf(System.currentTimeMillis()));
                    comment_input.setText("");
                } else {
                    Snackbar.make(comment_layout, "몰디 로그인이 필요합니다!", Snackbar.LENGTH_SHORT).show();
                }
//                if (isLoggedIn) {
//                    addToComment("lkj", "이기정", cardNewsId, comment_input.getText().toString(), String.valueOf(System.currentTimeMillis()));
//                    comment_input.setText("");
//                } else {
//                    Intent intent = new Intent(MagazineCommentActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                }
                // 로그인 안 된 상태라면 -> 로그인 화면으로 넘어가기
                // 서버에 데이터 넘겨주고 콜백함수로 하위에 댓글item 추가
                // 아래로 끌어당기면 새로고침 되도록 만들기

            }
        });


        /* 당겨서 새로고침 */
        comment_swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 댓글목록 새로 불러오기!
                Toast.makeText(getApplicationContext(), "refresh", Toast.LENGTH_SHORT).show();
                comment_swipe_layout.setRefreshing(false);
            }
        });

        loadComment(cardNewsId, PER_PAGE, FIRST_PAGE);

    }

    public void showSnack() {
        Snackbar.make(comment_layout, "댓글이 신고되었습니다.", 300).show();
    }

    private void loadComment(int cardNewsId, int perPage, int page) {

        // Log.e("호출확인2", "magazine fragment");

        // 1. 더 이상 불러올 데이터가 없는지 확인
        if (!hasMoreToLoad) return;

        // 2. 불러온다면 프로그래스바를 띄움
        // magazineCommentRecyclerAdapter.setProgressMore(true);

        // 3. 스크롤에 의해서 다시 호출될 수 있기 때문에 로딩중임을 명시해 줌
        comment_list_view.setIsLoading(true);

        // Log.e("호출확인3", "magazine fragment");

        MoldeRestfulService.Comment commentService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Comment.class);

        // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.
        Call<MoldeCommentResponseInfoEntityList> call = commentService.getNewsComment(cardNewsId, perPage, page);

        call.enqueue(new Callback<MoldeCommentResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<MoldeCommentResponseInfoEntityList> call, Response<MoldeCommentResponseInfoEntityList> response) {
                // Log.e("호출확인4", "magazine fragment");
                // 4. 호출 후 데이터 정리
                List<MoldeCommentEntity> entities = fromSchemaToLocalEntity(response.body().getData());
                // 5. 데이터가 세팅되기 이전에 프로그래스 바 세팅
                // magazineCommentRecyclerAdapter.setProgressMore(false);
                // 6. 데이터 추가
                magazineCommentRecyclerAdapter.addData(entities);
                // 7. 추가 완료 후 다음 페이지로 넘어가도록 세팅
                currentPage++;
                // 8. 더 이상 데이터를 세팅중이 아님을 명시
                comment_list_view.setIsLoading(false);
                // 9. 만약 불러온 데이터가 하나의 페이지에 들어가야 할 수보다 작으면 마지막 데이터인 것이므로 더 이상 데이터를 불러오지 않는다.
                if (entities.size() < PER_PAGE) {
                    // Log.e("호출확인5", "magazine fragment");
                    setHasMoreToLoad(false);
                }
            }

            @Override
            public void onFailure(Call<MoldeCommentResponseInfoEntityList> call, Throwable t) {

            }
        });
    }

    private void addToComment(final String userId, final String userName, final int newsId, final String content, final String regiDate) {
        MoldeRestfulService.Comment commentService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Comment.class);

        // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.
        Call<Result> call = commentService.createNewComment(userId, userName, newsId, content, regiDate);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    magazineCommentRecyclerAdapter.addData(new MoldeCommentEntity(
                            userId, userName, newsId, content, regiDate
                    ));
                    Toast.makeText(getApplicationContext(), "댓글 등록", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });


    }

    private List<MoldeCommentEntity> fromSchemaToLocalEntity(List<MoldeCommentResponseInfoEntity> schemas) {
        List<MoldeCommentEntity> entities = new ArrayList<>();
        for (MoldeCommentResponseInfoEntity schema : schemas) {
            entities.add(new MoldeCommentEntity(
                    schema.getCommId(),
                    schema.getUserId(),
                    schema.getUserName(),
                    schema.getNewsId(),
                    schema.getComment(),
                    schema.getCommDate()
            ));
        }
        return entities;
    }

    // 4. setHasMoreToLoad
    private void setHasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    /**
     * TODO 생명주기 관리가 전혀 안 됨. 왜 이게 어쩔 때는 저장되었다가 어쩔 때는 원상태인지 파악이 안 됨
     */
    // 5. 생명주기
//    @Override
//    public void onPause() {
//        super.onPause();
//        setHasMoreToLoad(true);
//        currentPage = 0;
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setHasMoreToLoad(true);
        currentPage = 0;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}
