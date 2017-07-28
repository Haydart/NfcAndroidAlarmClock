package background;

/**
 * Created by Radek on 2016-05-28.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeviceBootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, AlarmSchedulerService.class);
        context.startService(serviceIntent);
    }
}
