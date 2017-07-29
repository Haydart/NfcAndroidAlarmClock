package ui.screen_alarm_alert;

import ui.base.BasePresenter;

final class AlarmAlertPresenter extends BasePresenter<AlarmAlertView> {
    @Override
    public AlarmAlertView getNoOpView() {
        return NoOpAlarmAlertView.INSTANCE;
    }
}
