package misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import model.Alarm;

public class SharedPrefsManager {
    private SharedPreferences sharedPreferences;
    private Context activityContext;

    public SharedPrefsManager(Context activityContext) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activityContext);
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

    // TODO: 16/03/2017 uncomment after refactor
/*
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
            expandableListItems.add(new ExpandableListItem(alarm, misc.Constants.COLLAPSED_HEIGHT));
        }
        return expandableListItems;
    }
*/

    public boolean wasNfcTagAttached() {
        return sharedPreferences.getBoolean(Constants.NFC_TAG_ATTACHED, false);
    }

    public void notifyNfcTagAttached() {
        sharedPreferences.edit().putBoolean(Constants.NFC_TAG_ATTACHED, true).apply();
    }

    public void resetNfcTagAttached() {
        sharedPreferences.edit().putBoolean(Constants.NFC_TAG_ATTACHED, false).apply();
    }
}
