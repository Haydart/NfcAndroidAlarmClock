package com.example.radek.nfc_test;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

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
//        Log.d("SPM","alarmsSaved");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putString(Settings.getInstance().getAlarmsWritePoint(), ObjectSerializer.serialize(alarmsList));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    public synchronized ArrayList<Alarm> loadAlarmsList()
    {
//        Log.d("SPM","alarmsLoaded");
        ArrayList<Alarm> alarmsList = null;
        try {
            alarmsList = (ArrayList<Alarm>) ObjectSerializer.deserialize(sharedPreferences.getString(Settings.getInstance().getAlarmsWritePoint(), ObjectSerializer.serialize(new ArrayList<Alarm>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return alarmsList;
    }
}
