package com.limefriends.molde.menu_feed.feed;

import com.limefriends.molde.menu_map.entity.MoldeReportEntity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;

public interface MoldeFeedRestService {

    @GET(MoldeFeedApi.GET_FEED_API)
    public Call<MoldeReportEntity> findFeedData(@Body MoldeReportEntity moldeReportEntity);

}
