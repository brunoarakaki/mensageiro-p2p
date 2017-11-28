package com.poli.usp.whatsp2p.ui.dht_visualization;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.poli.tcc.dht.DHT;

import poli.com.mobile2you.whatsp2p.R;
import butterknife.BindView;
import butterknife.ButterKnife;


public class DhtVisualizationActivity extends AppCompatActivity implements DhtVisualizationMvpView {
    private DhtVisualizationPresenter mPresenter;
    private DhtVisualizationAdapter mAdapter;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dht_visualization);
        ButterKnife.bind(this);
        mPresenter = new DhtVisualizationPresenter();
        mPresenter.attachView(this);

        setRecyclerView();
        mAdapter.setPeerAddresses(DHT.serverPeer.peer().peerBean().peerMap().all());
    }


    private void setRecyclerView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DhtVisualizationAdapter(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

}
