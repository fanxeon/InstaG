package fanx.instag.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import fanx.instag.activities.InstagramSupportLibrary.InstagramApp;
import fanx.instag.activities.InstagramSupportLibrary.InstagramApp.OAuthAuthenticationListener;
/**
 * Created by SShrestha on 7/09/2015.
 */
public class AppData {
    private final String CLIENT_ID = "5a842f46c9ab4d8fbbb8bfd1ff1a70d2";
    private final String CLIENT_SECRET = "5dbaad300b114934bf62b5acaa780464";
    private final String CALLBACK_URL = "https://github.com/fanxeon/InstaG";
    private final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    private final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    private final String API_URL = "https://api.instagram.com/v1";
    private final String SHARED = "Instagram_Preferences";

    public String response;

    public UserData[] searchUser(Context context, final String searchText, final int resultCount) {
        final String mAccessToken = getAccessToken(context);
        response = null;
        try {
            new Thread() {
                public void run() {
                    try {
                        String searchURL = API_URL + "/users/search?q=" + searchText + "&count=" + String.valueOf(resultCount) + "&access_token=" + mAccessToken;

                        URL url = new URL(searchURL);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setDoInput(true);
                        int responseCode = urlConnection.getResponseCode();
                        urlConnection.connect();
                        response = streamToString(urlConnection.getInputStream());

                    } catch (Exception e) {
                        System.out.print(e.fillInStackTrace());
                    }
                }
            }.start();
            //Commented by: Sandip Shrestha
            //I have spanned a new thread which will execute asynchronously, therefore waiting for the response from new thread
            while (response == null);

            if (!response.equalsIgnoreCase("!")) {
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray ar = jsonObj.getJSONArray("data");
                UserData ud[] = new UserData[ar.length()];
                for (int i = 0; i < ar.length(); i++) {
                    JSONObject obj = ar.getJSONObject(i);
                    UserData _ud = new UserData();
                    _ud.first_name = obj.getString("first_name");
                    _ud.last_name = obj.getString("last_name");
                    _ud.id = obj.getString("id");
                    _ud.username = obj.getString("username");
                    _ud.profile_picture = obj.getString("profile_picture");
                    ud[i] = _ud;

                }
                return ud;
            }
            else {
                return null;
            }

        } catch (Exception e) {
            System.out.print(e.fillInStackTrace());
            return null;
        }
    }

    public UserProfile getUserProfile(final Context context) {
        final String mAccessToken = getAccessToken(context);
        response = null;
        try {
            new Thread() {
                public void run() {
                    try {
                        String profileURL = API_URL + "/users/" + getUserId(context) + "/?access_token=" + mAccessToken;
                        URL url = new URL(profileURL);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setDoInput(true);
                        int responseCode = urlConnection.getResponseCode();
                        urlConnection.connect();
                        response = streamToString(urlConnection.getInputStream());

                    } catch (Exception e) {
                        System.out.print(e.fillInStackTrace());
                    }
                }
            }.start();
            //Commented by: Sandip Shrestha
            //I have spanned a new thread which will execute asynchronously, therefore waiting for the response from new thread
            while (response == null);

            if (!response.equalsIgnoreCase("!")) {
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                JSONObject obj = jsonObj.getJSONObject("data");

                UserProfile up = new UserProfile();

                up.id = obj.getString("id");
                up.full_name = obj.getString("full_name");
                up.profile_picture = obj.getString("profile_picture");
                up.username = obj.getString("username");
                up.bio = obj.getString("bio");
                up.website = obj.getString("website");

                JSONObject counts = obj.getJSONObject("counts");
                up.mediaCounts = counts.getString("media");
                up.followsCounts = counts.getString("follows");
                up.followed_byCounts = counts.getString("followed_by");

                return up;
            }
            else {
                return null;
            }

        } catch (Exception e) {
            System.out.print(e.fillInStackTrace());
            return null;
        }
    }

    public static String streamToString(InputStream is) throws IOException {
        String str = "";
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
            } finally {
                is.close();
            }

            str = sb.toString();
        }

        return str;
    }

    public class UserData
    {
        public String username;
        public String first_name;
        public String profile_picture;
        public String id;
        public String last_name;

        UserData(){}
    }

    public class UserProfile{
        public String id;
        public String username;
        public String full_name;
        public String profile_picture;
        public String bio;
        public String website;
        public String mediaCounts;
        public String followsCounts;
        public String followed_byCounts;

        UserProfile(){}
    }

    public String getAccessToken(Context c)
    {
        SharedPreferences s = c.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
        return s.getString("access_token",null);
    }

    public boolean hasAccessToken(Context c)
    {
        if (this.getAccessToken(c) != null )
            return true;
        else
            return false;
    }

    public void getAuthenticated(Context c, OAuthAuthenticationListener l)
    {
        /*Instance of un/authenticated instagram object*/
        InstagramApp mApp = new InstagramApp(c, CLIENT_ID, CLIENT_SECRET, CALLBACK_URL);
        mApp.setListener(l);
        mApp.authorize();
    }

    public String getUserId(Context c){
        SharedPreferences s = c.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
        return s.getString("id",null);
    }

    /*
    //Using AsyncTask (The constructor takes ImageView and URL string as parameter and sets the image )
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
    */

    public static class APICallForData extends AsyncTask<Void, Void, String> {

        private String url;
        private String data;
        public APICallForData(String url, Context c) {
            this.url = "https://api.instagram.com/v1"+url;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            try {

                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                result = streamToString(input);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            data = result;
        }

    }
}

