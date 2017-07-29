package ui.screen_tag_write;

import ui.base.BasePresenter;

final class TagWritingPresenter extends BasePresenter<TagWritingView> {
    @Override
    public TagWritingView getNoOpView() {
        return NoOpTagWritingView.INSTANCE;
    }
}
