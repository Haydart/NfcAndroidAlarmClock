package expanding_recycler_view.alarms_list;

import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.example.radek.nfc_test.R;
import model.Alarm;

/**
 * Created by Radek on 2016-08-13.
 */
public class AlarmChildViewHolder extends ChildViewHolder {

    @BindView(R.id.placeHolderChildTextView) TextView placeholderTextView;

    public AlarmChildViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindView(Alarm alarm) {
        placeholderTextView.setText(alarm.getAlarmName());
    }
}
