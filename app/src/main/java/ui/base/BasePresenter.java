package ui.base;

public abstract class BasePresenter<V extends BaseView> {
    protected V view;

    protected void onViewStarted(V view) {
        this.view = view;
    }

    protected void onViewStopped() {
        view = getNoOpView();
    }

    public abstract V getNoOpView();
}
