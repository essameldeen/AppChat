package com.example.toshiba.appchat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toshiba.appchat.Model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class activity_userProfile extends AppCompatActivity implements View.OnClickListener {
     private  String user_id;
     private ImageView iv_profileImage;
     private TextView tv_name;
     private TextView tv_status;
     private TextView tv_firends;
     private Button bt_sendRequest;
     private Button bt_declineRequest;
     private DatabaseReference databaseReference;
     private DatabaseReference referenceFrienndRequest;
    private DatabaseReference referenceNotification;
     private DatabaseReference referenceFriends;
     private DatabaseReference rootRef;
     private FirebaseAuth auth;
     private String  currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        user_id=getIntent().getStringExtra("user_id");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        referenceFrienndRequest=FirebaseDatabase.getInstance().getReference().child("FriendReq");
        referenceFriends=FirebaseDatabase.getInstance().getReference().child("Friends");
        referenceNotification=FirebaseDatabase.getInstance().getReference().child("Notifications");
        rootRef=FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        currentState="not_friend";
        //
          iv_profileImage=(ImageView)findViewById(R.id.iv_userProfileImage);
          tv_name=(TextView)findViewById(R.id.tv_userProfileName);
          tv_status=(TextView)findViewById(R.id.tv_userProfileStatus);
          tv_firends=(TextView)findViewById(R.id.tv_userProfileFirendsCount);
          bt_sendRequest=(Button)findViewById(R.id.bt_userProfileSendRequest);
          bt_declineRequest=(Button)findViewById(R.id.bt_userProfileUnFriend);
        //
        getData();
        bt_sendRequest.setOnClickListener(this);
        bt_declineRequest.setOnClickListener(this);




    }

    private void getData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData userData = dataSnapshot.getValue(UserData.class);
                updateUi(userData);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(activity_userProfile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUi(UserData userData) {
        Picasso.get().load(userData.getImage()).placeholder(R.drawable.profile).into(iv_profileImage);
        tv_name.setText(userData.getName());
        tv_status.setText(userData.getStatus());

        // check The current state
        referenceFrienndRequest.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id)){
                    String type_request = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                    if(type_request.equals("received")){
                        currentState="request_received";
                        bt_sendRequest.setText("ACCEPT FRIEND REQUEST");

                        bt_declineRequest.setVisibility(View.VISIBLE);
                        bt_declineRequest.setEnabled(true);

                    }else if(type_request.equals("sent")){
                        currentState="request_sent";
                        bt_sendRequest.setText("CANCEL FRIEND REQUEST");

                        bt_declineRequest.setVisibility(View.INVISIBLE);
                        bt_declineRequest.setEnabled(false);

                    }

                }else {
                    referenceFriends.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                                     if(dataSnapshot.hasChild(user_id)){
                                         currentState="friends";
                                         bt_sendRequest.setText(" UNFRIEND ");

                                         bt_declineRequest.setVisibility(View.INVISIBLE);
                                         bt_declineRequest.setEnabled(false);

                                     }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(activity_userProfile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(activity_userProfile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.child("online").setValue(1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.child("online").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.bt_userProfileSendRequest){

            // not friend request handling
            if(currentState.equals("not_friend")) {

                DatabaseReference newNotification = rootRef.child("Notifications").child(user_id).push();
                String notificatioId  =newNotification.getKey();
                bt_sendRequest.setEnabled(false);

                HashMap<String, String> mapNotification = new HashMap<>();
                mapNotification.put("from", auth.getCurrentUser().getUid());
                mapNotification.put("type", "request");

                Map userMap = new HashMap();
                userMap.put("FriendReq/"+auth.getCurrentUser().getUid() + "/" + user_id+"/request_type" , "sent");
                userMap.put("FriendReq/"+ user_id + "/"  + auth.getCurrentUser().getUid() + "/request_type", "received");
                userMap.put("Notifications/"+user_id+"/"+notificatioId, mapNotification);

                rootRef.updateChildren(userMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        bt_sendRequest.setEnabled(true);
                        currentState = "request_sent";
                        bt_sendRequest.setText("CANCEL FRIEND REQUEST");
                        bt_declineRequest.setVisibility(View.INVISIBLE);
                        bt_declineRequest.setEnabled(false);
                    }
                });
            }
            // handle request sent
            if (currentState.equals("request_sent")){
                referenceFrienndRequest.child(auth.getCurrentUser().getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        referenceFrienndRequest.child(user_id).child(auth.getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                bt_sendRequest.setEnabled(true);
                                currentState="not_friend";
                                bt_sendRequest.setText("SEND FRIEND REQUEST");
                                bt_declineRequest.setVisibility(View.INVISIBLE);
                                bt_declineRequest.setEnabled(false);


                            }
                        });

                    }
                });
            }
            // handle accept friend request
            if (currentState.equals("request_received")){

                final String currentDate = DateFormat.getDateInstance().format(new Date());

                 Map friendMap = new HashMap();
                 friendMap.put("Friends/"+auth.getCurrentUser().getUid()+"/"+user_id+"/date",currentDate);
                 friendMap.put("Friends/"+user_id+"/"+auth.getCurrentUser().getUid()+"/date",currentDate);

                friendMap.put("FriendReq/"+auth.getCurrentUser().getUid() + "/" + user_id , null);
                friendMap.put("FriendReq/"+ user_id + "/"  + auth.getCurrentUser().getUid(), null);


               rootRef.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                   @Override
                   public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                       if(databaseError==null){
                           currentState="friends";
                           bt_sendRequest.setText("UNFRIEND This Person");
                           bt_declineRequest.setVisibility(View.INVISIBLE);
                           bt_declineRequest.setEnabled(false);
                       }else {
                           Toast.makeText(activity_userProfile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                       }


                   }
               });
            }
            //
            if(currentState.equals("friends")){
                Map unfriendMap = new HashMap();

                unfriendMap.put("Friends/"+auth.getCurrentUser().getUid()+"/"+user_id,null);
                unfriendMap.put("Friends/"+user_id+"/"+auth.getCurrentUser().getUid(),null);

                rootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(databaseError==null){
                            currentState="not_friend";
                            bt_sendRequest.setText("SEND FRIEND REQUEST");
                            bt_declineRequest.setVisibility(View.INVISIBLE);
                            bt_declineRequest.setEnabled(false);

                        }else {
                            Toast.makeText(activity_userProfile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        else if(view.getId()==R.id.bt_userProfileUnFriend){
            Map declineMap = new HashMap();

            declineMap.put("FriendReq/"+auth.getCurrentUser().getUid() + "/" + user_id , null);
            declineMap.put("FriendReq/"+ user_id + "/"  + auth.getCurrentUser().getUid() , null);

            rootRef.updateChildren(declineMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError==null){
                        currentState="not_friend";
                        bt_sendRequest.setText("SEND FRIEND REQUEST");
                        bt_declineRequest.setVisibility(View.INVISIBLE);
                        bt_declineRequest.setEnabled(false);

                    }else {
                        Toast.makeText(activity_userProfile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
