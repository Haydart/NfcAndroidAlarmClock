package background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import misc.Constants;
import model.Alarm;
import ui.screen_alarm_alert.AlarmAlertActivity;

public class AlertActivityRelaunchService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(getClass().getSimpleName(), "Anti cheat service onstartcommand");
        Intent alertActivityIntent = new Intent(this, AlarmAlertActivity.class);
        alertActivityIntent.putExtra(Constants.ALARM_EXTRA, new Alarm());
        alertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        alertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(alertActivityIntent);
        stopSelf();
        return START_STICKY;
    }
}
