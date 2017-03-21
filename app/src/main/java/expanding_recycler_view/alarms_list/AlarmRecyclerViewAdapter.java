package expanding_recycler_view.alarms_list;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.example.radek.nfc_test.R;
import java.util.List;
import model.Alarm;

/**
 * Created by Radek on 2016-08-13.
 */
public class AlarmRecyclerViewAdapter extends ExpandableRecyclerAdapter<Alarm, String, AlarmParentViewHolder, AlarmChildViewHolder> {

    private List<Alarm> alarmsListSource;
    private AlarmsListRowListener parentInteractionListener;

    public AlarmRecyclerViewAdapter(List<Alarm> alarmList, AlarmsListRowListener interactionListener) {
        super(alarmList);
        this.alarmsListSource = alarmList;
        this.parentInteractionListener = interactionListener;
    }

    @UiThread
    @NonNull
    @Override
    public AlarmParentViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View parentItemView = LayoutInflater
                .from(parentViewGroup.getContext())
                .inflate(R.layout.alarm_list_parent_layout, parentViewGroup, false);
        Log.d(getClass().getSimpleName(), "ite count: " + getItemCount());
        return new AlarmParentViewHolder(parentItemView, parentInteractionListener);
    }

    @UiThread
    @NonNull
    @Override
    public AlarmChildViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View childItemView = LayoutInflater
                .from(childViewGroup.getContext())
                .inflate(R.layout.alarm_list_child_layout, childViewGroup, false);
        Log.d(getClass().getSimpleName(), "ite count: " + getItemCount());
        return new AlarmChildViewHolder(childItemView);
    }

    @UiThread
    @Override
    public void onBindParentViewHolder(@NonNull AlarmParentViewHolder parentViewHolder, int parentPosition, @NonNull Alarm dataItem) {
        Log.d(getClass().getSimpleName(), "ite count: " + getItemCount());
        parentViewHolder.bindView(dataItem);
    }

    @UiThread
    @Override
    public void onBindChildViewHolder(@NonNull AlarmChildViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull String childDataItem) {
        Log.d(getClass().getSimpleName(), "ite count: " + getItemCount());
        childViewHolder.bindView(childDataItem);
    }

    public void updateAlarmsList(List<Alarm> alarmsList) {
        int removedItemsCount = alarmsListSource.size();
        alarmsListSource.clear();
        notifyParentRangeRemoved(0, removedItemsCount);
        alarmsListSource.addAll(alarmsList);
        notifyParentRangeInserted(0, alarmsList.size());
    }

    public List<Alarm> getAlarmsList() {
        return alarmsListSource;
    }

    public void addAlarm(Alarm alarm) {
        alarmsListSource.add(alarm);
        notifyParentInserted(alarmsListSource.size() - 1);
    }

    public void modifyAlarm(Alarm alarm, int position) {
        alarmsListSource.set(position, alarm);
        notifyParentChanged(position);
    }

    public void remove(int position) {
        alarmsListSource.remove(position);
        notifyParentRemoved(position);
    }
}