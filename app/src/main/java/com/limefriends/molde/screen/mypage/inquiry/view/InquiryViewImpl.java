package com.limefriends.molde.screen.mypage.inquiry.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import com.limefriends.molde.R;
import com.limefriends.molde.common.Constant;
import com.limefriends.molde.common.util.RegexUtil;
import com.limefriends.molde.model.entity.faq.FaqEntity;
import com.limefriends.molde.screen.common.recyclerviewHelper.adapter.RecyclerViewAdapter;
import com.limefriends.molde.screen.common.recyclerviewHelper.addOnRecycler.AddOnScrollRecyclerView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.ItemViewType;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.toolbar.NestedToolbar;
import com.limefriends.molde.screen.common.view.BaseObservableView;
import com.limefriends.molde.screen.common.view.ViewFactory;

import java.util.List;


public class InquiryViewImpl
        extends BaseObservableView<InquiryView.Listener> implements InquiryView, RecyclerViewAdapter.OnItemClickListener {

    private Button faq_button;
    private EditText faq_content;
    private EditText faq_email_input;
    private EditText faq_email_self_input;
    private Spinner faq_email_select;
    private ImageView faq_self_close_button;
    private Button faq_send_button;
    private ProgressBar progressBar;
    private RelativeLayout inquire_container;
    private AddOnScrollRecyclerView inquiry_recyclerview;
    private Toolbar mToolbar;
    private NestedToolbar mNestedToolbar;

    private ViewFactory mViewFactory;
    private ToastHelper mToastHelper;
    private RecyclerViewAdapter<FaqEntity> inquiryAdapter;

    public InquiryViewImpl(LayoutInflater inflater,
                           ViewGroup parent,
                           ViewFactory viewFactory,
                           ToastHelper toastHelper) {

        this.mViewFactory = viewFactory;
        this.mToastHelper = toastHelper;

        setRootView(inflater.inflate(R.layout.activity_inquiry, parent, false));

        setupViews();

        setupToolbar();
    }


    @Override
    public void showProgressIndication() {
        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void hideProgressIndication() {
        progressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    public void bindAuthority(long authority) {
        if (authority == Constant.Authority.ADMIN) {
            inquire_container.setVisibility(View.GONE);
            inquiry_recyclerview.setVisibility(View.VISIBLE);
            setupInquiryList();
        } else {
            setupListener();
            setupEmailSpinner();
        }
    }


    private void setupViews() {
        faq_button = findViewById(R.id.faq_button);
        faq_content = findViewById(R.id.faq_content);
        faq_email_input = findViewById(R.id.faq_email_input);
        faq_email_self_input = findViewById(R.id.faq_email_self_input);
        faq_email_select = findViewById(R.id.faq_email_select);
        faq_self_close_button = findViewById(R.id.faq_self_close_button);
        faq_send_button = findViewById(R.id.faq_send_button);
        progressBar = findViewById(R.id.inquiry_progress);
        inquire_container = findViewById(R.id.inquire_container);
        inquiry_recyclerview = findViewById(R.id.inquiry_recyclerview);
    }


    private void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mNestedToolbar = mViewFactory.newInstance(NestedToolbar.class, mToolbar);
        mToolbar.addView(mNestedToolbar.getRootView());
        mNestedToolbar.setTitle(getContext().getText(R.string.inquire).toString());
    }


    private void setupListener() {

        mNestedToolbar.enableUpButtonAndListen(() -> {
            for (Listener listener : getListeners()) {
                listener.onNavigateUpClicked();
            }
        });

        faq_button.setOnClickListener(view -> {

        });

        // 뒤로가기
        faq_self_close_button.setOnClickListener(v -> {
            hideSoftKeyboard();
            hideSelfEmailInput();
        });

        // 전송하기
        faq_send_button.setOnClickListener(v -> {
            String faqEmail = "";
            if (faq_email_select.getVisibility() == View.GONE &&
                    faq_email_self_input.getVisibility() == View.VISIBLE) {
                faqEmail = faq_email_input.getText().toString()
                        + "@" + faq_email_self_input.toString();
            } else if (faq_email_select.getVisibility() == View.VISIBLE &&
                    faq_email_self_input.getVisibility() == View.GONE) {
                faqEmail = faq_email_input.getText().toString()
                        + "@" + faq_email_select.getSelectedItem().toString();
            }

            String faqContent = faq_content.getText().toString();

            if (faqContent.equals("")) {
                mToastHelper.showSnackBar(inquire_container,
                        getContext().getText(R.string.snackbar_content_absent).toString());
                return;
            }
            if (!faq_email_input.getText().toString().equals("")) {
                if (!RegexUtil.validateEmail(faqEmail)) {
                    mToastHelper.showSnackBar(inquire_container,
                            getContext().getText(R.string.snackbar_invalid_email).toString());
                    return;
                }
            }
            for (Listener listener : getListeners()) {
                listener.onSubmitInquireClicked(faqContent, faqEmail);
            }
        });
    }


    private void setupEmailSpinner() {
        ArrayAdapter emailArrayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.email_select, android.R.layout.simple_spinner_item);
        faq_email_select.setAdapter(emailArrayAdapter);

        faq_email_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    faq_email_select.setSelected(false);
                    return;
                }
                if (position == faq_email_select.getAdapter().getCount() - 1) {
                    faq_email_select.setVisibility(View.GONE);
                    faq_email_self_input.setVisibility(View.VISIBLE);
                    faq_self_close_button.setVisibility(View.VISIBLE);
                    faq_self_close_button.bringToFront();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager
                = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(inquire_container.getWindowToken(), 0);
    }


    private void hideSelfEmailInput() {
        faq_email_self_input.setVisibility(View.GONE);
        faq_self_close_button.setVisibility(View.GONE);
        faq_email_self_input.setText("");
        faq_email_select.setVisibility(View.VISIBLE);
        faq_email_select.bringToFront();
        faq_email_select.setSelection(0);
    }


    private void setupInquiryList() {
        inquiry_recyclerview.setLayoutManager(new LinearLayoutManager(getContext()), false);
        inquiryAdapter = new RecyclerViewAdapter<>(mViewFactory, ItemViewType.INQUIRY);
        inquiry_recyclerview.setAdapter(inquiryAdapter);
        inquiryAdapter.setOnItemClickListener(this);
    }


    @Override
    public void bindInquiry(List<FaqEntity> entityList) {
        inquiryAdapter.addData(entityList);
    }


    @Override
    public void onItemClicked(int itemId) {
        for (Listener listener : getListeners()) {
            listener.onInquireClicked(itemId);
        }
    }
}