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

public class ResetarSenha extends AppCompatActivity implements View.OnClickListener{
    private EditText codigo, senha, confirmaSenha;
    private Button enviar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetar_senha);

        codigo = (EditText) findViewById(R.id.editText1);
        senha = (EditText) findViewById(R.id.editText2);
        confirmaSenha = (EditText) findViewById(R.id.editText3);

        enviar = (Button) findViewById(R.id.button1);
        enviar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(codigo == null || senha == null || confirmaSenha == null) {
            Toast.makeText(getApplicationContext(), "Digite todos os campos!", Toast.LENGTH_LONG).show();
        } else if(!senha.getText().toString().equals(confirmaSenha.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Senhas não coincidem!", Toast.LENGTH_LONG).show();
        } else if(senha.getText().toString().length() < 8) {
            Toast.makeText(getApplicationContext(), "Senha deve possuir pelo menos 8 caracteres!", Toast.LENGTH_LONG).show();
        } else {
            AWSMobileClient.getInstance().confirmForgotPassword(senha.getText().toString(), codigo.getText().toString(), new Callback<ForgotPasswordResult>() {
                @Override
                public void onResult(final ForgotPasswordResult result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (result.getState()) {
                                case DONE:
                                    Toast.makeText(getApplicationContext(), "Senha alterada com sucesso!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(ResetarSenha.this, Login.class);
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
                    if(e instanceof com.amazonaws.services.cognitoidentityprovider.model.CodeMismatchException) {
                        Toast.makeText(getApplicationContext(), "Código digitado inválido!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
