package ui.screen_alarm_alert;

import ui.base.BasePresenter;

final class AlarmAlertPresenter extends BasePresenter<AlarmAlertView> {

    @Override
    protected void onViewStarted(AlarmAlertView view) {
        super.onViewStarted(view);
        view.startAlarm();
    }

    @Override
    public AlarmAlertView getNoOpView() {
        return NoOpAlarmAlertView.INSTANCE;
    }
}
