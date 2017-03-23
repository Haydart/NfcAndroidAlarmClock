package background;

/**
 * Created by Radek on 2016-05-28.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeviceBootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("alert service", "Starting alarm schedule service after device boot");
        Intent serviceIntent = new Intent(context, AlarmSchedulerService.class);
        context.startService(serviceIntent);
    }
}
