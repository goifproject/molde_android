package com.limefriends.molde.menu_mypage.faq;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MoldeFaqRestService {
    // 자주 묻는 질문 (문의 내용,답변 받을 이메일)
    @POST(FaqApi.POST_FAQ_API)
    public Call<MoldeFaQ> sendFaQData(@Body MoldeFaQ moldeFaQ);

}
