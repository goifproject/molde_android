package com.limefriends.molde.common;

public class Constant {

    public static int PER_PAGE = 10;

    public static class MoldeMain {
        public static final int FROM_LAUNCHER = 0;
        public static final int FROM_NOTIFICATION = 1;
    }

    public static class ReportState {
        public static final int RECEIVING = 0;
        public static final int ACCEPTED = 1;
        public static final int FOUND = 2;
        public static final int CLEAN = 3;
        public static final int DENIED = 9;
    }

    public static class Authority {
        public static final int GUEST = -1;
        public static final int MEMBER = 0;
        public static final int GUARDIAN = 1;
        public static final int ADMIN = 2;
    }

    public static class Common {
        public static final String EXTRA_KEY_POSITION = "position";
        public static final String EXTRA_KEY_ACTIVITY_NAME = "activityName";
        public static final String PREF_KEY_AUTHORITY = "authority";
        public static final String EXTRA_KEY_CARDNEWS_ID = "cardNewsId";
        public static final String PREF_KEY_FCM_TOKEN = "fcmToken";
        public static final String PREF_KEY_FIRESTORE_TOKEN = "storeKey";
        public static final String PREF_KEY_NEW_FEED_PUSH = "newFeedPush";
        public static final String PREF_KEY_FEED_CHANGE_PUSH = "feedChangePush";

        public static final int ALLOW_PUSH = 0;
        public static final int DISALLOW_PUSH = 1;
    }

    public static class Map {
        public static final String EXTRA_KEY_FAVORITE_INFO = "mapFavoriteInfo";
    }

    public static class Feed {
        public static final String FEED_BY_DISTANCE = "거리순";
        public static final String FEED_BY_LAST = "최신순";
        // Intent Key
        public static final int INTENT_KEY_MY_FEED = 992;
        public static final String EXTRA_KEY_FEED_ID = "feedId";
        // Value
        public static final String INTENT_VALUE_MY_FEED = "MyFeed";

        public static final String EXTRA_KEY_STATE = "state";
    }

    public static class MyPage {
        //구글 로그인 완료
        public static final int CONNECT_GOOGLE_AUTH_CODE = 1002;
        //페북 로그인 완료
        public static final int CONNECT_FACEBOOK_AUTH_CODE = 1003;
        //파이어베이스 인증 클라이언트
        public static final int RC_SIGN_IN = 9001;

        public static final String INTENT_VALUE_MYCOMMENT = "MyCommentActivity";
    }

    public static class Scrap {
        public static final int INTENT_KEY_CARDNEWS_DETAIL = 993;
        public static final String INTENT_VALUE_SCRAP = "ScrapActivity";
    }

    public static class Comment {
        public static final String EXTRA_KEY_COMMENT_DESCRIPTION = "description";
    }


}
