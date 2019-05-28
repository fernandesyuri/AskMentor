package com.yurifernandes.askmentor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Login extends Activity implements View.OnClickListener {

    private Button registerBtn, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.button1);
        loginBtn.setOnClickListener(this);

        registerBtn = findViewById(R.id.button2);
        registerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == registerBtn) {
            Intent intent = new Intent(Login.this, Cadastro.class);
            startActivity(intent);
        }

        if (v == loginBtn) {
            Intent intent = new Intent(Login.this, Home.class);
            startActivity(intent);
        }
    }
}
