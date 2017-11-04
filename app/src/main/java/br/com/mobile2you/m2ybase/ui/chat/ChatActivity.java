package br.com.mobile2you.m2ybase.ui.chat;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.poli.tcc.dht.DHT;

import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSignature;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.List;
import java.util.concurrent.ExecutionException;

import br.com.mobile2you.m2ybase.Constants;
import br.com.mobile2you.m2ybase.R;
import br.com.mobile2you.m2ybase.data.local.ChatClient;
import br.com.mobile2you.m2ybase.data.local.Contact;
import br.com.mobile2you.m2ybase.data.local.ContactDatabaseHelper;
import br.com.mobile2you.m2ybase.data.local.MessageDatabaseHelper;
import br.com.mobile2you.m2ybase.data.local.PGPManagerSingleton;
import br.com.mobile2you.m2ybase.data.local.PGPUtils;
import br.com.mobile2you.m2ybase.data.local.PreferencesHelper;
import br.com.mobile2you.m2ybase.data.local.ProgressDialogHelper;
import br.com.mobile2you.m2ybase.data.local.Utils;
import br.com.mobile2you.m2ybase.data.remote.models.MessageResponse;
import br.com.mobile2you.m2ybase.data.remote.models.SignatureResponse;
import br.com.mobile2you.m2ybase.ui.base.BaseActivity;
import br.com.mobile2you.m2ybase.ui.common.TrustDialogFragment;
import br.com.mobile2you.m2ybase.ui.main.MainActivity;
import br.com.mobile2you.m2ybase.utils.exceptions.ContactNotFoundException;
import br.com.mobile2you.m2ybase.utils.helpers.DialogHelper;
import br.com.mobile2you.m2ybase.utils.exceptions.CouldNotEncryptException;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends BaseActivity implements ChatMvpView{

    private ChatPresenter mPresenter;
    private ChatAdapter mAdapter;
    private ChatClient chatClient;
    private Contact me;
    private Contact friend;
    private boolean isDirectConnection;
    private messagesUpdateBroadcastReceiver messageBroadcast;
    private ProgressDialogHelper progressDialog;
    private SignatureUpdateBroadcast signatureUpdateBroadcast;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.send_message_button)
    Button mSendButton;
    @BindView(R.id.message_edit_text)
    TextInputEditText mMessageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        mPresenter = new ChatPresenter();
        mPresenter.attachView(this);

        Bundle extras = getIntent().getExtras();
        progressDialog = new ProgressDialogHelper(this);

        me = (Contact) extras.getSerializable(Constants.EXTRA_MYSELF);
        friend = (Contact) extras.getSerializable(Constants.EXTRA_CONTACT);
        isDirectConnection = extras.getBoolean(Constants.EXTRA_DIRECT_CONNECTION);

        signatureUpdateBroadcast = new SignatureUpdateBroadcast();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(signatureUpdateBroadcast, new IntentFilter(Constants.FILTER_SIGNATURE_UPDATE));

        chatClient = new ChatClient();

        new Thread(new Runnable() {
            @Override
            public void run() {
                progressDialog.show("Conectando...");
                friend.updateChatPublicKey(getApplicationContext());
                if (!connectToClient(friend.getIp(), friend.getPort())) {
                    if(isDirectConnection){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showConnectionFailDialog();
                            }
                        });
                    } else {
                        friend.setIp(null);
                        friend.setPort(0);
                        try {
                            lookupAndConnect();
                        } catch (IOException | InterruptedException | ExecutionException  e) {
                             e.printStackTrace();
                        }
                    }
                }
                progressDialog.hide();
                updateActionBar();
            }
        }).start();

        Log.d("DHT", "Contact IP: " + friend.getIp() + ":" + friend.getPort());
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String text = mMessageEditText.getText().toString();
                    PGPPublicKeyRing chatPublicKeyRing = PGPUtils.readPublicKeyRingFromStream(new ByteArrayInputStream(friend.getChatPublicKeyRingEncoded()));
                    PGPPublicKey chatPublicKey = PGPUtils.getEncryptionKeyFromKeyRing(chatPublicKeyRing);
                    byte[] encryptedText = PGPManagerSingleton.getInstance().encrypt(text.getBytes(), chatPublicKey);
                    if (encryptedText == null) {
                        throw new CouldNotEncryptException();
                    }
                    if (!text.isEmpty()) {
                        final MessageResponse message = new MessageResponse(me, friend);
                        message.setEncodedText(encryptedText);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (friend.getIp() == null) {
                                        progressDialog.show("Enviando mensagem...");
                                        lookupAndConnect();
                                        updateActionBar();
                                        progressDialog.hide();
                                    }
                                    if (chatClient.isConnected() && chatClient.sendMessage(message)) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                message.setPlainText(text);
                                                mPresenter.newMessage(message);
                                                mMessageEditText.setText("");
                                            }
                                        });
                                    } else {
                                        showToast("Não foi possível enviar a mensagem");
                                    }
                                } catch (IOException | InterruptedException | ExecutionException e) {
                                    progressDialog.hide();
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } else {
                        hideSoftKeyboard();
                    }
                } catch (CouldNotEncryptException e) {
                    showToast("Não foi possível encriptar a mensagem");
                } catch (IOException | NoSuchProviderException | PGPException e) {
                    showToast(e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        setRecyclerView();
        mPresenter.loadMessages(me.getId(), friend.getId());

        IntentFilter intentFilterMessage = new IntentFilter(Constants.FILTER_CHAT_RECEIVER);
        messageBroadcast = new messagesUpdateBroadcastReceiver();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(messageBroadcast, intentFilterMessage );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sign_key:
                //signKey();
                FragmentManager fm = getSupportFragmentManager();
                TrustDialogFragment trustDialogFragment = TrustDialogFragment.newInstance(this.getApplicationContext(), friend);
                trustDialogFragment.show(fm, "dialog_contact_trust");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateActionBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String status;
                if (chatClient != null && chatClient.isConnected()) {
                    status = "ONLINE";
                } else {
                    status = "OFFLINE";
                }
                setActionBar(friend.getId() + " - " + status, true);
            }
        });
    }

    private void setRecyclerView(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ChatAdapter(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    public void showConnectionFailDialog(){
        AlertDialog alertDialog = DialogHelper.createDisclaimerDialog(this, getString(R.string.connection_failed_dialog_tittle),
                getString(R.string.connection_failed_dialog_msg),
                "Voltar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void addMessage(MessageResponse message) {
        List<MessageResponse> messages = mAdapter.getMessages();
        messages.add(message);
        showMessages(messages);
    }

    @Override
    public long saveMessage(MessageResponse message){
        MessageDatabaseHelper dbHelper = new MessageDatabaseHelper(this);
        return dbHelper.add(message);
    }

    @Override
    public void loadContactMessages(String sender_id, String user_id) {
        MessageDatabaseHelper dbHelper = new MessageDatabaseHelper(this);
        List<MessageResponse> messages = dbHelper.getMessagesFromContact(user_id, sender_id);
        showMessages(messages);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mPresenter.detachView();
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(messageBroadcast);
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(signatureUpdateBroadcast);
            chatClient.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showMessages(List<MessageResponse> messages) {
        mAdapter.setMessages(messages);
        mRecyclerView.scrollToPosition(messages.size() - 1);
    }

    @Override
    public void showEmptyMessages() {
        showToast("Não existem mensagens no momento");
    }

    @Override
    public void showError(String error) {
        showToast(error);
    }

    @Override
    public void showProgress(boolean show) {
        showProgressDialog(show);
    }

    public void reloadMessages() {
        loadContactMessages(me.getId(), friend.getId());
    }

    private class messagesUpdateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            reloadMessages();
        }
    }

    public InetSocketAddress lookupUser() {
        try {
            PublicKey signPublicKey = Utils.getPublicKeyFromEncoded("DSA", friend.getSignPublicKeyEncoded());
            return (InetSocketAddress) DHT.getProtected("chatAddress", signPublicKey);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean connectToClient(String ip, int port) {
        if (ip != null && port != 0) {
            return chatClient.connect(ip, port);
        }
        return false;
    }

    private void lookupAndConnect() throws IOException, ExecutionException, InterruptedException {
        InetSocketAddress address = lookupUser();
        if (address != null) {
            String ip = address.getHostName();
            int port = address.getPort();
            friend.setIp(ip);
            friend.setPort(port);
            friend.save(this);
            Log.d("Chat", "Found that user address is " + ip + ":" + port);
            connectToClient(ip, port);
        }
    }

    private void updateSignature(boolean trust) {
        try {
            String userPassword = PreferencesHelper.getInstance().getUserPassword();
            PGPPublicKeyRing chatPublicKeyRing = PGPUtils.readPublicKeyRingFromStream(new ByteArrayInputStream(friend.getChatPublicKeyRingEncoded()));
            PGPSignature signature = PGPManagerSingleton.getInstance().generateSignatureForPublicKey(me.getId(), chatPublicKeyRing.getPublicKey(), userPassword.toCharArray());
            final SignatureResponse message = new SignatureResponse(me.getId(), signature, trust);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (chatClient.isConnected() && chatClient.sendMessage(message)) {
                        showToast("Assinatura atualizada");
                        try {
                            Thread.sleep(3000);
                            friend.updateChatPublicKey(getApplicationContext());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }).start();
        } catch (PGPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SignatureUpdateBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Boolean trust = intent.getBooleanExtra("trust", false);
            updateSignature(trust);
        }
    }


}
