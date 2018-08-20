package com.example.toshiba.appchat.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.toshiba.appchat.Adapter.AdapterAllUsers;
import com.example.toshiba.appchat.Adapter.AdapterFriends;
import com.example.toshiba.appchat.Model.UserData;
import com.example.toshiba.appchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_friends extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference referenceFriends;
    private DatabaseReference referenceUser;
    private FirebaseAuth auth;
    private List<UserData> dataList=new ArrayList<>();
    private AdapterFriends adapterFriends;


    public fragment_friends() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =inflater.inflate(R.layout.fragment_fragment_friends, container, false);
       //
       auth=FirebaseAuth.getInstance();
       referenceFriends= FirebaseDatabase.getInstance().getReference().child("Friends").child(auth.getCurrentUser().getUid());
       referenceFriends.keepSynced(true);
       referenceUser=FirebaseDatabase.getInstance().getReference().child("Users");
       referenceUser.keepSynced(true);
      //
       recyclerView=(RecyclerView)view.findViewById(R.id.fiendsFragmentList);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapterFriends=new AdapterFriends(dataList,getContext());
       recyclerView.setAdapter(adapterFriends);


       getDataFromServer();

        return view;
    }

    private void getDataFromServer() {

        referenceFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    String user_id= d.getKey();
                    getDataOfUser(user_id);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getDataOfUser(final String user_id) {
        dataList.clear();
        referenceUser.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserData   userData = dataSnapshot.getValue(UserData.class).withid(user_id);
                dataList.add(userData);
                adapterFriends.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
