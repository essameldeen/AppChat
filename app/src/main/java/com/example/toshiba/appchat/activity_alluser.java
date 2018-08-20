package com.example.toshiba.appchat;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Adapter;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.toshiba.appchat.Adapter.AdapterAllUsers;
import com.example.toshiba.appchat.Model.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class activity_alluser extends AppCompatActivity {
      private RecyclerView recyclerView;
      private List<UserData> userData = new ArrayList<>();
      private AdapterAllUsers adapterAllUsers;
      private DatabaseReference firebaseDatabase;
      private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alluser);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.my_toolbar_alluser);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //
        progressBar=(ProgressBar)findViewById(R.id.progressBarAllUser);
        recyclerView=(RecyclerView)findViewById(R.id.rv_alluser);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterAllUsers=new AdapterAllUsers(userData,this);
        recyclerView.setAdapter(adapterAllUsers);
        //
        firebaseDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseDatabase.keepSynced(true);
        LoadData();


    }

    @Override
    protected void onStart() {
        super.onStart();
        userData.clear();
    }

    private void LoadData() {
        progressBar.setVisibility(View.VISIBLE);
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    progressBar.setVisibility(View.INVISIBLE);
                    String user_id= d.getKey();
                    UserData  user = d.getValue(UserData.class).withid(user_id);
                     userData.add(user);
                     adapterAllUsers.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(activity_alluser.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
