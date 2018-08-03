package com.limefriends.molde.menu_map.report;

import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MoldeReportRestService {

    //@Headers({"Content-Type: application/json", "Accept: application/json"})
    @Multipart
    @POST(MoldeReportApi.POST_REPORT_API)
    public Call<ResponseBody> sendReportData(
            @Part("userId") String userId,
            @Part("userName") String userName,
            @Part("reportState") int reportState,
            @Part("reportContent") String reportContent,
            @Part("reportAddress") String reportAddress,
            @Part("reportDetailAddress") String reportDetailAddress,
            @Part("userEmail") String userEmail,
            @Part("reportLat") String reportLat,
            @Part("reportLng") String reportLng,
            @Part("reportDate") Date reportDate,
            @Part List<MultipartBody.Part> reportImageList
    );

}
