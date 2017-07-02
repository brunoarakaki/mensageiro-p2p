package br.com.mobile2you.m2ybase.ui.main;

import android.os.Bundle;
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

    @BindView(R.id.list)
    ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mMainPresenter = new MainPresenter();
        mMainPresenter.attachView(this);

        mAdapter = new MainAdapter(this);
        mListView.setAdapter(mAdapter);

//        mMainPresenter.loadQuestions();
        mMainPresenter.loadPosts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.detachView();
    }

    @Override
    public void showQuestions(List<PollsResponse> polls) {
        mAdapter.setPollsResponses(polls);
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
