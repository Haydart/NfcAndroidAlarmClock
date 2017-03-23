package background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import misc.Constants;
import misc.PersistentDataStorage;
import model.Alarm;

public class AlarmSchedulerService extends Service {

    PersistentDataStorage persistentDataStorage;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        persistentDataStorage = new PersistentDataStorage(this);
    }

    private Alarm getNext() {
        Set<Alarm> alarmTreeSet = new TreeSet<Alarm>(new Comparator<Alarm>() {
            @Override
            public int compare(Alarm lhs, Alarm rhs) {
                int result = 0;
                long diff = lhs.getAlarmTime().getTimeInMillis() - rhs.getAlarmTime().getTimeInMillis();
                if (diff > 0) {
                    return 1;
                } else if (diff < 0) {
                    return -1;
                }
                return result;
            }
        });

        List<Alarm> alarmsList = persistentDataStorage.loadAlarmsList();

        for (Alarm item : alarmsList) {
            if (item.isAlarmActive()) {
                alarmTreeSet.add(item);
            }
        }
        if (alarmTreeSet.iterator().hasNext()) {
            return alarmTreeSet.iterator().next();
        } else {
            return null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(this.getClass().getSimpleName(), "SERVICE STARTED");
        Alarm alarm = getNext();
        if (alarm != null) {
            alarm.schedule(getApplicationContext());
            Log.i(this.getClass().getSimpleName(), alarm.getTimeUntilNextAlarmMessage() + " on hour " + alarm.getStringNotation());
        } else {
            Log.i(this.getClass().getSimpleName(), "No alarms set, cancelling any that might have been scheduled");
            Intent cancellingIntent = new Intent(getApplicationContext(), AlarmAlertBroadcastReceiver.class);
            cancellingIntent.putExtra(Constants.ALARM_EXTRA, new Alarm());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, cancellingIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        persistentDataStorage = null;
        Log.i(AlarmSchedulerService.this.getClass().getSimpleName(), "Service onDestroy()");
        super.onDestroy();
    }
}
