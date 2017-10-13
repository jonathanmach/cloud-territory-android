package net.cloudterritory.cloudterritory.model;

/**
 * Created by Jonathan on 28/6/2015.
 */
public class DBStmts {

    public static String DATABASE_NAME = "cloudterritory";

    // PROFILE TABLE
    public static String TBNAME_PROFILE = "tbProfile";
    public static String TBCREATE_PROFILE = "CREATE TABLE `tbProfile` (\n" +
            "  `idProfile` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "  `profileName` VARCHAR(45) NOT NULL)";
    // CITY-ZONE TABLE
    public static String TBNAME_CITYZONE = "tbCityZone";
    public static String TBCREATE_CITYZONE = "CREATE TABLE `tbCityZone` (\n" +
            "  `idCityZone` INTEGER PRIMARY KEY,\n" +
            "  `name` VARCHAR(45) NOT NULL,\n" +
            "  `idProfileFK` INT NOT NULL)";
    // TERRITORY TABLE
    public static String TBNAME_TERRITORY = "tbTerritory";
    public static String TBCREATE_TERRITORY = "  CREATE TABLE `tbTerritory` (\n" +
            "  `idTerritory` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "  `name` VARCHAR(45) NULL,\n" +
            "  `number` INT NOT NULL,\n" +
            "  `idCityZoneFK` INT NOT NULL)";

    // STATUS TABLE
    public static String TBNAME_CONTACTSTATUS = "tbContactStatus";
    public static String TBCREATE_CONTACTSTATUS = "CREATE TABLE `tbContactStatus` (\n" +
            "  `idContactStatus` INTEGER PRIMARY KEY,\n" +
            "  `contact_status` VARCHAR(45) NULL);\n";
    // NATIONALITY TABLE
    public static String TBNAME_NATIONALITY = "tbNationality";
    public static String TBCREATE_NATIONALITY = "CREATE TABLE `tbNationality` (\n" +
            "  `idNationality` INTEGER PRIMARY KEY,\n" +
            "  `country` VARCHAR(45) NULL);";
    // CONTACT TABLE
    public static String TBNAME_CONTACT = "tbContact";
    public static String TBCREATE_CONTACT = "CREATE TABLE `tbContact` (\n" +
            "  `idContact` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "  `name` VARCHAR(60) NULL,\n" +
            "  `address` VARCHAR(45) NULL,\n" +
            "  `complement` VARCHAR(45) NULL,\n" +
            "  `phone` VARCHAR(45) NULL,\n" +
            "  `creation_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,\n" +
            "  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,\n" +
            "  `additional_info` VARCHAR(250) NULL,\n" +
            "   sync_flag INT  NOT NULL DEFAULT 1,\n" +     // 1=synced, 0=not synced, 2=new Contact, not synced
            "   watch_flag INT  NOT NULL DEFAULT 0,\n" +     // 0= Not Watched, 1=Watched
            "  `idTerritoryFK` INT NULL,\n" +
            "  `idContactStatusFK` INT NOT NULL);";
            //            "  `idNationalityFK` INT NOT NULL);";     Nova necessidade de nao armazenar nacionalidade.

    // WATCH CONTACT TABLE
    public static String TBNAME_FOLLOWEDCONTACTS = "tbFollowContact";
    public static String TBCREATE_FOLLOWEDCONTACTS = "CREATE TABLE `tbFollowContact` (\n" +
            "  `idContactFK` INTEGER PRIMARY KEY ,\n" +
            "  `name` VARCHAR(60) NULL,\n" +
            "   sync_flag INT  NOT NULL DEFAULT 1);";      // 1=synced, 0=not synced


    // contactHISTORY TABLE
    public static String TBNAME_HISTORYMESSAGES = "tbHistoryMessages";
    public static String TBCREATE_HISTORYMESSAGES = "CREATE TABLE tbHistoryMessages (\n" +
            "idGlobal VARCHAR(36) PRIMARY KEY,\n" +
            "message VARCHAR(250) NULL,\n" +
            "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,\n" + //Data e adicionada automaticamente ao inserir nova msg
            "idContactFK INT NOT NULL,\n" +
            "sync_flag INT  NOT NULL DEFAULT 0,\n" +
            "userfullname VARCHAR(250) NULL,\n" +
            "idUserFK INT NOT NULL DEFAULT 0);";

}
