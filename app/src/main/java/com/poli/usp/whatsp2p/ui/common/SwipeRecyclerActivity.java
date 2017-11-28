package com.poli.usp.whatsp2p.ui.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.poli.usp.whatsp2p.ui.base.BaseActivity;

import poli.com.mobile2you.whatsp2p.R;

/**
 * Created by mobile2you on 30/11/16.
 */

public class SwipeRecyclerActivity extends BaseActivity {
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiped_recycler_view);
    }

    //SWIPEREFRESH METHODS
    protected void setSwipeRefresh(SwipeRefreshLayout.OnRefreshListener listener) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        if (hasSwipeRefresh()) {
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
            mSwipeRefreshLayout.setOnRefreshListener(listener);
        }
    }

    protected void disableSwipeRefresh() {
        if (hasSwipeRefresh()) {
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    protected void dismissSwipeRefresh() {
        if (hasSwipeRefresh()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    protected boolean isRefreshing() {
        return hasSwipeRefresh() && mSwipeRefreshLayout.isRefreshing();
    }

    private boolean hasSwipeRefresh() {
        return mSwipeRefreshLayout != null;
    }
}
