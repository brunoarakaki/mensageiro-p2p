package com.poli.usp.whatsp2p.data.local;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by mayerlevy on 11/2/17.
 */

public class ErrorManager {

    private static Activity mainActivity;

    public static void setActivity(Activity activity) {
        mainActivity = activity;
    }

    public static void handleError(final Exception e) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mainActivity.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
            }
        });
    }

}
