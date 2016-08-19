package com.example.radek.nfc_test.expandingrecyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.example.radek.nfc_test.Alarm;
import com.example.radek.nfc_test.MainActivity;
import com.example.radek.nfc_test.R;

import java.util.List;

/**
 * Created by Radek on 2016-08-13.
 */
public class AlarmExpandableRecyclerViewAdapter extends ExpandableRecyclerAdapter<AlarmParentViewHolder, AlarmChildViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private MainActivity mainActivity;
    private List<Alarm> alarmsListResource;

    public interface OnItemClickListener {
        public void onItemClicked(int position);
    }

    public interface OnItemLongClickListener {
        public boolean onItemLongClicked(int position);
    }

    public AlarmExpandableRecyclerViewAdapter(MainActivity mainActivity, List<Alarm> parentItemList) {
        super(parentItemList);
        this.mainActivity = mainActivity;
        this.context = mainActivity.getApplicationContext();
        this.alarmsListResource = parentItemList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public AlarmParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.alarm_list_parent_layout, viewGroup, false);
        return new AlarmParentViewHolder(mainActivity, view);
    }

    @Override
    public AlarmChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.alarm_list_child_layout, viewGroup, false);
        return new AlarmChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(final AlarmParentViewHolder parentViewHolder, final int position, ParentListItem parentListItem) {
        Alarm alarm = (Alarm) parentListItem;

        parentViewHolder.alarmHourTextView.setText(alarm.getStringNotation());
        parentViewHolder.alarmDayTextView.setText(alarm.getAlarmName());
        parentViewHolder.alarmCheckBox.setChecked(alarm.isAlarmActive());

        parentViewHolder.alarmHourTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.launchAlarmDetailsActivity(MainActivity.LaunchType.CLICKED_ALARM, position);
            }
        });

        parentViewHolder.rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mainActivity.displayAlarmDeletionAlertDialog(position);
                return false;
            }
        });

        parentViewHolder.alarmCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mainActivity.updateAlarm(position,isChecked);
            }
        });
    }

    @Override
    public void onBindChildViewHolder(AlarmChildViewHolder childViewHolder, int position, Object childListItem) {
        Alarm alarm = (Alarm) childListItem;

        childViewHolder.placeholderTextView.setText(context.getResources().getString(R.string.placeholder_string));
    }

    public void updateAlarmsList(List<Alarm> alarmsList){
        notifyParentItemRangeRemoved(0, alarmsListResource.size());
        alarmsListResource.clear();
        alarmsListResource.addAll(alarmsList);
        notifyParentItemRangeInserted(0, alarmsList.size());
    }
}