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
import ui.alarms_list_screen.AlarmDialogListener;

public class AnalogTimePickerDialogFragment extends DialogFragment implements AnalogTimePickerDialogView, TimePickerDialog.OnTimeSetListener {
    private AnalogTimePickerDialogPresenter presenter;
    private AlarmDialogListener listener;
    private LaunchReason launchReason;
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
        Calendar currentTime = Calendar.getInstance();
        boolean isUsClockStandard = !DateFormat.is24HourFormat(getActivity());

        return new TimePickerDialog(
                getActivity(),
                this,
                isUsClockStandard ? currentTime.get(Calendar.HOUR) : currentTime.get(Calendar.HOUR_OF_DAY),
                currentTime.get(Calendar.MINUTE),
                !isUsClockStandard
        );
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (launchReason == LaunchReason.ALARM_CREATION) {
            listener.onAlarmCreated(null);
        } else {
            listener.onAlarmModified(null, 0);
        }
    }

    private void initPresenter() {
        presenter = new AnalogTimePickerDialogPresenter(this);
    }
}
