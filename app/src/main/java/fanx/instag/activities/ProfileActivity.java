package fanx.instag.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import fanx.instag.R;
import fanx.instag.activities.util.InstagramCurrentUserProfile;

public class ProfileActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Sandip Shrestha
        TextView full_name =  ((TextView) findViewById(R.id.label_fullname));
        TextView username = ((TextView)findViewById(R.id.label_username));
        TextView bio = ((TextView)findViewById(R.id.bio));
        TextView website = ((TextView)findViewById(R.id.website));
        TextView mediaCounts =((TextView)findViewById(R.id.mediacounts));
        TextView followsCounts =((TextView)findViewById(R.id.followscounts));
        TextView followed_byCounts =((TextView)findViewById(R.id.followed_byCounts));
        ImageView profile_picture = (ImageView) findViewById(R.id.profile_picture);

        InstagramCurrentUserProfile instagramUser = new InstagramCurrentUserProfile(ProfileActivity.this,
                full_name,
                username,
                bio,
                website,
                mediaCounts,
                followsCounts,
                followed_byCounts,
                profile_picture);

        instagramUser.execute();

        Button b = (Button) findViewById(R.id.button_bluetooth);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, BluetoothActivity.class);
                startActivity(i);
            }
        });

        /*Button server = (Button) findViewById(R.id.button_server);
        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, Sendingdata_serverActivity.class);
                startActivity(i);
            }
        });

        Button client = (Button) findViewById(R.id.button_client);
        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, SendingdataActivity.class);
                startActivity(i);
            }
        });*/

        TextView testView_SuggestUser = (TextView) findViewById(R.id.textView_SuggestUser);

        testView_SuggestUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SuggestUserActivity.class);
                startActivity(i);
            }
        });

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
