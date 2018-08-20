package com.example.toshiba.appchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toshiba.appchat.Model.Conversation;
import com.example.toshiba.appchat.Model.UserData;
import com.example.toshiba.appchat.R;
import com.example.toshiba.appchat.activity_chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterChat  extends RecyclerView.Adapter<AdapterChat.viewHolder>{
    private DatabaseReference referenceMessage;
    private DatabaseReference referenceUser;
    private FirebaseAuth auth;
    List<Conversation> conversations;
    Context context;

    public AdapterChat(List<Conversation> conversations, Context context) {
        auth=FirebaseAuth.getInstance();
        this.conversations = conversations;
        this.context = context;

        referenceMessage= FirebaseDatabase.getInstance().getReference().child("messages").child(auth.getCurrentUser().getUid());
        referenceMessage.keepSynced(true);
        referenceUser=FirebaseDatabase.getInstance().getReference().child("Users");

    }

    @Override
    public AdapterChat.viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.layout_single_chat,parent,false);


        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdapterChat.viewHolder holder, final int position) {
        LoadData(holder,position);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent= new Intent(context.getApplicationContext(),activity_chat.class);
                chatIntent.putExtra("user_id",conversations.get(position).convID);
                chatIntent.putExtra("user_name",holder.tv_name.getText().toString());
                context.startActivity(chatIntent);
            }
        });

    }

    private void LoadData(final viewHolder holder, int position) {
        if(conversations.get(position).isSeen()){
            holder.tv_lastMessage.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }else {
            holder.tv_lastMessage.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
        referenceUser.child(conversations.get(position).convID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData userData = dataSnapshot.getValue(UserData.class);
                holder.tv_name.setText(userData.getName());
                Picasso.get().load(userData.getImage_thumb()).placeholder(R.drawable.profile).into(holder.circleImageView);

                if(userData.getOnline()==1){
                    holder.iv_online.setVisibility(View.VISIBLE);
                }else {
                    holder.iv_online.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        Query lastMessage = referenceMessage.child(conversations.get(position).convID).limitToLast(1);
        lastMessage.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String messaage = dataSnapshot.child("message").getValue().toString();
                holder.tv_lastMessage.setText(messaage);

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

    @Override
    public int getItemCount() {
        return conversations.size();
    }
    public  class  viewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView tv_name;
        TextView tv_lastMessage;
        ImageView iv_online;
        View item;

        public viewHolder(View itemView) {
            super(itemView);
            item=itemView;
            circleImageView=(CircleImageView)itemView.findViewById(R.id.civ_singleChat_userProfile);
            iv_online=(ImageView)itemView.findViewById(R.id.iv_singleChat_online);
            tv_name=(TextView)itemView.findViewById(R.id.tv_singleChat_name);
            tv_lastMessage=(TextView)itemView.findViewById(R.id.tv_singleChat_lastMessage);


        }
    }
}
