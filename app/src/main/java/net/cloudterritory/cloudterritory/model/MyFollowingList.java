package net.cloudterritory.cloudterritory.model;

import android.content.Context;

import net.cloudterritory.cloudterritory.others.SessionManager;

import java.util.ArrayList;

/**
 * Created by Jonathan on 27/10/15.
 */
public class MyFollowingList {

    public int idUser;
    public ArrayList<Contact> following_array;

    private SessionManager session;

    public MyFollowingList(Context context)
    {
        session = new SessionManager(context);
        session.checkLogin();
        this.idUser = session.getUserID();
        this.following_array = new ArrayList<>();
    }
}
