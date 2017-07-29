package background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import ui.base.BaseActivity;
import ui.screen_alarm_alert.AlarmAlertActivity;

public class ScreenStateBroadcastReceiver extends BroadcastReceiver {

    //SCREEN ON & OFF are protected system intents, the receiver must therefore be registered manually in an activity
    // When the alarm activity is in foreground and the user turns the screen off, the activity gets killed, but when it gets killed, a relaunching service is launched
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_SCREEN_OFF:
                BaseActivity.unlockScreen();
                Log.i(getClass().getSimpleName(), "SCREEN_OFF");
                ((AlarmAlertActivity) context).finish();
                break;

            case Intent.ACTION_SCREEN_ON:
                Log.i(getClass().getSimpleName(), "SCREEN_ON");
                BaseActivity.clearScreen();
        }
    }
}
