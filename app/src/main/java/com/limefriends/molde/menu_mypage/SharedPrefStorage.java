package com.limefriends.molde.menu_mypage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by user on 2018-04-26.
 */

public class SharedPrefStorage {
    private Context context;

    public SharedPrefStorage(Context context) {
        this.context = context;
    }

    public void saveSwitchState1(String key,int value){
        SharedPreferences pref = context.getSharedPreferences("switch1",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, String.valueOf(value));
        editor.commit();
    }

    public String getSwtichState1(String key){
        SharedPreferences pref = context.getSharedPreferences("switch1",Context.MODE_PRIVATE);
        return (pref.getString(key,""));
    }

    public void saveSwitchState2(String key,int value){
        SharedPreferences pref = context.getSharedPreferences("switch2",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, String.valueOf(value));
        editor.commit();
    }

    public String getSwtichState2(String key){
        SharedPreferences pref = context.getSharedPreferences("switch2",Context.MODE_PRIVATE);
        return (pref.getString(key,""));
    }
}
