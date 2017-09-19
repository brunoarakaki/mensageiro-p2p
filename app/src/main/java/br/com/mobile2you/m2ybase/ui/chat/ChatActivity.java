package br.com.mobile2you.m2ybase.ui.chat;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.mobile2you.m2ybase.Constants;
import br.com.mobile2you.m2ybase.R;
import br.com.mobile2you.m2ybase.data.local.Contact;
import br.com.mobile2you.m2ybase.data.local.DHT;
import br.com.mobile2you.m2ybase.data.local.MessageDatabaseHelper;
import br.com.mobile2you.m2ybase.data.local.ReceiverThread;
import br.com.mobile2you.m2ybase.data.remote.models.MessageResponse;
import br.com.mobile2you.m2ybase.ui.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends BaseActivity implements ChatMvpView{

    private ChatPresenter mPresenter;
    private ChatAdapter mAdapter;
    private int mContactId;
    private DHT dht;
    private ReceiverThread receiver;
    private Timer connectionTimer;

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

        try {
            this.dht = new DHT();
            this.receiver = new ReceiverThread(dht, mPresenter);
            new Thread(receiver).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle extras = getIntent().getExtras();
        mContactId = extras.getInt(Constants.EXTRA_CONTACT_ID);
        String contactName = extras.getString(Constants.EXTRA_CONTACT_NAME);
        final String contactIp = extras.getString(Constants.EXTRA_CONTACT_IP);

        final Contact me = new Contact(dht.id.toString());
        me.setPeerId(dht.id.toString());
        final Contact friend = new Contact(contactName);

        Log.d("DHT", "Contact IP: " + contactIp);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mMessageEditText.getText().toString();
                if(!text.isEmpty()){
                    MessageResponse message = new MessageResponse(me, friend, text);
                    mPresenter.sendMessage(message);
                    mMessageEditText.setText("");
                    try {
                        dht.send(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    hideSoftKeyboard();
                }
            }
        });

        connectionTimer = new Timer();
        connectionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (dht.connectTo(contactIp)) {
                        this.cancel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 3000);

        setRecyclerView();
        setActionBar(contactName, true);
        mPresenter.loadMessages(0, mContactId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_person) {
            showToast("Fazer magia para adicionar pessoa");
//            aperta aqui pra dar refresh nos dados

        }
        return super.onOptionsItemSelected(item);
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
    public void loadContactMessages(int sender_id, int user_id) {
        MessageDatabaseHelper dbHelper = new MessageDatabaseHelper(this);
        List<MessageResponse> messages = dbHelper.getMessagesFromContact(user_id, sender_id);
        showMessages(messages);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        receiver.cancel();
        dht.shutDown();
        connectionTimer.cancel();
    }

    @Override
    public void showMessages(List<MessageResponse> messages) {
        mAdapter.setMessages(messages);
        mRecyclerView.scrollToPosition(messages.size() - 1);
    }

    @Override
    public void showEmptyMessages() {
        showToast("Não existem questões no momento");
    }

    @Override
    public void showError(String error) {
        showToast(error);
    }

    @Override
    public void showProgress(boolean show) {
        showProgressDialog(show);
    }
}
