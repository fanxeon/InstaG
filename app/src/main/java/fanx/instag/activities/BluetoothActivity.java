package fanx.instag.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fanx.instag.R;

public class BluetoothActivity extends Activity {
    private final static int REQUEST_ENABLE_BT = 1;
    private Set<BluetoothDevice> pairedDevices;
    private ListView listView_PairedDevices;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        listView_PairedDevices = (ListView)findViewById(R.id.listView_PairedDevices);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(BluetoothActivity.this, "The device does not support bluetooth", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (mBluetoothAdapter.isEnabled()) {
            Log.e("onCreate", "Bluetooth is enabled in the device");
            viewPairedDevices();

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
            viewPairedDevices();
        }
    }

    public void viewPairedDevices(){
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            ArrayList list = new ArrayList();

            for (BluetoothDevice btDevice : pairedDevices) {
                list.add(btDevice.getName() + "\n" + btDevice.getAddress());
            }
            Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();

            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
            listView_PairedDevices.setAdapter(adapter);

            listView_PairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // When clicked, show a toast with the TextView text
                    Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
                    /*Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("image*//*");
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sending Icon");
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, "Test String");
                    intent.putExtra(Intent.EXTRA_STREAM, ResourceToUri(getApplicationContext(), R.drawable.icon_5_n));*/
                    //System.out.print("**************DCIM Path: "+Environment.DIRECTORY_DCIM+"/Other/Small-Talk-image-211x300.jpeg");

                   /* PackageManager pm = getPackageManager();
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
                            startActivity(Intent.createChooser(intent, "Send the image"));
                        }
                    }*/


                }
            });
        }
    }

    public  Uri ResourceToUri (Context context,int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID) );
    }

}
