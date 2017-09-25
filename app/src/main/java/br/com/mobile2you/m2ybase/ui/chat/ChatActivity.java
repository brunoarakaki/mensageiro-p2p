package br.com.mobile2you.m2ybase.ui.chat;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.LocalBroadcastManager;
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
import br.com.mobile2you.m2ybase.data.remote.services.DHTService;
import br.com.mobile2you.m2ybase.ui.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends BaseActivity implements ChatMvpView{

    private ChatPresenter mPresenter;
    private ChatAdapter mAdapter;
    private String contactIp;

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

        IntentFilter filter = new IntentFilter(Constants.RECEIVER_CHAT_FILTER);
        LocalBroadcastManager.getInstance(this).registerReceiver(chatReceiver, filter);

        Bundle extras = getIntent().getExtras();

        final Contact me = (Contact) extras.getSerializable(Constants.EXTRA_MYSELF);
        final Contact friend = (Contact) extras.getSerializable(Constants.EXTRA_CONTACT);
        this.contactIp = friend.getIp();
        friend.setIp(this.contactIp);

        Intent in = new Intent(Constants.RECEIVER_DHT_FILTER);
        in.putExtra("op", Constants.DHT_OP_CONNECT_TO);
        in.putExtra("ip", this.contactIp);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);

        Log.d("DHT", "Contact IP: " + this.contactIp);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mMessageEditText.getText().toString();
                if(!text.isEmpty()){
                    MessageResponse message = new MessageResponse(me, friend, text);
                    mPresenter.sendMessage(message);
                    mMessageEditText.setText("");
                    Intent in = new Intent(Constants.RECEIVER_DHT_FILTER);
                    in.putExtra("op", Constants.DHT_OP_SEND);
                    in.putExtra("message", message);
                    LocalBroadcastManager.getInstance(ChatActivity.this).sendBroadcast(in);
                } else {
                    hideSoftKeyboard();
                }
            }
        });

        setRecyclerView();
        setActionBar(friend.getName(), true);
        mPresenter.loadMessages(0, friend.getId());
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
        Intent in = new Intent(Constants.RECEIVER_DHT_FILTER);
        in.putExtra("op", Constants.DHT_OP_CLOSE_CONNECTION);
        in.putExtra("ip", contactIp);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);
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

    private BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageResponse message = (MessageResponse)intent.getSerializableExtra("message");
            mPresenter.sendMessage(message);
        }
    };

}
