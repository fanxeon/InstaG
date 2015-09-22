package fanx.instag.activities;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fanx.instag.R;
import fanx.instag.activities.UploadActivity;


public class MainActivity extends TabActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,CompoundButton.OnCheckedChangeListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    // Tab bar variables
    private TabHost mTabHost;
    private Intent mAIntent;
    private Intent mBIntent;
    private Intent mCIntent;
    private Intent mDIntent;
    private Intent mEIntent;
    // Camera Variables
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintabs);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        //mNavigationDrawerFragment.setUp(
        //        R.id.navigation_drawer,
        //        (DrawerLayout) findViewById(R.id.drawer_layout));

        // Set action bar tab
        this.mAIntent = new Intent(this,UserFeedActivity.class);
        this.mBIntent = new Intent(this,DiscoverActivity.class);
        this.mCIntent = new Intent(this,UploadActivity.class);
        this.mDIntent = new Intent(this,ActivityFeedActivity.class);
        this.mEIntent = new Intent(this,ProfileActivity.class);

        ((RadioButton) findViewById(R.id.radio_button0))
                .setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.radio_button1))
                .setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.radio_button2))
                .setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.radio_button3))
                .setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.radio_button4))
                .setOnCheckedChangeListener(this);

        setupIntent();


    }
    // Tab bar supports functions
    private void setupIntent() {
        this.mTabHost = getTabHost();
        TabHost localTabHost = this.mTabHost;

        localTabHost.addTab(buildTabSpec("A_TAB", R.string.usersFeed,
                R.drawable.icon_1_n, this.mAIntent));

        localTabHost.addTab(buildTabSpec("B_TAB", R.string.discover,
                R.drawable.icon_2_n, this.mBIntent));

        localTabHost.addTab(buildTabSpec("C_TAB",
                R.string.upload, R.drawable.icon_3_n,
                this.mCIntent));

        localTabHost.addTab(buildTabSpec("D_TAB", R.string.activityFeed,
                R.drawable.icon_4_n, this.mDIntent));

        localTabHost.addTab(buildTabSpec("MORE_TAB", R.string.profile,
                R.drawable.icon_5_n, this.mEIntent));

    }

    private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
                                         final Intent content) {
        return this.mTabHost.newTabSpec(tag).setIndicator(getString(resLabel),
                getResources().getDrawable(resIcon)).setContent(content);
    }

    // Tab bar ends

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }*/
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

        //return super.onCreateOptionsMenu(menu);
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
        else if (id == R.id.add) {
            takePhoto();
            return true;
        }
        else if (id == R.id.add_album) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // Camera functions - Fan , Unused
    private void takePhoto() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //we will handle the returned data in onActivityResult
        startActivityForResult(captureIntent, CAMERA_CAPTURE);

    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    // Camera functions
    final int CAMERA_CAPTURE = 1;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<String> listOfImagesPath = UploadActivity.listOfImagesPath;
    public static final String GridViewDemo_ImagePath = UploadActivity.GridViewDemo_ImagePath;

    /*
    // On constriction - Fan
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if(requestCode == CAMERA_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                String imgcurTime = dateFormat.format(new Date());
                File imageDirectory = new File(GridViewDemo_ImagePath);
                imageDirectory.mkdirs();
                String _path = GridViewDemo_ImagePath + imgcurTime + ".jpg";
                try {
                    FileOutputStream out = new FileOutputStream(_path);
                    thePic.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.close();
                } catch (FileNotFoundException e) {
                    e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                listOfImagesPath = null;
                listOfImagesPath = UploadActivity.RetriveCapturedImagePath();
                if (listOfImagesPath != null) {
                    UploadActivity.grid.setAdapter(new UploadActivity.ImageListAdapter(this, listOfImagesPath));
                }

            }
        }
    }
    /*
    // Receive Result from camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }

        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }
    }*/


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // Tab bar used
        if(isChecked){
            switch (buttonView.getId()) {
                case R.id.radio_button0:
                    this.mTabHost.setCurrentTabByTag("A_TAB");
                    break;
                case R.id.radio_button1:
                    this.mTabHost.setCurrentTabByTag("B_TAB");
                    break;
                case R.id.radio_button2:
                    this.mTabHost.setCurrentTabByTag("C_TAB");
                    break;
                case R.id.radio_button3:
                    this.mTabHost.setCurrentTabByTag("D_TAB");
                    break;
                case R.id.radio_button4:
                    this.mTabHost.setCurrentTabByTag("MORE_TAB");
                    break;
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }


    }

    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        // The activity is either being restarted or started for the first time
        Log.e("MainActivity", "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        // Activity being restarted from stopped state
        Log.e("MainActivity", "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("MainActivity", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("MainActivity", "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("MainActivity", "onStop");
    }

}
