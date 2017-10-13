package net.cloudterritory.cloudterritory;


import android.app.Activity;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.cloudterritory.cloudterritory.model.DBHelper;
import net.cloudterritory.cloudterritory.model.RecyclerItemClickListener;
import net.cloudterritory.cloudterritory.model.Territory;
import net.cloudterritory.cloudterritory.model.TerritoryRecyViewAdapter;
import net.cloudterritory.cloudterritory.model.URLsClass;
import net.cloudterritory.cloudterritory.others.CTJsonObjectRequest;
import net.cloudterritory.cloudterritory.others.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements TerritoryRecyViewAdapter.LongClickListener {

    private static final String FRAGMENT_NAME = "My Assignments";
    private static final String FRAGMENT_TAG = "HOME";
    TerritoryRecyViewAdapter meu_adapter;
    ListView lv_territory;
    JSONObject response;
    RecyclerView recList;
    SessionManager session;
    Territory selected_territory;
    Context context;
    ArrayList<Territory> territoryArrayList;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        getActivity().setTitle(FRAGMENT_NAME);
        View view = inflater.inflate(R.layout.home_fragment, container, false); // Inflate the layout for this fragment
        view.setBackgroundColor(Color.WHITE); //http://vivekdubey.com/strange-fragment-overlapping-issue-in-android-lolipop/

        DBHelper dbHelper = new DBHelper(context);    // Provides database access
        territoryArrayList= dbHelper.getRecentTerritories(context);   //Load territories from SQLiteDB


        //Load the custom adapter
        RecyclerView recList = (RecyclerView) view.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        meu_adapter = new TerritoryRecyViewAdapter(territoryArrayList);
        meu_adapter.setLongClickListener(HomeFragment.this);
        recList.setAdapter(meu_adapter);

        recList.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Territory territory = meu_adapter.getItem(position);

                        TerritoryInfoFragment new_fragment = new TerritoryInfoFragment();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out);
                        fragmentTransaction.replace(R.id.frame, new_fragment);
                        fragmentTransaction.addToBackStack(FRAGMENT_TAG);

                        Bundle bundle = new Bundle();      // Passing objects through fragments
                        bundle.putSerializable("territory_obj", territory);
                        new_fragment.setArguments(bundle);

                        fragmentTransaction.commit();

                    }
                })
        );



/*
        meu_adapter = new TerritoryListAdapter(territoryArrayList,getActivity());
        lv_territory = (ListView) view.findViewById(R.id.lvterritory);
        lv_territory.setAdapter(meu_adapter);

        TextView emptyText = (TextView) view.findViewById(android.R.id.empty);
        lv_territory.setEmptyView(emptyText);
*/

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void itemLongClicked(View view, final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        final String return_card = "Return Territory Card";
        selected_territory = meu_adapter.getItem(position);
        final String[] option_array = new String[1];
        option_array[0] = return_card;

        builder.setTitle("Select an option:");
        builder.setItems(option_array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
                switch (option_array[which]) {
                    case return_card:
                        builder.setTitle("Confirmation");
                        builder.setMessage("The card will be sent back to the SO. Are you sure you're done with this card?");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        returnTerritoryCard(selected_territory, position);
                                    }
                                }

                        );
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }
                        );
                        builder.show();
                }
            }
        });

        builder.show();
    }

    private void returnTerritoryCard(final Territory territory, final int position) {
        String sourceUrl = URLsClass.URL_POST_TERRITORY_ASSIGNMENT_API + "?territoryID=" + territory._id + "&userID=return";
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Returning Territory Card...");
        pDialog.show();
        CTJsonObjectRequest jsObjRequest = new CTJsonObjectRequest
                (context, Request.Method.POST, sourceUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //TODO: Verificar se a resposta e true or false
                        pDialog.hide();
                        meu_adapter.delete(position);
                        DBHelper dbHelper = new DBHelper(context);
                        dbHelper.deleteLocalTerritory(territory);
                        Toast.makeText(getActivity(), "Territory Returned", Toast.LENGTH_SHORT).show();
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
}
