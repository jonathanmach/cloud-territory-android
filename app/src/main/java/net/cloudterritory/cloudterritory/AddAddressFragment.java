package net.cloudterritory.cloudterritory;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import net.cloudterritory.cloudterritory.model.Contact;
import net.cloudterritory.cloudterritory.model.DBHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddAddressFragment extends Fragment {

    private static final String FRAGMENT_NAME = "Add new address";
    EditText m_Address;
    //EditText m_Phone;
    EditText m_AdditionalInfo;
    EditText m_Complement;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(FRAGMENT_NAME);
        View view = inflater.inflate(R.layout.addaddress_fragment, container, false); // Inflate the layout for this fragment

        m_Address = (EditText) view.findViewById(R.id.editText_Address);
        //m_Phone = (EditText) view.findViewById(R.id.editText_Phone);
        m_AdditionalInfo = (EditText) view.findViewById(R.id.editText_AdditionalInfo);
        m_Complement = (EditText) view.findViewById(R.id.editText_complement);


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);   //http://stackoverflow.com/questions/24451026/android-l-floating-action-button-fab/24548910#24548910
        fab.setOnClickListener(onFabClickListener);

         return view;
    }


    View.OnClickListener onFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {

            if (m_Address.getText().toString().equals(""))
            {
                Toast.makeText(v.getContext(), "Insert address to proceed.", Toast.LENGTH_SHORT).show();
            }
            else {

                final Contact new_contact = new Contact();

                //new_contact.phone = m_Phone.getText().toString();
                new_contact.address = m_Address.getText().toString();
                new_contact.additional_info = m_AdditionalInfo.getText().toString();
                new_contact.complement = m_Complement.getText().toString();

                //Display confirmation Dialog

                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Confirm the information");
                String address = "Address: "+ new_contact.address;
//                String phone = "";
                String complement = "";
                String additional = "";

                if (!new_contact.complement.equals(""))
                {   complement = '\n'+ new_contact.complement;}
/*                if (!new_contact.phone.equals(""))
                { phone = '\n'+"Phone: "+  new_contact.phone;}*/
                if (!new_contact.additional_info.equals(""))
                { additional = '\n' + "Reference: " + new_contact.additional_info; }

                builder.setMessage(address+complement+additional);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        builder.setTitle("Add a name to your contact:");
                        builder.setMessage(null);
                        final EditText input = new EditText(v.getContext());
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);
                        // Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String m_Text = input.getText().toString();
                                new_contact.name = m_Text;
                                // Insert Address into database
                                DBHelper dbHelper = new DBHelper(getActivity());
                                dbHelper.insertNewContact(new_contact);

                                dialog.dismiss();
                                //Jogar usuário para tela de Watching
                                MyListFragment myListFragment = new MyListFragment();
                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.frame, myListFragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
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
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                builder.create();
                builder.show();

            }

        }
    };

}
