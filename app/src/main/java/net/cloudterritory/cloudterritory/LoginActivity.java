package net.cloudterritory.cloudterritory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import net.cloudterritory.cloudterritory.model.JSONUtil;
import net.cloudterritory.cloudterritory.model.URLsClass;
import net.cloudterritory.cloudterritory.others.CTJsonObjectRequest;
import net.cloudterritory.cloudterritory.others.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    Button login_button;
    // Session Manager Class
    SessionManager session;
    private int idUser;
    private String username;
    private String password;
    private String userfullname;
    private String email;
    private String create_time;
    private String profileName;
    private int userGroupID;
    private int idProfile;
    ProgressDialog pDialog;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = getApplicationContext();

        session = new SessionManager(context);

        login_button = (Button) findViewById(R.id.loginButton);
        final EditText ETusername = (EditText) findViewById(R.id.vUserName);
        final EditText ETpassword = (EditText) findViewById(R.id.vPassword);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = ETusername.getText().toString();
                password = ETpassword.getText().toString();

                checkLoginPassword(username, password);
            }
        });
    }

    private void checkLoginPassword(String username, String password) {

        String sourceUrl = URLsClass.URL_AUTHORIZATION;
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Authenticating...");
        pDialog.show();

        CTJsonObjectRequest jsObjRequest = new CTJsonObjectRequest
                (context, Request.Method.GET, sourceUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        process_response(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        pDialog.hide();
                        Toast.makeText(context, "" + error, Toast.LENGTH_LONG).show();
                    }
                });
        jsObjRequest.m_password = password;
        jsObjRequest.m_username = username;
        Volley.newRequestQueue(context).add(jsObjRequest);
    }

    private void process_response(JSONObject jsonObject) {
        /* Process the authentication API response */

        try {
            if (!jsonObject.has("user_info")) {
                Toast.makeText(context, jsonObject.getString("detail"), Toast.LENGTH_LONG).show();
                pDialog.hide();
            } else    //Authorization OK!
            {
                pDialog.setMessage("Downloading user data...");

                JSONObject userInfo = jsonObject.getJSONObject("user_info");

                idUser = userInfo.getInt("id");
                username = userInfo.getString("username");
                userfullname = userInfo.getString("userfullname");
                email = userInfo.getString("email");
                profileName = userInfo.getString("profile_name");
                userGroupID = userInfo.getInt("id_usergroup");
                idProfile = userInfo.getInt("id_profile");
                session.createLoginSession(idUser, userfullname, profileName, idProfile, userGroupID, password, username);
                // Load User data
                try {
                    callSyncAPI(null);
                } catch (JSONException e) {
                    Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();

        }
    }

    private void callSyncAPI(JSONObject jsonObject) throws JSONException {
        /* Download all user data */
        String URL_STRING = URLsClass.URL_2WAY_SYNC;

        JSONObject localChangesJSONObject = new JSONObject();
        localChangesJSONObject.put("data", jsonObject);
        //TODO: check if there is internet conn or not
        CTJsonObjectRequest jsObjRequest = new CTJsonObjectRequest
                (context, Request.Method.POST, URL_STRING, localChangesJSONObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Passar o JSONObject e receber um array de Territory.
                        JSONUtil jsonUtil = new JSONUtil();
                        pDialog.hide();
                        jsonUtil.syncAllUserData(response, getApplicationContext());
                        Toast.makeText(getApplicationContext(), "Your data has been synchronized", Toast.LENGTH_SHORT).show();
                        // Staring MainActivity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_LONG).show();
                    }
                });

        Volley.newRequestQueue(this).add(jsObjRequest);
    }
}