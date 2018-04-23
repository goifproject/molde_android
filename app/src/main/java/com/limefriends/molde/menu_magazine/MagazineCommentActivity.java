package com.limefriends.molde.menu_magazine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.limefriends.molde.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by joo on 2018. 4. 13..
 */

/*** make a pair with magazine_activity_comment.xml ***/


public class MagazineCommentActivity extends AppCompatActivity {

    @BindView(R.id.layout_activity_comment_wrapper)
    RelativeLayout layout_activity_comment_wrapper;

    @BindView(R.id.comment_recyclerview)
    RecyclerView comment_recyclerview;

    @BindView(R.id.edittext_comment)
    EditText edittext_comment;

    CommentRecyclerAdapter adapter;
    List<CommentListElement> commentDataList;
    InputMethodManager imm;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magazine_activity_comment);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String title = intent.getStringExtra("toolbarTitle");

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText(title);


        /* set dummy datalist */
        commentDataList = new ArrayList<CommentListElement>();
        commentDataList.add(new CommentListElement(R.drawable.img_dummy_profile,
                "user1", "2018.04.11 11:13", "세상에 이런 일이 다 있네요ㅠㅠ"));
        commentDataList.add(new CommentListElement(R.drawable.img_dummy_profile,
                "user2", "2018.04.11 11:14",
                "세상에 이런 일이 다 있네요ㅠㅠ 세상에 이런 일이 다 있네요ㅠㅠ 세상에 이런 일이 다 있네요ㅠㅠ 세상에 이런 일이 다 있네요ㅠㅠ 세상에 이런 일이 다 있네요ㅠㅠ 세상에 이런 일이 다 있네요ㅠㅠ"));
        commentDataList.add(new CommentListElement(R.drawable.img_dummy_profile,
                "user3", "2018.04.11 11:15", "세상에 이런 일이 다 있네요ㅠㅠ"));
        commentDataList.add(new CommentListElement(R.drawable.img_dummy_profile,
                "user4", "2018.04.11 11:16", "세상에 이런 일이 다 있네요ㅠㅠ"));


        adapter = new CommentRecyclerAdapter(getApplicationContext(),
                    R.layout.magazine_item_comment_recycler,
                    commentDataList,
                    layout_activity_comment_wrapper);
        comment_recyclerview.setAdapter(adapter);


        /* editText 이외의 부분을 터치하면 키패드가 내려가도록 함 TODO hide keyboard */
        layout_activity_comment_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edittext_comment.getWindowToken(), 0);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
