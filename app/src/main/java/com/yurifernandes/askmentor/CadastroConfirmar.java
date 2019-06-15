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
import com.amazonaws.mobile.client.results.SignUpResult;
import com.amazonaws.mobile.client.results.UserCodeDeliveryDetails;

public class CadastroConfirmar extends AppCompatActivity implements View.OnClickListener {
    private Button registerBtn;
    private EditText email, code;
    private String emailCadastrado;
    private final String TAG = AuthenticationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_confirmar);

        registerBtn = (Button) findViewById(R.id.button1);
        registerBtn.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle dados = intent.getExtras();

        emailCadastrado = dados.getString("email");
        email = (EditText) findViewById(R.id.editText1);
        code = (EditText) findViewById(R.id.editText2);
    }

    @Override
    public void onClick(View view) {
        if(view == registerBtn) {
            if (email.getText().toString().equals(emailCadastrado)) {
                AWSMobileClient.getInstance().confirmSignUp(email.getText().toString(), code.getText().toString(), new Callback<SignUpResult>() {
                    @Override
                    public void onResult(final SignUpResult signUpResult) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "Sign-up callback state: " + signUpResult.getConfirmationState());
                                if (!signUpResult.getConfirmationState()) {
                                    final UserCodeDeliveryDetails details = signUpResult.getUserCodeDeliveryDetails();
                                    Toast.makeText(getApplicationContext(), "Você entra aqui? " + details.getDestination(), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Cadastrado com Sucesso!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(CadastroConfirmar.this, Login.class);
                                    startActivity(intent);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(final Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Código incorreto!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Email digitado incorreto", Toast.LENGTH_LONG).show();
            }
        }
    }
}
