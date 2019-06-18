package com.yurifernandes.askmentor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.results.SignInResult;

public class Login extends Activity implements View.OnClickListener {
    private EditText username, password;
    private Button registerBtn, loginBtn, forgotPassBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.button1);
        loginBtn.setOnClickListener(this);

        registerBtn = findViewById(R.id.button2);
        registerBtn.setOnClickListener(this);

        forgotPassBtn = findViewById(R.id.button3);
        forgotPassBtn.setOnClickListener(this);

        username = (EditText) findViewById(R.id.editText1);
        password = (EditText) findViewById(R.id.editText2);

        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails userStateDetails) {}
                @Override
                public void onError(Exception e) {}
            }
        );
    }

    @Override
    public void onClick(View v) {
        if (v == registerBtn) {
            Intent intent = new Intent(Login.this, Cadastro.class);
            startActivity(intent);
            finish();
        }

        if (v == loginBtn) {
            if (username == null || password == null) {
                Toast.makeText(getApplicationContext(), "Digite Usuário e Senha!", Toast.LENGTH_LONG).show();
            } else {
                AWSMobileClient.getInstance().signIn(username.getText().toString(), password.getText().toString(), null, new Callback<SignInResult>() {
                    @Override
                    public void onResult(final SignInResult signInResult) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (signInResult.getSignInState()) {
                                    case DONE:
                                        Intent intent = new Intent(Login.this, Home.class);
                                        startActivity(intent);
                                        finish();
                                        break;
                                    default:
                                        Toast.makeText(getApplicationContext(), "ERRO", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(final Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Usuário ou Senha inválidos!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        }

        if(v == forgotPassBtn) {
            Intent intent = new Intent(Login.this, EsqueciSenha.class);
            startActivity(intent);
            finish();
        }
    }
}
