package br.com.mobile2you.m2ybase.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.com.mobile2you.m2ybase.Constants;
import br.com.mobile2you.m2ybase.R;
import br.com.mobile2you.m2ybase.data.local.Contact;
import br.com.mobile2you.m2ybase.data.local.DHT;
import br.com.mobile2you.m2ybase.data.local.ReceiverThread;
import br.com.mobile2you.m2ybase.data.local.Utils;
import br.com.mobile2you.m2ybase.data.remote.models.PollsResponse;
import br.com.mobile2you.m2ybase.data.remote.models.PostsResponse;
import br.com.mobile2you.m2ybase.data.remote.services.DHTService;
import br.com.mobile2you.m2ybase.ui.base.BaseActivity;
import br.com.mobile2you.m2ybase.ui.chat.ChatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainMvpView {

    private MainPresenter mMainPresenter;
    private MainAdapter mAdapter;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mMainPresenter = new MainPresenter();
        mMainPresenter.attachView(this);

        Toast.makeText(getApplicationContext(), Utils.getIPAddress(true), Toast.LENGTH_LONG).show();

        final Contact myself = new Contact(android.os.Build.MODEL);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MainAdapter(new MainAdapter.OnClicked() {
            @Override
            public void onContactClicked(Contact contact) {
                Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                chatIntent.putExtra(Constants.EXTRA_CONTACT, contact);
                chatIntent.putExtra(Constants.EXTRA_MYSELF, myself);
                startActivity(chatIntent);
            }
        },
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMainPresenter.loadContacts(v.getContext());
                }
        });
        mRecyclerView.setAdapter(mAdapter);
        setActionBar("Mensageiro P2P");

        mMainPresenter.loadContacts(this);

        Intent dhtService = new Intent(getApplicationContext(), DHTService.class);
        dhtService.putExtra("myself", myself);
        startService(dhtService);
    }

    public void showNewContactDialog(){
        LayoutInflater li = LayoutInflater.from(this);
        View dialogView = li.inflate(R.layout.dialog_add_contact, null);
        // create alert dialog
        AlertDialog alertDialog = getNewContactDialogBuilder(dialogView).create();
        alertDialog.show();
    }

    public AlertDialog.Builder getNewContactDialogBuilder(View dialogView){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(dialogView);

        final EditText userInput = (EditText) dialogView
                .findViewById(R.id.edit_text_contact_name);
        final EditText ipInput = (EditText) dialogView
                .findViewById(R.id.edit_text_contact_ip);

        // set dialog message
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    String contactName = userInput.getText().toString();
                    String contactIp = ipInput.getText().toString();
                    addContact(contactName, contactIp);
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    dialog.cancel();
                }
            });
        return alertDialogBuilder;

    }

    public void addContact(String name, String ip){
        mMainPresenter.addContact(this, name, ip);
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
            showNewContactDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.detachView();
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
}
