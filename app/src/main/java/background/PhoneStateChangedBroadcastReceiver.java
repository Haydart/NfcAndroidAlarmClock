package background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Radek on 2016-05-28.
 */
public class PhoneStateChangedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().getSimpleName(), "phone state changed");

        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            PhoneStateListener PhoneListener = new PhoneStateListener();

            telephonyManager.listen(PhoneListener, android.telephony.PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception ex) {
            Log.e("Telephony manager error", ex.toString());
        }
    }

    private class PhoneStateListener extends android.telephony.PhoneStateListener {

        public void onCallStateChanged(int state, String incomingNumber) {
            Log.d("MyPhoneListener", state + " incoming no:" + incomingNumber);

            if (state == TelephonyManager.CALL_STATE_RINGING) {
                String message = "New Phone Call Event. Incomming Number : " + incomingNumber;
            }
        }
    }
}
