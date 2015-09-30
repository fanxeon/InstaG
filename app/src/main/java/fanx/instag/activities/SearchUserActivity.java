package fanx.instag.activities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import fanx.instag.R;
import fanx.instag.activities.util.InstagramUserSearchTask;

public class SearchUserActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        final ListView searchResultListView = (ListView) findViewById(R.id.searchResultListView);
        final Button button_Return = (Button) findViewById(R.id.button_Return);

        button_Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SearchUserActivity.this, DiscoverActivity.class);
                startActivity(i);
            }

        });



        SearchView searchUserView =  (SearchView) findViewById(R.id.searchUserView);
        searchUserView.setQueryHint("Start typing to search user...");
        searchUserView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        searchUserView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query.length() > 3) {
                    Log.e("SearchUserActivity", "onQueryTextSubmit");
                    InstagramUserSearchTask instagramUserSearchTask =
                            new InstagramUserSearchTask(SearchUserActivity.this, 10, searchResultListView);
                    instagramUserSearchTask.execute(query.replace(" ", "+"));
                    searchResultListView.setVisibility(SearchView.VISIBLE);
                } else {
                    searchResultListView.setVisibility(SearchView.INVISIBLE);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 3) {
                    //Log.e("SearchUserActivity", "onQueryTextChange");
                } else {

                }
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_user, menu);

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
