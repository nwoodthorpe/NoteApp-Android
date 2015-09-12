package projects.noteapp;

import java.util.Date;

/**
 * Created by Nathaniel on 8/3/2015.
 */
public class Storage {
    private static Storage mInstance= null;

    public int userID;
    public String firstname;
    public String lastname;
    public long loginTime;
    public String email;
    public String noteData;

    protected Storage(){}

    public static synchronized Storage getInstance(){
        if(null == mInstance){
            mInstance = new Storage();
        }
        return mInstance;
    }
}
