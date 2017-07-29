package ui.screen_alarm_alert;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import background.AlertActivityRelaunchService;
import background.ScreenStateBroadcastReceiver;
import com.example.radek.nfc_test.R;
import java.nio.charset.Charset;
import java.util.Arrays;
import misc.Constants;
import misc.PersistentDataStorage;
import model.Alarm;
import ui.base.BaseActivity;

public final class AlarmAlertActivity extends BaseActivity<AlarmAlertPresenter> implements AlarmAlertView {
    private Alarm alarm;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private NfcAdapter nfcAdapter;
    private PersistentDataStorage persistentDataStorage;
    private ScreenStateBroadcastReceiver screenStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowFlags();
        initPersistentDataStorage();
        registerScreenOffReceiver();
        initNfcAdapter();
        getAlarmExtra();
        setPhoneStateListener();
        startAlarm();
    }

    private void setWindowFlags() {
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    private void initPersistentDataStorage() {
        persistentDataStorage = new PersistentDataStorage(this);
        persistentDataStorage.resetNfcTagAttached();
    }

    private void initNfcAdapter() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    private void registerScreenOffReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        screenStateReceiver = new ScreenStateBroadcastReceiver();
        registerReceiver(screenStateReceiver, filter);
    }

    private void getAlarmExtra() {
        Bundle bundle = this.getIntent().getExtras();
        alarm = bundle.getParcelable(Constants.ALARM_EXTRA);
    }

    private void setPhoneStateListener() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        try {
                            mediaPlayer.pause();
                        } catch (IllegalStateException ignored) {
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        try {
                            mediaPlayer.start();
                        } catch (IllegalStateException ignored) {
                        }
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void startAlarm() {
        if (!alarm.getAlarmTonePath().isEmpty()) {
            mediaPlayer = new MediaPlayer();
            if (alarm.getVibrate()) {
                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                long[] pattern = { 0, 100, 300, 100, 300, 100, 100, 100, 100, 100, 300, 100, 100, 100, 100, 100, 100, 100, 300, 100, 100, 100, 500 };
                vibrator.vibrate(pattern, 0);
            }
            try {
                mediaPlayer.setVolume(1f, 1f);
                mediaPlayer.setDataSource(this, Uri.parse(alarm.getAlarmTonePath()));
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                mediaPlayer.release();
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        enterFullscreenMode(hasFocus);
    }

    private void enterFullscreenMode(boolean hasFocus) {
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupForegroundDispatch();
    }

    private void setupForegroundDispatch() {
        final Intent intent = new Intent(this, getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            handleIncomingNdefDataIntent(intent);
        }
    }

    private void handleIncomingNdefDataIntent(Intent intent) {
        if ("text/plain".equals(intent.getType())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Ndef ndef = Ndef.get(tag);
            NdefMessage ndefMessage = ndef.getCachedNdefMessage();
            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    if (readNfcTagText(ndefRecord).equals("alarm")) {
                        Toast.makeText(this, "alarm tag attached", Toast.LENGTH_LONG).show();
                        vibrator.cancel();
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        persistentDataStorage.notifyNfcTagAttached();
                        finish();
                    } else {
                        Toast.makeText(this, "You are using a wrong tag", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } else {
            Toast.makeText(this, "You are using a wrong tag", Toast.LENGTH_LONG).show();
        }
    }

    private String readNfcTagText(NdefRecord ndefRecord) {
        byte[] payload = ndefRecord.getPayload();
        Charset textEncodingCharset = Charset.forName(((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16");
        int languageCodeLength = payload[0] & 51;
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncodingCharset);
    }

    @Override
    public void onBackPressed() {
        //no-op
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onDestroy() {
        vibrator.cancel();
        mediaPlayer.release();
        unregisterReceiver(screenStateReceiver);
        if (!persistentDataStorage.wasNfcTagAttached()) {
            Toast.makeText(getApplicationContext(), "You won`t get away with that cheating, I`m relaunching myself.", Toast.LENGTH_SHORT).show();
            startService(new Intent(this, AlertActivityRelaunchService.class));
        }
        super.onDestroy();
    }

    @Override
    protected void initPresenter() {
        presenter = new AlarmAlertPresenter();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.alarm_alert;
    }
}