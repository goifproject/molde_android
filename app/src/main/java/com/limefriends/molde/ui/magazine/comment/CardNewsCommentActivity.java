package com.limefriends.molde.ui.magazine.comment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.R;
import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.comm.custom.addOnListview.AddOnScrollRecyclerView;
import com.limefriends.molde.comm.custom.addOnListview.OnLoadMoreListener;
import com.limefriends.molde.entity.FromSchemaToEntitiy;
import com.limefriends.molde.entity.comment.CommentEntity;
import com.limefriends.molde.entity.comment.CommentResponseInfoEntity;
import com.limefriends.molde.entity.comment.CommentResponseInfoEntityList;
import com.limefriends.molde.entity.response.Result;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.remote.MoldeRestfulService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// TODO "lkj" 변경할 것 -> uId
// TODO auth Application 에서 가져다 쓸 것
// TODO swipe refresh 적용할까
// TODO snack 에서 showSnack 이 아니라 댓글 신고로 넘어가야 함
public class CardNewsCommentActivity extends AppCompatActivity {

    public static final int REPORT_COMMENT_EXIST = 2;
    public static final int REPORT_COMMENT_DONE = 1;
    @BindView(R.id.comment_layout)
    RelativeLayout comment_layout;
    @BindView(R.id.comment_list_view)
    AddOnScrollRecyclerView comment_list_view;
    @BindView(R.id.comment_input)
    EditText comment_input;
    @BindView(R.id.comment_send_button)
    Button comment_send_button;

    private CardNewsCommentRecyclerAdapter cardNewsCommentRecyclerAdapter;
    private MoldeRestfulService.Comment commentService;

    private static final int PER_PAGE = 20;
    private static final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;
    private int cardNewsId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardnews_comment);

        setupViews();

        setupListeners();

        setupCommentList();

        setupData();
    }

    //-----
    // View
    //-----

    private void setupViews() {
        ButterKnife.bind(this);
        String description = getIntent().getStringExtra("description");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title
                = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setSingleLine();
        toolbar_title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        toolbar_title.setText(description);
    }

    private void setupListeners() {

        comment_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(comment_input.getWindowToken(), 0);
            }
        });

        comment_send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 실제 로그인 상태 확인 구현
                // 로그인 된 상태라면 -> 댓글 등록
                FirebaseAuth auth = ((MoldeApplication)getApplication()).getFireBaseAuth();
                if (auth != null && auth.getUid() != null) {
                    String uId = auth.getCurrentUser().getUid();
                    String name = auth.getCurrentUser().getDisplayName();
                    if (name == null) name = auth.getCurrentUser().getEmail();
                    addToComment(uId, name,
                            cardNewsId, comment_input.getText().toString(),
                            String.valueOf(System.currentTimeMillis()));
                    comment_input.setText("");
                } else {
                    showSnack("몰디 로그인이 필요합니다!");
                }
            }
        });
    }

    private void setupCommentList() {
        cardNewsCommentRecyclerAdapter
                = new CardNewsCommentRecyclerAdapter(this, this);
        comment_list_view.setAdapter(cardNewsCommentRecyclerAdapter);
        comment_list_view.setLayoutManager(new LinearLayoutManager(this), false);
        comment_list_view.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                loadComment(cardNewsId, PER_PAGE, currentPage);
            }
        });
    }

    public void showSnack(String message) {
        Snackbar.make(comment_layout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    //--------
    // Network
    //--------

    private void setupData() {
        cardNewsId = getIntent().getIntExtra("cardNewsId", 0);
        loadComment(cardNewsId, PER_PAGE, FIRST_PAGE);
    }

    private MoldeRestfulService.Comment getCommentService() {
        if (commentService == null) {
            commentService
                    = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Comment.class);
        }
        return commentService;
    }

    private void loadComment(int cardNewsId, int perPage, int page) {

        if (!hasMoreToLoad) return;

        comment_list_view.setIsLoading(true);

        Call<CommentResponseInfoEntityList> call = getCommentService()
                .getNewsComment(cardNewsId, perPage, page);
        call.enqueue(new Callback<CommentResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<CommentResponseInfoEntityList> call,
                                   Response<CommentResponseInfoEntityList> response) {

                List<CommentResponseInfoEntity> schemas = response.body().getData();

                // 8. 더 이상 데이터를 세팅중이 아님을 명시
                comment_list_view.setIsLoading(false);

                if (schemas == null || schemas.size() == 0) {
                    setHasMoreToLoad(false);
                    return;
                }

                // 4. 호출 후 데이터 정리
                List<CommentEntity> entities
                        = FromSchemaToEntitiy.comment(schemas);
                // 6. 데이터 추가
                cardNewsCommentRecyclerAdapter.addData(entities);
                // 7. 추가 완료 후 다음 페이지로 넘어가도록 세팅
                currentPage++;

                // 9. 만약 불러온 데이터가 하나의 페이지에 들어가야 할 수보다 작으면 마지막 데이터인 것이므로 더 이상 데이터를 불러오지 않는다.
                if (entities.size() < PER_PAGE) {
                    setHasMoreToLoad(false);
                }
            }

            @Override
            public void onFailure(Call<CommentResponseInfoEntityList> call, Throwable t) {

            }
        });
    }

    private void addToComment(final String userId, final String userName,
                              final int newsId, final String content, final String regiDate) {
        Call<Result> call = getCommentService()
                .createNewComment(userId, userName, newsId, content, regiDate);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    cardNewsCommentRecyclerAdapter.addData(new CommentEntity(
                            userId, userName, newsId, content, regiDate
                    ));
                    Log.e("호출 확인", "댓글 등록 확인");
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }
    
    public void reportComment(int commentId) {

        FirebaseAuth auth = ((MoldeApplication)getApplication()).getFireBaseAuth();
        if (auth != null && auth.getUid() != null) {

            Call<Result> call = getCommentService()
                    .reportComment(auth.getCurrentUser().getUid(), commentId);

            call.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if (response.isSuccessful()) {
                        int resultCode = response.body().getResult();
                        switch (resultCode) {
                            case REPORT_COMMENT_DONE:
                                showSnack("댓글이 신고되었습니다.");
                                break;
                            case REPORT_COMMENT_EXIST:
                                showSnack("이미 신고한 댓글입니다.");
                                break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {

                }
            });

        } else {
            showSnack("몰디 로그인이 필요합니다!");
        }
    }

    private void setHasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    //----------
    // lifecycle
    //----------

    @Override
    public void onDestroy() {
        super.onDestroy();
        setHasMoreToLoad(true);
        currentPage = 0;
    }



}
