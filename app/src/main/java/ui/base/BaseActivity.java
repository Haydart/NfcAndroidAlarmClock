package ui.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import butterknife.ButterKnife;

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements BaseView {
    private static BaseActivity current;
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
    protected void onResume() {
        super.onResume();
        current = this;
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

    public static void unlockScreen() {
        if (current == null) return;

        Log.i("BASE", "Turning on screen ... ");

        Window window = current.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    public static void clearScreen() {
        if (current == null) return;

        Log.i("BASE", "Clearing screen flag when on ... ");

        Window window = current.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }
}
