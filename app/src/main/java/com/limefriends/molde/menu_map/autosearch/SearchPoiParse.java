package com.limefriends.molde.menu_map.autosearch;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.limefriends.molde.menu_map.MoldeMapInfoRecyclerViewAdapter;
import com.limefriends.molde.menu_map.entity.MoldeSearchMapInfoEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class SearchPoiParse extends AsyncTask<String, Void, ArrayList<MoldeSearchMapInfoEntity>> {
    private final String TMAP_API_KEY = "846fd0ff-fac4-4e07-9c7c-1950cc0131dd";
    private final int SEARCH_COUNT = 20;  // minimum is 20
    private ArrayList<MoldeSearchMapInfoEntity> searchMapListData;
    private MoldeMapInfoRecyclerViewAdapter mAdapter;

    public SearchPoiParse(MoldeMapInfoRecyclerViewAdapter adapter) {
        this.mAdapter = adapter;
        searchMapListData = new ArrayList<MoldeSearchMapInfoEntity>();
    }

    @Override
    protected ArrayList<MoldeSearchMapInfoEntity> doInBackground(String... word) {
        return getAutoComplete(word[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<MoldeSearchMapInfoEntity> autoCompleteItems) {
        mAdapter.setData(autoCompleteItems);
        mAdapter.notifyDataSetChanged();
    }

    public ArrayList<MoldeSearchMapInfoEntity> getAutoComplete(String word) {

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

            //Log.e("response", line);

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
                searchMapListData.add(new MoldeSearchMapInfoEntity(poi.get(i).getNoorLat(), poi.get(i).getNoorLon(), poi.get(i).getName(), mainAddr, streetAddr, poi.get(i).getLowerBizName(), poi.get(i).getTelNo()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchMapListData;
    }
}
