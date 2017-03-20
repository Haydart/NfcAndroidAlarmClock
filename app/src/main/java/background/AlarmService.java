package background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import misc.PersistentDataStorage;
import model.Alarm;

public class AlarmService extends Service {

    PersistentDataStorage spManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(this.getClass().getSimpleName(), "AlarmService.class onCreate()");
        super.onCreate();
        spManager = new PersistentDataStorage(this);
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

        List<Alarm> alarmsList = spManager.loadAlarmsList();

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
        Log.d(this.getClass().getSimpleName(), "SERVICE STARTED");
        Alarm alarm = getNext();
        if (alarm != null) {
            alarm.schedule(getApplicationContext());
            Log.d(this.getClass().getSimpleName(), alarm.getTimeUntilNextAlarmMessage() + " on hour " + alarm.getStringNotation());
        } else {
            /*Intent myIntent = new Intent(getApplicationContext(), AlarmAlertBroadcastReceiver.class);
            myIntent.putExtra("alarm", new Alarm());
            Log.d(this.getClass().getSimpleName(), "alarm was nullLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            alarmManager.cancel(pendingIntent);*/
            Log.d("SERVICE NO ALARMS", "NO ALARMS SET");
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        spManager = null;
        super.onDestroy();
    }
}
