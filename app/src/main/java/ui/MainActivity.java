package ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import background.AlarmServiceBroadcastReceiver;
import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import misc.Constants;
import com.example.radek.nfc_test.R;
import misc.SharedPrefsManager;
import expandingrecyclerview.AlarmExpandableRecyclerViewAdapter;

import java.util.List;
import model.Alarm;

public class MainActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private FloatingActionButton fab;
    private RecyclerView alarmsExpandableRecyclerView;
    private List<Alarm> alarmsList;
    private AlarmExpandableRecyclerViewAdapter alarmsAdapter;
    private SharedPrefsManager spManager;
    private CoordinatorLayout coordinatorLayout;

    public enum LaunchType {
        CLICKED_FAB,
        CLICKED_ALARM;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findReferences();

        alarmsList = spManager.loadAlarmsList();
        initializeRecyclerView();
        checkForNfcService();
        callNFCAlarmScheduleService();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAlarmDetailsActivity(LaunchType.CLICKED_FAB, -1);
            }
        });
    }

    private void initializeRecyclerView() {
        alarmsAdapter = new AlarmExpandableRecyclerViewAdapter(this, alarmsList);
        alarmsAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @Override
            public void onListItemExpanded(int position) {
                alarmsAdapter.collapseAllParents(); //only one expanded at a time
                alarmsAdapter.expandParent(position);
            }

            @Override
            public void onListItemCollapsed(int position) {
            }
        });

        alarmsExpandableRecyclerView.setAdapter(alarmsAdapter);
        alarmsExpandableRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    public void displayAlarmDeletionAlertDialog(final int position) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to delete alarm for " + position)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        MainActivity.this.onAlarmDeleted(position);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        myQuittingDialogBox.show();
    }

    private void checkForNfcService() {
        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn`t support NFC", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (!nfcAdapter.isEnabled()) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "NFC service is disabled", Snackbar.LENGTH_INDEFINITE)
                        .setAction("SETTINGS", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), 0);
                            }
                        });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "NFC service is available!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
    }

    public void launchAlarmDetailsActivity(LaunchType launchType, int position) {
        Intent intent = new Intent(getApplicationContext(), AlarmDetailsActivity.class);

        if (launchType == LaunchType.CLICKED_ALARM) {
            intent.putExtra("ALARM", alarmsList.get(position));
            intent.putExtra("ALARM_POSITION", position);
        } else if (launchType == LaunchType.CLICKED_FAB) {
            intent.putExtra("ALARM_POSITION", position); // -1
        }
        startActivityForResult(intent, Constants.ALARM_DETAILS_ACTIVITY_REQUESTCODE); //in activity, check if it is null
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
    protected void onNewIntent(Intent intent) {
        spManager.notifyNfcTagAttached();
        super.onNewIntent(intent);
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
                callNFCAlarmScheduleService();
            }
        }
    }

    private void refreshAlarmsList() {
        spManager.saveAlarmsList(alarmsList);
        alarmsList = spManager.loadAlarmsList();
    }

    private void findReferences() {
        alarmsExpandableRecyclerView = (RecyclerView) findViewById(R.id.alarm_recycler_view);
        spManager = new SharedPrefsManager(getApplicationContext());
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    protected void callNFCAlarmScheduleService() {
        Intent nfcAlarmServiceIntent = new Intent(this, AlarmServiceBroadcastReceiver.class);
        sendBroadcast(nfcAlarmServiceIntent, null);
    }

    public void updateAlarm(int position, boolean newState) {
        alarmsList.get(position).setAlarmActive(newState);

        if (newState) {
            displayAlarmTimeSnackbar(position);
        }

        refreshAlarmsList();
        callNFCAlarmScheduleService();
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
        callNFCAlarmScheduleService();
    }
}