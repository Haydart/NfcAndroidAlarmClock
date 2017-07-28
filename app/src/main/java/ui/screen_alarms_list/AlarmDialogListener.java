package ui.screen_alarms_list;

import model.Alarm;

public interface AlarmDialogListener {
    void onAlarmModified(Alarm alarm, int position);

    void onAlarmCreated(Alarm alarm);
}
