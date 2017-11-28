package com.poli.usp.whatsp2p.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.poli.usp.whatsp2p.Constants;
import com.poli.usp.whatsp2p.ui.base.BaseActivity;

import poli.com.mobile2you.whatsp2p.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends BaseActivity {
    private String mTitle;
    private String mUrl;

    @BindView(R.id.webview_webview) WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        getExtras();
        loadWebView();
        setActionBar(mTitle, true);
    }

    public static Intent createIntent(Context context, String title, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.EXTRA_ACTIVITY_TITLE, title);
        intent.putExtra(Constants.EXTRA_URL, url);
        return intent;
    }

    private void getExtras() {
        Intent intent = getIntent();
        mTitle = intent.getStringExtra(Constants.EXTRA_ACTIVITY_TITLE);
        mUrl = intent.getStringExtra(Constants.EXTRA_URL);
    }

    private void loadWebView() {
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(mUrl);

    }
}
