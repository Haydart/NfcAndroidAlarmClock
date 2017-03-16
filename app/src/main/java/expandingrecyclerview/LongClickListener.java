package expandingrecyclerview;

import android.view.View;

/**
 * Created by Radek on 2016-08-14.
 */
public interface LongClickListener {
    public void onClick(View view, int position);
    public void onLongClick(View view,int position);
}
