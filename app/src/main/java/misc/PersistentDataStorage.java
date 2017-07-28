package misc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import model.Alarm;

@SuppressLint("ApplySharedPref")
public class PersistentDataStorage {
    private SharedPreferences sharedPreferences;

    public PersistentDataStorage(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveAlarmsList(List<Alarm> alarmsList) {
        String alarmsListJson = new Gson().toJson(alarmsList);
        sharedPreferences.edit().putString(Constants.ALARMS_WRITE_POINT, alarmsListJson).commit();
    }

    public List<Alarm> loadAlarmsList() {
        List<Alarm> alarmsList = new ArrayList<>();
        try {
            alarmsList = new Gson().fromJson(sharedPreferences.getString(Constants.ALARMS_WRITE_POINT, "[]"), new TypeToken<List<Alarm>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alarmsList;
    }

    public boolean wasNfcTagAttached() {
        return sharedPreferences.getBoolean(Constants.NFC_TAG_ATTACHED, false);
    }

    public void notifyNfcTagAttached() {
        sharedPreferences.edit().putBoolean(Constants.NFC_TAG_ATTACHED, true).commit();
    }

    public void resetNfcTagAttached() {
        sharedPreferences.edit().putBoolean(Constants.NFC_TAG_ATTACHED, false).commit();
    }

    public String getAcceptedTagContentText() {
        return sharedPreferences.getString(Constants.ACCEPTED_TAG_TEXT, "");
    }

    public void setAcceptedTagContentText(String contentText) {
        sharedPreferences.edit().putString(Constants.ACCEPTED_TAG_TEXT, contentText).commit();
    }

    public boolean isFirstAppLaunch() {
        if (sharedPreferences.getBoolean(Constants.FIRST_LAUNCH, true)) {
            sharedPreferences.edit().putBoolean(Constants.FIRST_LAUNCH, false).commit();
            return true;
        }
        return false;
    }
}
