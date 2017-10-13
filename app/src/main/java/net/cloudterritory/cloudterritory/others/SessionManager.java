package net.cloudterritory.cloudterritory.others;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import net.cloudterritory.cloudterritory.LoginActivity;
import net.cloudterritory.cloudterritory.model.DBStmts;

import java.util.HashMap;

/**
 * Created by Jonathan on 06/08/15.
 */
public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    // Email address (make variable public to access from outside)
    public static final String KEY_CONGREGATION = "congregation";
    public static final String KEY_PROFILE = "idProfile";
    public static final String KEY_USERID = "userID";
    public static final String KEY_USERGROUPID = "idUserGroup";
    public static final String KEY_USERPASSWD = "userPassword";
    public static final String KEY_USERNAME = "username";


    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(int userID,String name, String congregation, int idProfile,int idUserGroup, String password, String username){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_CONGREGATION, congregation);
        editor.putInt(KEY_USERID, userID);
        editor.putInt(KEY_PROFILE, idProfile);
        editor.putInt(KEY_USERGROUPID, idUserGroup);
        editor.putString(KEY_USERPASSWD, password);
        editor.putString(KEY_USERNAME, username);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent intent = new Intent(_context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            _context.startActivity(intent);
        }
    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_CONGREGATION, pref.getString(KEY_CONGREGATION, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
        _context.deleteDatabase(DBStmts.DATABASE_NAME);

        Intent intent = new Intent(_context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        _context.startActivity(intent);

    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public int getUserID() {
        return  pref.getInt(KEY_USERID, -1);
    }

    public int getIdProfile() {
        return  pref.getInt(KEY_PROFILE, -1);
    }

    public int getIdUserGroup() {return pref.getInt(KEY_USERGROUPID, -1);}

    public String getUserFullName()
    {
        return pref.getString(KEY_NAME, null);
    }

    public String getUserPasswd()
    {
        return pref.getString(KEY_USERPASSWD,null);
    }

    public String getUsername()
    {
        return pref.getString(KEY_USERNAME,null);
    }
}
