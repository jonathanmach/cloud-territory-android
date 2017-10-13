package net.cloudterritory.cloudterritory;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.cloudterritory.cloudterritory.model.Contact;
import net.cloudterritory.cloudterritory.model.AddressRecyclerViewAdapter;
import net.cloudterritory.cloudterritory.model.ContactRecyclerViewAdapter;
import net.cloudterritory.cloudterritory.model.DBHelper;
import net.cloudterritory.cloudterritory.model.RecyclerItemClickListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyListFragment extends Fragment {

    private static final String FRAGMENT_NAME = "My List";
    private ContactRecyclerViewAdapter meu_adapter;
    private ArrayList<Contact> contact_array;
    public MyListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(FRAGMENT_NAME);
        View view = inflater.inflate(R.layout.watched_fragment, container, false); // Inflate the layout for this fragment
        view.setBackgroundColor(Color.WHITE); //http://vivekdubey.com/strange-fragment-overlapping-issue-in-android-lolipop/


        DBHelper dbHelper = new DBHelper(getActivity());    //Instancia para acesso ao DB
        contact_array = dbHelper.getWatchedContacts(getActivity());

        //Load the custom adapter
        RecyclerView recList = (RecyclerView) view.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        meu_adapter = new ContactRecyclerViewAdapter(contact_array);
        recList.setAdapter(meu_adapter);

        recList.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        Contact contact = (Contact) meu_adapter.getItem(position);

                        ContactDetailFragment new_fragment = new ContactDetailFragment();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out);
                        fragmentTransaction.replace(R.id.frame, new_fragment);
                        fragmentTransaction.addToBackStack(null);

                        Bundle bundle = new Bundle();      // Passing objects through fragments
                        bundle.putSerializable("contact", contact);
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
    View.OnClickListener handlerTest = new View.OnClickListener()
    {        public void onClick(View v) {
            DBHelper dbHelper = new DBHelper(getActivity());
            dbHelper.deleteAllData();

        }

    };

}
