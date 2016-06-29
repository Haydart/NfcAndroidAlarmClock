package com.example.radek.nfc_test;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Radek on 2016-05-28.
 */
public class SharedPrefsManager
{
    private SharedPreferences sharedPreferences;
    private Context activityContext;

    public SharedPrefsManager(Context activityContext)
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activityContext);
    }

    public synchronized void saveAlarmsList(ArrayList<Alarm> alarmsList)
    {
        String alarmsListJSON = new Gson().toJson(alarmsList);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putString(Settings.getInstance().getAlarmsWritePoint(), alarmsListJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    public synchronized ArrayList<Alarm> loadAlarmsList()
    {
        ArrayList<Alarm> alarmsList = new ArrayList<>();
        try {
            alarmsList = new Gson().fromJson(sharedPreferences.getString(Settings.getInstance().getAlarmsWritePoint(),"[]"), new TypeToken<List<Alarm>>(){}.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return alarmsList;
    }
}
