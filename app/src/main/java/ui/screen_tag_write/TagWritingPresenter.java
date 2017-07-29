package ui.screen_tag_write;

import ui.base.BasePresenter;
import ui.base.BaseView;

class TagWritingPresenter extends BasePresenter {

    @Override
    public BaseView getNoOpView() {
        return NoOpTagWritingView.INSTANCE;
    }
}
