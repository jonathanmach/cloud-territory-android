package net.cloudterritory.cloudterritory;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.cloudterritory.cloudterritory.model.CityZone;
import net.cloudterritory.cloudterritory.model.JSONUtil;
import net.cloudterritory.cloudterritory.model.Territory;
import net.cloudterritory.cloudterritory.model.TerritoryRecyViewAdapter;
import net.cloudterritory.cloudterritory.model.URLsClass;
import net.cloudterritory.cloudterritory.model.User;
import net.cloudterritory.cloudterritory.others.CTJsonObjectRequest;
import net.cloudterritory.cloudterritory.others.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AssignFragment extends Fragment implements TerritoryRecyViewAdapter.ClickListener, TerritoryRecyViewAdapter.LongClickListener {

    private static final String FRAGMENT_NAME = "Assign Territories";
    TerritoryRecyViewAdapter meu_adapter;
    AlertDialog ad;
    Context context;
    SessionManager session;
    CityZone selected_cityZone;
    View territory_view;


    @Override
    public void onResume() {
        super.onResume();
        if (!(meu_adapter == null) & AssignFragment.this.isVisible()) {
            recyclerView.setAdapter(meu_adapter); // It fixes the issue: No adapter attached; skipping layout.
        }
        else {
            promptCityZone();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(FRAGMENT_NAME);
        context = getActivity();
        session = new SessionManager(context);
        session.checkLogin();


        territory_view = inflater.inflate(R.layout.assign_fragment, container, false); // Inflate the layout for this fragment
        territory_view.setBackgroundColor(Color.WHITE); //http://vivekdubey.com/strange-fragment-overlapping-issue-in-android-lolipop/



        territoryArrayList = new ArrayList<>();
        //Load the custom adapter
        recyclerView = (RecyclerView) territory_view.findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
/*
        meu_adapter = new TerritoryRecyViewAdapter(territoryArrayList);
        recyclerView.setAdapter(meu_adapter);
*/
        return territory_view;
    }

    private void promptCityZone() {

        String url = URLsClass.URL_GET_CITYZONES;
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading City Zones...");
        pDialog.show();

        CTJsonObjectRequest jsObjRequest = new CTJsonObjectRequest
                (context, Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Passar o JSONObject e receber um array de Territory.
                        pDialog.hide();
                        processResponseCityZone(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        // TODO Auto-generated method stub
                        Toast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(getActivity()).add(jsObjRequest);
    }

    @Override
    public void itemLongClicked(View view, int position) {

        selected_territory = meu_adapter.getItem(position);
        final CharSequence options[];
        if (selected_territory.assigned_user.idUser == 0) //Revoke only when there is an user added
        { options = new CharSequence[]{"Assign"};
        }
        else {options = new CharSequence[]{"Revoke", "Assign"}; }
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Select an option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (options[which] == "Revoke") { //Revoke
                    revokeTerritory(selected_territory);

                }
                if (options[which] == "Assign") { //Assign
                    getUserList();
                    //Call UserList activity
//                    Intent intent = new Intent(view.getContext(), MainActivity.class);
//                    startActivityForResult(intent, 2); // Activity is started with requestCode 2
                }
            }
        });
        builder.show();

    }

    private void getUserList() {

        String url = URLsClass.URL_GET_USERLIST_API;
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading all users...");
        pDialog.show();

        CTJsonObjectRequest jsObjRequest = new CTJsonObjectRequest
                (context, Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        System.out.print(response.toString());
                        //Passar o JSONObject e receber um array de Territory.
                        pDialog.hide();
                        processResponseUser(response);
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

    private void processResponseCityZone(JSONObject responseHandler) {

        if (responseHandler == null) {
            return;
        }
        //Decode CityZone
        final ArrayList<CityZone> cityZones = JSONUtil.decodeJSONCityZone(responseHandler);
        if (cityZones == null) {
            return;
        }
        ArrayAdapter<CityZone> adapter = new ArrayAdapter<CityZone>(context, android.R.layout.simple_list_item_1, cityZones) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setText(cityZones.get(position).name);
                return view;
            }
        };

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        final View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        promptView.findViewById(R.id.editText).setVisibility(View.GONE);
        promptView.findViewById(R.id.txtView_Options).setVisibility(View.GONE);
        ListView lv = (ListView) promptView.findViewById(R.id.listView);

        lv.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select a City Zone:");
        builder.setCancelable(true);
        builder.setView(promptView);
        ad = builder.show();
        lv.setOnItemClickListener(handler_onCityZoneClickItem);
    }

    private void processResponseUser(JSONObject responseHandler) {
//        lv = (ListView) findViewById(R.id.listView2);
        if (responseHandler == null) {
            return;
        }
        //Decode USERLIST
        final ArrayList<User> userlist = JSONUtil.decodeUserList(responseHandler);
        if (userlist == null) {
            return;
        }
        ArrayAdapter<User> adapter = new ArrayAdapter<User>(getActivity(), android.R.layout.simple_list_item_1, userlist) {
            @Override
            public Filter getFilter() {
                return super.getFilter();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                text1.setText(userlist.get(position).userFullName);
                return view;
            }
        };

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        final View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        ListView lv = (ListView) promptView.findViewById(R.id.listView);
        //final EditText editText = (EditText)promptView.findViewById(R.id.editText);

        lv.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select an user:");
        builder.setCancelable(false)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        builder.setView(promptView);
        ad = builder.show();
        lv.setOnItemClickListener(handler_onUserClickItem);
    }

    AdapterView.OnItemClickListener handler_onUserClickItem = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            ad.dismiss();
            User user = (User) parent.getItemAtPosition(position);
            TerritoryAssignmentAPI(selected_territory, user);
        }
    };

    AdapterView.OnItemClickListener handler_onCityZoneClickItem = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            ad.dismiss();
            CityZone cityZone = (CityZone) parent.getItemAtPosition(position);
            selected_cityZone = cityZone;
            loadTerritories(cityZone);
        }
    };

    Territory selected_territory;
    JSONObject response;
    RecyclerView recyclerView;
    ArrayList<Territory> territoryArrayList;

    public AssignFragment() {
        // Required empty public constructor
    }

    private void loadTerritories(CityZone cityZone) {
        //Load territories from API
        String url = URLsClass.URL_GET_TERRITORIES + "/" + cityZone._id;
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        CTJsonObjectRequest jsObjRequest = new CTJsonObjectRequest
                (context, Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        System.out.print(response.toString());
                        //Passar o JSONObject e receber um array de Territory.
                        territoryArrayList = JSONUtil.decodeJSONAllTerritories(response);
                        pDialog.hide();
/*
                        .setHasFixedSize(true);
                        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        .setLayoutManager(llm);
*/
                        meu_adapter = new TerritoryRecyViewAdapter(territoryArrayList);
                        recyclerView.setAdapter(meu_adapter);
                        meu_adapter.setClickListener(AssignFragment.this);
                        meu_adapter.setLongClickListener(AssignFragment.this);
                        meu_adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        pDialog.hide();
                        Toast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(getActivity()).add(jsObjRequest);
    }


    private void revokeTerritory(Territory territory) {
        String sourceUrl = URLsClass.URL_POST_TERRITORY_ASSIGNMENT_API + "?territoryID=" + territory._id + "&userID=null";
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
        CTJsonObjectRequest jsObjRequest = new CTJsonObjectRequest
                (context, Request.Method.POST, sourceUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //TODO: Verificar se a resposta e true or false
                        pDialog.hide();
                        loadTerritories(selected_cityZone);
                        Toast.makeText(getActivity(), "Territory Revoked", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        pDialog.hide();
                        Toast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT).show();
                    }
                });
        Volley.newRequestQueue(getActivity()).add(jsObjRequest);


    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2 && data != null) //Data sera null se o usuário voltar sem adicionar nada.
        {
            User selected_user = (User) data.getSerializableExtra("SelectedUser");
            TerritoryAssignmentAPI(selected_territory, selected_user);
        }
    }*/

    private void TerritoryAssignmentAPI(Territory selected_territory, User selected_user) {
        //TODO: Arrumar essa POG:
        String sourceUrl = URLsClass.URL_POST_TERRITORY_ASSIGNMENT_API
                + "?territoryID=" + selected_territory._id
                + "&userID=" + selected_user.idUser;
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        CTJsonObjectRequest jsObjRequest = new CTJsonObjectRequest
                (context, Request.Method.POST, sourceUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //TODO: Verificar se a resposta é true or false
                        pDialog.hide();
                        loadTerritories(selected_cityZone);
                        Toast.makeText(getActivity(), "User successfully assigned", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        pDialog.hide();
                        Toast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT).show();

                    }
                });
        Volley.newRequestQueue(getActivity()).add(jsObjRequest);

    }

    @Override
    public void itemClicked(View view, int position) {

        selected_territory = meu_adapter.getItem(position);
        downloadTerritoryInfo();
    }

    private void downloadTerritoryInfo() {
        String URL_STRING = URLsClass.URL_GET_TERRITORY + "/" + selected_territory._id;
        final ProgressDialog pDialog = new ProgressDialog(getActivity());

        //TODO: check if there is internet conn or not
        CTJsonObjectRequest jsObjRequest = new CTJsonObjectRequest
                (context, Request.Method.GET, URL_STRING, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Passar o JSONObject e receber um array de Territory.
                        JSONUtil jsonUtil = new JSONUtil();
                        jsonUtil.addTerritoryDetails2Database(response, context);
                        pDialog.dismiss();
                        goToTerritoryDetails();
                        //syncAllUserData(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        ad.dismiss();
                        Toast.makeText(context, "" + error, Toast.LENGTH_LONG).show();
                    }
                });

        pDialog.setMessage("Loading details...");
        pDialog.show();
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 5, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(jsObjRequest);
    }

    private void goToTerritoryDetails() {

        TerritoryInfoFragment new_fragment = new TerritoryInfoFragment();
        Bundle bundle = new Bundle();      // Passing objects through fragments
        bundle.putSerializable("territory_obj", selected_territory);
        new_fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.hide(this);
        fragmentTransaction.replace(R.id.frame, new_fragment);

        fragmentTransaction.commit();
    }
}
