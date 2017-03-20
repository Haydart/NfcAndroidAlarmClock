package ui.alarms_list_screen;

import java.util.ArrayList;
import java.util.List;
import model.Alarm;
import ui.base.BasePresenter;

class AlarmsPresenter extends BasePresenter<AlarmsView> {

    private List<Alarm> alarmsList;

    public AlarmsPresenter() {
        this.alarmsList = new ArrayList<>();
    }

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

    public void onAlarmListRowLongClick(int position) {
        view.displayAlarmDeletionDialog(position);
    }

    @Override
    public AlarmsView getNoOpView() {
        return NoOpAlarmsView.INSTANCE;
    }

    public void onAlarmHourTextClicked(Alarm alarm, int position) {
        view.displayAlarmModificationDialog(alarm, position);
    }

    public void onAlarmDeleted(int position) {
        view.removeAlarmListElement(position);
    }

    public void onAlarmCreated(Alarm alarm) {
        view.addAlarmListElement(alarm);
    }
}
