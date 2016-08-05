package com.example.radek.nfc_test;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.radek.nfc_test.expandingcells.ExpandableListItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

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

    public synchronized void saveAlarmsList(List<ExpandableListItem> alarmsList)
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

    public synchronized List<ExpandableListItem> loadExpandableItemsList()
    {
        List<Alarm> alarmsList = new ArrayList<>();
        try {
            alarmsList = new Gson().fromJson(sharedPreferences.getString(Settings.getInstance().getAlarmsWritePoint(),"[]"), new TypeToken<List<Alarm>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<ExpandableListItem> result = new ArrayList<>();

        for(Alarm alarm : alarmsList){
            result.add(new ExpandableListItem(alarm, Settings.COLLAPSED_HEIGHT));
        }

        return result;
    }

    private List<Alarm> getAlarmList(List<ExpandableListItem> expandableListItems){
        List<Alarm> alarmList = new ArrayList<>();

        for(ExpandableListItem item : expandableListItems){
            alarmList.add(item.getAlarm());
        }
        return alarmList;
    }

    private List<ExpandableListItem> getPackedExpandableList(List<Alarm> alarmsList){
        List<ExpandableListItem> expandableListItems = new ArrayList<>();

        for(Alarm alarm : alarmsList){
            expandableListItems.add(new ExpandableListItem(alarm, Settings.COLLAPSED_HEIGHT));
        }
        return expandableListItems;
    }

    public boolean wasNfcTagAttached(){
        return sharedPreferences.getBoolean(Settings.NFC_TAG_ATTACHED,false);
    }

    public void notifyNfcTagAttached(){
        sharedPreferences.edit().putBoolean(Settings.NFC_TAG_ATTACHED,true).apply();
    }

    public void resetNfcTagAttached(){
        sharedPreferences.edit().putBoolean(Settings.NFC_TAG_ATTACHED,false).apply();
    }
}
