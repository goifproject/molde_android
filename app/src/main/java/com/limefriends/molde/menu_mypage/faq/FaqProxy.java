package com.limefriends.molde.menu_mypage.faq;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class FaqProxy {
    private FaqRestService faqRestService;

    public FaqProxy(Retrofit retrofit) {
        faqRestService = retrofit.create(FaqRestService.class);
    }

    public void sendFaqDataToServer(String user_id, String sns_id, String faq_contents, String faq_email, Callback<MoldeFaQ> callback){
        Call<MoldeFaQ> call = faqRestService.sendFaQDataToServer(user_id,sns_id,faq_contents,faq_email);
        call.enqueue(callback);
    }
}
