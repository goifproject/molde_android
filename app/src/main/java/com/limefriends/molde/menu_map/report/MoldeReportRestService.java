package com.limefriends.molde.menu_map.report;

import com.limefriends.molde.menu_map.entity.MoldeReportEntity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MoldeReportRestService {

    @Headers("Content-Type: application/json")
    @POST(MoldeReportApi.POST_REPORT_API)
    public Call<MoldeReportEntity> sendReportData(@Body MoldeReportEntity moldeReportEntity);

}
