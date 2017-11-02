package br.com.mobile2you.m2ybase.ui.dht_visualization;

import br.com.mobile2you.m2ybase.ui.base.BasePresenter;
import rx.Subscription;

/**
 * Created by Bruno on 02-Nov-17.
 */

public class DhtVisualizationPresenter extends BasePresenter<DhtVisualizationMvpView> {
    private DhtVisualizationMvpView mMvpView;
    private Subscription mSubscription;

    @Override
    public void attachView(DhtVisualizationMvpView mvpView) {
        super.attachView(mvpView);
        mMvpView = mvpView;
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();

    }
}
