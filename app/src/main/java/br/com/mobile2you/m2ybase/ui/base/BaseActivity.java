package br.com.mobile2you.m2ybase.ui.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import br.com.mobile2you.m2ybase.R;
import br.com.mobile2you.m2ybase.utils.ViewUtil;
import br.com.mobile2you.m2ybase.utils.helpers.DialogHelper;

/**
 * Created by mobile2you on 18/08/16.
 */
public class BaseActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FIX ORIENTATION TO PORTRAIT
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    //TOOLBAR METHODS
    public void setActionBar(String title, boolean displayHomeAsUpEnabled) {
        setActionBar(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled);
    }

    //TOAST METHODS
    public void showToast(final String string) {
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //ACTION BAR METHODS
    public void setActionBar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(title);
    }

    //PROGRESS DIALOG METHODS
    private void showProgressDialog() {
        mProgressDialog = (mProgressDialog == null) ? DialogHelper.createProgressDialog(this) : mProgressDialog;
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public void showProgressDialog(boolean show) {
        if(show) {
            showProgressDialog();
        } else {
            dismissProgressDialog();
        }
    }

    //KEYBOARD METHODS
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            ViewUtil.hideKeyboard(this);
        }
    }

    //MENU METHODS
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
