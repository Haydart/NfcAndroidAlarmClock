package ui.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements BaseView {
    protected P presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        bindViews();
        initPresenter();
    }

    private void bindViews() {
        ButterKnife.bind(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onStart() {
        super.onStart();
        if (presenter != null) {
            presenter.onViewStarted(this);
        }
    }

    @Override
    protected void onStop() {
        if (presenter != null) {
            presenter.onViewStopped();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        presenter = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (shouldMoveToBack()) {
            super.onBackPressed();
        } else {
            moveTaskToBack(true);
        }
    }

    protected abstract void initPresenter();

    protected abstract int getLayoutResId();

    protected boolean shouldMoveToBack() {
        return false;
    }
}
