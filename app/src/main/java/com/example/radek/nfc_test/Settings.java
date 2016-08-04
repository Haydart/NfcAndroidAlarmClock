package com.example.radek.nfc_test;

import java.io.Serializable;

/**
 * Created by Radek on 2016-05-28.
 */
public class Settings implements Serializable {

    private static Settings instance = null;
    private static String sharedPrefsName = "sharedPrefs";
    public static final int COLLAPSED_HEIGHT = 400;
    public static final int LIST_ANIMATION_DURATION = 300;
    private static String alarmsWritePoint = "ALARMS";

    private Settings(){}

    public static Settings getInstance()
    {
        if(instance == null)
            instance = new Settings();
        return instance;
    }

    public String getSharedPrefsName() {
        return sharedPrefsName;
    }

    public String getAlarmsWritePoint() {
        return alarmsWritePoint;
    }

}
