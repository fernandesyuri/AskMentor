package com.yurifernandes.askmentor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class Login extends Activity {
    private CallbackManager callbackManager;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(Login.this, "Logado com sucesso", Toast.LENGTH_LONG).show();
                AccessToken accessToken = loginResult.getAccessToken();

                if (isLoggedIn()) {
                    callActivity();
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(Login.this, "Login cancelado", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(Login.this, exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void callActivity()
    {
        //Intent intent = new Intent(Login.this, Pergunta.class);
        //startActivity(intent);
    }

    public boolean isLoggedIn()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClickFacebookButton(View view)
    {
        if(view == loginButton)
            loginButton.performClick();
    }
}
