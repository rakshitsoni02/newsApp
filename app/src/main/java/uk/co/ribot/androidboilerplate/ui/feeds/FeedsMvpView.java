package uk.co.ribot.androidboilerplate.ui.feeds;

import java.util.List;

import uk.co.ribot.androidboilerplate.data.model.Ribot;
import uk.co.ribot.androidboilerplate.ui.base.MvpView;

public interface FeedsMvpView extends MvpView {

    void showRibots(List<Ribot> ribots);

    void showRibotsEmpty();

    void showError();

}
