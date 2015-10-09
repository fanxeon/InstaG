package fanx.instag.activities.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import fanx.instag.activities.AppData;

/**
 * Created by SShrestha on 9/10/2015.
 */
public class DisplayMediaLikeUserTask extends AsyncTask<Void, Void, String> {

    Context context;
    String mediaId;

    public DisplayMediaLikeUserTask(Context context, String mediaId)
    {
        this.context = context;
        this.mediaId = mediaId;
    }
    @Override
    protected String doInBackground(Void... param)
    {
        String urlString = "https://api.instagram.com/v1/media/"+ mediaId +"/likes?access_token="+AppData.getAccessToken(context);

        try {
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            int responseCode = urlConnection.getResponseCode();
            urlConnection.connect();
            String response = AppData.streamToString(urlConnection.getInputStream());
            JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
            JSONArray data = jsonObj.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject likeUserInfo = data.getJSONObject(i);
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result)
    {
        Log.e("ActivityFeed", result);
    }
}
