package ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import misc.Constants;
import com.example.radek.nfc_test.R;
import java.util.Calendar;
import model.Alarm;

public class AlarmDetailsActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private Alarm alarm;
    private int alarmPosition;
    private Button confirmButton;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_details);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        confirmButton = (Button) findViewById(R.id.button);

        Intent intent = getIntent();
        alarm = intent.getParcelableExtra("ALARM");
        alarmPosition = intent.getIntExtra("ALARM_POSITION", -1);
        if (alarm == null) {
            alarm = new Alarm();
        }

        timePicker.setCurrentHour(alarm.getAlarmTime().get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(alarm.getAlarmTime().get(Calendar.MINUTE));

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Calendar newAlarmTime = Calendar.getInstance();
                newAlarmTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                newAlarmTime.set(Calendar.MINUTE, minute);
                newAlarmTime.set(Calendar.SECOND, 0);
                alarm.setAlarmTime(newAlarmTime);
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("ALARM", alarm);
                if (alarmPosition != -1) {
                    resultIntent.putExtra("ALARM_POSITION", alarmPosition);
                }
                setResult(Constants.ALARM_DETAILS_ACTIVITY_RESULTCODE, resultIntent);
                finish();
            }
        });
    }
}
