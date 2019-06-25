package com.yurifernandes.askmentor;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.yurifernandes.askmentor.ChatFolder.ChatAdapter;
import com.yurifernandes.askmentor.ChatFolder.ChatContract;
import com.yurifernandes.askmentor.ChatFolder.ChatPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, ChatContract.View {
    private EditText chat;
    private ImageButton button;
    private boolean controleBotao = false;
    private RecyclerView rvChatList;
    private ChatPresenter presenter;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Cria a Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Habilita título na Toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        //Habilita o botão voltar na Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Define o fundo para a Activity
        getWindow().setBackgroundDrawable(getDrawable(R.drawable.chat_bg));

        //Metodo para receber dados do usuário logado
        AWSMobileClient.getInstance().getUserAttributes(new Callback<Map<String, String>>() {
            @Override
            public void onResult(Map<String, String> result) {
                getSupportActionBar().setTitle(result.get("name"));
            }

            @Override
            public void onError(Exception e) {}
        });

        rvChatList = (RecyclerView) findViewById(R.id.recyclerView);
        chat = (EditText) findViewById(R.id.editText);
        chat.setOnEditorActionListener(searchBoxListener);

        // Instantiate presenter and attach view
        this.presenter = new ChatPresenter();
        presenter.attachView(this);

        // Instantiate the adapter and give it the list of chat objects
        this.chatAdapter = new ChatAdapter(presenter.getChatObjects());

        // Set up the RecyclerView with adapter and layout manager
        rvChatList.setAdapter(chatAdapter);
        rvChatList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvChatList.setItemAnimator(new DefaultItemAnimator());

        button = (ImageButton) findViewById(R.id.imageButton);
        button.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Botão de voltar redireciona para a Home
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(this, Home.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view == button) {
            //Abre a camera
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivity(intent);
        }
    }

    @Override
    public void notifyAdapterObjectAdded(int position) {
        this.chatAdapter.notifyItemInserted(position);
    }

    @Override
    public void scrollChatDown() {
        this.rvChatList.scrollToPosition(presenter.getChatObjects().size() - 1);
    }

    private EditText.OnEditorActionListener searchBoxListener = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView tv, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!TextUtils.isEmpty(tv.getText())) {
                    presenter.onEditTextActionDone(tv.getText().toString());
                    chat.getText().clear();
                    return true;
                }
            }
            return false;
        }
    };
}
