package fanx.instag.activities.util;

import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import fanx.instag.activities.AppData;

/**
 * Created by SShrestha on 27/09/2015.
 */

public class InstagramAPICall extends AsyncTask<Void, Void, String> {
    String url;

    public InstagramAPICall(String url) {
        this.url = "https://api.instagram.com/v1"+url;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {

            URL urlConnection = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) urlConnection.openConnection();

            //connection.setRequestMethod("GET");//Default

            //Specifies whether this URLConnection allows receiving data.
            connection.setDoInput(true);

            //Specifies whether this URLConnection allows sending data.
            //connection.setDoOutput(true);

            int responseCode = connection.getResponseCode();

            connection.connect();
            String response = AppData.streamToString(connection.getInputStream());

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.e("Result",result);
    }

}