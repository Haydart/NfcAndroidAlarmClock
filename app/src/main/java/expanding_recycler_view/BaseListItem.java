package expanding_recycler_view;

import android.os.Parcelable;

public abstract class BaseListItem implements Parcelable {
    public abstract int getLayoutResId();

    @Override
    public int describeContents() {
        return 0;
    }
}
