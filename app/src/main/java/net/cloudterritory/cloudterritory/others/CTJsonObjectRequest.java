package net.cloudterritory.cloudterritory.others;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jonathan on 16/11/15.
 */
public class CTJsonObjectRequest extends JsonObjectRequest {

    public String m_username;
    public String m_password;
    SessionManager session;
    Context context;

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        String creds;
        HashMap<String, String> params = new HashMap<String, String>();
        session = new SessionManager(context);
        if (m_username != null) { //First Login
            creds = String.format("%s:%s", m_username, m_password);
        } else { // User already logged in
            session.checkLogin();
            creds = String.format("%s:%s", session.getUsername(), session.getUserPasswd());
        }

        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        params.put("Authorization", auth);
        return params;

    }

    public CTJsonObjectRequest(Context context, int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {

        super(method, url, jsonRequest, listener, errorListener);
        this.context = context;

    }


}