package com.example.toshiba.appchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.toshiba.appchat.Model.UserData;
import com.example.toshiba.appchat.R;
import com.example.toshiba.appchat.activity_userProfile;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterAllUsers extends RecyclerView.Adapter<AdapterAllUsers.viewHolder> {

    private List<UserData> userData;
    private Context context;

    public AdapterAllUsers(List<UserData> userData, Context context) {
        this.userData = userData;
        this.context = context;
    }

    @Override
    public AdapterAllUsers.viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_user,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterAllUsers.viewHolder holder, final int position) {
          holder.tv_name.setText(userData.get(position).getName());
          holder.tv_status.setText(userData.get(position).getStatus());
          Picasso.get().load(userData.get(position).getImage_thumb()).placeholder(R.drawable.profile).into(holder.iv_profile);
           holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent goProfile=new Intent(context.getApplicationContext(),activity_userProfile.class);
                 goProfile.putExtra("user_id",userData.get(position).UserId);
                 context.startActivity(goProfile);

              }
         });
    }

    @Override
    public int getItemCount() {
        return userData.size();
    }
    public class viewHolder extends  RecyclerView.ViewHolder{
           private TextView tv_name;
           private TextView tv_status;
           private CircleImageView iv_profile;
           private View itemView;
        public viewHolder(View itemView) {
            super(itemView);
            this.itemView=itemView;
            tv_name=(TextView)itemView.findViewById(R.id.tv_nameSingleUser);
            tv_status=(TextView)itemView.findViewById(R.id.tv_status_singleUser);
            iv_profile=(CircleImageView)itemView.findViewById(R.id.iv_profileSingleUser);

        }
    }
}
