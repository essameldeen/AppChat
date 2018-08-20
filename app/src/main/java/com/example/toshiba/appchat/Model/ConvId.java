package com.example.toshiba.appchat.Model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class ConvId {
    @Exclude
    public String convID;

    public  <T extends  ConvId> T withid(@NonNull final String convID ){
        this.convID=convID;

        return (T)this;

    }


}
