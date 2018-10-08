package com.limefriends.molde.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.limefriends.molde.R;
import com.limefriends.molde.entity.map.SearchMapInfoEntity;
import com.limefriends.molde.entity.map.poi.Poi;
import com.limefriends.molde.entity.map.poi.TMapSearchInfo;
import com.limefriends.molde.ui.map.search.SearchMapInfoAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class SearchPoiService extends AsyncTask<String, Void, ArrayList<SearchMapInfoEntity>> {

    private final int SEARCH_COUNT = 20;  // minimum is 20
    private ArrayList<SearchMapInfoEntity> searchMapListData;
    private SearchMapInfoAdapter mAdapter;
    private String TMAP_API_KEY;

    public SearchPoiService(SearchMapInfoAdapter adapter, Context context) {
        this.mAdapter = adapter;
        searchMapListData = new ArrayList<>();
        context.getResources().getString(R.string.tmap_key);
    }

    @Override
    protected ArrayList<SearchMapInfoEntity> doInBackground(String... word) {
        return getAutoComplete(word[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<SearchMapInfoEntity> autoCompleteItems) {
        mAdapter.setData(autoCompleteItems);
        mAdapter.notifyDataSetChanged();
    }

    public ArrayList<SearchMapInfoEntity> getAutoComplete(String word) {

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

            TMapSearchInfo searchPoiInfo = new Gson().fromJson(line, TMapSearchInfo.class);

            ArrayList<Poi> poi = searchPoiInfo.getSearchPoiInfo().getPois().getPoi();
            for (int i = 0; i < poi.size(); i++) {
                String mainAddr = "";
                if(poi.get(i).getSecondNo() == null || poi.get(i).getSecondNo().trim().equals("")){
                    mainAddr = poi.get(i).getUpperAddrName().trim() + " " + poi.get(i).getMiddleAddrName().trim() +
                            " " + poi.get(i).getLowerAddrName().trim() + " " + poi.get(i).getDetailAddrName().trim() + " " + poi.get(i).getFirstNo().trim();
                }else{
                    mainAddr = poi.get(i).getUpperAddrName().trim() + " " + poi.get(i).getMiddleAddrName().trim() +
                            " " + poi.get(i).getLowerAddrName().trim() + " " + poi.get(i).getDetailAddrName().trim() + " " + poi.get(i).getFirstNo().trim() + "-" + poi.get(i).getSecondNo().trim();
                }
                String streetAddr = poi.get(i).getRoadName().trim() + " " + poi.get(i).getFirstBuildNo().trim();
                searchMapListData.add(new SearchMapInfoEntity(poi.get(i).getNoorLat(), poi.get(i).getNoorLon(), poi.get(i).getName(), mainAddr, streetAddr, poi.get(i).getLowerBizName(), poi.get(i).getTelNo()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchMapListData;
    }
}
