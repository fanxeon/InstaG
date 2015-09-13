package fanx.instag.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Handler;
import android.widget.Toast;

import fanx.instag.activities.InstagramSupportLibrary.InstagramApp;

import fanx.instag.R;


public class AppStart extends Activity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);

        // Splash screen for SPLASH_DISPLAY_LENGTH milliseconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*if (!ApplicationData.mApp.hasAccessToken()) {
                    ApplicationData.mApp = new InstagramApp(AppStart.this, ApplicationData.CLIENT_ID,
                            ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
                    ApplicationData.mApp.setListener(listener);
                    ApplicationData.mApp.authorize();
                } else {*/
                         /* Create an Intent that will start the Main Activity. */
                    Intent mainIntent = new Intent(AppStart.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            //}
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_start, menu);
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

    InstagramApp.OAuthAuthenticationListener listener = new InstagramApp.OAuthAuthenticationListener() {

        @Override
        public void onSuccess() {
            //ApplicationData.mApp.getUserName());
            /* Create an Intent that will start the Main Activity. */
            Intent mainIntent = new Intent(AppStart.this, MainActivity.class);
            AppStart.this.startActivity(mainIntent);
        }

        @Override
        public void onFail(String error) {
            Toast.makeText(AppStart.this, error, Toast.LENGTH_SHORT).show();
        }
    };
}
