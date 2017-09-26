package br.com.mobile2you.m2ybase.ui.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import br.com.mobile2you.m2ybase.Constants;
import br.com.mobile2you.m2ybase.data.remote.models.MessageResponse;
import br.com.mobile2you.m2ybase.data.remote.services.DHTService;
import br.com.mobile2you.m2ybase.ui.base.BasePresenter;
import rx.Subscription;

/**
 * Created by Bruno on 11-Aug-17.
 */

public class ChatPresenter extends BasePresenter<ChatMvpView> {

    private ChatMvpView mChatMvpView;
    private List<MessageResponse> mCachedMessages;
    private Subscription mSubscription;

    public ChatPresenter(){

    }


    public void loadMessages(String user_id, String sender_id){
//        mChatMvpView.showMessages(messages);
        mChatMvpView.loadContactMessages(user_id, sender_id);
    }

    public void sendMessage(MessageResponse messageResponse){
        mChatMvpView.addMessage(messageResponse);
        mChatMvpView.saveMessage(messageResponse);
//        long id = mChatMvpView.saveMessage(messageResponse);
    }

    @Override
    public void attachView(ChatMvpView mvpView) {
        super.attachView(mvpView);
        mChatMvpView = mvpView;
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }
}
