package com.limefriends.molde.molde_backend;


import com.limefriends.molde.MoldeApplication;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MoldeNetwork {
    private static MoldeNetwork moldeNetwork;
    private Retrofit retrofit;
    public static MoldeNetwork getNetworkInstance() {
        if (moldeNetwork == null) {
            moldeNetwork = new MoldeNetwork();
        }
        return moldeNetwork;
    }

    public MoldeNetwork() {
        retrofit = new Retrofit.Builder().baseUrl(MoldeApplication.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
