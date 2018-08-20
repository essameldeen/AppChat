package com.example.toshiba.appchat.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.toshiba.appchat.Model.Message;
import com.example.toshiba.appchat.Model.UserData;
import com.example.toshiba.appchat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.viewHolder> {
        private List<Message> allMessage;
        private Context context;
        private DatabaseReference userReferees;

    public AdapterMessage(List<Message> allMessage, Context context) {
        this.allMessage = allMessage;
        this.context = context;
        userReferees= FirebaseDatabase.getInstance().getReference().child("Users");
    }


    @Override
    public AdapterMessage.viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_single_message,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdapterMessage.viewHolder holder, int position) {

        if(allMessage.get(position).getType().equals("text")){
            holder.tv_message.setText(allMessage.get(position).getMessage());
            holder.iv_image.setVisibility(View.INVISIBLE);
        }else {
            holder.tv_message.setVisibility(View.INVISIBLE);
            Picasso.get().load(allMessage.get(position).getMessage()).placeholder(R.drawable.profile).into(holder.iv_image);
        }


        userReferees.child(allMessage.get(position).getFrom()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData userData = dataSnapshot.getValue(UserData.class);
                holder.tv_name.setText(userData.getName());
                Picasso.get().load(userData.getImage_thumb()).placeholder(R.drawable.profile).into(holder.civ_profile);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    public int getItemCount() {
        return  allMessage.size();
    }

    public  class  viewHolder extends RecyclerView.ViewHolder{
           TextView tv_message;
           TextView tv_name;
           ImageView iv_image;
           CircleImageView civ_profile;
    public viewHolder(View itemView) {
        super(itemView);
        tv_message=(TextView)itemView.findViewById(R.id.tv_singleMessage_message);
        civ_profile=(CircleImageView)itemView.findViewById(R.id.civ_singleMessage_userProfile);
        tv_name=(TextView)itemView.findViewById(R.id.tv_singleMessage_name);
        iv_image=(ImageView)itemView.findViewById(R.id.iv_singleMessage_image);

    }
}
}

