package fanx.instag.activities.util;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import fanx.instag.activities.AppData;

/**
 * Created by SShrestha on 28/09/2015.
 */
public class InstagramUserSearchTask extends AsyncTask<Void,Void,InstagramUser[]> {
    private final String API_URL = "https://api.instagram.com/v1";
    private final String SHARED = "Instagram_Preferences";
    ListView listView;
    Context context;
    String url;
    public InstagramUserSearchTask(Context context, final int count, String searchText, ListView listView){
        this.context = context;
        this.url = API_URL + "/users/search?q=" + searchText + "&count=" + String.valueOf(count) + "&access_token=" ;
    }
    @Override
    protected InstagramUser[] doInBackground(Void... p){
        try {
            URL url = new URL(this.url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            int responseCode = urlConnection.getResponseCode();
            urlConnection.connect();
            String response = AppData.streamToString(urlConnection.getInputStream());
            if (!response.equalsIgnoreCase("!")) {
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray ar = jsonObj.getJSONArray("data");
                InstagramUser ud[] = new InstagramUser[ar.length()];
                for (int i = 0; i < ar.length(); i++) {
                    JSONObject obj = ar.getJSONObject(i);
                    InstagramUser _ud = new InstagramUser();
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
            e.printStackTrace();
        }
        return null;
    }
}

