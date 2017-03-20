package expanding_recycler_view.alarms_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.example.radek.nfc_test.R;
import java.util.List;
import model.Alarm;

/**
 * Created by Radek on 2016-08-13.
 */
public class AlarmRecyclerViewAdapter extends ExpandableRecyclerAdapter<AlarmParentViewHolder, AlarmChildViewHolder> {

    private List<Alarm> alarmsListSource;
    private AlarmsListRowListener parentInteractionListener;

    public AlarmRecyclerViewAdapter(List<Alarm> alarmList, AlarmsListRowListener interactionListener) {
        super(alarmList);
        this.alarmsListSource = alarmList;
        this.parentInteractionListener = interactionListener;
    }

    @Override
    public AlarmParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View parentItemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.alarm_list_parent_layout, viewGroup, false);
        return new AlarmParentViewHolder(parentItemView, parentInteractionListener);
    }

    @Override
    public AlarmChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View childItemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.alarm_list_child_layout, viewGroup, false);
        return new AlarmChildViewHolder(childItemView);
    }

    @Override
    public void onBindParentViewHolder(AlarmParentViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        parentViewHolder.bindView(parentListItem);
    }

    @Override
    public void onBindChildViewHolder(AlarmChildViewHolder childViewHolder, int position, Object ignored) {
        childViewHolder.bindView(alarmsListSource.get(position));
    }

    public void updateAlarmsList(List<Alarm> alarmsList) {
        notifyParentItemRangeRemoved(0, alarmsListSource.size());
        alarmsListSource.clear();
        alarmsListSource.addAll(alarmsList);
        notifyParentItemRangeInserted(0, alarmsList.size());
    }

    public void addAlarm(Alarm alarm) {
        alarmsListSource.add(alarm);
        notifyItemInserted(alarmsListSource.size() - 1);
    }

    public void modifyAlarm(Alarm alarm, int position) {
        alarmsListSource.set(position, alarm);
        notifyItemChanged(position);
    }

    public void remove(int position) {
        alarmsListSource.remove(position);
        notifyItemRemoved(position);
    }
}