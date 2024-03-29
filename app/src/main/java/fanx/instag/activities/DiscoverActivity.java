package fanx.instag.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import fanx.instag.R;

public class DiscoverActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        TextView searchUser = (TextView)findViewById(R.id.searchUser);
        searchUser.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_discover, menu);
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

    public void getSearchUserActivity(View view)
    {
        Intent intent =  new Intent(this, SearchUserActivity.class);
        Log.e("getSearchUserActivity", "Search User Activity is being loaded..");
        startActivity(intent);
    }

    public void getMainActivity(View view)
    {
        Intent intent =  new Intent(this, MainActivity.class);
        Log.e("getMainActivity", "Main Activity is being loaded..");
        startActivity(intent);
    }
}
