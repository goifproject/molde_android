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

}
