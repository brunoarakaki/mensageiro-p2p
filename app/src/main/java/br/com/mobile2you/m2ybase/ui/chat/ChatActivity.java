package br.com.mobile2you.m2ybase.ui.chat;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.List;

import br.com.mobile2you.m2ybase.R;
import br.com.mobile2you.m2ybase.data.local.MessageDatabaseHelper;
import br.com.mobile2you.m2ybase.data.remote.models.MessageResponse;
import br.com.mobile2you.m2ybase.ui.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends BaseActivity implements ChatMvpView{

    private ChatPresenter mPresenter;
    private ChatAdapter mAdapter;


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

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mMessageEditText.getText().toString();
                if(!text.isEmpty()){
                    MessageResponse message = new MessageResponse(0,text, 2);
                    mPresenter.sendMessage(message);
                    mMessageEditText.setText("");
                } else {
                    hideSoftKeyboard();
                }
            }
        });

        setRecyclerView();
        setActionBar("Nome do contatinho", true);
//        TODO: load messages calling presenter
//        mPresenter.dummyData();
        mPresenter.loadMessages(0, 2);
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
    }

    @Override
    public void showMessages(List<MessageResponse> messages) {
        mAdapter.setMessages(messages);
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
