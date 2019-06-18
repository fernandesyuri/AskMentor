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
import com.amazonaws.mobile.client.results.SignUpResult;
import com.amazonaws.mobile.client.results.UserCodeDeliveryDetails;

import java.util.HashMap;
import java.util.Map;

public class Cadastro extends Activity implements View.OnClickListener {
    private Button registerBtn;
    private EditText email, username, password, passwordVerify;
    private final String TAG = AuthenticationActivity.class.getSimpleName();
    final Map<String, String> attributes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        registerBtn = (Button) findViewById(R.id.button1);
        registerBtn.setOnClickListener(this);

        username = (EditText) findViewById(R.id.editText1);
        email = (EditText) findViewById(R.id.editText2);
        password = (EditText) findViewById(R.id.editText3);
        passwordVerify = (EditText) findViewById(R.id.editText4);
    }

    private boolean verificaSenha() {
        if(passwordVerify.getText().toString().equals(password.getText().toString())) {
            return true;
        } else {
            return false;
        }
    }

    private boolean tamanhoSenha() {
        if(password.getText().toString().length() >= 8) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        if(v == registerBtn) {
            if (username.getText().toString().equals("") || email.getText().toString().equals("")
                    || password.getText().toString().equals("") || passwordVerify.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "Preencha todos os campos!", Toast.LENGTH_LONG).show();
            } else if (!verificaSenha()) {
                Toast.makeText(getApplicationContext(), "Senhas não coincidem!", Toast.LENGTH_LONG).show();
            } else if (!tamanhoSenha()) {
                Toast.makeText(getApplicationContext(), "Senha deve possuir pelo menos 8 caracteres!", Toast.LENGTH_LONG).show();
            } else {
                attributes.put("email", email.getText().toString());
                attributes.put("name", username.getText().toString());
                AWSMobileClient.getInstance().signUp(email.getText().toString(), password.getText().toString(), attributes, null, new Callback<SignUpResult>() {
                    @Override
                    public void onResult(final SignUpResult signUpResult) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "Sign-up callback state: " + signUpResult.getConfirmationState());
                                if (!signUpResult.getConfirmationState()) {
                                    final UserCodeDeliveryDetails details = signUpResult.getUserCodeDeliveryDetails();
                                    Toast.makeText(getApplicationContext(), "Código de verificação enviado para " + details.getDestination(), Toast.LENGTH_LONG).show();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("email", email.getText().toString());
                                    Intent intent = new Intent(Cadastro.this, CadastroConfirmar.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        if(e instanceof com.amazonaws.services.cognitoidentityprovider.model.UsernameExistsException) {
                            AWSMobileClient.getInstance().resendSignUp(email.getText().toString(), new Callback<SignUpResult>() {
                                @Override
                                public void onResult(SignUpResult signUpResult) {
                                    Toast.makeText(getApplicationContext(), "Código de verificação enviado para " + signUpResult.getUserCodeDeliveryDetails().getDestination(), Toast.LENGTH_LONG).show();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("email", email.getText().toString());
                                    Intent intent = new Intent(Cadastro.this, CadastroConfirmar.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onError(Exception err) {
                                    Log.e(TAG, err.getMessage());
                                }
                            });
                        } else {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "ERRO!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
            }
        }
    }
}
