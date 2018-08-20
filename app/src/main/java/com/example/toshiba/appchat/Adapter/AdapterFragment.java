package com.example.toshiba.appchat.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.toshiba.appchat.Fragment.fragment_chat;
import com.example.toshiba.appchat.Fragment.fragment_friends;
import com.example.toshiba.appchat.Fragment.fragment_requests;

public class AdapterFragment extends FragmentPagerAdapter {
    public AdapterFragment(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
    switch (position){
        case 0:
            fragment_requests requests=new fragment_requests();
            return requests;
        case 1:
            fragment_chat chat=new fragment_chat();
            return chat;
        case 2:
            fragment_friends fragment_friends=new fragment_friends();
            return fragment_friends;
            default:
                return null;
          }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "REQUESTS";
            case 1:
                return "CHATS";
            case 2:
                return "FRIENDS";
                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
