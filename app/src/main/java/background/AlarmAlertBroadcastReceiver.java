package background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import misc.Constants;
import model.Alarm;
import ui.AlarmAlertActivity;

public class AlarmAlertBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("alert brd reveiver", "alarmAlertBroadcastReceiver");
        Intent nfcAlarmServiceIntent = new Intent(context, DeviceBootBroadcastReceiver.class);
        context.sendBroadcast(nfcAlarmServiceIntent, null);

        StaticWakeLock.lockOn(context);
        Bundle bundle = intent.getExtras();
        final Alarm alarm = bundle.getParcelable(Constants.ALARM_EXTRA);

        Intent alertActivityIntent;
        alertActivityIntent = new Intent(context, AlarmAlertActivity.class);
        alertActivityIntent.putExtra(Constants.ALARM_EXTRA, alarm);
        alertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(alertActivityIntent);
    }
}
