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
import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.example.radek.nfc_test.R;
import expanding_recycler_view.alarms_list.AlarmRecyclerViewAdapter;
import expanding_recycler_view.alarms_list.AlarmsListRowListener;
import java.util.List;
import misc.PersistentDataStorage;
import model.Alarm;
import ui.base.BaseActivity;
import ui.widget.dialog_fragment.AnalogTimePickerDialogFragment;

public final class AlarmsActivity extends BaseActivity<AlarmsPresenter> implements AlarmsView, AlarmsListRowListener, AlarmDialogListener {

    @BindView(R.id.alarm_recycler_view) RecyclerView alarmsRecyclerView;
    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    private NfcAdapter nfcAdapter;
    private AlarmRecyclerViewAdapter alarmsAdapter;
    private PersistentDataStorage sharedPrefsManager;
    private List<Alarm> alarmsList;

    @OnClick(R.id.fab)
    public void onFabClicked() {
        presenter.onActionButtonClicked();
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
        alarmsList = sharedPrefsManager.loadAlarmsList();
        callAlarmScheduleService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPrefsManager.saveAlarmsList(alarmsList);
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
            public void onListItemExpanded(int position) {
                alarmsAdapter.collapseAllParents();
                alarmsAdapter.expandParent(position);
            }

            @Override
            public void onListItemCollapsed(int position) {
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
        // TODO: 16/03/2017 recognize NDEF formatted contents data
        finish();
    }

    private void refreshAlarmsList() {
        sharedPrefsManager.saveAlarmsList(alarmsList);
        alarmsList = sharedPrefsManager.loadAlarmsList();
    }

    protected void callAlarmScheduleService() {
        Intent nfcAlarmServiceIntent = new Intent(this, AlarmServiceBroadcastReceiver.class);
        sendBroadcast(nfcAlarmServiceIntent, null);
    }

    public void updateAlarm(int position, boolean newState) {
        alarmsList.get(position).setAlarmActive(newState);

        if (newState) {
            displayAlarmTimeSnackbar(position);
        }

        refreshAlarmsList();
        callAlarmScheduleService();
        printActiveAlarmsList();
    }

    public void displayAlarmTimeSnackbar(int alarmPosition) {
        Snackbar.make(coordinatorLayout, alarmsList.get(alarmPosition).getTimeUntilNextAlarmMessage(), Snackbar.LENGTH_LONG).show();
    }

    public void printActiveAlarmsList() {
        for (int i = 0; i < alarmsList.size(); i++) {
            if (alarmsList.get(i).isAlarmActive()) {
                Log.d("ACTIVE ALARM PRINTOUT", alarmsList.get(i).getTimeUntilNextAlarmMessage() + " on " + alarmsList.get(i).getStringNotation());
                //Log.d("ALARM ACTIVE?", "ALARM " + i + " is " + alarmsList.get(i).isAlarmActive());
            }
        }
    }

    @Override
    public void displayAlarmCreationDialog() {
        displayAlarmDialog(null);
    }

    @Override
    public void displayAlarmModificationDialog(Alarm alarm, int position) {
        Bundle arguments = new Bundle();
        arguments.putParcelable("alarm", alarm);
        arguments.putInt("position", position);
        displayAlarmDialog(arguments);
    }

    private void displayAlarmDialog(Bundle arguments) {
        DialogFragment analogClockFragment = new AnalogTimePickerDialogFragment();
        analogClockFragment.setArguments(arguments);
        analogClockFragment.show(getSupportFragmentManager(), "digitalTimePicker");
    }

    @Override
    public void removeAlarmListElement(int position) {
        alarmsAdapter.remove(position);
        alarmsAdapter.updateAlarmsList(alarmsList);
        sharedPrefsManager.saveAlarmsList(alarmsList);
        callAlarmScheduleService();
    }

    @Override
    public void addAlarmListElement(Alarm alarm) {
        alarmsAdapter.addAlarm(alarm);
    }

    @Override
    public void onAlarmModified(Alarm alarm, int position) {
        alarmsAdapter.modifyAlarm(alarm, position);
    }

    @Override
    public void onAlarmCreated(Alarm alarm) {
        presenter.onAlarmCreated(alarm);
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