package com.poli.usp.whatsp2p.ui.main;

import android.content.Context;
import java.util.List;
import com.poli.usp.whatsp2p.data.local.Contact;
import com.poli.usp.whatsp2p.data.local.ContactDatabaseHelper;
import com.poli.usp.whatsp2p.data.local.MessageDatabaseHelper;
import com.poli.usp.whatsp2p.data.local.PreferencesHelper;
import com.poli.usp.whatsp2p.data.remote.ApiaryDataManager;
import com.poli.usp.whatsp2p.data.remote.JsonPlaceholderDataManager;
import com.poli.usp.whatsp2p.data.remote.models.PollsResponse;
import com.poli.usp.whatsp2p.data.remote.models.PostsResponse;
import com.poli.usp.whatsp2p.ui.base.BasePresenter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mobile2you on 28/11/16.
 */

public class MainPresenter extends BasePresenter<MainMvpView> {

    private MainMvpView mMainMvpView;
    private Subscription mSubscription;
    private ApiaryDataManager mApiaryDataManager;
    private List<Contact> mCachedContacts;
    private JsonPlaceholderDataManager mPlaceholderDataManager;

    public MainPresenter() {
        mApiaryDataManager = ApiaryDataManager.getInstance();
        mPlaceholderDataManager = JsonPlaceholderDataManager.getInstance();
    }

    @Override
    public void attachView(MainMvpView mvpView) {
        super.attachView(mvpView);
        mMainMvpView = mvpView;
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadPosts() {
        mMainMvpView.showProgress(true);
        mSubscription = mPlaceholderDataManager.getPolls()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<PostsResponse>>() {
                    @Override
                    public void onCompleted() {
                        mMainMvpView.showProgress(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMainMvpView.showProgress(false);
                        mMainMvpView.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(List<PostsResponse> postsResponses) {
//                        mMainMvpView.showPosts(postsResponses);
                    }
                });
    }

    public void loadContacts(Context context){
        ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(context);
        mCachedContacts = dbHelper.getContacts();
        mMainMvpView.showContacts(mCachedContacts);
    }

    public void addContact(Context context, Contact contact) {
        ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(context);
        dbHelper.add(contact);
        mCachedContacts.add(contact);
        mMainMvpView.showContacts(mCachedContacts);
    }

    public void addContact(Context context, String username, String ip, int port, byte[] signPublicKeyEncoded, byte[] chatPublicKeyRingEncoded){
        Contact contact = new Contact(username);
        contact.setIp(ip);
        contact.setPort(port);
        contact.setSignPublicKeyEncoded(signPublicKeyEncoded);
        contact.setChatPublicKeyRingEncoded(chatPublicKeyRingEncoded);
        addContact(context, contact);

    }

    public void deleteContact(Context context, Contact contact){
        ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(context);
        dbHelper.delete(contact.getId());
        mCachedContacts.remove(contact);
        mMainMvpView.showContacts(mCachedContacts);
    }

    public void updateContact(Context context, Contact contact){
        ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(context);
        dbHelper.update(contact);
        loadContacts(context);
    }

    public void deleteConversation(Context context, Contact contact){
        MessageDatabaseHelper dbHelper = new MessageDatabaseHelper(context);
        String user_id = PreferencesHelper.getInstance().getUserId();
        dbHelper.deleteConversation(user_id, contact.getId());
    }

    private Observable<List<PollsResponse>> getPollsOberservable() {
//        if (mCachedPolls != null) {
//            return Observable.just(mCachedPolls);
//        } else {
            return mApiaryDataManager.deletePolls()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io());
//        }
    }
}
