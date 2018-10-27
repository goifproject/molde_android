package com.limefriends.molde.screen.common.screensNavigator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.limefriends.molde.common.manager.cameraHelper.MoldeReportCameraActivity;
import com.limefriends.molde.screen.magazine.comment.CardNewsCommentActivity;
import com.limefriends.molde.screen.magazine.detail.CardNewsDetailActivity;
import com.limefriends.molde.screen.magazine.info.HowToDetectActivity;
import com.limefriends.molde.screen.magazine.info.HowToRespondActivity;
import com.limefriends.molde.screen.magazine.info.RecentMolcaInfoActivity;
import com.limefriends.molde.screen.map.report.ReportActivity;

import static com.limefriends.molde.common.Constant.Common.EXTRA_KEY_ACTIVITY_NAME;
import static com.limefriends.molde.common.Constant.Common.EXTRA_KEY_CARDNEWS_ID;
import static com.limefriends.molde.common.Constant.MyPage.INTENT_VALUE_MYCOMMENT;
import static com.limefriends.molde.common.Constant.Scrap.INTENT_VALUE_SCRAP;

public class ActivityScreenNavigator {

    private final Context mContext;

    public ActivityScreenNavigator(Context mContext) {
        this.mContext = mContext;
    }

    public void toRecentMolcaInfoActivity() {
        RecentMolcaInfoActivity.start(mContext);
    }

    public void toHowToDetectActivity() {
        HowToDetectActivity.start(mContext);
    }

    public void toHowToRespondActivity() {
        HowToRespondActivity.start(mContext);
    }

    public void toCardNewsCommentActivity(int cardNewsId, String description) {
        CardNewsCommentActivity.start(mContext, cardNewsId, description);
    }

    // 아무리 생각해도 Activity 를 static 으로 넘겨주는 것은 너무 위험하다
    public void toCardNewsDetailActivity(Context context, int newsId) {
        Intent intent = new Intent(context, CardNewsDetailActivity.class);
        intent.putExtra(EXTRA_KEY_CARDNEWS_ID, newsId);
        intent.putExtra(EXTRA_KEY_ACTIVITY_NAME, INTENT_VALUE_MYCOMMENT);
        context.startActivity(intent);
    }

    public void toCardNewsDetailActivity(Activity activity, int newsId, int reqCode) {
        Intent intent = new Intent(activity, CardNewsDetailActivity.class);
        intent.putExtra(EXTRA_KEY_CARDNEWS_ID, newsId);
        intent.putExtra(EXTRA_KEY_ACTIVITY_NAME, INTENT_VALUE_SCRAP);
        activity.startActivityForResult(intent, reqCode);
    }

    public void toMoldeReportCameraActivity(ReportActivity activity, int imageSeq, int size, int reqCode) {
        Intent intent = new Intent();
        intent.setClass(activity, MoldeReportCameraActivity.class);
        intent.putExtra("imageSeq", imageSeq);
        intent.putExtra("imageArraySize", size);
        activity.startActivityForResult(intent, reqCode);
    }

}
