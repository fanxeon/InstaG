package fanx.instag.activities;

import android.app.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.security.Policy;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Parameters;
import android.widget.ImageView;
import android.widget.Toast;

import fanx.instag.R;
import fanx.instag.utils.CameraPreview;

public class CameraActivity extends Activity {
    protected static final String TAG = "main";
    private Camera mCamera;
    private CameraPreview mPreview;
    private boolean isLighOn = false;
    private boolean gridLineOn = false;

    @Override
    protected void onStop() {
        super.onStop();

        if (mCamera != null) {
            mCamera.release();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mCamera = getCameraInstance();

        // 创建预览类，并与Camera关联，最后添加到界面布局中
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        // Buttons
        Button captureButton = (Button) findViewById(R.id.button_capture);
        Button gridButton = (Button) findViewById(R.id.button_grid);
        Button flashButton = (Button) findViewById(R.id.button_Flashlight);

        Context context = this;
        PackageManager pm = context.getPackageManager();
        // Grid Line
        final ImageView gridLine = (ImageView) findViewById(R.id.grid);
        gridLine.setVisibility(View.INVISIBLE);

        // if device support camera?
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.e("err", "Device has no camera!");
            return;
        }
        final Parameters p = mCamera.getParameters();

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Auto - focus on camera
                mCamera.autoFocus(new AutoFocusCallback() {

                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        // Capture from camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                });
            }
        });
        // ! ERRORS
        gridButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gridLineOn == false) {
                    Toast.makeText(getApplicationContext(), "Grid line Enabled",
                            Toast.LENGTH_SHORT).show();
                    gridLine.setVisibility(View.VISIBLE);
                    gridLineOn = true;
                } else if (gridLineOn == true) {
                    Toast.makeText(getApplicationContext(), "Grid line Disabled",
                            Toast.LENGTH_SHORT).show();
                    gridLine.setVisibility(View.INVISIBLE);
                    gridLineOn = false;
                }
            }
        });
        // FLASH light option
        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLighOn) {

                    Log.i("info", "torch is turn off!");

                    p.setFlashMode(Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(p);
                    mCamera.startPreview();
                    isLighOn = false;

                } else {

                    Log.i("info", "torch is turn on!");

                    p.setFlashMode(Parameters.FLASH_MODE_TORCH);

                    mCamera.setParameters(p);
                    mCamera.startPreview();
                    isLighOn = true;

                }
            }

        });
    }

    //Detect Camera support
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {

            return true;
        } else {

            return false;
        }
    }

    /** 打开一个Camera */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            Log.d(TAG, "Fail to open camera");
        }
        return c;
    }

    private PictureCallback mPicture = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // Save jpg to sd
            File pictureFile = new File("/sdcard/" + System.currentTimeMillis()
                    + ".jpg");
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (Exception e) {
                Log.d(TAG, "Fail to save picture");
            }
        }
    };

    @Override
    protected void onDestroy() {
        // Release Camera
        if(mCamera!=null){
            mCamera.release();
            mCamera=null;
        }
        super.onDestroy();
    }
    public void onPause() {
        super.onPause();
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }
}