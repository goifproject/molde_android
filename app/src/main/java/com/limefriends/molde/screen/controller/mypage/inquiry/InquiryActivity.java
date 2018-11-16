package com.limefriends.molde.screen.controller.mypage.inquiry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.common.Constant;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.helper.PreferenceUtil;
import com.limefriends.molde.R;
import com.limefriends.molde.model.entity.faq.FaqEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.viewController.BaseActivity;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.view.mypage.inquiry.InquiryView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

import static com.limefriends.molde.common.Constant.Common.*;

public class InquiryActivity extends BaseActivity implements InquiryView.Listener {

    public static void start(Context context) {
        Intent intent = new Intent(context, InquiryActivity.class);
        context.startActivity(intent);
    }

    private final int PER_PAGE = 10;
    private final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;
    private boolean isLoading;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Service private Repository.Faq mFaqRepository;
    @Service private ToastHelper mToastHelper;
    @Service private ViewFactory mViewFactory;
    private InquiryView mInquiryView;
    private List<FaqEntity> currentlyShownFaqList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        mInquiryView = mViewFactory.newInstance(InquiryView.class, null);

        setContentView(mInquiryView.getRootView());
    }

    @Override
    protected void onStart() {
        super.onStart();

        mInquiryView.registerListener(this);

        long authority = PreferenceUtil.getLong(this, PREF_KEY_AUTHORITY, Constant.Authority.GUEST);

        mInquiryView.bindAuthority(authority);

        if (authority == Constant.Authority.ADMIN) {
            loadInquiry();
        }
    }

    private void inquire(String userId, String userName, String content, String email) {

        mCompositeDisposable.add(
                mFaqRepository
                        .createNewFaq(userId, userName, content, email)
                        .subscribe(
                                e -> {
                                    mInquiryView.hideProgressIndication();
                                    mToastHelper.showShortToast(getText(R.string.snackbar_inquire_accepted).toString());
                                    finish();
                                },
                                err -> {},
                                () -> {}
                        )
        );
    }

    private void loadInquiry() {

        if (!hasMoreToLoad) return;

        if (isLoading) return;

        isLoading = true;

        List<FaqEntity> data = new ArrayList<>();
        mCompositeDisposable.add(
                mFaqRepository
                        .getFaqList()
                        .subscribe(
                                data::addAll,
                                err -> { },
                                () -> {
                                    isLoading = false;

                                    if (data.size() == 0) {
                                        hasMoreToLoad(false);
                                        return;
                                    }

                                    mInquiryView.bindInquiry(data);
                                    currentlyShownFaqList.addAll(data);

                                    currentPage++;

                                    if (data.size() < PER_PAGE) {
                                        hasMoreToLoad(false);
                                    }
                                }
                        )
        );
    }

    private void hasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    @Override
    public void onNavigateUpClicked() {
        onBackPressed();
    }

    @Override
    public void onSubmitInquireClicked(String content, String email) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uId = auth.getUid();
        String userName = "";

        if (auth.getCurrentUser().getEmail() != null) {
            userName = auth.getCurrentUser().getEmail();
        }
        else if (auth.getCurrentUser().getDisplayName() != null) {
            userName = auth.getCurrentUser().getDisplayName();
        } else {
            userName = "no-name";
        }
        inquire(uId, userName, content, email);
    }

    @Override
    public void onInquireClicked(int itemId) {
        String sender = FirebaseAuth.getInstance().getUid();
        for (FaqEntity entity :  currentlyShownFaqList) {
            if (entity.getFaqId() == itemId) {
                String[] tos = {entity.getFaqEmail()};
                String[] me = {sender};
                Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                intent.putExtra(Intent.EXTRA_EMAIL, tos);
                intent.putExtra(Intent.EXTRA_CC, me);
                intent.putExtra(Intent.EXTRA_TEXT, entity.getFaqContents());
                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "선택"));
                break;
            }
        }
    }
}

