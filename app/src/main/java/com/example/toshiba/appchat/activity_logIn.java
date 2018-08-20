package com.example.toshiba.appchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class activity_logIn extends AppCompatActivity implements View.OnClickListener {
    private EditText et_email;
    private EditText et_passWord;
    private ProgressBar progressBar;
    private DatabaseReference referenceUsers;
    private Button bt_logIn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        auth=FirebaseAuth.getInstance();
        referenceUsers= FirebaseDatabase.getInstance().getReference().child("Users");
       //
        android.support.v7.widget.Toolbar toolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.my_toolbar_logIn);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //
        et_email=(EditText)findViewById(R.id.et_email_login);
        et_passWord=(EditText)findViewById(R.id.et_passWord_login);
        progressBar=(ProgressBar)findViewById(R.id.progressBar_login);
        bt_logIn=(Button)findViewById(R.id.bt_logIn);

        bt_logIn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.bt_logIn){
            String email = et_email.getText().toString();
            String passWord=et_passWord.getText().toString();
            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(passWord)){
                LogInServer(email,passWord);

            }else {
                Toast.makeText(this, "Please Fill All The Data.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void LogInServer(String email, String passWord) {
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email,passWord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.INVISIBLE);
                if(task.isSuccessful()){
                    SaveTokenId();


                }else {
                    Toast.makeText(activity_logIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SaveTokenId() {
        String token_id = FirebaseInstanceId.getInstance().getToken();
        referenceUsers.child(auth.getCurrentUser().getUid()).child("token_id").setValue(token_id).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful()){
                                 sendToMainActivity();
                             }else {
                                 Toast.makeText(activity_logIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                             }
            }
        });


    }

    private void sendToMainActivity() {
        Intent gotToMainActivity=new Intent(this,MainActivity.class);
        gotToMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(gotToMainActivity);
        finish();
    }
}
