package net.cloudterritory.cloudterritory.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Jonathan on 20/6/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = DBStmts.DATABASE_NAME;
    private Context context;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //To Converting the DateTime

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(DBStmts.TBCREATE_PROFILE);
            db.execSQL(DBStmts.TBCREATE_CITYZONE);
            db.execSQL(DBStmts.TBCREATE_TERRITORY);
            db.execSQL(DBStmts.TBCREATE_CONTACTSTATUS);
            db.execSQL(DBStmts.TBCREATE_NATIONALITY);
            db.execSQL(DBStmts.TBCREATE_CONTACT);
            db.execSQL(DBStmts.TBCREATE_HISTORYMESSAGES);
            db.execSQL(DBStmts.TBCREATE_FOLLOWEDCONTACTS);

            //Toast.makeText(context, "Database Created", Toast.LENGTH_LONG).show();
            //addSomeFakeData(db);
            //ToastMessage.ToastMessage(this.context, "Fake Data Inserted");
        } catch (SQLException e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }

    public ArrayList<Territory> getAllTerritories(Context context) {

        this.context = context;
        ArrayList contact_array = new ArrayList();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        try {
            Cursor resultSet = sqLiteDatabase.rawQuery("Select * from tbTerritory;", null);
            resultSet.moveToFirst();
            while (resultSet.isAfterLast() == false) {
                Territory territory = new Territory();
                territory._id = resultSet.getInt((resultSet.getColumnIndex("idTerritory")));
                territory.number = resultSet.getInt((resultSet.getColumnIndex("number")));
                territory.name = resultSet.getString((resultSet.getColumnIndex("name")));
                territory.city._id = resultSet.getInt((resultSet.getColumnIndex("idCityZoneFK"))); //TODO: Link with the c
                contact_array.add(territory);
                resultSet.moveToNext();
            }


        } catch (Exception e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        }

        return contact_array;
    }

    private void addSomeFakeData(SQLiteDatabase db) {

/*        db.execSQL("insert into tbTerritory values (1, 'Rua dos Texteis', 12, 'Cidade Tiradentes');");
        db.execSQL("insert into tbTerritory values (2, 'Centro', 41, 'Guaianazes');");
        db.execSQL("insert into tbTerritory values (3, 'Gianneti', 65, 'Ferraz de Vasconcelos');");
        db.execSQL("insert into tbTerritory values (4, 'Jardim Fanganiello', 59, 'Ferraz de Vasconcelos');");*/
        db.execSQL("insert into tbTerritory values (5, 'Dados do SQLite!', 102, 1, 1);");
    }

    //DELETES
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE if exists " + DBStmts.TBNAME_PROFILE);
        db.execSQL("DROP TABLE if exists " + DBStmts.TBNAME_CITYZONE);
        db.execSQL("DROP TABLE if exists " + DBStmts.TBNAME_TERRITORY);
        db.execSQL("DROP TABLE if exists " + DBStmts.TBNAME_CONTACTSTATUS);
        db.execSQL("DROP TABLE if exists " + DBStmts.TBNAME_NATIONALITY);
        db.execSQL("DROP TABLE if exists " + DBStmts.TBNAME_HISTORYMESSAGES);
        db.execSQL("DROP TABLE if exists " + DBStmts.TBNAME_CONTACT);
        db.execSQL("DROP TABLE if exists " + DBStmts.TBNAME_FOLLOWEDCONTACTS);
        onCreate(db);
    }

    public void truncateAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM  " + DBStmts.TBNAME_PROFILE);
        db.execSQL("DELETE FROM  " + DBStmts.TBNAME_CITYZONE);
        db.execSQL("DELETE FROM  " + DBStmts.TBNAME_TERRITORY);
        db.execSQL("DELETE FROM  " + DBStmts.TBNAME_CONTACTSTATUS);
        db.execSQL("DELETE FROM  " + DBStmts.TBNAME_NATIONALITY);
        db.execSQL("DELETE FROM  " + DBStmts.TBNAME_HISTORYMESSAGES);
        db.execSQL("DELETE FROM  " + DBStmts.TBNAME_CONTACT);
        db.execSQL("DELETE FROM  " + DBStmts.TBNAME_FOLLOWEDCONTACTS);
    }

    public void deleteLocalTerritory(Territory territory) {
/*
        Usado quando usuario faz o Return do Territory Card. A versão local do Território é apagado. No próximo
         sync, não será sincronizado.
*/
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "idTerritory = " + territory._id;

        db.delete(DBStmts.TBNAME_TERRITORY, whereClause, null);
    }

    //SELECTS
    public ArrayList getNewContactsToSync() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor resultSet = sqLiteDatabase.rawQuery("Select * from tbContact where sync_flag = 2 order by creation_time desc", null); // 2=new Contact, not synced TODO:Hardcoded value
        ArrayList contactArray = new ArrayList();
        if (resultSet.getCount() > 0) {
            contactArray = extractContactObject(resultSet);
        }

        return contactArray;

    }

    public ArrayList getLocalContactChangesToSync() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor resultSet = sqLiteDatabase.rawQuery("Select * from tbContact where sync_flag = 0 order by creation_time desc", null); // 0= Not Wached, 1=Watched
        ArrayList contactArray = new ArrayList();
        if (resultSet.getCount() > 0) {
            contactArray = extractContactObject(resultSet);
        }
        return contactArray;
    }

    public MyFollowingList getLocalNewFollowedContacts() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        MyFollowingList followingList = new MyFollowingList(context);
        Cursor resultSet = sqLiteDatabase.rawQuery(
                "SELECT * FROM tbFollowContact\n" +
                        " WHERE sync_flag = 0", null); // 0= Not synced
        if (resultSet.getCount() > 0) {
            followingList = extractNewFollowedContactObject(resultSet);
        }
        return followingList;
    }

    public ArrayList<Contact> getContactsFromTerritoryID(Context context, int territory_id) {

        this.context = context;
        ArrayList contact_array = new ArrayList();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        try {
            //TODO: Melhorar a concatenação abaixo. Como passar um param sem ter que concatenar?
            Cursor resultSet = sqLiteDatabase.rawQuery(
                    "SELECT c.idContact, f.name, c.address, c.complement, c.phone, c.creation_time, c.update_time, \n" +
                            " c.additional_info, c.idTerritoryFK, c.idContactStatusFK,\n" +
                            " case when f.idContactFK is null then 0\n" +
                            " else 1 end as isFollowed\n" +
                            " FROM tbContact c\n" +
                            " LEFT OUTER JOIN tbFollowContact f on c.idContact = f.idContactFk\n" +
                            " where idTerritoryFK = " +
                            territory_id + " ;"
                    , null);
            resultSet.moveToFirst();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //To Converting the DateTime

            while (resultSet.isAfterLast() == false) {
                Contact contact = new Contact();
                contact.idContact = resultSet.getInt((resultSet.getColumnIndex("idContact")));
                contact.name = resultSet.getString((resultSet.getColumnIndex("name")));
                contact.address = resultSet.getString((resultSet.getColumnIndex("address")));
                contact.complement = resultSet.getString((resultSet.getColumnIndex("complement")));
                contact.phone = resultSet.getString((resultSet.getColumnIndex("phone")));
                //TODO: Parse Datetime corretamente!!!
                //contact.creation_time = simpleDateFormat.parse(resultSet.getString((resultSet.getColumnIndex("creation_time"))));
                //contact.update_time = simpleDateFormat.parse(resultSet.getString((resultSet.getColumnIndex("update_time"))));
                contact.creation_time = simpleDateFormat.parse("2015-13-12 07:52:18"); //TODO: Data hardcoded, arrumar!!
                contact.update_time = simpleDateFormat.parse("2015-13-12 07:52:18");    //TODO: Data hardcoded, arrumar!!

                contact.additional_info = resultSet.getString((resultSet.getColumnIndex("additional_info")));
                contact.territory._id = resultSet.getInt((resultSet.getColumnIndex("idTerritoryFK")));
                contact.status = resultSet.getInt((resultSet.getColumnIndex("idContactStatusFK")));
                Integer m_isfollwed = resultSet.getInt((resultSet.getColumnIndex("isFollowed")));
                if (m_isfollwed == 1) {
                    contact.isFollowed = true;
                }
                //contact.nationality = resultSet.getInt((resultSet.getColumnIndex("idNationalityFK")));


                contact_array.add(contact);
                resultSet.moveToNext();
            }


        } catch (Exception e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        }
        return contact_array;
    }

    public ArrayList<Contact> getWatchedContacts(Context context) {

        this.context = context;
        ArrayList contact_array = new ArrayList();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        try {
            //TODO: Melhorar a concatenação abaixo. Como passar um param sem ter que concatenar?
            Cursor resultSet = sqLiteDatabase.rawQuery(
                    "Select c.idContact, f.name, c.address, c.complement, c.phone, c.creation_time,\n" +
                            " c.update_time, c.additional_info, c.idTerritoryFK, c.idContactStatusFK,\n" +
                            " c.sync_flag\n" +
                            " from tbContact c\n" +
                            " join tbFollowContact f on c.idContact = f.idContactFK" +
                            " ORDER BY f.name ASC"
                    , null); //TODO: hardcoded tablename
            resultSet.moveToFirst();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //To Converting the DateTime

            while (resultSet.isAfterLast() == false) {
                Contact contact = new Contact();
                contact.isFollowed = true;
                contact.idContact = resultSet.getInt((resultSet.getColumnIndex("idContact")));
                contact.name = resultSet.getString((resultSet.getColumnIndex("name")));
                contact.address = resultSet.getString((resultSet.getColumnIndex("address")));
                contact.complement = resultSet.getString((resultSet.getColumnIndex("complement")));
                contact.phone = resultSet.getString((resultSet.getColumnIndex("phone")));
                //TODO: Parse Datetime corretamente!!!
                //contact.creation_time = simpleDateFormat.parse(resultSet.getString((resultSet.getColumnIndex("creation_time"))));
                //contact.update_time = simpleDateFormat.parse(resultSet.getString((resultSet.getColumnIndex("update_time"))));
                contact.creation_time = simpleDateFormat.parse("2015-13-12 07:52:18"); //TODO: Data hardcoded, arrumar!!
                contact.update_time = simpleDateFormat.parse("2015-13-12 07:52:18");    //TODO: Data hardcoded, arrumar!!

                contact.additional_info = resultSet.getString((resultSet.getColumnIndex("additional_info")));
                contact.territory._id = resultSet.getInt((resultSet.getColumnIndex("idTerritoryFK")));
                contact.status = resultSet.getInt((resultSet.getColumnIndex("idContactStatusFK")));
                contact.sync_status = resultSet.getInt((resultSet.getColumnIndex("sync_flag")));
                //contact.nationality = resultSet.getInt((resultSet.getColumnIndex("idNationalityFK")));


                contact_array.add(contact);
                resultSet.moveToNext();
            }


        } catch (Exception e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        }
        return contact_array;
    }

    public ArrayList<MessageHistory> getMessagesFromContactId(int idContact) {

        ArrayList message_array = new ArrayList();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        //TODO: Melhorar a concatenação abaixo. Como passar um param sem ter que concatenar?
        Cursor resultSet = sqLiteDatabase.rawQuery("Select * from tbHistoryMessages " +
                " where idContactFK =" + idContact +
                " order by date desc", null);
        message_array = extractMessageObject(resultSet);
        return message_array;
    }

    public ArrayList getMessagesToSync() {

        JSONArray jsonArray = new JSONArray();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor resultSet = sqLiteDatabase.rawQuery("Select * from tbHistoryMessages where sync_flag = 0", null);
        ArrayList messageArray = extractMessageObject(resultSet);

        return messageArray;

    }

    public ArrayList<Territory> getRecentTerritories(Context context) {
        this.context = context;
        ArrayList territory_array = new ArrayList();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        try {
            Cursor resultSet = sqLiteDatabase.rawQuery("select idTerritory, territory.name, territory.number, zone.idCityZone, zone.name as 'Cityname'" +
                    "from tbterritory territory\n" +
                    "join tbcityzone zone on territory.idCityZoneFK = zone.idCityZone;", null);

            resultSet.moveToFirst();
            while (resultSet.isAfterLast() == false) {
                Territory territory = new Territory();
                territory._id = resultSet.getInt((resultSet.getColumnIndex("idTerritory")));
                territory.number = resultSet.getInt((resultSet.getColumnIndex("number")));
                territory.name = resultSet.getString((resultSet.getColumnIndex("name")));
                territory.city._id = resultSet.getInt((resultSet.getColumnIndex("idCityZone"))); //TODO: Link with the c
                territory.city.name = resultSet.getString((resultSet.getColumnIndex("Cityname")));
                territory_array.add(territory);
                resultSet.moveToNext();
            }
        } catch (Exception e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        }
        return territory_array;
    }

    //INSERTS
    public void insertCityZone(CityZone cityZone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idCityZone", cityZone._id);
        values.put("name", cityZone.name);
        values.put("idProfileFK", 1);   //Todo: Tratar para se basear no profile do usuário.

        db.insert(DBStmts.TBNAME_CITYZONE, null, values);
    }

    public void insertNewMessageHistory(MessageHistory msg_object) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idGlobal", UUID.randomUUID().toString());
        values.put("message", msg_object.message);
        values.put("idUserFK", msg_object.idUserFK);
        values.put("userfullname", msg_object.nameUserFK);
        values.put("idContactFK", msg_object.idContactFK);
        values.put("sync_flag", 0); // 0 means it's not synced to the server
        db.insert(DBStmts.TBNAME_HISTORYMESSAGES, null, values);
    }


    private void insertTerritory(Territory territory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idTerritory", territory._id);
        values.put("number", territory.number);
        values.put("name", territory.name);
        values.put("idCityZoneFK", territory.city._id);

        db.insert(DBStmts.TBNAME_TERRITORY, null, values);
    }

    public void insertContacts(ArrayList<Contact> decoded_contacts) {

        SQLiteDatabase db = this.getWritableDatabase();
        //for all items on decoded_territories
        int j = 0;
        while (decoded_contacts.size() > j) {
            ContentValues values = new ContentValues();
            Contact contact = decoded_contacts.get(j);

            values.put("idContact", contact.idContact);
            values.put("name", contact.name);
            values.put("address", contact.address);
            values.put("complement", contact.complement);
            values.put("phone", contact.phone);
            values.put("creation_time", contact.creation_time.toString());
//            values.put("update_time", contact.update_time.toString()); TODO: tratar valor nulo. Vou utilizar esse campo no app?
            values.put("additional_info", contact.additional_info);
            values.put("idTerritoryFK", contact.territory._id);
            values.put("idContactStatusFK", contact.status);
//            values.put("idNationalityFK", contact.nationality);

            db.insert(DBStmts.TBNAME_CONTACT, null, values);

            j++;
        }
    }

    public void insertNewContact(Contact contact) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", contact.name);
        values.put("address", contact.address);
        values.put("complement", contact.complement);
        values.put("phone", contact.phone);
        values.put("additional_info", contact.additional_info);
        values.put("sync_flag", 2); // 1=synced, 0=not synced, 2=New Contact not synced
        values.put("watch_flag", 1); // 0= Not Watched, 1=Watched
        values.put("idContactStatusFK", 1); //1=normal

        Long m_id = db.insertOrThrow(DBStmts.TBNAME_CONTACT, null, values);
        contact.idContact = (int) (long) m_id;
        markAsFollowedContact(contact, true);

    }

    public void markAsFollowedContact(Contact contact, Boolean new_contact) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (new_contact) {
            values.put("sync_flag", 2); // 1=synced, 0=not synced, 2=from a new Contact (Contact not synced yet) TODO:Hardcode
        } else {
            values.put("sync_flag", 0);
        } // 1=synced, 0=not synced, 2=from a new Contact (Contact not synced yet) TODO:Hardcode

        values.put("idContactFK", contact.idContact);
        values.put("name", contact.name);

        try {
            db.insertOrThrow(DBStmts.TBNAME_FOLLOWEDCONTACTS, null, values);
            Toast.makeText(context, "Address added to My List", Toast.LENGTH_SHORT).show();
        } catch (SQLiteConstraintException e) {
            Toast.makeText(context, "User already exists on My List", Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }


    }

    public void insertFollowedContacts(JSONObject jsonObject) {

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("following");
            if (jsonArray.length() != 0) { // If no of array elements is not zero
                // Loop through each array element, get JSON object which has userid and username
                SQLiteDatabase db = this.getWritableDatabase();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = (JSONObject) jsonArray.get(i);
                    ContentValues values = new ContentValues();

                    values.put("idContactFK", obj.get("idContactFK").toString()); //Todo: Assumo que sempre retornará um ID.
                    values.put("name", obj.get("name").toString());
                    values.put("sync_flag", 1); // 1=synced, 0=not synced TODO:Hardcode


                    db.insertOrThrow(DBStmts.TBNAME_FOLLOWEDCONTACTS, null, values);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //UPDATES
    public void updateTerritories(ArrayList<Territory> decoded_territories) {
        //for all items on decoded_territories
        int j = 0;
        while (decoded_territories.size() > j) {
            this.insertTerritory(decoded_territories.get(j));
            j++;
        }
    }

/*    public void updateLocalSyncedMessages(JSONArray jsonArray) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                if (object.get("status") == 1) {
                    ContentValues values = new ContentValues();
                    values.put("sync_flag", 1);
                    String whereclause = "idGlobal='" + object.get("idGlobal").toString() + "'";
                    db.update(DBStmts.TBNAME_HISTORYMESSAGES, values, whereclause, null);
                }
            }
        } catch (JSONException e) {
        }
    }*/

    public void updateContactAdditionalInfo(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("additional_info", contact.additional_info);
        values.put("sync_flag", 0); // 0 means it's not synced to the server - it needs to be synced
        String whereclause = "idContact= " + contact.idContact;
        db.update(DBStmts.TBNAME_CONTACT, values, whereclause, null);
    }

    public void updateContactPhoneNumber(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", contact.phone);
        values.put("sync_flag", 0); // 0 means it's not synced to the server - it needs to be synced
        String whereclause = "idContact= " + contact.idContact;
        db.update(DBStmts.TBNAME_CONTACT, values, whereclause, null);
    }

    public void updateCityZones(ArrayList<CityZone> cityZoneArrayList) {

        //for all items on decoded_territories
        int j = 0;
        while (cityZoneArrayList.size() > j) {
            this.insertCityZone(cityZoneArrayList.get(j));
            j++;
        }
    }

    public void updateMessages(ArrayList<MessageHistory> decoded_messages) {
        //for all items on decoded_territories
        int j = 0;
        while (decoded_messages.size() > j) {
            this.updateMessageHistory(decoded_messages.get(j));
            j++;
        }
    }

    private void updateMessageHistory(MessageHistory msg_object) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idGlobal", msg_object.idGlobal);
        values.put("message", msg_object.message);
        values.put("idContactFK", msg_object.idContactFK);
        values.put("userfullname", msg_object.nameUserFK);
        String timestamp = simpleDateFormat.format(msg_object.dateTime);
        values.put("date", timestamp);
        values.put("sync_flag", 1); // 1 means it's synced to the server
        db.insert(DBStmts.TBNAME_HISTORYMESSAGES, null, values);
    }

    //Data handling
    private ArrayList extractContactObject(Cursor resultSet) {
        ArrayList contact_array = new ArrayList();
        resultSet.moveToFirst();
        try {
            while (resultSet.isAfterLast() == false) {
                Contact contact = new Contact();
                if (resultSet.getInt((resultSet.getColumnIndex("sync_flag"))) == 2) { // Verifica se o Contact e um novo Contact
                    contact.idContact = -1; //Se for novo, an idContact will be given on the server side
                    contact.address = resultSet.getString((resultSet.getColumnIndex("address")));
                } else {
                    contact.idContact = resultSet.getInt((resultSet.getColumnIndex("idContact")));
                }
                contact.name = resultSet.getString((resultSet.getColumnIndex("name")));
                contact.additional_info = resultSet.getString((resultSet.getColumnIndex("additional_info")));
                contact.complement = resultSet.getString((resultSet.getColumnIndex("complement")));
                contact.phone = resultSet.getString((resultSet.getColumnIndex("phone")));

                contact_array.add(contact);
                resultSet.moveToNext();
            }
        } catch (Exception e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        }
        return contact_array;
    }

    private MyFollowingList extractNewFollowedContactObject(Cursor resultSet) {
        resultSet.moveToFirst();
        MyFollowingList myFollowingList = new MyFollowingList(context);
        try {
            while (resultSet.isAfterLast() == false) {
                Contact contact = new Contact();
                contact.idContact = resultSet.getInt((resultSet.getColumnIndex("idContactFK")));
                contact.name = resultSet.getString((resultSet.getColumnIndex("name")));
                myFollowingList.following_array.add(contact);
                resultSet.moveToNext();
            }
        } catch (Exception e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        }
        return myFollowingList;
    }

    private ArrayList extractMessageObject(Cursor resultSet) {
        ArrayList message_array = new ArrayList();
        resultSet.moveToFirst();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //To Converting the DateTime
        try {
            while (resultSet.isAfterLast() == false) {
                MessageHistory messageHistory = new MessageHistory();
                messageHistory.idGlobal = resultSet.getString((resultSet.getColumnIndex("idGlobal")));
                messageHistory.message = resultSet.getString((resultSet.getColumnIndex("message")));
                messageHistory.dateTime = simpleDateFormat.parse(resultSet.getString((resultSet.getColumnIndex("date"))));
                //messageHistory.dateTime = simpleDateFormat.parse("2015-13-12 07:52:18"); //TODO: Data hardcoded, arrumar!!
                messageHistory.idContactFK = resultSet.getInt((resultSet.getColumnIndex("idContactFK")));
                messageHistory.idUserFK = resultSet.getInt((resultSet.getColumnIndex("idUserFK")));
                //Todo: Verificar se existe essa coluna na query
                messageHistory.nameUserFK = resultSet.getString((resultSet.getColumnIndex("userfullname")));

                message_array.add(messageHistory);
                resultSet.moveToNext();
            }
        } catch (Exception e) {
            Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        }
        return message_array;
    }


}
