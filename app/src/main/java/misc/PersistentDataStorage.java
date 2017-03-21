package misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import model.Alarm;

public class PersistentDataStorage {
    private SharedPreferences sharedPreferences;

    public PersistentDataStorage(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public synchronized void saveAlarmsList(List<Alarm> alarmsList) {
        String alarmsListJSON = new Gson().toJson(alarmsList);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putString(Constants.ALARMS_WRITE_POINT, alarmsListJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    public synchronized List<Alarm> loadAlarmsList() {
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
}
