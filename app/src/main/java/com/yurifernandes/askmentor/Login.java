package com.yurifernandes.askmentor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateUserMutation;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.results.SignInResult;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.sigv4.CognitoUserPoolsAuthProvider;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

public class Login extends Activity implements View.OnClickListener {
    private EditText username, password;
    private Button registerBtn, loginBtn, forgotPassBtn;
    private AWSAppSyncClient mAWSAppSyncClient;

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

        username.setText("contato@yurifernandes.com");
        password.setText("teste123");

        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
                    @Override
                    public void onResult(UserStateDetails userStateDetails) {
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                }
        );
    }

    public void mutation() {

        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .cognitoUserPoolsAuthProvider(new CognitoUserPoolsAuthProvider() {
                    @Override
                    public String getLatestAuthToken() {
                        try {
                            return AWSMobileClient.getInstance().getTokens().getIdToken().getTokenString();
                        } catch (Exception e) {
                            Log.e("APPSYNC_ERROR", e.getLocalizedMessage());
                            return e.getLocalizedMessage();
                        }
                    }
                }).build();

        CreateUserMutation createUserMutation = CreateUserMutation.builder()
                .username(AWSMobileClient.getInstance().getUsername())
                .build();

        mAWSAppSyncClient.mutate(createUserMutation).enqueue(mutationCallback);
    }

    private GraphQLCall.Callback<CreateUserMutation.Data> mutationCallback = new GraphQLCall.Callback<CreateUserMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<CreateUserMutation.Data> response) {
            Log.i("###Results", "Added User");
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("###Error", e.toString());
        }
    };

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

                                        mutation();

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

        if (v == forgotPassBtn) {
            Intent intent = new Intent(Login.this, EsqueciSenha.class);
            startActivity(intent);
            finish();
        }
    }
}
