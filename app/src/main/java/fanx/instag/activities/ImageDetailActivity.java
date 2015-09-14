package fanx.instag.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import fanx.instag.R;

public class ImageDetailActivity extends Activity {
    ImageView imgView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        // Recieve img from UploadActivity
        //Intent i = getIntent();
        //File f = i.getExtras().getParcelable("img");

        String img = getIntent().getStringExtra("img");
        imgView = (ImageView) findViewById(R.id.imageView);
        imgView.setImageURI(Uri.parse(img));

        //descrption - currently position and id
        String position = getIntent().getStringExtra("position");
        String id = getIntent().getStringExtra("id");
        textView = (TextView) findViewById(R.id.imgDescription);
        textView.setText("Position : " + position
                + "| ID ： " +  id);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_detail, menu);
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
