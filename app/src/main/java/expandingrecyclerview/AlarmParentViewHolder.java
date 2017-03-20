package expandingrecyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.example.radek.nfc_test.R;
import ui.alarms_list.AlarmsActivity;

/**
 * Created by Radek on 2016-08-13.
 */
public class AlarmParentViewHolder extends ParentViewHolder{

    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;

    public View rowView;
    public TextView alarmHourTextView;
    public TextView alarmDayTextView;
    public CheckBox alarmCheckBox;
    public ImageView arrowExpandImageView;
    private Context context;
    private AlarmsActivity mainActivity;

    public AlarmParentViewHolder(AlarmsActivity mainActivity, View itemView) {
        super(itemView);
        this.rowView = itemView;
        this.mainActivity = mainActivity;
        this.context = mainActivity.getApplicationContext();
        alarmHourTextView = (TextView) itemView.findViewById(R.id.alarmHourTextView);
        alarmDayTextView = (TextView) itemView.findViewById(R.id.alarmDayTextView);
        alarmCheckBox = (CheckBox) itemView.findViewById(R.id.alarmStateCheckBox);
        arrowExpandImageView = (ImageView) itemView.findViewById(R.id.arrowExpandImageView);
    }

    @SuppressLint("NewApi")
    @Override
    public void setExpanded(boolean expanded) {
        super.setExpanded(expanded);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (expanded) {
                arrowExpandImageView.setRotation(ROTATED_POSITION);
            } else {
                arrowExpandImageView.setRotation(INITIAL_POSITION);
            }
        }
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        super.onExpansionToggled(expanded);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            RotateAnimation rotateAnimation;
            if (expanded) { // rotate clockwise
                rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            } else { // rotate counterclockwise
                rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                        INITIAL_POSITION,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            }

            rotateAnimation.setDuration(200);
            rotateAnimation.setFillAfter(true);
            arrowExpandImageView.startAnimation(rotateAnimation);
        }
    }
}
