package expanding_recycler_view.alarms_list;

import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.example.radek.nfc_test.R;

/**
 * Created by Radek on 2016-08-13.
 */
class AlarmChildViewHolder extends ChildViewHolder {

    @BindView(R.id.placeHolderChildTextView) TextView placeholderTextView;

    AlarmChildViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    void bindView(String dataItem) {
        //placeholderTextView.setText(dataItem);
    }
}
