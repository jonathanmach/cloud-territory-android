package net.cloudterritory.cloudterritory.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jonathan on 25/5/2015.
 */
public class Contact implements Serializable {

    public int idContact;

    public String name;
    public String address;
    public String complement;
    public String phone;
    public Date creation_time;
    public Date update_time;
    public String additional_info;
    public Territory territory;
    public int status; //Todo: Create Status class
    public int nationality; //Todo: Create Nationality class
    public ArrayList<MessageHistory> messagehistory;
    public boolean isFollowed;
    public int sync_status;


    public Contact()
    {
        territory = new Territory();
        messagehistory = new ArrayList<>();
    }
    public Contact(String name, String address)
    {
        this.name = name;
        this.address = address;
    }

    public Contact(String name, String address, ArrayList<MessageHistory> historymsg)
    {
        this.name = name;
        this.address = address;
        this.messagehistory = historymsg;
    }


}
