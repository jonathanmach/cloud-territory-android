package net.cloudterritory.cloudterritory.model;

import java.io.Serializable;

/**
 * Created by Jonathan on 25/5/2015.
 */
public class Territory implements Serializable {

    public int _id;
    public int number;
    public String name;
    public CityZone city;
    public User assigned_user;

    public Territory()
    {
        city = new CityZone();
        assigned_user = new User();

    }
    public Territory(int id, int number, String name, String address)
    {
        this._id = id;
        this.number = number;
        this.name = name;
        //this.city = address;

    }



}
