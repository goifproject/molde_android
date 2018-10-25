package com.limefriends.molde.networking.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.limefriends.molde.R;
import com.limefriends.molde.model.entity.search.SearchInfoEntity;
import com.limefriends.molde.networking.schema.search.SearchSchema;
import com.limefriends.molde.networking.schema.search.TMapSearchResponseSchema;
import com.limefriends.molde.screen.map.search.SearchMapInfoAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class SearchPoiService extends AsyncTask<String, Void, ArrayList<SearchInfoEntity>> {

    private static final int SEARCH_COUNT = 20;  // minimum is 20
    private ArrayList<SearchInfoEntity> searchMapListData;
    private SearchMapInfoAdapter mAdapter;
    private String TMAP_API_KEY;

    public SearchPoiService(SearchMapInfoAdapter adapter, Context context) {
        this.mAdapter = adapter;
        searchMapListData = new ArrayList<>();
        TMAP_API_KEY = context.getResources().getString(R.string.tmap_key);
    }

    @Override
    protected ArrayList<SearchInfoEntity> doInBackground(String... word) {
        return getAutoComplete(word[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<SearchInfoEntity> autoCompleteItems) {
        mAdapter.setData(autoCompleteItems);
        mAdapter.notifyDataSetChanged();
    }

    private ArrayList<SearchInfoEntity> getAutoComplete(String word) {

        try {
            String encodeWord = URLEncoder.encode(word, "UTF-8");
            URL acUrl = new URL(
                    "https://api2.sktelecom.com/tmap/pois?version=" +
                            "1" +
                            "&callback=" +
                            "&count=" + SEARCH_COUNT +
                            "&searchKeyword=" + encodeWord +
                            "&areaLLCode=" +
                            "&areaLMCode=" +
                            "&resCoordType=WGS84GEO" +
                            "&searchType=" +
                            "&multiPoint=" +
                            "&searchtypCd=" +
                            "&radius=" +
                            "&reqCoordType=" +
                            "&centerLon=" +
                            "&centerLat="
            );

            HttpURLConnection acConn = (HttpURLConnection) acUrl.openConnection();
            acConn.setRequestProperty("Accept", "application/json");
            acConn.setRequestProperty("appKey", TMAP_API_KEY);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    acConn.getInputStream()));

            String line = reader.readLine();
            if (line == null) {
                searchMapListData.clear();
                return searchMapListData;
            }

            reader.close();

            Log.e("response", line);

            TMapSearchResponseSchema searchPoiInfo = new Gson().fromJson(line, TMapSearchResponseSchema.class);

            ArrayList<SearchSchema> searchSchema = searchPoiInfo.getSearchResponseSchema().getSearchSchemaList().getSearchSchema();
            for (int i = 0; i < searchSchema.size(); i++) {
                String mainAddr = "";
                if(searchSchema.get(i).getSecondNo() == null || searchSchema.get(i).getSecondNo().trim().equals("")){
                    mainAddr = searchSchema.get(i).getUpperAddrName().trim() + " " + searchSchema.get(i).getMiddleAddrName().trim() +
                            " " + searchSchema.get(i).getLowerAddrName().trim() + " " + searchSchema.get(i).getDetailAddrName().trim() + " " + searchSchema.get(i).getFirstNo().trim();
                }else{
                    mainAddr = searchSchema.get(i).getUpperAddrName().trim() + " " + searchSchema.get(i).getMiddleAddrName().trim() +
                            " " + searchSchema.get(i).getLowerAddrName().trim() + " " + searchSchema.get(i).getDetailAddrName().trim() + " " + searchSchema.get(i).getFirstNo().trim() + "-" + searchSchema.get(i).getSecondNo().trim();
                }
                String streetAddr = searchSchema.get(i).getRoadName().trim() + " " + searchSchema.get(i).getFirstBuildNo().trim();
                searchMapListData.add(new SearchInfoEntity(searchSchema.get(i).getNoorLat(), searchSchema.get(i).getNoorLon(), searchSchema.get(i).getName(), mainAddr, streetAddr, searchSchema.get(i).getLowerBizName(), searchSchema.get(i).getTelNo()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchMapListData;
    }
}
