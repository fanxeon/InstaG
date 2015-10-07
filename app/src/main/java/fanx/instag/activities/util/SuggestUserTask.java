package fanx.instag.activities.util;

import android.content.Context;
import android.os.AsyncTask;

import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import fanx.instag.activities.AppData;

/**
 * Created by SShrestha on 7/10/2015.
 */
public class SuggestUserTask extends AsyncTask <Void, Void, ArrayList<SuggestUserTask.InstaUser>>{
    private final int TIMEOUT_SEC = 5;
    private Context appContext;
    private TextView textView_Message;

    public SuggestUserTask(Context appContext, TextView textView_Message){
        this.appContext = appContext;
        this.textView_Message = textView_Message;
    }
    @Override
    protected ArrayList<InstaUser> doInBackground(Void... param)
    {
        try {
            //Get all the users the authenticated user is following
            ArrayList<String> mFollows = getFollows("self");

            final ArrayList<String> mmFollows = new  ArrayList<String>();

            final ArrayList<InstaUser> mInstaUsers = new ArrayList<InstaUser>();

            ExecutorService es = Executors.newCachedThreadPool();

            //Get all the users the being followed by  "the users the authenticated user is following" - (suggestion)
            for (String instaUser: mFollows) {
                final String userId = instaUser;

                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        mmFollows.addAll(getFollows(userId));
                    }
                });

            }

            es.shutdown();
            boolean finshed;
            finshed = es.awaitTermination(TIMEOUT_SEC, TimeUnit.SECONDS);

            if(finshed) {

                Log.e("SuggestedUserTask", "mmFollows"+mmFollows.size());
                //Get the count of media the above users have posted and the count of followers for the users for ordering
                ExecutorService es2 = Executors.newCachedThreadPool();

                for (String _userId : mmFollows) {
                    final String userId = _userId;

                    es2.execute(new Runnable() {
                        @Override
                        public void run() {
                            InstaUser instaUser = getInstaUserData(userId);
                            try {
                                if (instaUser.toString() != null)
                                    mInstaUsers.add(getInstaUserData(userId));
                            } catch (Exception e) {
                                Log.e("InstaUser", "Missing Values ... ("+userId+")");
                            }
                        }
                    });

                }

                es2.shutdown();

                finshed = es2.awaitTermination(TIMEOUT_SEC*mFollows.size(), TimeUnit.SECONDS);

                if (finshed) {
                    Log.e("SuggestedUserTask", "mInstaUsers"+mInstaUsers.size());
                    return mInstaUsers;
                }

            }

            return mInstaUsers;

        } catch (Exception e) {
            Log.d("SuggestUserTask", e.getMessage());
            return null;
        }
    }
    @Override
    protected void onPostExecute(ArrayList<InstaUser> result)
    {
        Collections.sort(result, new CustomComparator());

        String txt = "";

        for (InstaUser iu:result) {
            try{
            txt= txt+iu.toString()+"\n----------------\n";}
            catch (Exception e)
            {}
        }
        textView_Message.setText(Integer.toString(result.size())+"\n"+txt);
    }


    public ArrayList<String> getFollows(String userId)
    {
        try {
            String urlString = AppData.API_URL + "/users/"+userId+"/follows?access_token="+AppData.getAccessToken(appContext);
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            int responseCode = urlConnection.getResponseCode();
            if(responseCode == 200)
            {
                ArrayList<String> follows=  new  ArrayList<String>();
                String response = AppData.streamToString(urlConnection.getInputStream());
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray data = jsonObj.getJSONArray("data");


                for (int i = 0; i < data.length(); i++) {
                    JSONObject follow = data.getJSONObject(i);

                    follows.add(follow.getString("id"));
                }

                return follows;
            }
        } catch (Exception e) {
            Log.d("SuggestUserTask", e.getMessage());
        }

        return null;
    }

    public InstaUser getInstaUserData(String userId)
    {
        try {
            String urlString = AppData.API_URL + "/users/"+userId+"/?access_token="+AppData.getAccessToken(appContext);
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            int responseCode = urlConnection.getResponseCode();
            if(responseCode == 200)
            {
                String response = AppData.streamToString(urlConnection.getInputStream());
                JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                JSONObject data = jsonObj.getJSONObject("data");
                JSONObject counts = data.getJSONObject("counts");

                InstaUser i = new InstaUser(userId,
                        counts.getString("follows"),
                        counts.getString("followed_by"),
                        counts.getString("media"),
                        data.getString("profile_picture"));


                return i;
            }
        } catch (Exception e) {
            Log.d("SuggestUserTask", e.getMessage());
        }

        return null;
    }

    public class InstaUser
    {
        public String userId;
        public String follows;
        public String followed_by;
        public String media;
        public String profile_picture;


        public InstaUser(String userId,
                String follows,
                String followed_by,
                String media,
                String profile_picture)
        {
            this.userId = userId;

            if (follows == null)
                this.follows = "0";
            else
                this.follows = media;

            if (followed_by == null)
                this.followed_by = "0";
            else
                this.followed_by = followed_by;

            if (media == null)
                this.media = "0";
            else
                this.media = media;

            this.profile_picture = profile_picture;

        }

        public String toString(){
            return "USERID: "+userId+"\nFollows: "+follows+"\nFollowedBy: "+ followed_by+"\nMedia: "+media+"\nProfile Pic: "+profile_picture;
        }

    }

    public class CustomComparator implements Comparator<InstaUser> {
        @Override
        public int compare(InstaUser f1, InstaUser f2) {
            try{
                Integer v1 = Integer.parseInt(f1.followed_by);
                Integer v2 = Integer.parseInt(f2.followed_by);
                return v2-v1  ;
            }catch (Exception e){
                System.out.println("Error: 000"+e.getMessage());
                return 0;
            }
        }
    }

}

