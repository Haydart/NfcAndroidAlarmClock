package com.example.radek.nfc_test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Radek on 2016-05-27.
 */
public class AlarmsListAdapter extends ArrayAdapter<Alarm>
{
    private Context context;
    private MainActivity main;
    private List<Alarm> alarmsListResource;
    private TextView hourTextView;
    private TextView alarmDay;
    private CheckBox checkBox;

    private static LayoutInflater inflater=null;

    AlarmsListAdapter(Activity main, List<Alarm> alarmsListResource)
    {
        super(main, R.layout.alarm_list_layout, alarmsListResource);
        this.main = (MainActivity) main;
        this.context = main;
        this.alarmsListResource = alarmsListResource;
        inflater = ( LayoutInflater )main.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return alarmsListResource.size();
    }

    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }

    @Override
    public Alarm getItem(int position) {
        return alarmsListResource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final View rowView = inflater.inflate(R.layout.alarm_list_layout, parent, false);

        hourTextView = (TextView) rowView.findViewById(R.id.hourTextView);
        alarmDay = (TextView) rowView.findViewById(R.id.alarmDayTextView);
        checkBox = (CheckBox) rowView.findViewById(R.id.checkBox);

        if(alarmsListResource.get(position).isAlarmActive())
            checkBox.setChecked(true);

        hourTextView.setText(alarmsListResource.get(position).getStringNotation().toString());

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(main.getApplicationContext(), AlarmDetailsActivity.class);
                intent.putExtra("ALARM", alarmsListResource.get(position));
                intent.putExtra("ALARM_POSITION", position);
                main.startActivityForResult(intent, 666);
            }
        });

        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                boolean result = false;
                return result;
            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                main.updateAlarm(position);
            }
        });

        return rowView;
    }

    public void setAlarms(List<Alarm> alarms) {alarmsListResource = alarms;}
}
