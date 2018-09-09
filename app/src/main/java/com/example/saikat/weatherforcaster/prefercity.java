package com.example.saikat.weatherforcaster;

import android.app.Activity;
import android.content.SharedPreferences;


/**
 * Created by saikat on 8/28/15.
 */
public class prefercity {

    SharedPreferences prefs;

    public prefercity(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }
    public String getCity(){
        return prefs.getString("city", "Dhaka, BD");
    }
    void setCity(String city){
        prefs.edit().putString("city",city).commit();
    }
}
