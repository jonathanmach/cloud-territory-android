//http://www.android4devs.com/2014/12/how-to-make-material-design-app.html
// How To Make Material Design App Bar/ActionBar and Style It
package net.cloudterritory.cloudterritory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.cloudterritory.cloudterritory.model.CityZone;
import net.cloudterritory.cloudterritory.model.Contact;
import net.cloudterritory.cloudterritory.model.DBHelper;
import net.cloudterritory.cloudterritory.model.JSONUtil;
import net.cloudterritory.cloudterritory.model.MessageHistory;
import net.cloudterritory.cloudterritory.model.MyFollowingList;
import net.cloudterritory.cloudterritory.model.Territory;
import net.cloudterritory.cloudterritory.model.URLsClass;
import net.cloudterritory.cloudterritory.others.SessionManager;
import net.cloudterritory.cloudterritory.others.CTJsonObjectRequest;
import net.cloudterritory.cloudterritory.others.UserRoles;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//    Since the version 22.1.0, the class ActionBarActivity is deprecated. You should use AppCompatActivity.
public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private static final String TAG = "MainActivity";
    ProgressDialog pDialog;
    HomeFragment fragment;
    SessionManager session;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        session = new SessionManager(context);
        session.checkLogin();

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);                  // Setting toolbar as the ActionBar with setSupportActionBar() call


        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        ((TextView) findViewById(R.id.username)).setText(session.getUserDetails().get(SessionManager.KEY_NAME));
        ((TextView) findViewById(R.id.congregation)).setText(session.getUserDetails().get(SessionManager.KEY_CONGREGATION));
        if (session.getIdUserGroup() == UserRoles.PUBLISHER)
        {
            navigationView.getMenu().setGroupVisible(R.id.adm_menu,false);
        }



        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();
                FragmentTransaction fragmentTransaction;
                fragmentTransaction = getSupportFragmentManager().beginTransaction();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.home:
                        //Toast.makeText(getApplicationContext(), "Inbox Selected", Toast.LENGTH_SHORT).show();
                        //HomeFragment fragment = new HomeFragment();
                        fragmentTransaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out);
                        fragmentTransaction.replace(R.id.frame, fragment);
                        fragmentTransaction.addToBackStack("HOME");
                        fragmentTransaction.commit();
                        return true;
                    case R.id.watch:
                        MyListFragment myListFragment = new MyListFragment();
                        fragmentTransaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out);
                        fragmentTransaction.replace(R.id.frame, myListFragment);
                        fragmentTransaction.addToBackStack("WATCH");
                        fragmentTransaction.commit();
                        return true;
                    // For rest of the options we just show a toast on click
                    case R.id.assign:
                        AssignFragment assignFragment = new AssignFragment();
                        fragmentTransaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out);
                        fragmentTransaction.replace(R.id.frame, assignFragment);
                        fragmentTransaction.addToBackStack("ASSIGN");
                        fragmentTransaction.commit();
                        return true;
                    case R.id.add_address:
                        AddAddressFragment addAddressFragment = new AddAddressFragment();
                        fragmentTransaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out);
                        fragmentTransaction.replace(R.id.frame, addAddressFragment);
                        fragmentTransaction.addToBackStack("ADDADDRESS");
                        fragmentTransaction.commit();
                        return true;
                    case R.id.event_track:
                        EventLogFragment eventLogFragment = new EventLogFragment();
                        fragmentTransaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out);
                        fragmentTransaction.replace(R.id.frame, eventLogFragment);
                        fragmentTransaction.addToBackStack("EVENTLOG");
                        fragmentTransaction.commit();
                        return true;
                    case R.id.lougout:
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Confirmation");
                        builder.setMessage("Are you sure you want to logout?");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        session.logoutUser();
                                        finish();
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
                        return true;
                    default:
                        Toast.makeText(context, "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();


        //AssignFragment fragment = new AssignFragment();
        fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

/*      Abaixo: Settings no canto superior direito
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
*/
        if (id == R.id.action_refresh) {

            pDialog = new ProgressDialog(MainActivity.this);

            //No need to use asyncTask because volley already creates a new thread
            //SyncPhone2Web syncPhone2Web = new SyncPhone2Web();
            //syncPhone2Web.execute();

            FragmentTransaction fragmentTransaction;
            JSONObject localData= getAllLocalData2beSync();
            try {
                callSyncAPI(localData);

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_LONG).show();
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private JSONObject getAllLocalData2beSync() {

        JSONObject jsonObject= new JSONObject();
        // TIME TO SYNC THE PHONE DATA BACK TO SERVER
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        // History Messages
        ArrayList messagesToSync = dbHelper.getMessagesToSync();    //Get new messages to sync //Todo: also handle deleted msgs
        ArrayList newContactsToSync = dbHelper.getNewContactsToSync();    //Get new Contacts to sync
        MyFollowingList myFollowingList = dbHelper.getLocalNewFollowedContacts();
        ArrayList localContactChanges = dbHelper.getLocalContactChangesToSync();  // Get Contact changes (currently not used 27/10)

        try {
            if (messagesToSync.size() > 0){ //Se houver NOVAS MENSAGENS...
            jsonObject.put("phone_changes", JSONUtil.Messages2JSON(messagesToSync));}
            if (newContactsToSync.size() > 0){ //Se houver NOVOS ENDERECOS...
            jsonObject.put("new_contacts", JSONUtil.Contacts2JSON(newContactsToSync));}
            if (localContactChanges.size() > 0){ //Se houver Local Changes (References)...
                jsonObject.put("contact_updates", JSONUtil.Contacts2JSON(localContactChanges));}
            if (myFollowingList.following_array.size() > 0){ //Se houver contatos adicionados a MY LIST...
                jsonObject.put("myFollowingList", JSONUtil.FollowedContacts2JSON(myFollowingList));
            }

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_LONG).show();
        }
        return jsonObject;

    }

    private void callSyncAPI(JSONObject jsonObject) throws JSONException {
        Log.i(TAG, "Iniciando a sincronizacao!");
        String URL_STRING = URLsClass.URL_2WAY_SYNC;

        JSONObject localChangesJSONObject;
        localChangesJSONObject = jsonObject;

        //TODO: check if there is internet conn or not
        CTJsonObjectRequest jsObjRequest = new CTJsonObjectRequest
                (context, Request.Method.POST, URL_STRING, localChangesJSONObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Passar o JSONObject e receber um array de Territory.
                        JSONUtil jsonUtil = new JSONUtil();
                        jsonUtil.syncAllUserData(response,getApplicationContext());
                        //syncAllUserData(response);
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Your data has been synchronized", Toast.LENGTH_SHORT).show();
                        fragment = new HomeFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();

/*  Removi do if, para evitar casos de exibição de dados não sincronizados após uma atualização à partir de outro fragmento
                        if (fragment.isVisible())
                        {
                            fragment = new HomeFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
                        }
*/
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(), ""+error, Toast.LENGTH_LONG).show();
                    }
                });

        pDialog.setMessage("Syncing...");
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(true);

        pDialog.show();
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 5, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(jsObjRequest);

    }

    private class SyncPhone2Web extends AsyncTask<String, String, String> {



    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
        protected String doInBackground(String... params) {

            return null;
        }
    }


}


