package br.com.mobile2you.m2ybase.ui.main;

import java.util.List;

import br.com.mobile2you.m2ybase.data.remote.models.PollsResponse;
import br.com.mobile2you.m2ybase.data.remote.models.PostsResponse;
import br.com.mobile2you.m2ybase.ui.base.MvpView;

/**
 * Created by mobile2you on 28/11/16.
 */

public interface MainMvpView extends MvpView {
    void showQuestions(List<PollsResponse> polls);

    void showEmptyQuestions();

    void showError(String error);

    void showProgress(boolean show);

    void showPosts(List<PostsResponse> posts);
}

