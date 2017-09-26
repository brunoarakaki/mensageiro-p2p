package br.com.mobile2you.m2ybase.ui.main;

import android.content.Context;

import java.util.List;
import java.util.Random;

import br.com.mobile2you.m2ybase.data.local.Contact;
import br.com.mobile2you.m2ybase.data.local.ContactDatabaseHelper;
import br.com.mobile2you.m2ybase.data.local.MessageDatabaseHelper;
import br.com.mobile2you.m2ybase.data.local.PreferencesHelper;
import br.com.mobile2you.m2ybase.data.remote.ApiaryDataManager;
import br.com.mobile2you.m2ybase.data.remote.JsonPlaceholderDataManager;
import br.com.mobile2you.m2ybase.data.remote.models.PollsResponse;
import br.com.mobile2you.m2ybase.data.remote.models.PostsResponse;
import br.com.mobile2you.m2ybase.ui.base.BasePresenter;
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

//    public void loadQuestions() {
//        mMainMvpView.showProgress(true);
//        mSubscription = getPollsOberservable().subscribe(new Subscriber<List<PollsResponse>>() {
//            @Override
//            public void onCompleted() {
//                mMainMvpView.showProgress(false);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                mMainMvpView.showProgress(false);
//                mMainMvpView.showError(e.getMessage());
//            }
//
//            @Override
//            public void onNext(List<PollsResponse> pollsResponses) {
//                mCachedPolls = pollsResponses;
//                if (pollsResponses.isEmpty()) {
//                    mMainMvpView.showEmptyQuestions();
//                } else {
//                    mMainMvpView.showQuestions(pollsResponses);
//                }
//            }
//        });
//    }

    public void loadContacts(Context context){
        ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(context);
        mCachedContacts = dbHelper.getContacts();
        mMainMvpView.showContacts(mCachedContacts);
    }

    public void addContact(Context context, String name, String ip){
        ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(context);
        Contact contact = new Contact(name);
        contact.setIp(ip);

        Random generator = new Random();
        String randomId = String.valueOf (generator.nextInt(96) + 32);
        contact.setId(randomId);

        dbHelper.add(contact);
        mCachedContacts.add(contact);
        mMainMvpView.showContacts(mCachedContacts);

    }

    public void deleteContact(Context context, Contact contact){
        ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(context);
        dbHelper.delete(contact.getId());
        mCachedContacts.remove(contact);
        mMainMvpView.showContacts(mCachedContacts);
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
