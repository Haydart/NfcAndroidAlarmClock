package ui.alarms_list_screen;

import model.Alarm;

public interface AlarmDialogListener {
    void onAlarmModified(Alarm alarm, int position);

    void onAlarmCreated(Alarm alarm);
}
