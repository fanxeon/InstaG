package fanx.instag.activities;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import fanx.instag.R;
import fanx.instag.activities.util.InstagramAPICall;

public class ActivityFeedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);

        SharedPreferences s = this.getSharedPreferences("Instagram_Preferences", Context.MODE_PRIVATE);
        String id = s.getString("id", null);
        String access_token = s.getString("access_token", null);
        String url = "/users/"+id+"/follows?access_token="+access_token;


       /* InstagramAPICall a = new InstagramAPICall(url);
        a.execute();*/

        //AppData.createInstagramIntent("image/*", Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/File_Name.jpg", this);
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
}
