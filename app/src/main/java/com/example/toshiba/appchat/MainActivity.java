package com.example.toshiba.appchat;

import android.app.Service;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.example.toshiba.appchat.Adapter.AdapterFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private AdapterFragment adapterFragment;
    private DatabaseReference referenceUser;


    @Override
    protected void onStart() {
        super.onStart();

            if(auth.getCurrentUser()==null){
                GoToStartActivity();
            }else {
                  referenceUser.child("online").setValue(1);
            }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(auth.getCurrentUser()!=null){
            referenceUser.child("online").setValue(ServerValue.TIMESTAMP);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.support.v7.widget.Toolbar toolbar=(android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("EM Chat");
        toolbar.inflateMenu(R.menu.main_menu);
        auth=FirebaseAuth.getInstance();



        //
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        tabLayout=(TabLayout)findViewById(R.id.tabs);
        adapterFragment=new AdapterFragment(getSupportFragmentManager());
        viewPager.setAdapter(adapterFragment);
        tabLayout.setupWithViewPager(viewPager);
        //
        if(auth.getCurrentUser()!=null){
            referenceUser= FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());
        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_logout){
            auth.signOut();
            GoToStartActivity();

        }else if(item.getItemId()==R.id.menu_setting){
            gotToSetting();
        }else  if(item.getItemId()==R.id.menu_allusers){
            gotToAllUsers();
        }


        return false;
    }

    private void gotToAllUsers() {
        Intent settingActivity=new Intent(this,activity_alluser.class);
        startActivity(settingActivity);
    }


    private void gotToSetting() {
        Intent settingActivity=new Intent(this,activity_setting.class);
        startActivity(settingActivity);
    }

    private void GoToStartActivity() {
        Intent startActivity=new Intent(this,activity_start.class);
        startActivity(startActivity);
        finish();
    }
}
