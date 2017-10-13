package net.cloudterritory.cloudterritory.model;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Jonathan on 14/7/2015.
 */
public class JSONUtil {

    private static final String TAG = "JSONUtil";

    public static JSONArray Messages2JSON(ArrayList messageArray) throws JSONException {
        JSONArray jsonArray = new JSONArray();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //To Converting the DateTime

        if (messageArray.size()!= 0) {
            for (int i=0; i < messageArray.size(); i++) {
                JSONObject object = new JSONObject();
                MessageHistory messageHistory= (MessageHistory) messageArray.get(i);
                object.put("idGlobal", messageHistory.idGlobal);
                object.put("idMessage", messageHistory.idMessage);
                object.put("message", messageHistory.message);
                object.put("date", simpleDateFormat.format(messageHistory.dateTime));
                object.put("idContactFK", messageHistory.idContactFK);
                object.put("idUserFK", messageHistory.idUserFK);

                jsonArray.put(object);
            }
        }
        return jsonArray;
    }

    public static ArrayList<MessageHistory> decodeJSONMessages(JSONObject jsonObject) {

        ArrayList<MessageHistory> messageHistories = new ArrayList<>();
        // Create GSON object
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("messages");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //To Converting the DateTime
            if(jsonArray.length() != 0){ // If no of array elements is not zero
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < jsonArray.length(); i++) {
                    MessageHistory message = new MessageHistory();
                    // Get JSON object
                    JSONObject obj = (JSONObject) jsonArray.get(i);
                    message.idGlobal = obj.get("idGlobal").toString(); //Todo: Assumo que sempre retornará um ID.
                    message.message = handleNullString(obj, "message");
                    // message.dateTime = simpleDateFormat.parse(obj.get("creation_time").toString()); TODO: Parse correto da data
                    message.dateTime = simpleDateFormat.parse(obj.get("date").toString());
                    message.idContactFK = obj.getInt("idContactFK");
                    message.idUserFK = obj.getInt("idUserFK");
                    message.nameUserFK = handleNullString(obj, "userfullname");

                    messageHistories.add(message);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return messageHistories;

    }
    //Todo: Melhores práticas para esse método. Está certo fazer isso? Há uma maneira melhor?

    private static String handleNullString(JSONObject jsonObject, String key) throws JSONException {
        String string = null;
        if (!jsonObject.isNull(key))
        {
            string = jsonObject.get(key).toString();
        }
        return string;
    }

    public static ArrayList<User> decodeUserList(JSONObject jsonObject) {
        ArrayList<User> userArrayList = new ArrayList<>();
        // Create GSON object
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("userlist");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //To Converting the DateTime
            if(jsonArray.length() != 0){ // If no of array elements is not zero
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < jsonArray.length(); i++) {
                    User user = new User();
                    // Get JSON object
                    JSONObject obj = (JSONObject) jsonArray.get(i);
                    user.idUser = obj.getInt("idUser");
                    user.userName = handleNullString(obj, "username");
                    user.userFullName = handleNullString(obj, "userfullname");

                    userArrayList.add(user);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return userArrayList;

    }

    public static ArrayList<Territory> decodeJSONAllTerritories(JSONObject api_response) {
        //TODO: Decode a JSON object
        ArrayList<Territory> territoryArrayList = new ArrayList<>();
        // Create JSON object
        try {
            JSONObject jsonObject = api_response;
            JSONArray jsonArray = jsonObject.getJSONArray("territory");
            // If no of array elements is not zero
            if(jsonArray.length() != 0){
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < jsonArray.length(); i++) {
                    Territory territory = new Territory();
                    // Get JSON object
                    JSONObject obj = (JSONObject) jsonArray.get(i);
                    territory._id = Integer.parseInt(obj.get("idTerritory").toString());
                    territory.name = obj.get("TerritoryName").toString();
                    //territory.number = obj.getInt("number");
                    territory.city._id = obj.getInt("idCityZoneFK");
                    territory.city.name = obj.get("CityZoneName").toString();
                    if (!obj.isNull("idUser")) {    //Território não associado a nenhum publicador
                        territory.assigned_user.idUser = obj.getInt("idUser");
                        territory.assigned_user.userFullName = obj.get("userFullName").toString();
                    }
                    territoryArrayList.add(territory);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return territoryArrayList;
    }

    public static ArrayList<Territory> decodeJSON_UserTerritory(JSONObject jsonObject) {
        //TODO: Decode a JSON object
        ArrayList<Territory> territoryArrayList = new ArrayList<>();
        // Create JSON object
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("territories");
            // If no of array elements is not zero
            if(jsonArray.length() != 0){
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < jsonArray.length(); i++) {
                    Territory territory = new Territory();
                    JSONObject obj = (JSONObject) jsonArray.get(i);
                    territory._id = Integer.parseInt(obj.get("id").toString());
                    territory.name = obj.get("name").toString();
                    territory.number = handleNullInt(obj, "number");
                    territory.city._id = Integer.parseInt(obj.get("region_id").toString());

                    territoryArrayList.add(territory);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return territoryArrayList;
    }

    public static ArrayList<CityZone> decodeJSONCityZone(JSONObject jsonObject) {
        //TODO: Decode a JSON object
        ArrayList<CityZone> cityZoneArrayList = new ArrayList<>();
        // Create GSON object
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("regions");
            // If no of array elements is not zero
            if(jsonArray.length() != 0){
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < jsonArray.length(); i++) {
                    CityZone cityZone = new CityZone();
                    // Get JSON object
                    JSONObject obj = (JSONObject) jsonArray.get(i);
                    cityZone._id = Integer.parseInt(obj.get("id").toString());
                    cityZone.name = obj.get("name").toString();
                    cityZone.idProfileFK = Integer.parseInt(obj.get("profile_id").toString());

                    cityZoneArrayList.add(cityZone);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return cityZoneArrayList;
    }

    public static ArrayList<Contact> decodeJSONContacts(JSONObject jsonObject) {

        //TODO: Decode a JSON object
        ArrayList<Contact> contactArrayList = new ArrayList<>();
        // Create GSON object
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("contacts");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //To Converting the DateTime
            if(jsonArray.length() != 0){ // If no of array elements is not zero
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < jsonArray.length(); i++) {
                    Contact contact = new Contact();
                    // Get JSON object
                    JSONObject obj = (JSONObject) jsonArray.get(i);
                    contact.idContact = Integer.parseInt(obj.get("id").toString()); //Todo: Assumo que sempre retornará um ID.
                    //contact.name = handleNullString(obj, "name");
                    contact.address = handleNullString(obj, "address");
                    contact.complement = handleNullString(obj, "complement");
                    //contact.phone = handleNullString(obj, "phone");
                    try {
                        contact.creation_time = simpleDateFormat.parse(obj.get("creation_time").toString());
                        //contact.update_time = simpleDateFormat.parse(obj.get("update_time").toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    contact.additional_info = handleNullString(obj, "additional_info");
                    contact.territory._id = handleNullInt(obj, "territory_id");
                    contact.status = handleNullInt(obj, "status_id");
                    //contact.nationality = handleNullInt(obj, "idNationalityFK");

                    contactArrayList.add(contact);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return contactArrayList;
    }

    private static int handleNullInt(JSONObject jsonObject, String key) throws JSONException {
        int i = -1;
        if (!jsonObject.isNull(key))
        {
            i = Integer.parseInt(jsonObject.get(key).toString());
        }
        return i;
    }


    public static JSONArray Contacts2JSON(ArrayList arrayList) throws JSONException {

        JSONArray jsonArray = new JSONArray();

        if (arrayList.size()!= 0) {
            for (int i=0; i < arrayList.size(); i++) {
                JSONObject object = new JSONObject();
                Contact contact= (Contact) arrayList.get(i);
                object.put("idContact", contact.idContact);
                object.put("address", contact.address);
                object.put("name", contact.name);
                object.put("phone", contact.phone);
                object.put("additional_info", contact.additional_info);
                object.put("complement", contact.complement);

                jsonArray.put(object);
            }
        }
        return jsonArray;

    }

    public static JSONObject FollowedContacts2JSON(MyFollowingList myFollowingList) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if (myFollowingList.following_array.size()!= 0) {
            JSONArray jsonArray = new JSONArray();
            for (int i=0; i < myFollowingList.following_array.size(); i++) {
                JSONObject object = new JSONObject();
                Contact contact= myFollowingList.following_array.get(i);
                object.put("idContact", contact.idContact);
                object.put("name", contact.name);
                jsonArray.put(object);
            }
            jsonObject.put("idUser", myFollowingList.idUser);
            jsonObject.put("followed", jsonArray);
        }
        return jsonObject;
    }


    public void syncAllUserData(JSONObject api_response, Context context) {
        if (api_response != null) {
            JSONObject api_object = api_response;

            DBHelper dbHelper = new DBHelper(context);
            //TODO: Decode PROFILES

            Log.d(TAG, "Iniciando truncateAllData()");
            //TODO: Implement a better approach other then TRUNCATING Database Tables
            dbHelper.truncateAllData();         // media: 66ms - 77ms - BOM TEMPO!
            Log.d(TAG, "feito truncateAllData()");
            //Decode CITYZONES
            Log.d(TAG, "Iniciando Decodes ()");
            ArrayList<CityZone> decoded_cityzones  = JSONUtil.decodeJSONCityZone(api_object);
            dbHelper.updateCityZones(decoded_cityzones);
            Log.d(TAG, "Decode() CITYZONES OK ");
            //Decode TERRITORY
            ArrayList<Territory> decoded_territories = JSONUtil.decodeJSON_UserTerritory(api_object);
            dbHelper.updateTerritories(decoded_territories);     // Adiciona os dados ao SQLite
            Log.d(TAG, "Decode() TERRITORY OK ");
            //Decode CONTACTS
            ArrayList<Contact> decoded_contacts = JSONUtil.decodeJSONContacts(api_object);
            Log.d(TAG, "Decode() CONTACTS OK ");
            dbHelper.insertContacts(decoded_contacts);    // Adiciona os dados ao SQLite
            Log.d(TAG, "updateContacts() CONTACTS OK ");
            //Decode MESSAGES
            ArrayList<MessageHistory> decoded_messages = JSONUtil.decodeJSONMessages(api_object);
            dbHelper.updateMessages(decoded_messages);    // Adiciona os dados ao SQLite
            Log.d(TAG, "Decode() MESSAGES OK ");
            Log.d(TAG, "Done Decodes ()");
            //FOLLOWING CONTACTS
            dbHelper.insertFollowedContacts(api_object);    // Adiciona os dados ao SQLite
            Log.d(TAG, "Decode() FOLLOWED CONTACTS OK ");
            Log.d(TAG, "Done Decodes ()");

        }

    }

    public void addTerritoryDetails2Database(JSONObject api_response, Context context) {
        if (api_response != null) {

            DBHelper dbHelper = new DBHelper(context);

            ArrayList<Contact> decoded_contacts = JSONUtil.decodeJSONContacts(api_response);
            dbHelper.insertContacts(decoded_contacts);    // Adiciona os dados ao SQLite

            //Decode MESSAGES
            ArrayList<MessageHistory> decoded_messages = JSONUtil.decodeJSONMessages(api_response);
            dbHelper.updateMessages(decoded_messages);    // Adiciona os dados ao SQLite
        }
    }

}
