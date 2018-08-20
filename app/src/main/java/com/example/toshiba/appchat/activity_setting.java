package com.example.toshiba.appchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toshiba.appchat.Model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class activity_setting extends AppCompatActivity implements View.OnClickListener {
        private DatabaseReference firestore;
        private FirebaseAuth auth;
        private TextView tv_name;
        private TextView tv_status;
        private CircleImageView iv_profile;
        private Button bt_changeSetting;
        private UserData user;


    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser()==null){
            Intent goToStart = new Intent(this,activity_setting.class);
            startActivity(goToStart);
            finish();

        }else {
            firestore.child("online").setValue(1);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firestore.child("online").setValue(ServerValue.TIMESTAMP);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        auth=FirebaseAuth.getInstance();
        firestore= FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());
        firestore.keepSynced(true);
        //
        tv_name=(TextView)findViewById(R.id.tv_nameSetting);
        tv_status=(TextView)findViewById(R.id.tv_statusSetting);
        iv_profile=(CircleImageView)findViewById(R.id.iv_profileSetting);
        bt_changeSetting=(Button)findViewById(R.id.bt_changeSetting);

        bt_changeSetting.setOnClickListener(this);
        //
        loadDataFromServer();



    }
    private void loadDataFromServer() {

         firestore.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 user = dataSnapshot.getValue(UserData.class);
                 UpdateUi(user);
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {
                 Toast.makeText(activity_setting.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

             }
         });

    }

    private void UpdateUi(final UserData user) {
        tv_name.setText(user.getName());
        tv_status.setText(user.getStatus());
        if(user.getImage().equals("Default")){

        }else {

            Picasso.get().load(user.getImage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile).into(iv_profile, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(iv_profile);
                }
            });

        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_changeSetting:
                Intent changeActivity=new Intent(this,activity_changeSetting.class);
                startActivity(changeActivity);
                break;
                default:
                    return;

        }
    }
}
