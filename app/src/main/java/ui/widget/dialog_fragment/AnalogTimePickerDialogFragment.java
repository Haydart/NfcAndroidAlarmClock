package ui.widget.dialog_fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import java.util.Calendar;
import misc.Constants;
import model.Alarm;
import ui.alarms_list_screen.AlarmDialogListener;

public class AnalogTimePickerDialogFragment extends DialogFragment implements AnalogTimePickerDialogView, TimePickerDialog.OnTimeSetListener {
    private AnalogTimePickerDialogPresenter presenter;
    private AlarmDialogListener listener;
    private LaunchReason launchReason;
    private Alarm alarm;
    private int alarmListPosition;

    private enum LaunchReason {
        ALARM_CREATION,
        ALARM_MODIFICATION
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listener = (AlarmDialogListener) context;
        initPresenter();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            alarm = arguments.getParcelable(Constants.ALARM_EXTRA);
            alarmListPosition = arguments.getInt(Constants.ALARM_POSITION_EXTRA);
            launchReason = LaunchReason.ALARM_MODIFICATION;
        } else {
            launchReason = LaunchReason.ALARM_CREATION;
        }

        int hour;
        int minutes;
        Calendar currentTime = Calendar.getInstance();
        boolean isUsClockStandard = !DateFormat.is24HourFormat(getActivity());
        if (launchReason == LaunchReason.ALARM_CREATION) {
            if (isUsClockStandard) {
                hour = currentTime.get(Calendar.HOUR);
            } else {
                hour = currentTime.get(Calendar.HOUR_OF_DAY);
            }
            minutes = currentTime.get(Calendar.MINUTE);
        } else {
            if (isUsClockStandard) {
                hour = alarm.getAlarmTime().get(Calendar.HOUR);
            } else {
                hour = alarm.getAlarmTime().get(Calendar.HOUR_OF_DAY);
            }
            minutes = alarm.getAlarmTime().get(Calendar.MINUTE);
        }

        return new TimePickerDialog(
                getActivity(),
                this,
                hour,
                minutes,
                !isUsClockStandard
        );
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (launchReason == LaunchReason.ALARM_CREATION) {
            Alarm alarm = new Alarm();
            alarm.setHour(hourOfDay, minute);
            listener.onAlarmCreated(alarm);
        } else {
            alarm.setHour(hourOfDay, minute);
            listener.onAlarmModified(alarm, alarmListPosition);
        }
    }

    private void initPresenter() {
        presenter = new AnalogTimePickerDialogPresenter(this);
    }
}
