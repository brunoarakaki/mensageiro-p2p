package br.com.mobile2you.m2ybase.utils.helpers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Helps handling the permissions in fragments or activities.
 * Created by mobile2you on 27/10/16.
 */

public class PermissionDispatcherHelper {
    private String[] mPermissions;
    private Activity mActivity;
    private Fragment mFragment;
    private final int mRequestCode;
    private OnPermissionResult mOnPermissionResult;
    private boolean mIsFromFragment = false;
    private boolean mIsPermissionAlreadyGranted = false;

    public PermissionDispatcherHelper(Activity activity, int requestCode, String[] permissions, OnPermissionResult onPermissionResult) {
        mPermissions = permissions;
        mActivity = activity;
        mRequestCode = requestCode;
        mOnPermissionResult = onPermissionResult;
    }

    public PermissionDispatcherHelper(Fragment fragment, int requestCode, String[] permissions, OnPermissionResult onPermissionResult) {
        mPermissions = permissions;
        mFragment = fragment;
        mActivity = fragment.getActivity();
        mIsFromFragment = true;
        mRequestCode = requestCode;
        mOnPermissionResult = onPermissionResult;
    }

    public void dispatchPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (areAllPermissionsGranted()) {
                mIsPermissionAlreadyGranted = true;
                mOnPermissionResult.onPermissionsGranted();
            } else {
                requestPermissions(mPermissions, mRequestCode);
            }
        } else {
            mIsPermissionAlreadyGranted = true;
            mOnPermissionResult.onPermissionsGranted();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissions(String[] permissions, int requestCode) {
        if (mIsFromFragment) {
            mFragment.requestPermissions(permissions, requestCode);
        } else {
            mActivity.requestPermissions(permissions, requestCode);
        }
    }

    private int checkPermissions(String permission) {
        return ContextCompat.checkSelfPermission(mActivity, permission);
    }

    private boolean shouldShowRequestPermissionRationale(String permission) {
        boolean returnable;
        if (mIsFromFragment) {
            returnable = mFragment.shouldShowRequestPermissionRationale(permission);
        } else {
            returnable = ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission);
        }
        return returnable;
    }

    private boolean areAllPermissionsGranted() {
        boolean returnable = true;
        for (String permission : mPermissions) {
            if (checkPermissions(permission) != PackageManager.PERMISSION_GRANTED) {
                returnable = false;
            }
        }
        return returnable;
    }

    private boolean allPermissionsRequestedGranted(String[] permissions, int[] grantResults) {
        boolean returnable = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                returnable = false;
            }
        }
        return returnable;
    }


    private boolean checkPermissionsRationale() {
        boolean returnable = true;
        for (String permission : mPermissions) {
            if (!shouldShowRequestPermissionRationale(permission)) {
                returnable = false;
            }
        }
        return returnable;
    }

    public boolean isPermissionAlreadyGranted() {
        return mIsPermissionAlreadyGranted;
    }

    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        if (permsRequestCode == mRequestCode) {
            if (grantResults.length > 0 && allPermissionsRequestedGranted(permissions, grantResults)) {
                mIsPermissionAlreadyGranted = true;
                mOnPermissionResult.onPermissionsGranted();
            } else {
                if (!checkPermissionsRationale()) {
                    mOnPermissionResult.onPermissionsDeniedWithNeverAskAgainOption();
                } else {
                    mOnPermissionResult.onPermissionsDenied();
                }
            }
        }
    }


    public interface OnPermissionResult {
        void onPermissionsGranted();

        void onPermissionsDenied();

        void onPermissionsDeniedWithNeverAskAgainOption();
    }
}