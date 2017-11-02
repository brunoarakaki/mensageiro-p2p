package br.com.mobile2you.m2ybase.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.poli.tcc.dht.DHT;
import com.poli.tcc.dht.DHTException;
import com.poli.tcc.dht.DHTNode;

import net.tomp2p.dht.FuturePut;
import net.tomp2p.peers.Number160;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.List;

import br.com.mobile2you.m2ybase.Constants;
import br.com.mobile2you.m2ybase.R;
import br.com.mobile2you.m2ybase.data.local.ChatServer;
import br.com.mobile2you.m2ybase.data.local.Contact;
import br.com.mobile2you.m2ybase.data.local.ContactDatabaseHelper;
import br.com.mobile2you.m2ybase.data.local.NodeDiscovery;
import br.com.mobile2you.m2ybase.data.local.PGPManager;
import br.com.mobile2you.m2ybase.data.local.PGPManagerSingleton;
import br.com.mobile2you.m2ybase.data.local.PGPUtils;
import br.com.mobile2you.m2ybase.data.local.PreferencesHelper;
import br.com.mobile2you.m2ybase.data.local.ProgressDialogHelper;
import br.com.mobile2you.m2ybase.data.local.Utils;
import br.com.mobile2you.m2ybase.data.remote.models.MessageResponse;
import br.com.mobile2you.m2ybase.ui.base.BaseActivity;
import br.com.mobile2you.m2ybase.ui.chat.ChatActivity;
import br.com.mobile2you.m2ybase.utils.exceptions.*;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainMvpView {

    private MainPresenter mMainPresenter;
    private MainAdapter mAdapter;
    private String mUserId;
    private Contact me;
    private Thread chatThread;
    private NodeDiscovery nodeDiscovery;
    private NewMessageBroadcast newMessageBroadcast;
    private DHTConnectionBroadcast dhtConnectionBroadcast;
    private ProgressDialogHelper progressDialog;
    private int mChatPort;
    private int mDHTPort;
    private Boolean mInitialized = false;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

//    public String trackerAddress = "13.59.232.73";
    public String trackerAddress = "192.168.1.105";
    public int trackerPort = 4001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mMainPresenter = new MainPresenter();
        mMainPresenter.attachView(this);

        newMessageBroadcast = new NewMessageBroadcast();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(newMessageBroadcast, new IntentFilter(Constants.FILTER_CHAT_RECEIVER));

        dhtConnectionBroadcast = new DHTConnectionBroadcast();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(dhtConnectionBroadcast, new IntentFilter(Constants.FILTER_DHT_CONNECTION));

        mChatPort = Utils.getAvailablePort();
        mDHTPort = Utils.getAvailablePort();

        progressDialog = new ProgressDialogHelper(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MainAdapter(new MainAdapter.OnClicked() {
            @Override
            public void onContactClicked(final Contact contact) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            startChat(contact, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public boolean onContactLongClicked(Contact contact) {
                showContactSettingsDialog(contact);
                return true;
            }
        }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMainPresenter.loadContacts(v.getContext());
                }
        });
        mRecyclerView.setAdapter(mAdapter);
        setActionBar("Mensageiro P2P");

        mUserId = PreferencesHelper.getInstance().getUserId();
        if (mUserId.equals("")) {
            showUserNameDialog();
        } else if (!mInitialized){
            initialize();
        }

    }

    public void initialize() {
        progressDialog.show("Conectando à rede...", 20000, new Runnable() {
            @Override
            public void run() {
                showMessage("Não foi possível conectar na rede DHT!");
            }
        });
        me = new Contact(mUserId);
        nodeDiscovery = new NodeDiscovery(getApplicationContext(), mDHTPort);
        buildPGPManager();
        connectToDHT();
        startChatServer(mChatPort);
        mMainPresenter.loadContacts(this);
        mInitialized = true;
    }

    public void startChat(Contact contact, boolean directConnection) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra(Constants.EXTRA_MYSELF, me);
        intent.putExtra(Constants.EXTRA_CONTACT, contact);
        intent.putExtra(Constants.EXTRA_DIRECT_CONNECTION, directConnection);
        startActivity(intent);
    }

    public void showMessage(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showNewContactDialog(){
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog_add_contact, null);
        // create alert dialog
        AlertDialog alertDialog = getNewContactDialogBuilder(dialogView).create();
        alertDialog.show();
    }

    public void showEditContactDialog(Contact contact){
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog_add_contact, null);
        // create alert dialog
        AlertDialog alertDialog = getEditContactDialogBuilder(dialogView, contact).create();
        alertDialog.show();
    }


    public void showContactSettingsDialog(final Contact contact){
        CharSequence options[] = new CharSequence[] {"Editar", "Apagar Mensagens", "Deletar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(contact.getName());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        showEditContactDialog(contact);
                        break;
                    case 1:
                        deleteConversation(contact);
                        break;
                    case 2:
                        deleteContact(contact);
                        break;
                }
            }
        });
        builder.show();
    }

    public void showUserNameDialog(){
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog_set_username, null);
        // create alert dialog
        AlertDialog alertDialog = getUserNameDialogBuilder(dialogView).create();
        alertDialog.show();
    }

    public void showEditUserNameDialog(){
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog_set_username, null);
        // create alert dialog
        AlertDialog alertDialog = editUserNameDialogBuilder(dialogView).create();
        alertDialog.show();
    }

    public void showDirectConnectionDialog(){
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog_direct_connection, null);
        // create alert dialog
        AlertDialog alertDialog = directConnectionDialogBuilder(dialogView).create();
        alertDialog.show();
    }

    public AlertDialog.Builder getNewContactDialogBuilder(View dialogView){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(dialogView);

        final EditText userInput = (EditText) dialogView
                .findViewById(R.id.edit_text_contact_name);

        // set dialog message
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    String username = userInput.getText().toString();
                    addContact(username);
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    dialog.cancel();
                }
            });
        return alertDialogBuilder;

    }

    public AlertDialog.Builder getEditContactDialogBuilder(View dialogView, final Contact contact){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(dialogView);

        final EditText userInput = (EditText) dialogView
                .findViewById(R.id.edit_text_contact_name);

        userInput.setText(contact.getId());

        // set dialog message
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    String username = userInput.getText().toString();
                    contact.setId(username);
                    editContact(contact);
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    dialog.cancel();
                }
            });
        return alertDialogBuilder;

    }

    public AlertDialog.Builder getUserNameDialogBuilder(View dialogView){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(dialogView);

        final EditText userInput = (EditText) dialogView
                .findViewById(R.id.edit_text_user_name);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        String userName = userInput.getText().toString();
                        PreferencesHelper.getInstance().putUserId(userName);
                        mUserId = userName;
                        initialize();
                    }
                });
        return alertDialogBuilder;

    }

    public AlertDialog.Builder editUserNameDialogBuilder(View dialogView){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(dialogView);

        final EditText userInput = (EditText) dialogView
                .findViewById(R.id.edit_text_user_name);
        userInput.setText(mUserId);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        String userName = userInput.getText().toString();
                        PreferencesHelper.getInstance().putUserId(userName);
                        mUserId = userName;
                    }
                });
        return alertDialogBuilder;

    }

    public AlertDialog.Builder directConnectionDialogBuilder(View dialogView){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(dialogView);

//        final EditText editTextName = (EditText) dialogView
//                .findViewById(R.id.edit_text_user_name);
        final EditText editTextIP = (EditText) dialogView
                .findViewById(R.id.edit_text_contact_ip);
        final EditText editTextPort = (EditText) dialogView
                .findViewById(R.id.edit_text_contact_port);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Connectar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        String name = "Conexão Direta";//editTextName.getText().toString();
                        String ip = editTextIP.getText().toString();
                        int port = Integer.parseInt(editTextPort.getText().toString());
                        createDirectConnection(name, ip, port);
                    }
                });
        return alertDialogBuilder;

    }

    public void addContact(final String username){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    progressDialog.show("Buscando usuário...");
                    final PublicKey signPublicKey = (PublicKey) DHT.get(username);
                    if (signPublicKey == null) {
                        throw new ContactNotFoundException();
                    }
                    final InetSocketAddress address = (InetSocketAddress) DHT.getProtected("chatAddress", signPublicKey);
                    final byte[] chatPublicKeyRingEncoded = (byte[]) DHT.getProtected("chatPublicKey", signPublicKey);
                    if (address == null || chatPublicKeyRingEncoded == null) {
                        throw new ContactNotFoundException();
                    }
                    final String ip = address.getHostName();
                    final int port = address.getPort();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMainPresenter.addContact(getApplicationContext(), username, ip, port, signPublicKey.getEncoded(), chatPublicKeyRingEncoded);
                        }
                    });
                    progressDialog.hide();
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                    progressDialog.hide();
                } catch (ContactNotFoundException e) {
                    progressDialog.hide();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Usuário não encontrado!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    public void editContact(Contact contact){
        mMainPresenter.updateContact(this, contact);
    }

    public void deleteContact(Contact contact){
        mMainPresenter.deleteContact(this, contact);
    }

    public void deleteConversation(Contact contact){
        mMainPresenter.deleteConversation(this, contact);
    }

    public void createDirectConnection(String name, String ip, int port) {
        Contact contact = new Contact(name);
        contact.setIp(ip);
        contact.setPort(port);
        startChat(contact, true);
    }

    private void buildPGPManager() {
        try {
            PGPManagerSingleton.initialize(new PGPManager(this.getApplicationContext(), PreferencesHelper.getInstance().getUserId(), "12345".toCharArray()));
            PGPUtils.printSignaturesFromKey(PGPManagerSingleton.getInstance().getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectToDHT() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String mUserId = PreferencesHelper.getInstance().getUserId();
                    Number160 peerId = DHT.createPeerID(mUserId);
                    DHTNode thisNode = new DHTNode(peerId);
                    KeyPair signKeyPair = Utils.getKeyPairFromKeyStore(getApplicationContext(), "DSA");
//                    KeyPair chatKeyPair = Utils.getKeyPairFromKeyStore(getApplicationContext(), "RSA");
                    if (signKeyPair == null) {
                        throw new KeyPairNullException();
                    }
                    thisNode.setUsername(mUserId);
                    thisNode.setIp(Utils.getIPAddress(true));
                    thisNode.setPort(mChatPort);
                    thisNode.setSignKeyPair(signKeyPair);
                    me.setIp(Utils.getIPAddress(true));
                    me.setPort(mChatPort);
                    me.setSignPublicKeyEncoded(signKeyPair.getPublic().getEncoded());
                    me.setChatPublicKeyRingEncoded(PGPManagerSingleton.getInstance().getPublicKeyRing().getEncoded());
                    DHT.start(thisNode, mDHTPort);
                    if (DHT.connectTo(trackerAddress, trackerPort)) {
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(Constants.FILTER_DHT_CONNECTION));
                    } else {
                        nodeDiscovery.startLookup();
                    }
                } catch (DHTException.UsernameAlreadyTakenException e) {
                    showToast("Username already taken!");
                } catch (KeyPairNullException e) {
                    showToast("Couldn' generate KeyPair!");
                    e.printStackTrace();
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startChatServer(int port) {
        chatThread = new Thread(new ChatServer(port, getApplicationContext()));
        chatThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_person:
                showNewContactDialog();
                break;
            case R.id.action_edit_info:
                showEditUserNameDialog();
                break;
            case R.id.action_view_dht:
                break;
            case R.id.action_reconnect:
                ProgressDialogHelper pd = new ProgressDialogHelper(this);
                pd.show("Conectando à rede...", 10000, new Runnable() {
                    @Override
                    public void run() {
                        showMessage("Não foi possível conectar na rede DHT!");
                    }
                });
                connectToDHT();
                break;
            case R.id.action_direct_connection:
                showDirectConnectionDialog();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainPresenter.loadContacts(this);
    }

    protected void onPause() {
        super.onPause();
        if (nodeDiscovery != null) {
            nodeDiscovery.stopLookup();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.detachView();
        DHT.shutDown();
        nodeDiscovery.shutdown();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(newMessageBroadcast);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(dhtConnectionBroadcast);
    }

    @Override
    public void showEmptyContacts() {
        showToast("Você não possui nenhum amigo");
    }

    @Override
    public void showError(String error) {
        showToast(error);
    }

    @Override
    public void showProgress(boolean show) {
        showProgressDialog(show);
    }

    @Override
    public void showContacts(List<Contact> contacts) {
        mAdapter.setContacts(contacts);
    }

    private class NewMessageBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MessageResponse mes = (MessageResponse) intent.getSerializableExtra("message");
            Toast.makeText(MainActivity.this, "New message received from " + mes.getSender().getId(), Toast.LENGTH_SHORT).show();
            ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(context);
            if (dbHelper.search(mes.getSender().getId()).isEmpty()) {
                mMainPresenter.addContact(getApplicationContext(), mes.getSender());
            }
        }
    }

    private class DHTConnectionBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            progressDialog.hide();
            try {
                DHT.verify();
                Log.i("DHT", "[DHT] Broadcasting my chat address: " + me.getIp() + ":" + mChatPort);
                FuturePut fput = DHT.putProtected("chatAddress", new InetSocketAddress(me.getIp(), mChatPort));
                if (fput.isFailed()) {
                    Log.i("DHT", "[DHT] Chat address update failed: " + fput.failedReason());
                    showMessage("Chat address update failed!");
                }
                Log.i("DHT", "[DHT] Broadcasting my public chat key");
                fput = DHT.putProtected("chatPublicKey", PGPManagerSingleton.getInstance().getPublicKeyRing().getEncoded());
                if (fput.isFailed()) {
                    Log.i("DHT", "[DHT] Chat public key update failed: " + fput.failedReason());
                    showMessage("Chat public key update failed!");
                }
                showMessage("Connected to DHT network!");
            } catch (DHTException.UsernameAlreadyTakenException e) {
                e.printStackTrace();
                showMessage("Username already taken!");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
