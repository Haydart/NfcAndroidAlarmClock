package ui.screen_alarms_list;

import com.jenzz.noop.annotation.NoOp;
import model.Alarm;
import ui.base.BaseView;

@NoOp
interface AlarmsView extends BaseView {
    void displayAlarmDeletionDialog(int position);

    void displayAlarmCreationDialog();

    void displayAlarmModificationDialog(Alarm alarm, int position);

    void removeAlarmListElement(int position);

    void addAlarmListElement(Alarm alarm);

    void modifyAlarmListElement(Alarm alarm, int position);
}
