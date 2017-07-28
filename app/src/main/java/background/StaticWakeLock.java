package background;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by Radek on 2016-05-28.
 */
public class StaticWakeLock {
    private static PowerManager.WakeLock wakeLock = null;

    public static void lockOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        Object flags;
        if (wakeLock == null) {
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "NFC_ALARM");
        }
        wakeLock.acquire();
    }

    public static void lockOff(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        try {
            if (wakeLock != null) {
                wakeLock.release();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}
