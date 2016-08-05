package com.example.radek.nfc_test;

import java.io.Serializable;

/**
 * Created by Radek on 2016-05-28.
 */
public class Settings implements Serializable {

    public static final int ALARM_DETAILS_ACTIVITY_RESULTCODE = 201;
    private static Settings instance = null;
    private static String sharedPrefsName = "sharedPrefs";
    public static final int COLLAPSED_HEIGHT = 400;
    public static final int LIST_ANIMATION_DURATION = 300;
    public static final String NFC_TAG_ATTACHED = "nfc_tag_attached";
    public static final int ALARM_DETAILS_ACTIVITY_REQUESTCODE = 200;
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
