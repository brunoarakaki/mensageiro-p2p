package com.poli.usp.whatsp2p.utils.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import poli.com.mobile2you.whatsp2p.R;

/**
 * Created by mobile2you on 11/08/16.
 */
public class BaseDialogHelper {

    /**
     * Shows progress dialog with standard message.
     */
    public static ProgressDialog createProgressDialog(Context context) {
        return createProgressDialog(context, context.getString(R.string.progress_dialog_standard_msg));
    }

    public static ProgressDialog createProgressDialog(Context context, String msg) {
        ProgressDialog dialog = buildProgressDialog(context);
        dialog.setMessage(msg);
        return dialog;
    }

    private static ProgressDialog buildProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static AlertDialog createListDialog(Context context, String title, String negativeText, CharSequence[] options, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title).setItems(options, listener);

        builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        return builder.create();
    }

    public static AlertDialog createListDialog(Context context, String title, CharSequence[] options, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setItems(options, listener);
        return builder.create();
    }


    public static AlertDialog createDisclaimerDialog(Context context, String title, String msg, String positiveText, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context).setMessage(msg).setTitle(title).setPositiveButton(positiveText, listener).setCancelable(false);
        return alertDialogBuilder.create();
    }

    public static AlertDialog createAlertDialog(Context context, String msg, String positiveText, String negativeText, DialogInterface.OnClickListener postiveButtonListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context).setMessage(msg).setPositiveButton(positiveText, postiveButtonListener)
                .setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        return alertDialogBuilder.create();
    }

    public static AlertDialog createAlertDialog(Context context, String msg, String positiveText, String negativeText, DialogInterface.OnClickListener postiveButtonListener, DialogInterface.OnClickListener negativeButtonListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context).setMessage(msg).setPositiveButton(positiveText, postiveButtonListener)
                .setNegativeButton(negativeText, negativeButtonListener);
        return alertDialogBuilder.create();
    }
}
