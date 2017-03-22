package ui.alarms_list_screen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import background.AlarmServiceBroadcastReceiver;
import butterknife.BindView;
import butterknife.OnClick;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.example.radek.nfc_test.R;
import expanding_recycler_view.alarms_list.AlarmRecyclerViewAdapter;
import expanding_recycler_view.alarms_list.AlarmsListRowListener;
import misc.Constants;
import misc.PersistentDataStorage;
import model.Alarm;
import ui.AlarmAlertActivity;
import ui.base.BaseActivity;
import ui.widget.dialog_fragment.AnalogTimePickerDialogFragment;

public final class AlarmsActivity extends BaseActivity<AlarmsPresenter> implements AlarmsView, AlarmsListRowListener, AlarmDialogListener {

    @BindView(R.id.alarm_recycler_view) RecyclerView alarmsRecyclerView;
    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    private NfcAdapter nfcAdapter;
    private AlarmRecyclerViewAdapter alarmsAdapter;
    private PersistentDataStorage sharedPrefsManager;

    @OnClick(R.id.fab)
    public void onFabClicked() {
        presenter.onActionButtonClicked();
    }

    @OnClick(R.id.debug_button)
    public void onDebugButtonClicked() {
        Intent alarmIntent = new Intent(this, AlarmAlertActivity.class);
        alarmIntent.putExtra("alarm", new Alarm());
        startActivity(alarmIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        callAlarmScheduleService();
        sharedPrefsManager = new PersistentDataStorage(this);
        initializeRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkForNfcService();
        alarmsAdapter.updateAlarmsList(sharedPrefsManager.loadAlarmsList());
        callAlarmScheduleService();
        Log.d(getClass().getSimpleName(), "on start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(getClass().getSimpleName(), "on resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(getClass().getSimpleName(), "on pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPrefsManager.saveAlarmsList(alarmsAdapter.getAlarmsList());
        Log.d(getClass().getSimpleName(), "on stop");
    }

    @Override
    protected void initPresenter() {
        presenter = new AlarmsPresenter();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_alarms;
    }

    private void initializeRecyclerView() {
        alarmsAdapter = new AlarmRecyclerViewAdapter(sharedPrefsManager.loadAlarmsList(), this);
        alarmsAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @Override
            public void onParentExpanded(int parentPosition) {
                alarmsAdapter.collapseAllParents();
                alarmsAdapter.expandParent(parentPosition);
            }

            @Override
            public void onParentCollapsed(int parentPosition) {
                //no-op
            }
        });

        alarmsRecyclerView.setAdapter(alarmsAdapter);
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void displayAlarmDeletionDialog(final int position) {
        AlertDialog alarmDeletionDialog = new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Do you want to delete alarm for " + position)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        presenter.onAlarmDeleted(position);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alarmDeletionDialog.show();
    }

    private void checkForNfcService() {
        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn`t support NFC", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (!nfcAdapter.isEnabled()) {
                Snackbar
                        .make(coordinatorLayout, "NFC service is disabled", Snackbar.LENGTH_INDEFINITE)
                        .setAction("SETTINGS", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), 0);
                            }
                        })
                        .setActionTextColor(Color.YELLOW)
                        .show();
            } else {
                Snackbar.make(coordinatorLayout, "NFC service is available!", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        sharedPrefsManager.notifyNfcTagAttached();
        super.onNewIntent(intent);
        Toast.makeText(this, "Attached nfc tag", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, AlarmsActivity.class));
        finish();
        System.exit(0);
        finishAffinity();
        // TODO: 16/03/2017 recognize NDEF formatted contents data
    }

    protected void callAlarmScheduleService() {
        Intent nfcAlarmServiceIntent = new Intent(this, AlarmServiceBroadcastReceiver.class);
        sendBroadcast(nfcAlarmServiceIntent, null);
    }

    @Override
    public void displayAlarmCreationDialog() {
        displayAlarmDialog(null);
    }

    @Override
    public void displayAlarmModificationDialog(Alarm alarm, int position) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(Constants.ALARM_EXTRA, alarm);
        arguments.putInt(Constants.ALARM_POSITION_EXTRA, position);
        displayAlarmDialog(arguments);
    }

    private void displayAlarmDialog(Bundle arguments) {
        DialogFragment analogClockFragment = new AnalogTimePickerDialogFragment();
        analogClockFragment.setArguments(arguments);
        analogClockFragment.show(getSupportFragmentManager(), "digitalTimePicker");
    }

    @Override
    public void onAlarmCreated(Alarm alarm) {
        presenter.onAlarmCreated(alarm);
    }

    @Override
    public void onAlarmModified(Alarm alarm, int position) {
        presenter.onAlarmModified(alarm, position);
    }

    @Override
    public void removeAlarmListElement(int position) {
        alarmsRecyclerView.getRecycledViewPool().clear();
        alarmsAdapter.remove(position);
        sharedPrefsManager.saveAlarmsList(alarmsAdapter.getAlarmsList());
        callAlarmScheduleService();
    }

    @Override
    public void addAlarmListElement(Alarm alarm) {
        alarmsRecyclerView.getRecycledViewPool().clear();
        alarmsAdapter.addAlarm(alarm);
        sharedPrefsManager.saveAlarmsList(alarmsAdapter.getAlarmsList());
        callAlarmScheduleService();
    }

    @Override
    public void modifyAlarmListElement(Alarm alarm, int position) {
        alarmsRecyclerView.getRecycledViewPool().clear();
        alarmsAdapter.modifyAlarm(alarm, position);
        sharedPrefsManager.saveAlarmsList(alarmsAdapter.getAlarmsList());
        callAlarmScheduleService();
    }

    @Override
    public void onListRowLongClick(int position) {
        presenter.onAlarmListRowLongClick(position);
    }

    @Override
    public void onAlarmCheckBoxClicked(Alarm alarm, int position, boolean checked) {
        presenter.onAlarmCheckboxClicked(position, checked);
    }

    @Override
    public void onAlarmHourTextClicked(Alarm alarm, int position) {
        presenter.onAlarmHourTextClicked(alarm, position);
    }
}