package com.limefriends.molde.remote;

public class MoldeRestfulApi {

    /**
     * 카드 뉴스
     */
    public static class CardNews {

        public static final String GET_NEWS_API = "/v1/news";

        public static final String GET_NEWS_BY_ID_API = "/v1/newsid";
    }

    /**
     * 피드
     * 신고할 때는 계정으로 신고하기 때문에 user_id, user_name, user_email 를 포함하고 있고
     * 신고받은 내용에서 유저정보를 뺴고 신고정보로 취합한 것이 report
     */
    public static class Feed {

        /**
         * 로그인 아이디로 검색할 경우 '내 신고내역'이 리턴됨
         */
        public static final String GET_FEED_API = "v1/pin";

        public static final String GET_MY_FEED_API = "v1/pin2";

        public static final String GET_FEED_BY_DATE_API = "v1/pin3";

        public static final String GET_FEED_BY_LOCATION_API = "v1/pin4";

        public static final String GET_FEED_BY_ID_API = "v1/pin5";

        public static final String UPDATE_FEED_API = "v1/pin";

        /**
         * 따로 신고(report) API 를 두지 않고 피드 생성을 신고로 취급함
         */
        public static final String POST_FEED_API = "v1/pin";

        public static final String DELETE_FEED_API = "v1/pin";
    }

    /**
     * 즐겨찾기
     */
    public static class Favorite {

        public static final String GET_FAVORITE_API = "/v1/favorite";

        public static final String DELETE_FAVORITE_API = "/v1/favorite";

        public static final String POST_FAVORITE_API = "/v1/favorite";
    }

    public static class Report {

        public static final String GET_REPORT_API = "/v1/report";
    }

    /**
     * 댓글
     */
    public static class Comment {

        public static final String GET_COMMENT_BY_NEWSID_API = "/v1/commentn";

        public static final String GET_COMMENT_BY_USERID_API = "/v1/commentu";

        public static final String GET_REPORTED_COMMENT_API = "/v1/commreport";

        public static final String GET_COMMENT_BY_COMMENTID_API = "/v1/commenti";

        public static final String POST_COMMENT_API = "/v1/comment";

        public static final String PUT_COMMENT_API = "/v1/comment";

        public static final String DELETE_COMMENT_API = "/v1/comment";

        public static final String DELETE_REPORTED_COMMENT_API = "/v1/commentadmin";
    }

    /**
     * 스크랩
     */
    public static class Scrap {

        public static final String GET_SCRAP_API = "/v1/scrap";

        public static final String GET_SCRAP_BY_ID_API = "/v1/scrap2";

        public static final String POST_SCRAP_API = "/v1/scrap";

        public static final String DELETE_SCRAP_API = "/v1/scrap";
    }

    /**
     * 자주 묻는 질문
     */
    public static class Faq {

        public static final String GET_FAQ_API = "/v1/faq";

        /**
         * 따로 문의하기 API 를 두는 대신 자주 묻는 질문으로 질문을 등록할 수 있
         */
        public static final String POST_FAQ_API = "/v1/faq";
    }
}
