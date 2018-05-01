package com.limefriends.molde.molde_backend.proxy;

import com.limefriends.molde.menu_mypage.backend_data_type.MoldeFaQ;
import com.limefriends.molde.molde_backend.Service;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class FaqProxy {
    private Service service;

    public FaqProxy(Retrofit retrofit) {
        service = retrofit.create(Service.class);
    }

    public void sendFaqDataToServer(String user_id, String sns_id, String faq_contents, String faq_email, Callback<MoldeFaQ> callback){
        Call<MoldeFaQ> call = service.sendFaQDataToServer(user_id,sns_id,faq_contents,faq_email);
        call.enqueue(callback);
    }
}
