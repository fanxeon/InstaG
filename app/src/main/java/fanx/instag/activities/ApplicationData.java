package fanx.instag.activities;

import fanx.instag.activities.InstagramSupportLibrary.InstagramApp;
/**
 * Created by SShrestha on 7/09/2015.
 */
public class ApplicationData {
    public static final String CLIENT_ID = "5a842f46c9ab4d8fbbb8bfd1ff1a70d2";
    public static final String CLIENT_SECRET = "5dbaad300b114934bf62b5acaa780464";
    public static final String CALLBACK_URL = "https://github.com/fanxeon/InstaG";

    /*Instance of un/authenticated instagram object*/
    public static InstagramApp mApp = new InstagramApp();
}
