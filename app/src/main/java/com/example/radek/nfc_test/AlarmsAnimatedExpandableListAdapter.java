package com.example.radek.nfc_test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.radek.nfc_test.expandingcells.ExpandableListItem;
import com.example.radek.nfc_test.expandingcells.ExpandingLayout;
import com.example.radek.nfc_test.expandingcells.ExpandingListView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Radek on 2016-05-27.
 */
public class AlarmsAnimatedExpandableListAdapter extends ArrayAdapter<ExpandableListItem>
{
    private Context context;
    private MainActivity main;
    private List<ExpandableListItem> alarmsListResource;
    private ExpandingListView expandingListView;
    private TextView hourTextView;
    private TextView alarmDay;
    private CheckBox checkBox;

    private static LayoutInflater inflater=null;

    AlarmsAnimatedExpandableListAdapter(Context context, List<ExpandableListItem> alarmsListResource, ExpandingListView expandingListView)
    {
        super(context, R.layout.alarm_list_layout, alarmsListResource);
        this.context = context;
        this.alarmsListResource = alarmsListResource;
        this.expandingListView = expandingListView;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        RelativeLayout relativeLayout = (RelativeLayout)(rowView.findViewById(R.id.collapsedListItemPartLayour));
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams
                (AbsListView.LayoutParams.MATCH_PARENT, alarmsListResource.get(position).getCollapsedHeight());
        relativeLayout.setLayoutParams(relativeLayoutParams);

        if(alarmsListResource.get(position).getAlarm().isAlarmActive())
            checkBox.setChecked(true);

        hourTextView.setText(alarmsListResource.get(position).getAlarm().getStringNotation().toString());

        rowView.setLayoutParams(new ListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT));

        ExpandingLayout expandingLayout = (ExpandingLayout)rowView.findViewById(R.id.expanding_layout);
        expandingLayout.setExpandedHeight(alarmsListResource.get(position).getExpandedHeight());
        expandingLayout.setSizeChangedListener(alarmsListResource.get(position));

        if (!alarmsListResource.get(position).isExpanded()) {
            expandingLayout.setVisibility(View.GONE);
        } else {
            expandingLayout.setVisibility(View.VISIBLE);
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("asxasxasxasxasx", "CLICKED ITEM" + position);
                if(!alarmsListResource.get(position).isExpanded()){
                    expandingListView.expandView(rowView);
                    alarmsListResource.get(position).setExpanded(true);
                }else{
                    expandingListView.collapseView(rowView);
                    alarmsListResource.get(position).setExpanded(false);
                }
                Log.d("asxasxasxasxasx", "CLICKED ITEM EXPANDED = " + alarmsListResource.get(position).isExpanded());
            }
        });

        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getContext())
                        //set message, title, and icon
                        .setTitle("Delete alarm clock")
                        .setMessage("Do you really want to delete specified record?")

                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                ((MainActivity)context).OnAlarmDeleted(position);
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

        hourTextView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context.getApplicationContext(), AlarmDetailsActivity.class);
            intent.putExtra("ALARM", alarmsListResource.get(position).getAlarm());
            intent.putExtra("ALARM_POSITION", position);
            ((MainActivity)context).startActivityForResult(intent, 666);
        }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(context, "clicked checkbox", Toast.LENGTH_SHORT).show();
                ((MainActivity)context).updateAlarm(position, checkBox.isChecked());
                ((MainActivity)context).printActiveAlarmsList();
            }
        });

        return rowView;
    }

    public void setAlarms(List<ExpandableListItem> alarms) {alarmsListResource = alarms;}
}
