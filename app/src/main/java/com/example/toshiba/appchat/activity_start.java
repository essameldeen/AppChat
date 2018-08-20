package com.example.toshiba.appchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class activity_start extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button bt_goReigster =(Button)findViewById(R.id.bt_goRegister);
        Button bt_goLogIn = (Button)findViewById(R.id.bt_goLogIn);

        bt_goLogIn.setOnClickListener(this);
        bt_goReigster.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.bt_goLogIn:
                Intent goToLogIn = new Intent(this,activity_logIn.class);
                startActivity(goToLogIn);

                break;
            case  R.id.bt_goRegister:
                Intent goToRegister = new Intent(this,activity_register.class);
                startActivity(goToRegister);
                break;


                default:
                    break;
        }
    }
}
