package br.com.mobile2you.m2ybase.ui.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import br.com.mobile2you.m2ybase.R;
import br.com.mobile2you.m2ybase.data.remote.models.PollsResponse;
import br.com.mobile2you.m2ybase.data.remote.models.PostsResponse;
import br.com.mobile2you.m2ybase.ui.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainMvpView {

    private MainPresenter mMainPresenter;
    private MainAdapter mAdapter;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mMainPresenter = new MainPresenter();
        mMainPresenter.attachView(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MainAdapter(new MainAdapter.OnClicked() {
            @Override
            public void onContactClicked(PostsResponse postsResponse) {
                showToast("Nice job, you are trying again");
            }
        },
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showToast("Nice job, you are trying again");
                }
        });
        mRecyclerView.setAdapter(mAdapter);
        setActionBar("Mensageiro P2P");

        mMainPresenter.loadPosts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_person) {
            showToast("Fazer magia para adicionar pessoa");
//            aperta aqui pra dar refresh nos dados
            mMainPresenter.loadPosts();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.detachView();
    }

    @Override
    public void showQuestions(List<PollsResponse> polls) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyQuestions() {
        showToast("Não existem questões no momento");
    }

    @Override
    public void showError(String error) {
        showToast(error);
    }

    @Override
    public void showProgress(boolean show) {
        showProgressDialog(show);
    }

    @Override
    public void showPosts(List<PostsResponse> posts) {
        mAdapter.setPosts(posts);
    }
}
