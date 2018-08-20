package com.example.toshiba.appchat.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.toshiba.appchat.Adapter.AdapterChat;
import com.example.toshiba.appchat.Model.Conversation;
import com.example.toshiba.appchat.Model.UserData;
import com.example.toshiba.appchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class fragment_chat extends Fragment {
    private DatabaseReference referenceChat;
    private RecyclerView recyclerView;
      private AdapterChat adapterChat;
    private FirebaseAuth auth;
    private List<Conversation> conversations = new ArrayList<>();
    public fragment_chat() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_fragment_chat, container, false);
        auth=FirebaseAuth.getInstance();
        referenceChat= FirebaseDatabase.getInstance().getReference().child("Chat").child(auth.getCurrentUser().getUid());
        referenceChat.keepSynced(true);



        //
        recyclerView=(RecyclerView)view.findViewById(R.id.rv_chats);
        adapterChat = new AdapterChat(conversations,getContext());
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        linearLayout.setReverseLayout(true);
        linearLayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterChat);




     return  view ;
    }

    @Override
    public void onStart() {
        super.onStart();
        conversations.clear();
        Query conversationQuery = referenceChat.orderByChild("timeStamp");
        conversationQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    String conv_id= d.getKey();
                    Conversation conversation = d.getValue(Conversation.class).withid(conv_id);
                    conversations.add(conversation);
                    adapterChat.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
