package ui.alarms_list;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import expandingrecyclerview.AlarmRecyclerViewAdapter;
import java.util.List;
import misc.Constants;
import misc.SharedPrefsManager;
import model.Alarm;
import ui.base.BaseActivity;

public final class AlarmsActivity extends BaseActivity<AlarmsPresenter> implements AlarmsView {

    @BindView(R.id.alarm_recycler_view) RecyclerView alarmsRecyclerView;
    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    private NfcAdapter nfcAdapter;
    private AlarmRecyclerViewAdapter alarmsAdapter;
    private List<Alarm> alarmsList;
    private SharedPrefsManager spManager;

    @OnClick(R.id.fab)
    public void onFabClicked() {
        presenter.onActionButtonClicked();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spManager = new SharedPrefsManager(getApplicationContext());
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        alarmsList = spManager.loadAlarmsList();
        initializeRecyclerView();
        callAlarmScheduleService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkForNfcService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        alarmsList = spManager.loadAlarmsList();
        printActiveAlarmsList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        spManager.saveAlarmsList(alarmsList);
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
        alarmsAdapter = new AlarmRecyclerViewAdapter(this, alarmsList);
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
    public void displayAlarmDeletionAlertDialog(final int position) {
        AlertDialog alarmDeletionDialog = new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Do you want to delete alarm for " + position)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        AlarmsActivity.this.onAlarmDeleted(position);
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
                        }).setActionTextColor(Color.YELLOW).show();
            } else {
                Snackbar.make(coordinatorLayout, "NFC service is available!", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        spManager.notifyNfcTagAttached();
        super.onNewIntent(intent);
        // TODO: 16/03/2017 recognize NDEF formatted contents data
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.ALARM_DETAILS_ACTIVITY_REQUESTCODE) {
            if (resultCode == Constants.ALARM_DETAILS_ACTIVITY_RESULTCODE) {
                Alarm alarm = data.getExtras().getParcelable("ALARM");
                int updatedAlarmPosition = data.getIntExtra("ALARM_POSITION", -1);

                if (updatedAlarmPosition == -1) { //if that`s a new alarm
                    alarmsList.add(alarm);
                    updatedAlarmPosition = alarmsList.size() - 1;
                } else {//the alarm was only edited
                    alarmsList.set(updatedAlarmPosition, alarm);
                }

                alarmsAdapter.updateAlarmsList(alarmsList);

                //newly created or edited alarm are set to active by default
                refreshAlarmsList();
                displayAlarmTimeSnackbar(updatedAlarmPosition);
                callAlarmScheduleService();
            }
        }
    }

    private void refreshAlarmsList() {
        spManager.saveAlarmsList(alarmsList);
        alarmsList = spManager.loadAlarmsList();
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

    private void onAlarmDeleted(int position) {
        alarmsList.remove(position);
        alarmsAdapter.updateAlarmsList(alarmsList);
        spManager.saveAlarmsList(alarmsList);
        callAlarmScheduleService();
    }

    @Override
    public void displayAlarmModificationDialog() {
        // TODO: 16/03/2017 implement
    }

    @Override
    public void displayAlarmCreationDialog() {
        // TODO: 16/03/2017 implement
    }
}