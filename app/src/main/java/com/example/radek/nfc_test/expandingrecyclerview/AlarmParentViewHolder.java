package com.example.radek.nfc_test.expandingrecyclerview;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.example.radek.nfc_test.MainActivity;
import com.example.radek.nfc_test.R;

/**
 * Created by Radek on 2016-08-13.
 */
public class AlarmParentViewHolder extends ParentViewHolder{

    public View rowView;
    public TextView mAlarmHourTextView;
    public TextView mAlarmDayTextView;
    public CheckBox alarmCheckBox;
    private Context context;
    private MainActivity mainActivity;

    public AlarmParentViewHolder(MainActivity mainActivity, View itemView) {
        super(itemView);
        this.rowView = itemView;
        this.mainActivity = mainActivity;
        this.context = mainActivity.getApplicationContext();
        mAlarmHourTextView = (TextView) itemView.findViewById(R.id.alarmHourTextView);
        mAlarmDayTextView = (TextView) itemView.findViewById(R.id.alarmDayTextView);
        alarmCheckBox = (CheckBox) itemView.findViewById(R.id.alarmStateCheckBox);
    }


}
