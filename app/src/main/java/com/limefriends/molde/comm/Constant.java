package com.limefriends.molde.comm;

public class Constant {

    public static int PER_PAGE = 10;

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

    public static class Feed {
        public static final String FEED_BY_DISTANCE = "거리순";
        public static final String FEED_BY_LAST = "최신순";
        // Intent Key
        public static final String INTENT_KEY_ACTIVITY_NAME = "activityName";
        public static final String INTENT_KEY_FEED_ID = "feedId";
        public static final String INTENT_KEY_POSITION = "position";
        // Preference Key
        public static final String PREF_KEY_AUTHORITY = "authority";
        // Value
        public static final String INTENT_VALUE_MY_FEED = "MyFeed";
    }

}
