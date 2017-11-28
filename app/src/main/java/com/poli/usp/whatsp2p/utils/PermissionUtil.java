package com.poli.usp.whatsp2p.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Bruno on 23-Sep-17.
 */

public class PermissionUtil {

    public static boolean checkPermission(Activity activity, String permission, int callback_id){
        int permissionResult = ContextCompat.checkSelfPermission(activity, permission);
        if (permissionResult != PackageManager.PERMISSION_GRANTED) {
            // Asking permission
            ActivityCompat.requestPermissions(activity, new String[]{permission}, callback_id);
            return false;
        }
        return true;
    }

}
