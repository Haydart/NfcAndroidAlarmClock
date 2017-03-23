package ui.alarms_list_screen;

import model.Alarm;
import ui.base.BasePresenter;

class AlarmsPresenter extends BasePresenter<AlarmsView> {

    @Override
    protected void onViewStarted(AlarmsView view) {
        super.onViewStarted(view);
    }

    @Override
    protected void onViewStopped() {
        super.onViewStopped();
    }

    void onActionButtonClicked() {
        view.displayAlarmCreationDialog();
    }

    void onAlarmCheckboxClicked(int position, boolean checked) {

    }

    void onAlarmListRowLongClick(int position) {
        view.displayAlarmDeletionDialog(position);
    }

    @Override
    public AlarmsView getNoOpView() {
        return NoOpAlarmsView.INSTANCE;
    }

    void onAlarmHourTextClicked(Alarm alarm, int position) {
        view.displayAlarmModificationDialog(alarm, position);
    }

    void onAlarmDeleted(int position) {
        view.removeAlarmListElement(position);
    }

    void onAlarmCreated(Alarm alarm) {
        view.addAlarmListElement(alarm);
    }

    void onAlarmModified(Alarm alarm, int position) {
        view.modifyAlarmListElement(alarm, position);
    }
}
