package net.cloudterritory.cloudterritory.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Jonathan on 04/6/2015.
 */
public class MessageHistory implements Serializable {

    public String idGlobal;
    public long idMessage;
//    public boolean isMe;
    public String message;
    public Date dateTime;
    public int idContactFK;
    public int idUserFK;
    public String nameUserFK;



    public String getFirstNameUserFK() {
        if (nameUserFK == null)
        {
            return "You";
        }
        else{
        String[] splitname = nameUserFK.split(" ");
        return splitname[0];}
    }


}
