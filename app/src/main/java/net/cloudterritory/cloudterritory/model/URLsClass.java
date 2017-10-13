package net.cloudterritory.cloudterritory.model;

/**
 * Created by Jonathan on 04/7/2015.
 */
public class URLsClass {

    public static String URL_AUTHORIZATION = "http://api.cloudterritory.net/authentication";

    //GETS
    public static String URL_GET_TERRITORIES = "http://cloudterritory.net/API/getTerritories";    //EXTERNAL
    public static String URL_GET_USERLIST_API = "http://cloudterritory.net/API/getUserList";    //EXTERNAL
    public static String URL_GET_EVENTLOGS = "http://cloudterritory.net/API/getLastEventLogs";    //EXTERNAL
    public static String URL_GET_CITYZONES = "http://cloudterritory.net/API/getCityZones";    //EXTERNAL
    public static String URL_GET_TERRITORY = "http://cloudterritory.net/API/getTerritoryDetails";    //EXTERNAL
    //POSTS
    public static String URL_POST_TERRITORY_ASSIGNMENT_API = "http://cloudterritory.net/API/assignTerritory2User";    //EXTERNAL
    public static String URL_2WAY_SYNC = "http://api.cloudterritory.net/sync_userdata/";    //EXTERNAL

}
