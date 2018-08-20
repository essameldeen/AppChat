package com.example.toshiba.appchat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toshiba.appchat.Adapter.AdapterMessage;
import com.example.toshiba.appchat.Services.TimeAgo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class activity_chat extends AppCompatActivity implements View.OnClickListener {
private  String userId;
private String userName;
private TextView tv_name;
private TextView tv_lastSeen;
private CircleImageView profileImage;
private DatabaseReference referenceRoot;
private FirebaseAuth auth;
private String currentUser;
private ImageButton ib_sendMessage;
private ImageButton ib_addFile;
private EditText et_message;
private RecyclerView messageList;
private List<com.example.toshiba.appchat.Model.Message> allMessage = new ArrayList<>();
private AdapterMessage adapterMessage;
private SwipeRefreshLayout swipeRefreshLayout;
    private StorageReference mStorageRef;
private static  final  int TOTAL_LOAD=10;
private int currentPage = 1 ;
private  int itemPosition = 0 ;
private String lastKeyItem="";
private String previousKey="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbarChat);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.layout_custom_app_par,null);

        actionBar.setCustomView(actionBarView);

        //
         mStorageRef= FirebaseStorage.getInstance().getReference();
         referenceRoot= FirebaseDatabase.getInstance().getReference();
         auth=FirebaseAuth.getInstance();
         currentUser=auth.getCurrentUser().getUid();
         //
        userId = getIntent().getStringExtra("user_id");
        userName = getIntent().getStringExtra("user_name");
        //
        tv_name=(TextView)findViewById(R.id.tv_name_custome_app_par);
        tv_lastSeen=(TextView)findViewById(R.id.tv_lastSeen_custom_app_par);
        profileImage=(CircleImageView)findViewById(R.id.imageAppPar);

        et_message=(EditText)findViewById(R.id.et_message);
        ib_addFile=(ImageButton)findViewById(R.id.ib_addPicture);
        ib_sendMessage=(ImageButton)findViewById(R.id.ib_sentmessage);
        messageList=(RecyclerView)findViewById(R.id.messageList);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipRefresh);

        //
         adapterMessage = new AdapterMessage(allMessage ,this);
         messageList.setLayoutManager(new LinearLayoutManager(this));
         messageList.setHasFixedSize(true);
         messageList.setAdapter(adapterMessage);

        //
        tv_name.setText(userName);
        //
        GetInfoOfUser();
        CheckIfUserExist();
        GetMessage();
        //

        ib_sendMessage.setOnClickListener(this);
        ib_addFile.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               currentPage++;
               itemPosition=0;
               GetMoreMessage();
            }
        });

    }

    private void GetMoreMessage() {
        DatabaseReference messageRef= referenceRoot.child("messages").child(currentUser).child(userId);
        Query messageQuery = messageRef.orderByKey().endAt(lastKeyItem).limitToLast(10);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                com.example.toshiba.appchat.Model.Message message =dataSnapshot.getValue(com.example.toshiba.appchat.Model.Message.class);
                String messagekey=dataSnapshot.getKey();


                if(!previousKey.equals(messagekey)){
                    allMessage.add(itemPosition++ ,message);
                }else {
                    previousKey=messagekey;
                }
                if(itemPosition==1){
                    lastKeyItem=messagekey;

                }

                adapterMessage.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void GetMessage() {

        DatabaseReference messageRef= referenceRoot.child("messages").child(currentUser).child(userId);
        Query messageQuery = messageRef.limitToLast(currentPage*TOTAL_LOAD);


        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                com.example.toshiba.appchat.Model.Message message =dataSnapshot.getValue(com.example.toshiba.appchat.Model.Message.class);

                   allMessage.add(message);
                if(itemPosition==1){
                    String messagekey=dataSnapshot.getKey();
                    lastKeyItem=messagekey;
                    previousKey=lastKeyItem;
                }
                  itemPosition++;

                adapterMessage.notifyDataSetChanged();
                messageList.scrollToPosition(allMessage.size()-1);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void CheckIfUserExist() {

        referenceRoot.child("Chat").child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                          if(!dataSnapshot.hasChild(userId)){
                              AddUSerToChat();

                          }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(activity_chat.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void AddUSerToChat() {
        Map addUser = new HashMap();
        addUser.put("seen",false);
        addUser.put("timeStamp", ServerValue.TIMESTAMP);
        Map chatAdd = new HashMap();
        chatAdd.put("Chat/"+currentUser+"/"+userId,addUser);
        chatAdd.put("Chat/"+userId+"/"+currentUser,addUser);

        referenceRoot.updateChildren(chatAdd, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError !=null){
                        Toast.makeText(activity_chat.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
            }
        });

    }

    private void GetInfoOfUser() {
        referenceRoot.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    String online = dataSnapshot.child("online").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();
                    if(online.equals("true")){
                        tv_lastSeen.setText("online");

                    }else {
                        TimeAgo getTimeAgo =  new TimeAgo();
                        Long lastTime = Long.parseLong(online);
                        String lastSeen =getTimeAgo.getTime(lastTime,getApplicationContext());

                        tv_lastSeen.setText(lastSeen);
                    }
                Picasso.get().load(image).placeholder(R.drawable.profile).into(profileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(activity_chat.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.ib_sentmessage){
            SendMessage();
        }else if(view.getId()==R.id.ib_addPicture){
            Intent gallery = new Intent();
            gallery.setType("image/*");
            gallery.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(gallery,"Select Image"),1);
        }
    }

    private void SendMessage() {
        String message = et_message.getText().toString();
        if(!TextUtils.isEmpty(message)){
            et_message.setText("");
            DatabaseReference messageRef = referenceRoot.child("messages").child(currentUser).child(userId).push();
            String pushId= messageRef.getKey();

            String currentUserRef = "messages/"+currentUser+"/"+userId;
            String userRef = "messages/"+userId+"/"+currentUser;

             Map messageMap = new HashMap();
             messageMap.put("message",message);
             messageMap.put("seen",false);
             messageMap.put("type","text");
             messageMap.put("time",ServerValue.TIMESTAMP);
             messageMap.put("from",currentUser);

             Map addMessage = new HashMap();
            addMessage.put(currentUserRef+"/"+pushId,messageMap);
            addMessage.put(userRef+"/"+pushId,messageMap);

            referenceRoot.updateChildren(addMessage, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(databaseError !=null){
                            Toast.makeText(activity_chat.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                }
            });




        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            Uri  image= data.getData();
            SendImageMessage(image);




        }
    }

    private void SendImageMessage(Uri image) {
        DatabaseReference messageRef = referenceRoot.child("messages").child(currentUser).child(userId).push();
      final   String pushId= messageRef.getKey();

       final String currentUserRef = "messages/"+currentUser+"/"+userId;
       final String userRef = "messages/"+userId+"/"+currentUser;

        StorageReference pathImage = mStorageRef.child("message_image").child(pushId+".jpg");
        pathImage.putFile(image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            String downloadUrl = task.getResult().getDownloadUrl().toString();
                            Map messageMap = new HashMap();

                            messageMap.put("message",downloadUrl);
                            messageMap.put("seen",false);
                            messageMap.put("type","image");
                            messageMap.put("time",ServerValue.TIMESTAMP);
                            messageMap.put("from",currentUser);
                            Map addMessage = new HashMap();
                            addMessage.put(currentUserRef+"/"+pushId,messageMap);
                            addMessage.put(userRef+"/"+pushId,messageMap);

                            referenceRoot.updateChildren(addMessage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if(databaseError !=null){
                                        Toast.makeText(activity_chat.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });



                        }else {
                            Toast.makeText(activity_chat.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
            }
        });



    }
}
