package br.com.mobile2you.m2ybase.ui.dht_visualization;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import br.com.mobile2you.m2ybase.R;
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
