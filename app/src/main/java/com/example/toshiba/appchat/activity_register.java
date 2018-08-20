package com.example.toshiba.appchat;

import android.annotation.SuppressLint;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;


public class activity_register extends AppCompatActivity implements View.OnClickListener {

    private EditText et_name;
    private EditText et_email;
    private EditText et_passWord;
    private Button bt_register;
    private ProgressBar progressBar;
    private DatabaseReference mstorage;

    private FirebaseAuth auth;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth=FirebaseAuth.getInstance();
        mstorage=FirebaseDatabase.getInstance().getReference();
        //
        android.support.v7.widget.Toolbar toolbar;
        toolbar=(android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar);
        et_name=(EditText)findViewById(R.id.et_name);
        et_email=(EditText)findViewById(R.id.et_email);
        et_passWord=(EditText)findViewById(R.id.et_passWord);
        bt_register=(Button)findViewById(R.id.bt_register);
        progressBar=(ProgressBar)findViewById(R.id.progressBar_register);
        bt_register.setOnClickListener(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }




    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.bt_register){
            progressBar.setVisibility(View.VISIBLE);

            String name = et_name.getText().toString();
            String email = et_email.getText().toString();
            String passWordd=et_passWord.getText().toString();
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && ! TextUtils.isEmpty(passWordd)){
               auth.createUserWithEmailAndPassword(email,passWordd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {

                       if(task.isSuccessful()){
                           StoreDataInFireBase();

                       }else {
                               progressBar.setVisibility(View.INVISIBLE);
                               Toast.makeText(activity_register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   }
               });
            }else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Please Fill All The Data.", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void StoreDataInFireBase() {

        String user_id = auth.getCurrentUser().getUid();
        String token_id = FirebaseInstanceId.getInstance().getToken();
        String name =et_name.getText().toString();
        Map<String , Object> user_map= new HashMap<>();

        user_map.put("name",name);
        user_map.put("status","Hi , im using EM App Chat");
        user_map.put("image","Default");
        user_map.put("image_thumb","Default");
        user_map.put("token_id",token_id);
        mstorage.child("Users").child(user_id).setValue(user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    GoToMainActivity();
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(activity_register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void GoToMainActivity() {
        Intent toMainActivity=new Intent(this,MainActivity.class);
        toMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toMainActivity);
        finish();
    }
}
