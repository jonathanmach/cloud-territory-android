package net.cloudterritory.cloudterritory;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import net.cloudterritory.cloudterritory.model.Contact;
import net.cloudterritory.cloudterritory.model.AddressRecyclerViewAdapter;
import net.cloudterritory.cloudterritory.model.DBHelper;
import net.cloudterritory.cloudterritory.model.Territory;

import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TerritoryInfoFragment extends Fragment implements AddressRecyclerViewAdapter.ClickListener, AddressRecyclerViewAdapter.LongClickListener{

    private static final String FRAGMENT_NAME = "Territory Details";
    private MapView mMapView;
    private MapController mMapController;
    private AddressRecyclerViewAdapter meu_adapter;
    private ListView lv_territory;
    private JSONObject response;
    private ArrayList<Contact> contact_array;
    public TerritoryInfoFragment() {
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
        View view = inflater.inflate(R.layout.territoryinfo_fragment, container, false); // Inflate the layout for this fragment

        Bundle bundle = getArguments();

        Territory territory = (Territory) bundle.getSerializable("territory_obj");

        TextView txvTerritoryName = (TextView) view.findViewById(R.id.vterritoryname);
        txvTerritoryName.setText(territory.name);
        TextView txvTerritoryCityZone = (TextView) view.findViewById(R.id.vCityZone);
        txvTerritoryCityZone.setText(territory.city.name);

        mMapView = (MapView) view.findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setMultiTouchControls(true);



        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(100);

        GeoPoint gPt = new GeoPoint(-23.5784,-46.4078);
        mMapController.setCenter(gPt);

        DBHelper dbHelper = new DBHelper(getActivity());    //Instancia para acesso ao DB
        contact_array = dbHelper.getContactsFromTerritoryID(getActivity(), territory._id);

        //Load the custom adapter
        RecyclerView recList = (RecyclerView) view.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        meu_adapter = new AddressRecyclerViewAdapter(contact_array);
        meu_adapter.setClickListener(TerritoryInfoFragment.this);
        meu_adapter.setLongClickListener(TerritoryInfoFragment.this);
        recList.setAdapter(meu_adapter);

        return view;
    }

    @Override
    public void itemClicked(View view, int position) {

        Contact contact = (Contact) meu_adapter.getItem(position);

        ContactDetailFragment new_fragment = new ContactDetailFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in,
                R.anim.abc_fade_out);
        fragmentTransaction.replace(R.id.frame, new_fragment);
        fragmentTransaction.addToBackStack(null);

        Bundle bundle = new Bundle();      // Passing objects through fragments
        bundle.putSerializable("contact", contact);
        new_fragment.setArguments(bundle);

        fragmentTransaction.commit();

    }

    @Override
    public void itemLongClicked(View view, final int position) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        final String[] option_array = new String[1];
        option_array[0] = ("Add to My List");

        builder.setTitle("Select an option:");
        builder.setItems(option_array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
                switch (option_array[which]) {
                    case "Add to My List":
                        addAdress2FollowedContact(meu_adapter.getItem(position));
                }
            }
        });

        builder.show();
    }

    private void addAdress2FollowedContact(final Contact contact) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add a name to your contact:");
        builder.setMessage(null);
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        contact.name = m_Text;
                        // Insert Address into database
                        DBHelper dbHelper = new DBHelper(getActivity());
                        dbHelper.markAsFollowedContact(contact, false);

                        //TODO: Marcar flag de Followed Contact
                    }
                }

        );
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick (DialogInterface dialog,int which){
                        dialog.cancel();
                    }
                }
        );

        builder.show();

    }
}
