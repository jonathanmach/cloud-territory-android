package net.cloudterritory.cloudterritory;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.cloudterritory.cloudterritory.model.Contact;
import net.cloudterritory.cloudterritory.model.DBHelper;
import net.cloudterritory.cloudterritory.model.MessageHistory;
import net.cloudterritory.cloudterritory.model.MessagesAdapter;
import net.cloudterritory.cloudterritory.others.SessionManager;
import net.cloudterritory.cloudterritory.others.UserRoles;

import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import java.sql.Timestamp;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactDetailFragment extends Fragment {

    private static int MIN_HISTORY_MSGS = 3;
    private static final String FRAGMENT_NAME = "Address Details";
    private MessagesAdapter meu_adapter;
    private EditText editText;
    private JSONObject response;
    private ArrayList<Contact> contact_array;
    Contact contact;
    private TextView vAdditional_info;
    private TextView vPhone_number;
    SessionManager session;

    public ContactDetailFragment() {
        // Required empty public constructor
    }

    private MapView mMapView;
    private MapController mMapController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        session = new SessionManager(getActivity());
        getActivity().setTitle(FRAGMENT_NAME);
        View view = inflater.inflate(R.layout.contactdetail_fragment2, container, false); // Inflate the layout for this fragment

        Bundle bundle = getArguments();

        contact = (Contact) bundle.getSerializable("contact");

        DBHelper dbHelper = new DBHelper(getActivity());    //Instancia para acesso ao DB
        contact.messagehistory = dbHelper.getMessagesFromContactId(contact.idContact);

        View viewHeader = getActivity().getLayoutInflater().inflate(R.layout.contactdetail_header, null);
        View viewFooter = getActivity().getLayoutInflater().inflate(R.layout.contactdetail_footer, null);

        //Load the custom adapter
        // http://www.codelearn.org/android-tutorial/android-listview (Working with Listviews)
        if (contact.messagehistory == null) {
            /* TODO: Handle null message object Contact*/
        } else {
            meu_adapter = new MessagesAdapter(contact.messagehistory, getActivity());

            final ListView messageListView = (ListView) view.findViewById(R.id.listViewMessages);
            messageListView.addHeaderView(viewHeader);
            messageListView.setAdapter(meu_adapter);


            //Adiciona o botão View More, caso exista algum historico
            if (contact.messagehistory.size() > MIN_HISTORY_MSGS) {
                messageListView.addFooterView(viewFooter);
            }

        }
        //View viewHeader = inflater.inflate(R.layout.contactdetail_header, container, false); // Inflate the layout for this fragment

        TextView vTerritory_name = (TextView) viewHeader.findViewById(R.id.vterritoryname);
        View viewContactName = viewHeader.findViewById(R.id.ln_nameLinearLayout);
        TextView vContact_name = (TextView) viewHeader.findViewById(R.id.vcontact_name);

/**  Nome e Telefone foram removidos do projeto:

         TextView vContact_name = (TextView) viewHeader.findViewById(R.id.vcontact_name);
         vPhone_number = (TextView) viewHeader.findViewById(R.id.phone_number);
         LinearLayout linearLayout_Phone = (LinearLayout) viewHeader.findViewById(R.id.ll_phone_number);
         linearLayout_Phone.setOnLongClickListener(onPhoneLongClickListener);
 */

        vAdditional_info = (TextView) viewHeader.findViewById(R.id.additional_obs);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);   //http://stackoverflow.com/questions/24451026/android-l-floating-action-button-fab/24548910#24548910
        fab.setOnClickListener(onFabClickListener);

        // (19/11) Apenas usuarios com permissao poderão editar // (26/10) As referencias nao são mais editáveis:
        if (session.getIdUserGroup() != UserRoles.PUBLISHER){
        vAdditional_info.setOnLongClickListener(OnAdditionalInfoLongClickListener);}

        final TextView tvViewAllOrHide = (TextView) viewFooter.findViewById(R.id.tvViewAllOrHide);

        ImageButton zoomButtom = (ImageButton) viewHeader.findViewById(R.id.zoomButton);
        zoomButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment mapViewFragment = new MapViewFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, mapViewFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        viewFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (meu_adapter.getAll) {
                    meu_adapter.getAll = false;
                    meu_adapter.notifyDataSetChanged();
                    tvViewAllOrHide.setText("View more...");

                } else {
                    meu_adapter.getAll = true;
                    meu_adapter.notifyDataSetChanged();
                    tvViewAllOrHide.setText("View less...");
                }
            }
        });

        vTerritory_name.setText(contact.address);
/*
        vContact_name.setText(contact.name);
        // Phone Number
        if (contact.phone == null) {
            vPhone_number.setText("(Tap and hold to update phone)");
            vPhone_number.setTextSize(11);
        } else {
            vPhone_number.setText(contact.phone);
        }
*/

        if (contact.isFollowed)
        {
            viewContactName.setVisibility(View.VISIBLE);
            vContact_name.setText(contact.name);

        }

        //Additional Information
        if (contact.additional_info == null | contact.additional_info.equals("")) {
            vAdditional_info.setText("(There are no references to this address.)");
        } else {
            vAdditional_info.setText(contact.additional_info);
        }


        mMapView = (MapView) viewHeader.findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setMultiTouchControls(true);
        mMapView.setMinZoomLevel(16);
        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(18);
        GeoPoint gPt = new GeoPoint(-23.5784, -46.4078);
        mMapController.setCenter(gPt);

        TextView openInGoogleMaps = (TextView) viewHeader.findViewById(R.id.openInGMaps);
        if (contact.address == null) {
            openInGoogleMaps.setVisibility(View.INVISIBLE);
            } else {
            openInGoogleMaps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String map = "http://maps.google.co.in/maps?q=" + contact.address; //TODO: Hard-coded
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                    startActivity(i);

                }
            });
        }


        return view;
    }

    View.OnClickListener handlerTest = new View.OnClickListener() {
        public void onClick(View v) {
            DBHelper dbHelper = new DBHelper(getActivity());
            dbHelper.deleteAllData();
        }

    };

    View.OnLongClickListener OnAdditionalInfoLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(final View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Update additional information:");

// Set up the input
            final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            input.setSingleLine(false);
            input.setText(contact.additional_info);
            builder.setView(input);
// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m_Text = input.getText().toString();
                    if (!m_Text.equals("")) {
                        contact.additional_info = m_Text;
                        //Update info in the database
                        DBHelper dbHelper = new DBHelper(getActivity());
                        dbHelper.updateContactAdditionalInfo(contact);
                        //Update the fragment view
                        vAdditional_info.setText(m_Text);
                        dialog.dismiss();
                    }
                    //TODO: Else
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
            return true;
        }
    };

    View.OnClickListener onFabClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


            if (contact.sync_status == 2) //TODO: Hardcoded status
            {
                Toast.makeText(getActivity(), "Please, sync the new address before adding a feedback.", Toast.LENGTH_LONG).show();
                //TODO: Permitir adicionar feedback em novos endereços
                return;
            }

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            final View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
            ListView lv = (ListView) promptView.findViewById(R.id.listView);
            editText = (EditText) promptView.findViewById(R.id.editText);
            lv.setOnItemClickListener(onItemClickListener);

            builder.setTitle("Add a new history:");
            builder.setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            if (editText.getText().toString().equals(""))   //Ignora se nao houver texto
                            {
                                Toast.makeText(getActivity(), "No option has been selected.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            MessageHistory msg_object = new MessageHistory();
                            msg_object.message = editText.getText().toString();
                            msg_object.idContactFK = contact.idContact;
                            msg_object.idUserFK = session.getUserID();
                            msg_object.nameUserFK = session.getUserFullName();
                            msg_object.dateTime = new Timestamp(System.currentTimeMillis());
                            DBHelper dbHelper = new DBHelper(getActivity());
                            dbHelper.insertNewMessageHistory(msg_object); //Adiciona messagem ao banco
                            contact.messagehistory.add(0,msg_object); //msg deve ir para o topo da lista.
                            meu_adapter.notifyDataSetChanged();

                            if (isNetworkOnline()) {
                                //syncJustAddedMsg();//TODO: Implement sync single msg
                            }
                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            builder.setView(promptView);
            builder.show();
        }

            AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    editText.setText(parent.getItemAtPosition(position).toString());
                    //Posicionar cursor para o final da linha
                    //editText.setSelection(editText.getText().length());
//                  Toast.makeText(view.getContext(), "onItemClick", Toast.LENGTH_SHORT).show();
                }
            };
        };

    View.OnLongClickListener onPhoneLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Update Phone Number:");
            // Set up the input
            final EditText input = new EditText(v.getContext());
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_PHONE);
            input.setText(contact.phone);
            builder.setView(input);
            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m_Text = input.getText().toString();
                    if(!m_Text.equals(""))
                    {
                        contact.phone = m_Text;
                        //Update info in the database
                        DBHelper dbHelper = new DBHelper(getActivity());
                        dbHelper.updateContactPhoneNumber(contact);
                        //Update the fragment view
                        vPhone_number.setText(m_Text);
                        dialog.dismiss();
                    }
                    //TODO: Else
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
            return true;
        }
    };
        private boolean isNetworkOnline() {
            return true; //TODO:!!! Implement a network validation
        }


    }
