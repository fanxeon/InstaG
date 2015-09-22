package fanx.instag.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;

import fanx.instag.activities.AppData.UserProfile;
import fanx.instag.activities.AppData.APICallForData;

import fanx.instag.R;

public class ProfileActivity extends Activity {

    boolean imageLoadStatus = false;
    AppData mAppData;
    UserProfile u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);

        mAppData = new AppData();
        u = mAppData.getUserProfile(this);

        ((TextView)findViewById(R.id.full_name)).setText(u.full_name);
        ((TextView)findViewById(R.id.label_username)).setText(u.username);
        ((TextView)findViewById(R.id.bio)).setText(u.bio);
        ((TextView)findViewById(R.id.website)).setText(u.website);
        ((TextView)findViewById(R.id.mediacount)).setText(u.mediaCounts);
        ((TextView)findViewById(R.id.followscount)).setText(u.followsCounts);
        ((TextView)findViewById(R.id.followed_byCount)).setText(u.followed_byCounts);
        ((TextView)findViewById(R.id.profil_picture)).setText(u.profile_picture);

        final String pp = u.profile_picture;

        new Thread() {
            public void run() {
                try {
                    URL url = new URL(pp);
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    ((ImageView) findViewById(R.id.profileImage)).setImageBitmap(bmp);
                    imageLoadStatus = true;
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }}.start();

        while (!imageLoadStatus);


        String url = "/users/"+mAppData.getUserId(ProfileActivity.this)+"/media/recent/?access_token="+mAppData.getAccessToken(ProfileActivity.this)+"&count=20";
        APICallForData r =  new APICallForData(url,ProfileActivity.this);
        r.execute();
        //Log.e("Expected Data", r.getData());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_a, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        // The activity is either being restarted or started for the first time
        Log.e("Profile", "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        // Activity being restarted from stopped state
        Log.e("Profile", "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Profile", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Profile", "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Profile", "onStop");
    }
}
