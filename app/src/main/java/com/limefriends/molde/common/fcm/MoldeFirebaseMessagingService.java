package com.limefriends.molde.common.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.limefriends.molde.R;
import com.limefriends.molde.common.helper.PreferenceUtil;
import com.limefriends.molde.screen.controller.main.MoldeMainActivity;
import com.limefriends.molde.screen.controller.feed.detail.FeedDetailActivity;

import java.util.HashMap;
import java.util.Map;

import static com.limefriends.molde.common.Constant.Common.ALLOW_PUSH;
import static com.limefriends.molde.common.Constant.Common.DISALLOW_PUSH;
import static com.limefriends.molde.common.Constant.Common.PREF_KEY_FCM_TOKEN;
import static com.limefriends.molde.common.Constant.Common.PREF_KEY_FEED_CHANGE_PUSH;
import static com.limefriends.molde.common.Constant.Common.PREF_KEY_NEW_FEED_PUSH;
import static com.limefriends.molde.common.Constant.Feed.EXTRA_KEY_FEED_ID;

// TODO 시간 기다리는 문제
// TODO 오레오부터 바뀐 채널 문제
public class MoldeFirebaseMessagingService extends FirebaseMessagingService {

    public static final int TYPE_REPORT_STATE_CHANGE = 1;
    public static final int TYPE_NEW_FEED = 0;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Called when message is received.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            int type = Integer.parseInt(data.get("type"));
            int feedId = Integer.parseInt(data.get("feedId"));

            if (type == TYPE_NEW_FEED) {
                int push = PreferenceUtil.getInt(this, PREF_KEY_NEW_FEED_PUSH, DISALLOW_PUSH);
                if (push == ALLOW_PUSH) {
                    sendNotification(feedId, "새로운 신고", "즐겨찾기 지역에 신고가 발생했습니다.");
                }
            } else if (type == TYPE_REPORT_STATE_CHANGE) {
                int push = PreferenceUtil.getInt(this, PREF_KEY_FEED_CHANGE_PUSH, DISALLOW_PUSH);
                if (push == ALLOW_PUSH) {
                    sendNotification(feedId, "신고 상태 변화", "신고한 피드 상태가 변경되었습니다.");
                }
            }
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

    /**
     * TODO Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MoldeJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
    }

    /**
     * Persist token to third-party servers.
     */
    private void sendRegistrationToServer(final String token) {
        String uId = FirebaseAuth.getInstance().getUid();

        if (uId == null) return;

        db.collection("users").document(uId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String uuId = (String) documentSnapshot.get("uId");
                        long authority = (long) documentSnapshot.get("authority");

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("uId", uuId);
                        userMap.put("token", token);
                        userMap.put("authority", authority);

                        PreferenceUtil.putString(MoldeFirebaseMessagingService.this, PREF_KEY_FCM_TOKEN, token);

                        refreshFcmToken(uuId, userMap);
                    }
                });
    }

    private void refreshFcmToken(String uId, Map<String, Object> userMap) {
        db.collection("users").document(uId).set(userMap);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(int feedId, String title, String message) {

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("molde_channel", title, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(message);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            channel.setLightColor(Color.GREEN);
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = createNotification(title, message);
        mBuilder.setChannelId("molde_channel");
        mBuilder.setContentIntent(createGoFeedPendingIntent(feedId));
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

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        Intent baseIntent = new Intent(this, MoldeMainActivity.class);
        stackBuilder.addParentStack(MoldeMainActivity.class);
        stackBuilder.addNextIntent(baseIntent);

        Intent resultIntent = new Intent(this, FeedDetailActivity.class);
        resultIntent.putExtra(EXTRA_KEY_FEED_ID, feedId);
        stackBuilder.addNextIntent(resultIntent);

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
    }

}
