package fanx.instag.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

import fanx.instag.R;
import fanx.instag.utils.Utils;
import fanx.instag.database.DatabaseManager;
import fanx.instag.database.Photo;

public class IndividualActivity extends Activity {

    private Photo photoDetails = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_display_message);
        setContentView(R.layout.activity_display_message);
        Intent intent = getIntent();

        String position = intent.getExtras().getString(UploadActivity.STRING_ID);
        ImageView imageView = (ImageView) findViewById(R.id.SingleView);

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        final int longest = (height > width ? height : width) / 2;
        photoDetails = DatabaseManager.getInstance(getApplicationContext())
                .getPhoto(position, longest, longest);

        this.getActionBar().setTitle(photoDetails.getPhotoID());
        imageView.setImageBitmap(photoDetails.getBitmap());
        //Toast.makeText(getApplicationContext(), photoDetails.getDescription(), Toast.LENGTH_LONG).show();

        // -- Text view for Description --//
        TextView textView = (TextView) findViewById(R.id.Description);

        textView.setText(photoDetails.getDescription() + "\n@ " + photoDetails.getAlbum());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.display_message, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //-- ACTION BAR IMPLEMNTATION @ FAN --//
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_share:
                // Share current image
                openShare();
                return true;
            case R.id.action_discard:
                // Delete current image
                discard();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
        //-- ACTION BAR END --//
    }
    private void getPhotoInfo() {
        AlertDialog.Builder infoDialog =  new AlertDialog.Builder(this);
        infoDialog.setTitle(photoDetails.getPhotoID());
        infoDialog.setMessage("Album : " + photoDetails.getAlbum() + "\nUpload : "
                + photoDetails.isUploadedToServerAsYesNO());
        infoDialog.show();

    }

    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openShare() {

    }

    private void discard()
    {
        int n = DatabaseManager.getInstance(getApplicationContext()).deletePhoto(photoDetails.getPhotoID());

        if( n > 0 )
            Toast.makeText(getApplicationContext(), "Photo deleted.", Toast.LENGTH_LONG).show();

        Utils.setIndividualPhotoDeleted(true);
        Utils.setDeletedPhotoID(photoDetails.getPhotoID());
        finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
