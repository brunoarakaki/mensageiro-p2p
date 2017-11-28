package com.poli.usp.whatsp2p.ui.dht_visualization;

import com.poli.usp.whatsp2p.ui.base.BasePresenter;
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
