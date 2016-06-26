package com.example.radek.nfc_test;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.SharedElementCallback;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.provider.*;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "Alarm Clock";
    private static final String MIME_TEXT_PLAIN = "text/plain";
    private TextView textView;
    private NfcAdapter nfcAdapter;
    private FloatingActionButton fab;
    private ListView alarmsListView;
    private ArrayList<Alarm> alarmsList;
    private AlarmsListAdapter alarmsAdapter;
    private SharedPrefsManager spManager;
    private CoordinatorLayout coordinatorLayout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toast.makeText(this, "ON CREATE", Toast.LENGTH_SHORT).show();

        findReferences();
        alarmsList = spManager.loadAlarmsList();

        callNFCAlarmScheduleService();

        alarmsAdapter = new AlarmsListAdapter(this, alarmsList);
        alarmsListView.setAdapter(alarmsAdapter);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AlarmDetailsActivity.class);
                alarmsList.add(new Alarm());
                intent.putExtra("ALARM", alarmsList.get(alarmsList.size() - 1)); // pass newly created alement
                intent.putExtra("ALARM_POSITION", alarmsList.size() - 1); // indicate that the newly zreated alarm is on last position
                startActivityForResult(intent, 666);
            }
        });

        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn`t support NFC", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
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

        alarmsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int position2 = position;
                boolean result = false;

                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(MainActivity.this)
                        //set message, title, and icon
                        .setTitle("Delete alarm clock")
                        .setMessage("Do you really want to delete specified record?")

                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                callNFCAlarmScheduleService();
                                alarmsList.remove(position2);
                                alarmsAdapter.setAlarms(alarmsList);
                                alarmsAdapter.notifyDataSetChanged();
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
                return true;
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        spManager.saveAlarmsList(alarmsList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        spManager.saveAlarmsList(alarmsList);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Snackbar alarmPutOffSnackbar = Snackbar.make(coordinatorLayout,"Proper NFC tag discovered, have a good day!",Snackbar.LENGTH_SHORT);
        alarmPutOffSnackbar.show();
        super.onNewIntent(intent);
        finish();
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode == 666) {
            if(resultCode == 666) {
                Alarm alarm = (Alarm) data.getExtras().getSerializable("ALARM");
                int updatedAlarmPosition = data.getIntExtra("ALARM_POSITION", 0);
                alarmsList.set(updatedAlarmPosition, alarm);
                alarmsAdapter.setAlarms(alarmsList);
                alarmsAdapter.notifyDataSetChanged();

                displayAlarmTimeSnackbar(updatedAlarmPosition);
            }
        }
    }

    private void refreshAlarmsList()
    {
        spManager.saveAlarmsList(alarmsList);
        alarmsList = spManager.loadAlarmsList();
    }

    private void findReferences() {
        alarmsListView = (ListView) findViewById(R.id.listView);
        spManager = new SharedPrefsManager(getApplicationContext());
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        alarmsListView = (ListView) findViewById(R.id.listView);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    protected void callNFCAlarmScheduleService() {
        Intent nfcAlarmServiceIntent = new Intent(this, AlarmServiceBroadcastReceiver.class);
        sendBroadcast(nfcAlarmServiceIntent, null);
    }

    public void updateAlarm(int position){
        refreshAlarmsList();
        alarmsAdapter.setAlarms(alarmsList);

        if(alarmsList.get(position).isAlarmActive()) {
            alarmsList.get(position).setAlarmActive(false);
        }else{
            alarmsList.get(position).setAlarmActive(true);
            displayAlarmTimeSnackbar(position);
        }

        callNFCAlarmScheduleService();
    }

    public void displayAlarmTimeSnackbar(int alarmPosition){
        Snackbar.make(coordinatorLayout,alarmsList.get(alarmPosition).getTimeUntilNextAlarmMessage(),Snackbar.LENGTH_LONG).show();
    }
}