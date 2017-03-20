package expanding_recycler_view.alarms_list;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.example.radek.nfc_test.R;
import model.Alarm;

/**
 * Created by Radek on 2016-08-13.
 */
public class AlarmParentViewHolder extends ParentViewHolder {

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    private View itemView;
    private AlarmsListRowListener listener;

    @BindView(R.id.alarmHourTextView) TextView alarmHourTextView;
    @BindView(R.id.alarmDayTextView) TextView alarmDayTextView;
    @BindView(R.id.alarmStateCheckBox) CheckBox alarmCheckBox;
    @BindView(R.id.arrowExpandImageView) ImageView arrowExpandImageView;

    public AlarmParentViewHolder(View itemView, AlarmsListRowListener listener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.itemView = itemView;
        this.listener = listener;
    }

    public void bindView(final ParentListItem listRowDataModel) {
        final Alarm alarm = (Alarm) listRowDataModel;
        alarmHourTextView.setText(alarm.getStringNotation());
        alarmDayTextView.setText(alarm.getAlarmName());
        alarmCheckBox.setChecked(alarm.isAlarmActive());

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onListRowLongClick(getAdapterPosition());
                return false;
            }
        });

        alarmHourTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAlarmHourTextClicked(alarm, getAdapterPosition());
            }
        });

        alarmCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = getAdapterPosition();
                listener.onAlarmCheckBoxClicked(alarm, position, isChecked);
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (expanded) {
            arrowExpandImageView.setRotation(ROTATED_POSITION);
        } else {
            arrowExpandImageView.setRotation(INITIAL_POSITION);
        }
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
        RotateAnimation rotateAnimation;
        if (expanded) {
            rotateAnimation = new RotateAnimation(
                    ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f
            );
        } else {
            rotateAnimation = new RotateAnimation(
                    -1 * ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f
            );
        }

        rotateAnimation.setDuration(300);
        rotateAnimation.setFillAfter(true);
        arrowExpandImageView.startAnimation(rotateAnimation);
    }
}
