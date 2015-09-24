package fanx.instag.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import fanx.instag.R;
import fanx.instag.activities.util.InstagramClass;



public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button logInButton =  (Button)findViewById(R.id.LoginButton);
        final TextView username = (TextView) findViewById(R.id.editText_Username);
        final TextView password = (TextView) findViewById(R.id.editText_Password);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Username: " + username.getText() + "\n" + "Password: " + password.getText(), Toast.LENGTH_SHORT).show();

                try {
                    //Log.e("HTML: ", submittingForm());
                    Intent nextIntent = new Intent(LoginActivity.this, MainActivity.class);
                    InstagramClass i = new InstagramClass(LoginActivity.this, username.getText().toString(), password.getText().toString(), nextIntent);
                    i.execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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