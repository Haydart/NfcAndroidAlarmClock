package ui.screen_alarms_list;

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
        // TODO: 28/07/2017
    }

    void onAlarmListRowLongClick(int position) {
        view.displayAlarmDeletionDialog(position);
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

    @Override
    public AlarmsView getNoOpView() {
        return NoOpAlarmsView.INSTANCE;
    }
}
