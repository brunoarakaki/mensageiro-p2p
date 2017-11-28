package com.poli.usp.whatsp2p.ui.main;

import com.poli.usp.whatsp2p.data.local.Contact;

import java.util.List;

import com.poli.usp.whatsp2p.ui.base.MvpView;

/**
 * Created by mobile2you on 28/11/16.
 */

public interface MainMvpView extends MvpView {

    void showEmptyContacts();

    void showError(String error);

    void showProgress(boolean show);

    void showContacts(List<Contact> contacts);
}

