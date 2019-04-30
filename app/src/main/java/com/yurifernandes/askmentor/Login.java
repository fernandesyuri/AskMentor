package com.yurifernandes.askmentor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.regions.Regions;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class Login extends Activity implements View.OnClickListener {

    private AWSAppSyncClient mAWSAppSyncClient;

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private Button btnLogin;

    private CognitoCachingCredentialsProvider credentialsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        // Inicializar o provedor de credenciais do Amazon Cognito
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:2d957a98-f39c-40f7-bcaa-f7ef27ec4566", // ID do grupo de identidades
                Regions.US_EAST_1 // Região
        );

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

        btnLogin = findViewById(R.id.button1);
    }

    public void callActivity() {
        Intent intent = new Intent(Login.this, Teste.class);
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
        if (view == loginButton)
            loginButton.performClick();
    }

    @Override
    public void onClick(View v) {
        if (v == btnLogin) {

        }
    }
}
