package com.example.toshiba.appchat.Model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class UserId  {
   @Exclude
    public String UserId;

    public  <T extends  UserId> T withid(@NonNull final String userId ){
        this.UserId=userId;

        return (T)this;

    }


}
