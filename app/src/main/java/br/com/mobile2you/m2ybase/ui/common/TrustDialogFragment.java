package br.com.mobile2you.m2ybase.ui.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyRing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import br.com.mobile2you.m2ybase.Constants;
import br.com.mobile2you.m2ybase.R;
import br.com.mobile2you.m2ybase.data.local.Contact;
import br.com.mobile2you.m2ybase.data.local.PGPManager;
import br.com.mobile2you.m2ybase.data.local.PGPManagerSingleton;
import br.com.mobile2you.m2ybase.data.local.PGPUtils;

/**
 * Created by mayerlevy on 11/1/17.
 */

public class TrustDialogFragment extends DialogFragment {

    private Context mContext;
    private Contact mUser;
    private TextView mTitle;
    private ListView mList;
    private CheckBox mCheck;

    public TrustDialogFragment() {

    }

    public static TrustDialogFragment newInstance(Context context, Contact user) {
        TrustDialogFragment frag = new TrustDialogFragment();
        frag.setContext(context);
        frag.setUser(user);
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_contact_trust, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        try {
            String username = mUser.getId();
            byte[] pubKeyEncoded = mUser.getChatPublicKeyRingEncoded();
            PGPPublicKeyRing pubKeyRing = PGPUtils.readPublicKeyRingFromStream(new ByteArrayInputStream(pubKeyEncoded));
            PGPPublicKey pubKey = PGPUtils.getEncryptionKeyFromKeyRing(pubKeyRing);
            ArrayList<String> trusteeList = PGPUtils.getSignaturesUserList(pubKey);
            mTitle = (TextView) view.findViewById(R.id.contact_trust_title);
            mTitle.setText(getString(R.string.contact_trust_title, username));
            mList = (ListView) view.findViewById(R.id.contact_trust_list);
            mList.setAdapter(new ArrayAdapter<>(mContext, R.layout.item_text, trusteeList));
            mCheck = (CheckBox) view.findViewById(R.id.contact_trust_check);
            mCheck.setText(getString(R.string.contact_trust_check, username));
            mCheck.setChecked(PGPManagerSingleton.getInstance().hasSignedPublicKey(pubKey));
            mCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    Intent intent = new Intent(Constants.FILTER_SIGNATURE_UPDATE);
                    intent.putExtra("trust", checked);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
            });
            PGPUtils.printSignaturesFromKey(pubKey);
        } catch (IOException | PGPException e) {
            e.printStackTrace();
        }
    }

    private void setContext(Context context) {
        this.mContext = context;
    }

    private void setUser(Contact user) {
        this.mUser = user;
    }
}