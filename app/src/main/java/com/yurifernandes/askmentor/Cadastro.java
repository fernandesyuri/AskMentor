package com.yurifernandes.askmentor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Cadastro extends Activity implements View.OnClickListener {
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        registerBtn = (Button) findViewById(R.id.button4);
        registerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == registerBtn) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
    }
}
