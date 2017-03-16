package expandingrecyclerview;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.example.radek.nfc_test.R;

/**
 * Created by Radek on 2016-08-13.
 */
public class AlarmChildViewHolder extends ChildViewHolder {

    public TextView placeholderTextView;

    public AlarmChildViewHolder(View itemView) {
        super(itemView);
        placeholderTextView = (TextView) itemView.findViewById(R.id.placeHolderChildTextView);
    }


}
