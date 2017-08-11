package br.com.mobile2you.m2ybase.ui.chat;
import android.os.Bundle;

import br.com.mobile2you.m2ybase.R;
import br.com.mobile2you.m2ybase.ui.base.BaseActivity;

public class ChatActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setActionBar("Nome do contatinho", true);
    }

}
