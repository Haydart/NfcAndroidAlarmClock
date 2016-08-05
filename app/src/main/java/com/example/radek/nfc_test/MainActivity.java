package com.example.radek.nfc_test;

import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.example.radek.nfc_test.expandingcells.ExpandableListItem;
import com.example.radek.nfc_test.expandingcells.ExpandingListView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private FloatingActionButton fab;
    private ExpandingListView alarmsAnimatedExpandableListView;
    private List<ExpandableListItem> expandableListItemList;
    private AlarmsAnimatedExpandableListAdapter alarmsAdapter;
    private SharedPrefsManager spManager;
    private CoordinatorLayout coordinatorLayout;

    public enum LaunchType{
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
        expandableListItemList = spManager.loadExpandableItemsList();

        callNFCAlarmScheduleService();

        alarmsAdapter = new AlarmsAnimatedExpandableListAdapter(this, expandableListItemList, alarmsAnimatedExpandableListView);
        alarmsAnimatedExpandableListView.setAdapter(alarmsAdapter);
        alarmsAnimatedExpandableListView.setDivider(null);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAlarmDetailsActivity(LaunchType.CLICKED_FAB, -1);
            }
        });

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

        if(launchType==LaunchType.CLICKED_ALARM){
            intent.putExtra("ALARM", expandableListItemList.get(position).getAlarm());
            intent.putExtra("ALARM_POSITION", position);
            Toast.makeText(getApplicationContext(), "CLICKED ALARM", Toast.LENGTH_SHORT).show();
        }else if(launchType==LaunchType.CLICKED_FAB){
            intent.putExtra("ALARM_POSITION", position); // -1
            Toast.makeText(getApplicationContext(), "CLICKED FAB", Toast.LENGTH_SHORT).show();
        }
        startActivityForResult(intent, Settings.ALARM_DETAILS_ACTIVITY_REQUESTCODE); //in activity, check if it is null
    }

    @Override
    protected void onResume() {
        super.onResume();
        expandableListItemList = spManager.loadExpandableItemsList();
        printActiveAlarmsList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        spManager.saveAlarmsList(expandableListItemList);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Snackbar alarmPutOffSnackbar = Snackbar.make(coordinatorLayout, "Proper NFC tag discovered, have a good day!", Snackbar.LENGTH_SHORT);
        alarmPutOffSnackbar.show();
        super.onNewIntent(intent);
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Settings.ALARM_DETAILS_ACTIVITY_REQUESTCODE) {
            if (resultCode == Settings.ALARM_DETAILS_ACTIVITY_RESULTCODE) {
                Alarm alarm = (Alarm) data.getExtras().getParcelable("ALARM");
                int updatedAlarmPosition = data.getIntExtra("ALARM_POSITION", -1);

                if(updatedAlarmPosition == -1) { //if that`s a new alarm
                    expandableListItemList.add(new ExpandableListItem(alarm, Settings.COLLAPSED_HEIGHT));
                    updatedAlarmPosition = expandableListItemList.size()-1; // was throwing ArrayOutOfBounds in displayAlarmTimeSnackbar
                    Log.d("asxasxasxasx","expandable size after add =" +expandableListItemList.size());
                }else{//the alarm was only edited
                    expandableListItemList.get(updatedAlarmPosition).setAlarm(alarm);
                }
                alarmsAdapter.setAlarmsResource(expandableListItemList);
                alarmsAdapter.notifyDataSetChanged();

                //newly created or edited alarm are set to active by default
                refreshAlarmsList();
                displayAlarmTimeSnackbar(updatedAlarmPosition);
                callNFCAlarmScheduleService();
            }
        }
    }

    private void refreshAlarmsList() {
        spManager.saveAlarmsList(expandableListItemList);
        expandableListItemList = spManager.loadExpandableItemsList();
    }

    private void findReferences() {
        alarmsAnimatedExpandableListView = (ExpandingListView) findViewById(R.id.alarmsExpandableListView);
        spManager = new SharedPrefsManager(getApplicationContext());
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        alarmsAnimatedExpandableListView = (ExpandingListView) findViewById(R.id.alarmsExpandableListView);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    protected void callNFCAlarmScheduleService() {
        Intent nfcAlarmServiceIntent = new Intent(this, AlarmServiceBroadcastReceiver.class);
        sendBroadcast(nfcAlarmServiceIntent, null);
    }

    public void updateAlarm(int position, boolean newState) {
        expandableListItemList.get(position).getAlarm().setAlarmActive(newState);

        if(newState){
            displayAlarmTimeSnackbar(position);
            Log.d("aaaaaaaaaaaaa", "newState is " + newState);
        }

        refreshAlarmsList();
        alarmsAdapter.setAlarmsResource(expandableListItemList);
        callNFCAlarmScheduleService();
    }

    public void displayAlarmTimeSnackbar(int alarmPosition) {
        Snackbar.make(coordinatorLayout, expandableListItemList.get(alarmPosition).getAlarm().getTimeUntilNextAlarmMessage(), Snackbar.LENGTH_LONG).show();
    }

    public void printActiveAlarmsList() {
        for (int i = 0; i < expandableListItemList.size(); i++) {
            if (expandableListItemList.get(i).getAlarm().isAlarmActive()) {
                //Log.d("ACTIVE ALARM PRINTOUT",alarmsList.get(i).getTimeUntilNextAlarmMessage() + " on " + alarmsList.get(i).getStringNotation() );
                Log.d("ALARM ACTIVE?", "ALARM " + i + " is " + expandableListItemList.get(i).getAlarm().isAlarmActive());
            }
        }
    }

    public void OnAlarmDeleted(int position) {
        expandableListItemList.remove(position);
        alarmsAdapter.setAlarmsResource(expandableListItemList);
        alarmsAdapter.notifyDataSetChanged();

        spManager.saveAlarmsList(expandableListItemList);
        callNFCAlarmScheduleService();
    }
}