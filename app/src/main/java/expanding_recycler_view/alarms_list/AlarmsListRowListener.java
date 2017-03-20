package expanding_recycler_view.alarms_list;

import model.Alarm;

public interface AlarmsListRowListener {
    void onListRowLongClick(int position);

    void onAlarmCheckBoxClicked(Alarm alarm, int position, boolean checked);

    void onAlarmHourTextClicked(Alarm alarm, int position);
}
