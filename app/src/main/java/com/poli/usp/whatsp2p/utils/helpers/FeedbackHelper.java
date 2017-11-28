package com.poli.usp.whatsp2p.utils.helpers;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import poli.com.mobile2you.whatsp2p.R;

/**
 * This class helps to show placeholders of error, emptiness and loading.
 * Created by mobile2you on 21/09/16.
 */

public class FeedbackHelper {

    private final int RESOURCE_NOT_DEFINED = 0;

    private Context mContext;

    private View.OnClickListener mOnTryAgainClicked;
    private View mLoadingView, mPlaceholderView;
    private ViewGroup mContainer;

    private LayoutInflater mInflater;

    //Resources
    @ColorRes
    private int mProgressBarColor = RESOURCE_NOT_DEFINED, mIconsColor = RESOURCE_NOT_DEFINED;
    @DrawableRes
    private int mErrorDrawable = RESOURCE_NOT_DEFINED, mEmptyDrawable = RESOURCE_NOT_DEFINED;
    private String mErrorMsg, mEmptyMsg;

    public FeedbackHelper(Context context, ViewGroup containerToShowFeedback, View.OnClickListener onTryAgainClicked) {
        mOnTryAgainClicked = onTryAgainClicked;
        mContext = context;
        mContainer = containerToShowFeedback;
        mInflater = LayoutInflater.from(mContext);
    }

    private FeedbackHelper setProgressBarColor(@ColorRes int progressBarColor) {
        mProgressBarColor = progressBarColor;
        return this;
    }

    public FeedbackHelper setIconsColor(@ColorRes int iconsColor) {
        mIconsColor = iconsColor;
        return this;
    }

    public FeedbackHelper setErrorMsg(String errorMsg) {
        mErrorMsg = errorMsg;
        return this;
    }

    public FeedbackHelper setEmptyMsg(String emptyMsg) {
        mEmptyMsg = emptyMsg;
        return this;
    }

    public FeedbackHelper setErrorDrawable(@DrawableRes int errorDrawable) {
        mErrorDrawable = errorDrawable;
        return this;
    }

    public FeedbackHelper setEmptyDrawable(@DrawableRes int emptyDrawable) {
        mEmptyDrawable = emptyDrawable;
        return this;
    }

    public void startLoading() {
        hideViews();
        addLoading();
        removeErrorPlaceHolder();
    }

    public void showEmptyPlaceHolder() {
        hideViews();

        addErrorPlaceHolder();
        bind(mEmptyMsg == null ? mContext.getString(R.string.placeholder_empty_label) : mEmptyMsg,
                mEmptyDrawable == RESOURCE_NOT_DEFINED ? R.drawable.ic_default_empty : mEmptyDrawable, null);
    }

    public void showErrorPlaceHolder() {
        hideViews();

        addErrorPlaceHolder();
        bind(mErrorMsg == null ? mContext.getString(R.string.placeholder_error_label) : mErrorMsg,
                mErrorDrawable == RESOURCE_NOT_DEFINED ? R.drawable.ic_default_request_error : mErrorDrawable, mOnTryAgainClicked);
    }

    public void dismissFeedback() {
        showViews();
        removeErrorPlaceHolder();
        removeLoading();
    }

    private void addLoading() {
        mLoadingView = mInflater.inflate(R.layout.placeholder_default_loading, mContainer, false);
        ((ProgressBar) mLoadingView.findViewById(R.id.default_progressbar)).getIndeterminateDrawable().mutate().setColorFilter(ContextCompat.getColor(mContext, mProgressBarColor == RESOURCE_NOT_DEFINED ? R.color.colorPrimary : mProgressBarColor), PorterDuff.Mode.SRC_ATOP);
        mContainer.addView(mLoadingView);
    }

    private void addErrorPlaceHolder() {
        mPlaceholderView = mInflater.inflate(R.layout.placeholder_default_general, mContainer, false);
        mContainer.addView(mPlaceholderView);
    }

    private void removeLoading() {
        mContainer.removeView(mLoadingView);
    }

    private void removeErrorPlaceHolder() {
        mContainer.removeView(mPlaceholderView);
    }

    private void hideViews() {
        for (int i = 0; i < mContainer.getChildCount(); i++) {
            mContainer.getChildAt(i).setVisibility(View.GONE);
        }
    }

    private void showViews() {
        for (int i = 0; i < mContainer.getChildCount(); i++) {
            mContainer.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

    private void bind(String msg, int drawable, View.OnClickListener refreshClickListener) {
        //Msg
        ((TextView) mPlaceholderView.findViewById(R.id.item_default_label)).setText(msg);

        //Drawable
        Drawable tintedDrawable = ContextCompat.getDrawable(mContext, drawable).mutate();
        tintedDrawable.setColorFilter(ContextCompat.getColor(mContext, mIconsColor == RESOURCE_NOT_DEFINED ? R.color.colorPrimary : mIconsColor), PorterDuff.Mode.SRC_ATOP);
        ((ImageView) mPlaceholderView.findViewById(R.id.item_default_icon)).setImageDrawable(tintedDrawable);

        //Button
        if (refreshClickListener != null) {
            (mPlaceholderView.findViewById(R.id.item_default_button)).setVisibility(View.VISIBLE);
            (mPlaceholderView.findViewById(R.id.item_default_button)).setOnClickListener(refreshClickListener);
        }
    }
}
