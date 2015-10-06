package fanx.instag.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fanx.instag.R;
import fanx.instag.activities.util.ImageLoadTask;
import fanx.instag.activities.util.InstagramUser;

public class BluetoothActivity extends Activity {
    private final static int REQUEST_ENABLE_BT = 1;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothAdapter mBluetoothAdapter;
    private ListView listView_Generic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        listView_Generic = (ListView)findViewById(R.id.listView_Generic);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(BluetoothActivity.this, "The device does not support bluetooth", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (mBluetoothAdapter.isEnabled()) {
            Log.e("onCreate", "Bluetooth is enabled in the device");
            //viewPairedDevices();
            viewPhotosToSwipe();
        }else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth, menu);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            //viewPairedDevices();
            viewPhotosToSwipe();
        }
    }


    public void viewPhotosToSwipe()
    {
        ArrayList<String> filePaths =  new ArrayList<String>();

        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera/");

        String filePath = null;
        for (File file: f.listFiles())//Filter can be used here to only show the photos taken by app
        {
            try {
                filePath = file.getAbsolutePath() ;
                filePaths.add(filePath);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        CustomAdapter adapter = new CustomAdapter(this, filePaths);
        listView_Generic.setAdapter(adapter);
    }

    public void viewPairedDevices()
    {

        pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            ArrayList list = new ArrayList();

            for (BluetoothDevice btDevice : pairedDevices) {
                list.add(btDevice.getName() + "\n" + btDevice.getAddress());
            }
            Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();

            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
            listView_Generic.setAdapter(adapter);

        }
    }

    public  Uri ResourceToUri (Context context,int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID) );
    }

    private class CustomAdapter extends BaseAdapter {
        int count;
        private LayoutInflater layoutInflater;
        private ArrayList<String> filePaths = new ArrayList<String>();
        Context context;

        public CustomAdapter(Context context, ArrayList<String> filePaths){
            layoutInflater = LayoutInflater.from(context);
            this.filePaths = filePaths;
            this.count = filePaths.size();
            this.context = context;
        }

        @Override
        public int getCount()
        {
            return count;
        }

        public View getView(final int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;

            if(convertView == null)
            {
                convertView = layoutInflater.inflate(R.layout.image_layout, null);
                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                holder.button_Send = (Button) convertView.findViewById(R.id.button_Send);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            (holder.imageView).setImageURI(Uri.parse(filePaths.get(position)));
            (holder.button_Send).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // When clicked, show a toast with the TextView text
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("image/jpeg");

                    /*
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sending Photo");
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, "Text Message");
                    */

                    File f = new File(filePaths.get(position));

                    Log.e("File", f.getAbsolutePath()+" - "+Boolean.toString(f.isFile()));

                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));

                    PackageManager pm = getPackageManager();
                    List<ResolveInfo> appsList = pm.queryIntentActivities(intent, 0);

                    if (appsList.size() > 0) {
                        String packageName = null;
                        String className = null;
                        boolean found = false;

                        for (ResolveInfo info : appsList) {
                            packageName = info.activityInfo.packageName;
                            if (packageName.equals("com.android.bluetooth")) {
                                className = info.activityInfo.name;
                                found = true;
                                break;
                            }
                        }

                        if (found) {
                            intent.setClassName(packageName, className);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Bluetooth haven't been found or paired", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            return convertView;

        }

        @Override
        public Object getItem(int position)
        {
            return filePaths.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

    }

    private class ViewHolder{
        public ImageView imageView;
        public Button button_Send;
    }
}
