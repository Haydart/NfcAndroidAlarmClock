package ui.alarms_list;

import ui.base.BasePresenter;

class AlarmsPresenter extends BasePresenter<AlarmsView> {

    void onActionButtonClicked() {
        view.displayAlarmCreationDialog();
    }

    void onAlarmListItemClicked(int position) {

    }

    void onAlarmCheckboxClicked(int position) {

    }

    @Override
    public AlarmsView getNoOpView() {
        return NoOpAlarmsView.INSTANCE;
    }
}
