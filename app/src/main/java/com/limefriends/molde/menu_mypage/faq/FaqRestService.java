package com.limefriends.molde.menu_mypage.faq;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by haams on 2017-12-04.
 */

public interface FaqRestService {
    // 자주 묻는 질문 (문의 내용,답변 받을 이메일)
    @FormUrlEncoded
    @POST(FaqApi.POST_FAQ_API)
    public Call<MoldeFaQ> sendFaQDataToServer(
            @Field("user_id") String user_id,
            @Field("sns_id") String sns_id,
            @Field("faq_contents") String faq_contents,
            @Field("faq_email") String faq_email
    );

}
