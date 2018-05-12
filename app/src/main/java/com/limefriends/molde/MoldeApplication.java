package com.limefriends.molde;

import android.app.Application;
import android.content.Context;

/**
 * Created by leekijung on 2018. 5. 5..
 */

public class MoldeApplication extends Application {
    private static Context moldeContext;
    @Override
    public void onCreate() {
        super.onCreate();
        moldeContext = this;
    }
    public static Context getMoldeContext(){
        return moldeContext;
    }
}
