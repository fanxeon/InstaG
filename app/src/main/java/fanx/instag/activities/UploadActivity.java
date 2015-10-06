package fanx.instag.activities;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import fanx.instag.R;
import fanx.instag.utils.AddToCacheTask;
import fanx.instag.utils.Utils;
import fanx.instag.adapter.ImageAdapter;
import fanx.instag.adapter.ImageCache;
import fanx.instag.database.DatabaseManager;
import fanx.instag.database.Photo;
import fanx.instag.database.PhotoManager;

public class UploadActivity extends Activity implements OnClickListener {
    // Declaration take photo
    Button captureBtn = null;
    Button captureBtn2 = null;
    final int CAMERA_CAPTURE = 1;
    private Uri picUri;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String photoPath = null;
    String newPhotoID = null;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    // Grid view and image path
    public static 	GridView gridview;
    public static  List<String> listOfImagesPath;
    Bitmap gridBitmap = null;
    public static final String GridViewDemo_ImagePath =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/sdcard/InstaG/";

    // Individual view and animation effect
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration; // Animation effect time
    private int j = 0; // counter

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private ImageView expandedImageView;
    // NEW
    protected ImageAdapter imgadapter;
    private ArrayList<String> list = null;
    private String descriptionStr = null;
    Bitmap individualBitmap = null;
    AlertDialog.Builder albumDialog;
    int indx = 0;
    ArrayList<String> albumList = null;
    public final static String LIST_NAME = "AdapterList";
    public final static String STRING_ID = "string_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c);
        captureBtn = (Button)findViewById(R.id.capture_btn1);
        captureBtn.setOnClickListener(this);
        //NEW
        captureBtn2 = (Button)findViewById(R.id.capture_btn2);
        captureBtn2.setOnClickListener(this);
        // Grid view
        DatabaseManager db = DatabaseManager.getInstance(this.getApplicationContext());
        list = db.getPhotoIDs();
        ImageCache cache = getImageCache(this.getFragmentManager());
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setDrawSelectorOnTop(true);
        imgadapter = new ImageAdapter(this,cache);
        imgadapter.setList(list);
        gridview.setAdapter(imgadapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                toIndividual(v, position);
            }
        });
        /*
        listOfImagesPath = null;
        listOfImagesPath = RetriveCapturedImagePath();
        if(listOfImagesPath!=null){
            grid.setAdapter(new ImageListAdapter(this, listOfImagesPath));
            // React on click items and zoom in
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // test
                    Toast.makeText(getApplicationContext(), "pos/id: " + position + id,
                            Toast.LENGTH_LONG).show();

                    // Jump to individual intent
                    Intent individualIntent = new Intent(UploadActivity.this, ImageDetailActivity.class);
                    individualIntent.putExtra("img", listOfImagesPath.get(position));
                    // BUG here
                    individualIntent.putExtra("position", listOfImagesPath.get(position));
                    individualIntent.putExtra("id", id);
                    startActivity(individualIntent);
                }
            });

        }*/




    }


    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        if (arg0.getId() == R.id.capture_btn1) {
            newPhotoID = PhotoManager.getInstance(this).getCurrentTimeStampAsString();
            File fileDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File photoFile = null;
            Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {
                photoFile = File.createTempFile(newPhotoID, ".jpg", fileDirectory);
                photoPath = photoFile.getAbsolutePath();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            if (photoFile != null) {
                takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));

                //takePicIntent.putExtra(Photo.PHOTO_ID, photoID);
                startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE);
                //use standard intent to capture an image

            }


            // Test Custom Camera
        }else if (arg0.getId() == R.id.capture_btn2) {

            try {
                customCamera();
            } catch (ActivityNotFoundException anfe) {
                //display an error message
                String errorMessage = "Whoops - your device doesn't support capturing images!";
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add){
            // Click on action bar take pic
            //takePicture();
            customCamera();
            return true;
        }
        else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void customCamera() {
        Intent CameraIntent = new Intent(UploadActivity.this, CameraActivity.class);
        startActivityForResult(CameraIntent, CAMERA_CAPTURE);

    }

    // On construction
    static final int PICK_CONTACT_REQUEST = 1;  // The request code



    /*
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
                    listOfImagesPath = RetriveCapturedImagePath();
                    if (listOfImagesPath != null) {
                        grid.setAdapter(new ImageListAdapter(this, listOfImagesPath));
                    }

                }
            }
        }*/
    public ArrayList<String> getList() {
        return imgadapter.getList();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            descriptionStr = "Temp";

            gridBitmap = Utils.getGridBitmapFromFile(photoPath, getApplicationContext());

            if (gridBitmap != null) {
                individualBitmap = Utils.getBitmapFromFile(photoPath);

                // -- Get Album list on Capture view-- //
                albumList = DatabaseManager.getInstance(getApplicationContext()).getAlbumNames();
                albumList.toArray(new String[albumList.size()]);
                indx = 0;
                AddToCacheTask cacheTask = new AddToCacheTask(getApplicationContext(), getImageCache(getFragmentManager()), gridBitmap);
                cacheTask.executeOnExecutor(Utils.getThreadPoolExecutorInstance(), newPhotoID);
                Photo newPhoto = new Photo(newPhotoID, descriptionStr, individualBitmap,
                        gridBitmap, albumList.get(indx), false);

                getList().add(newPhotoID);

                DatabaseWorker dbWorker = new DatabaseWorker();
                dbWorker.executeOnExecutor(Utils.getThreadPoolExecutorInstance(), newPhoto);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Unable to take photo. Try later.", Toast.LENGTH_LONG).show();
            }
            update();
        }
    }
    public void update()
    {
        imgadapter.notifyDataSetChanged();
    }
    public static List<String> RetriveCapturedImagePath() {
        List<String> tFileList = new ArrayList<String>();
        List<String> reverseFileList = new ArrayList<String>();
        List<String> tmpList = new ArrayList<String>();
        File f = new File(GridViewDemo_ImagePath);

        if (f.exists()) {
            File[] files=f.listFiles();
            Arrays.sort(files);

            for(int i = 0; i < files.length; i ++){
                File file = files[i];
                if(file.isDirectory())
                    continue;
                tFileList.add(file.getPath());
            }
        }
        // Copy original list
        tmpList = tFileList;

        @SuppressWarnings("rawtypes")
        ListIterator myIterator = tmpList.listIterator(tmpList.size());

        // Iterate in reverse direction
        while (myIterator .hasPrevious()) {
            reverseFileList.add((String) myIterator.previous());
        }
        // Keep the latest photo up
        return reverseFileList;
    }

    public static class ImageListAdapter extends BaseAdapter {
        private Context context;
        private List<String> imgPic;
        public ImageListAdapter(Context c, List<String> thePic)        {
            context = c;
            imgPic = thePic;
        }
        public int getCount() {
            if(imgPic != null)
                return imgPic.size();
            else
                return 0;
        }

        //---returns the ID of an item---
        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        //---returns an ImageView view---
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ImageView imageView;
            BitmapFactory.Options bfOptions=new BitmapFactory.Options();
            bfOptions.inDither=false;                     //Disable Dithering mode
            bfOptions.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
            bfOptions.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
            bfOptions.inTempStorage=new byte[32 * 1024];
            if (convertView == null) {
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setPadding(0, 0, 0, 0);

            } else {
                imageView = (ImageView) convertView;

            }
            FileInputStream fs = null;
            Bitmap bm;


            try {
                fs = new FileInputStream(new File(imgPic.get(position).toString()));

                if(fs!=null) {
                    bm=BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
                    imageView.setImageBitmap(bm);
                    imageView.setId(position);
                    imageView.setLayoutParams(new GridView.LayoutParams(200, 160));
                }

            } catch (IOException e) {
                e.printStackTrace();

            } finally{

                if(fs!=null) {
                    try {
                        fs.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return imageView;
        }
    }

    public void toIndividual(View view, int position) {
        // Do something in response to button
        Intent intent = new Intent(this, IndividualActivity.class);
        //EditText editText = (EditText) findViewById(R.id.edit_message);
        //String message = editText.getText().toString();
        intent.putExtra(STRING_ID, getList().get(position));
        //intent.putStringArrayListExtra(STRING_LIST, getList());
        //Animation
        startActivity(intent);
        //Animation
    }

    private ImageCache getImageCache(FragmentManager fragmentManager) {

        return ImageCache.getInstance(fragmentManager);
    }
    private class DatabaseWorker extends AsyncTask<Photo, Void , Void> {
        public DatabaseWorker() {
            // TODO Auto-generated constructor stub
        }

        @Override
        protected Void doInBackground(Photo... newPhoto){
            //super(newPhoto);
            DatabaseManager.getInstance(getApplicationContext()).addPhoto( newPhoto[0], 50);


            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //imgadapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "Photo saved.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        Log.e("UploadActivity", "onResume");
        super.onResume();  // Always call the superclass method first
    }
    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        // Activity being restarted from stopped state
        Log.e("UploadActivity", "onRestart");
        gridview.smoothScrollToPosition(0);
        if(Utils.isIndividualPhotoDeleted())
        {
            getList().remove(Utils.getDeletedPhotoID());
            imgadapter.notifyDataSetChanged();
            Utils.setIndividualPhotoDeleted(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("UploadActivity", "onPause");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e("UploadActivity", "onStop");
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        //public final static String LIST_NAME = "AdapterList";
        savedInstanceState.putStringArrayList(LIST_NAME, imgadapter.getList());
        System.out.println("Length of list being saved to state is " + list.size());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

}
