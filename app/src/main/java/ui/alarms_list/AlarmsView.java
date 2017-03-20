package ui.alarms_list;

import com.jenzz.noop.annotation.NoOp;
import ui.base.BaseView;

@NoOp
interface AlarmsView extends BaseView {
    void displayAlarmDeletionAlertDialog(int position);

    void displayAlarmModificationDialog();

    void displayAlarmCreationDialog();
}
