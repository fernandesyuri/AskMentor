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
    private EditText username, password, newpassword;
    private Button registerBtn, loginBtn;
    private final String TAG = AuthenticationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.button1);
        loginBtn.setOnClickListener(this);

        registerBtn = findViewById(R.id.button1);
        registerBtn.setOnClickListener(this);

        username = (EditText) findViewById(R.id.editText1);
        password = (EditText) findViewById(R.id.editText2);

        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails userStateDetails) {
                    Log.i("INIT", "onResult: " + userStateDetails.getUserState());
                }
                @Override
                public void onError(Exception e) {
                    Log.e("INIT", "Initialization error.", e);
                }
            }
        );
    }

    @Override
    public void onClick(View v) {
        if (v == registerBtn) {
            Intent intent = new Intent(Login.this, Cadastro.class);
            startActivity(intent);
        }

        if (v == loginBtn) {
            AWSMobileClient.getInstance().signIn(username.getText().toString(), password.getText().toString(), null, new Callback<SignInResult>() {
                @Override
                public void onResult(final SignInResult signInResult) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Sign-in callback state: " + signInResult.getSignInState());
                            switch (signInResult.getSignInState()) {
                                case DONE:
                                    Intent intent = new Intent(Login.this, Home.class);
                                    startActivity(intent);
                                    break;
                                default:
                                    Toast.makeText(getApplicationContext(), "Unsupported sign-in confirmation:" + signInResult.getSignInState(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getApplicationContext(), "Usuário ou Senha incorretos!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
    }
}
