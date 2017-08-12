package br.com.mobile2you.m2ybase.ui.chat;

import java.util.List;

import br.com.mobile2you.m2ybase.data.remote.models.MessageResponse;
import br.com.mobile2you.m2ybase.ui.base.MvpView;

/**
 * Created by Bruno on 11-Aug-17.
 */

public interface ChatMvpView extends MvpView {
    void showMessages(List<MessageResponse> messages);

    void showEmptyMessages();

    void showError(String error);

    void showProgress(boolean show);

}
