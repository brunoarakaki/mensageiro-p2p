package com.poli.usp.whatsp2p.ui.base;

import android.support.v4.app.Fragment;

import com.poli.usp.whatsp2p.utils.ViewUtil;


/**
 * Created by mobile2you on 11/08/16.
 */
public class BaseFragment extends Fragment {

    //TOAST METHODS
    public void showToast(String string) {
        ((BaseActivity) getActivity()).showToast(string);
    }

    //PROGRESS DIALOG METHODS
    public void showProgressdialog(boolean show) {
        ((BaseActivity)getActivity()).showProgressDialog(show);
    }

    //KEYBOARD METHODS
    public void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            ViewUtil.hideKeyboard(getActivity());
        }
    }
}
