package net.cloudterritory.cloudterritory;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.cloudterritory.cloudterritory.model.CityZone;
import net.cloudterritory.cloudterritory.model.EventLogAdapter;
import net.cloudterritory.cloudterritory.model.JSONUtil;
import net.cloudterritory.cloudterritory.model.Territory;
import net.cloudterritory.cloudterritory.model.TerritoryRecyViewAdapter;
import net.cloudterritory.cloudterritory.model.URLsClass;
import net.cloudterritory.cloudterritory.model.User;
import net.cloudterritory.cloudterritory.others.CTJsonObjectRequest;
import net.cloudterritory.cloudterritory.others.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventLogFragment extends Fragment {

    private static final String FRAGMENT_NAME = "Event Logs";
    TerritoryRecyViewAdapter meu_adapter;
    AlertDialog ad;
    Context context;
    SessionManager session;
    ListView listView_EventLog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(FRAGMENT_NAME);
        context = getActivity();
        session = new SessionManager(context);
        session.checkLogin();

        View view = inflater.inflate(R.layout.eventlog_fragment, container, false); // Inflate the layout for this fragment
        view.setBackgroundColor(Color.WHITE); //http://vivekdubey.com/strange-fragment-overlapping-issue-in-android-lolipop/

        listView_EventLog = (ListView) view.findViewById(R.id.listView_EventLog);


        getEventLog();

        return view;
    }

    private void getEventLog() {

        String url = URLsClass.URL_GET_EVENTLOGS;
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading event logs...");
        pDialog.show();

        CTJsonObjectRequest jsObjRequest = new CTJsonObjectRequest
                (context, Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Passar o JSONObject e receber um array de Territory.
                        pDialog.hide();
                        JSONArray jsonArray = null;
                        String[] strings = new String[0];
                        try {
                            jsonArray = response.getJSONArray("eventlog");
                            strings = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                strings[i] = (String) jsonArray.get(i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        EventLogAdapter adapter = new EventLogAdapter(strings, context);
                        listView_EventLog.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        System.out.print(error.toString());

                    }
                });
        Volley.newRequestQueue(getActivity()).add(jsObjRequest);
    }



    public EventLogFragment() {
        // Required empty public constructor
    }


}
