package com.yurifernandes.askmentor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class Login extends Activity implements View.OnClickListener {

    private AWSAppSyncClient mAWSAppSyncClient;

    private CallbackManager callbackManager;
    private Button btnLogin;

    private CognitoCachingCredentialsProvider credentialsProvider;
    private LoginButton loginFacebook;
    private Button registerBtn, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       /* mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        // Inicializar o provedor de credenciais do Amazon Cognito
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:2d957a98-f39c-40f7-bcaa-f7ef27ec4566", // ID do grupo de identidades
                Regions.US_EAST_1 // Região
        ); */

        callbackManager = CallbackManager.Factory.create();

        loginFacebook = (LoginButton) findViewById(R.id.login_button);
        loginFacebook.setReadPermissions("email");

        loginBtn = (Button) findViewById(R.id.button1);
        loginBtn.setOnClickListener(this);

        registerBtn = (Button) findViewById(R.id.button2);
        registerBtn.setOnClickListener(this);

        loginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
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

        btnLogin = findViewById(R.id.button1);
    }

    public void callActivity() {
        Intent intent = new Intent(Login.this, Home.class);
        startActivity(intent);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClickFacebookButton(View view) {
        if(view == loginFacebook)
            loginFacebook.performClick();
    }

    @Override
    public void onClick(View v) {
        if(v == registerBtn) {
            Intent intent = new Intent(Login.this, Cadastro.class);
            startActivity(intent);
        }

        if (v == btnLogin) {
            Intent intent = new Intent(Login.this, Home.class);
            startActivity(intent);
        }
    }
}
