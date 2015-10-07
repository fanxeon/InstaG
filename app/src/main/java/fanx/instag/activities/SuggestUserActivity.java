package fanx.instag.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import fanx.instag.R;
import fanx.instag.activities.util.SuggestUserTask;

public class SuggestUserActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_user);

        (new SuggestUserTask(getApplicationContext(), (TextView) findViewById(R.id.textView_Message))).execute();
    }
}
