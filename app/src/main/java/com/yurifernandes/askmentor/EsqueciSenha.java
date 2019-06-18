package com.yurifernandes.askmentor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.results.ForgotPasswordResult;

public class EsqueciSenha extends AppCompatActivity implements View.OnClickListener{
    private EditText email;
    private Button enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_senha);

        email = (EditText) findViewById(R.id.editText1);
        enviar = (Button) findViewById(R.id.button1);
        enviar.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == enviar) {
            AWSMobileClient.getInstance().forgotPassword(email.getText().toString(), new Callback<ForgotPasswordResult>() {
                @Override
                public void onResult(final ForgotPasswordResult result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (result.getState()) {
                                case CONFIRMATION_CODE:
                                    Toast.makeText(getApplicationContext(), "Código de confirmação enviado para seu email", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(EsqueciSenha.this, ResetarSenha.class);
                                    startActivity(intent);
                                    finish();
                                default:
                                    Log.e("###", "ERRO");
                            }
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    if(e instanceof com.amazonaws.services.cognitoidentityprovider.model.UserNotFoundException) {
                        Toast.makeText(getApplicationContext(), "Email não cadastrado!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
