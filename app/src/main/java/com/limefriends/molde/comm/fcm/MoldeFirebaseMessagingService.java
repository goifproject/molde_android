package com.limefriends.molde.comm.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.limefriends.molde.R;
import com.limefriends.molde.comm.utils.PreferenceUtil;
import com.limefriends.molde.ui.MoldeMainActivity;
import com.limefriends.molde.ui.feed.FeedDetailActivity;

import java.util.Map;

import static com.limefriends.molde.comm.Constant.Common.ALLOW_PUSH;
import static com.limefriends.molde.comm.Constant.Common.DISALLOW_PUSH;
import static com.limefriends.molde.comm.Constant.Common.PREF_KEY_FEED_CHANGE_PUSH;
import static com.limefriends.molde.comm.Constant.Common.PREF_KEY_NEW_FEED_PUSH;
import static com.limefriends.molde.comm.Constant.Feed.EXTRA_KEY_FEED_ID;
import static com.limefriends.molde.comm.Constant.MoldeMain.FROM_NOTIFICATION;

// TODO 시간 기다리는 문제
// TODO 오레오부터 바뀐 채널 문제
public class MoldeFirebaseMessagingService extends FirebaseMessagingService {

    public static final int TYPE_REPORT_STATE_CHANGE = 1;
    public static final int TYPE_NEW_FEED = 0;

    /**
     * Called when message is received.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // FCM에서 메시지를 전달할 경우 앱이 백그라운드에 있을 때 안드로이드 OS 에서 바로 FCM을 실행시켜
        // 프로그래밍 해 놓은 코드를 실행시키지 못하고 계속 default 값으로만 가능하다
        // 앱 서버(임시로 포스트맨)에서 firebase 서버에 POST로
        // 헤더 : 권한 설정, 토큰 값
        // 바디 : 페이로드(4kb)
        // 를 넘겨주면 포어그라운드, 백그라운드 모두 아래 코드가 실행된다.

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e("토큰호출", "Message data payload: "+remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();
            int type = Integer.parseInt(data.get("type"));
            int feedId = Integer.parseInt(data.get("feedId"));
            // String title = data.get("title");
            // String message = data.get("content");

            if (type == TYPE_NEW_FEED) {
                int push = PreferenceUtil.getInt(this, PREF_KEY_NEW_FEED_PUSH, DISALLOW_PUSH);
                if (push == ALLOW_PUSH)
                sendNotification(type, feedId, "새로운 신고", "즐겨찾기 지역에 신고가 발생했습니다.");
            }
            else if (type == TYPE_REPORT_STATE_CHANGE) {
                int push = PreferenceUtil.getInt(this, PREF_KEY_FEED_CHANGE_PUSH, DISALLOW_PUSH);
                if (push == ALLOW_PUSH)
                sendNotification(type, feedId, "신고 상태 변화", "신고한 피드 상태가 변경되었습니다.");
            }



//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }
        }
    }


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MoldeJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Persist token to third-party servers.
     */
    private void sendRegistrationToServer(String token) {
        // TODO 파이어베이스에 올려놓을 것
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(int type, int feedId, String title, String message) {
        NotificationCompat.Builder mBuilder = createNotification(title, message);
//        if (type == TYPE_NEW_FEED) {
//            mBuilder.setContentIntent(createGoMainPendingIntent());
//        }
//        else if (type == TYPE_REPORT_STATE_CHANGE) {
            mBuilder.setContentIntent(createGoFeedPendingIntent(feedId));
//        }
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            mNotificationManager.createNotificationChannel(channel);
        }
        mNotificationManager.notify(1, mBuilder.build());
    }

    private NotificationCompat.Builder createNotification(String title, String message) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_noti_large);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_noti_small)
                .setLargeIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        return builder;
    }

    private PendingIntent createGoFeedPendingIntent(int feedId) {
        Intent resultIntent = new Intent(this, FeedDetailActivity.class);
        resultIntent.putExtra(EXTRA_KEY_FEED_ID, feedId);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MoldeMainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createGoMainPendingIntent() {
        Intent intent = new Intent(this, MoldeMainActivity.class);
        intent.putExtra("origin", FROM_NOTIFICATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    }

}
