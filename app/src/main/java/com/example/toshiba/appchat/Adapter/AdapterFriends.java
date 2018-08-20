package com.example.toshiba.appchat.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.toshiba.appchat.Model.UserData;
import com.example.toshiba.appchat.R;
import com.example.toshiba.appchat.activity_chat;
import com.example.toshiba.appchat.activity_userProfile;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFriends extends  RecyclerView.Adapter<AdapterFriends.viewHolder>  {
    private List<UserData> userData;
    private Context context;
    private  String userId;

    public AdapterFriends(List<UserData> userData, Context context) {
        this.userData = userData;
        this.context = context;
    }

    @Override
    public AdapterFriends.viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_user,parent,false);
        return new AdapterFriends.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdapterFriends.viewHolder holder, final int position) {
        holder.tv_name.setText(userData.get(position).getName());
        holder.tv_status.setText(userData.get(position).getStatus());
        Picasso.get().load(userData.get(position).getImage_thumb()).placeholder(R.drawable.profile).into(holder.iv_profile);
        userId=userData.get(position).UserId;
        if(userData.get(position).getOnline()==1){
            holder.online.setVisibility(View.VISIBLE);
        }else {
            holder.online.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence [] options= new CharSequence[]{"Open Profile","Send Message"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                builder.setTitle("Select Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            GotToProfileActivity();

                        }else if(i==1){
                            GoTpChatActivity(holder.tv_name.getText().toString());
                        }

                    }
                });
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return userData.size();
    }

    private void GoTpChatActivity(String name) {
        Intent chatActivity = new Intent(context,activity_chat.class);
        chatActivity.putExtra("user_id",userId);
        chatActivity.putExtra("user_name",name);
        context.startActivity(chatActivity);
    }

    private void GotToProfileActivity() {
        Intent profileActivity = new Intent(context,activity_userProfile.class);
        profileActivity.putExtra("user_id",userId);
        context.startActivity(profileActivity);
    }

    public class viewHolder extends  RecyclerView.ViewHolder{
        private TextView tv_name;
        private TextView tv_status;
        private CircleImageView iv_profile;
        private ImageView online;
        private View itemView;
        public viewHolder(View itemView) {
            super(itemView);
            this.itemView=itemView;
            tv_name=(TextView)itemView.findViewById(R.id.tv_nameSingleUser);
            tv_status=(TextView)itemView.findViewById(R.id.tv_status_singleUser);
            iv_profile=(CircleImageView)itemView.findViewById(R.id.iv_profileSingleUser);
            online=(ImageView)itemView.findViewById(R.id.iv_online_singleUser);

        }
    }
}
