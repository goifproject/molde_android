package com.limefriends.molde.common.manager.cameraHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.limefriends.molde.R;
import com.limefriends.molde.common.manager.galleryHelper.MoldeReportGalleryActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeReportCameraActivity extends AppCompatActivity {

    @BindView(R.id.molde_camera_layout)
    RelativeLayout molde_camera_layout;
    @BindView(R.id.molde_camera_view)
    SurfaceView molde_camera_view;
    @BindView(R.id.molde_camera_capture_button)
    ImageView molde_camera_capture_button;
    @BindView(R.id.molde_gallary_button)
    ImageView molde_gallary_button;

    CameraPreview cameraPreview;
    Camera camera;

    private static final String TAG = "카메라 디버그 로그";
    public static final int TAKE_PICTURE_FOR_ADD_IMAGE = 994;
    private final static int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK;

    private int imageArraySize;
    private int imageSeq = 0;
    public Bitmap bitmap;

    public void startCamera() {
        if (cameraPreview == null) {
            cameraPreview = new CameraPreview(this, molde_camera_view);
            cameraPreview.setLayoutParams(
                    new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            molde_camera_layout.addView(cameraPreview);
            cameraPreview.setKeepScreenOn(true);
        }

        cameraPreview.setCamera(null);

        if (camera != null) {
            camera.release();
            camera = null;
        }

        // 카메라 개수 파악
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                // 정면 카메라 선택
                camera = Camera.open(CAMERA_FACING);
                // 카메라 방향 설정
                camera.setDisplayOrientation(
                        setCameraDisplayOrientation(this, CAMERA_FACING, camera));
                // get Camera parameters
                Camera.Parameters params = camera.getParameters();
                // picture image orientation
                params.setRotation(setCameraDisplayOrientation(this, CAMERA_FACING, camera));
                // 미리보기 시작
                camera.startPreview();
            } catch (RuntimeException ex) {
                Toast.makeText(this, "camera_not_found " + ex.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                Log.d(TAG, "camera_not_found " + ex.getMessage().toString());
            }
        }
        // 미리보기 화면에 띄워줌
        cameraPreview.setCamera(camera);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_camera);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        imageSeq = intent.getIntExtra("imageSeq", 1);
        imageArraySize = intent.getIntExtra("imageArraySize", 1);

        setupViews();
    }

    private void setupViews() {
        //액션바 구현
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText("카메라");

        setupListener();
    }

    private void setupListener() {

        // 갤러리 선택 기능 구현
        molde_gallary_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MoldeReportGalleryActivity.class);
                intent.putExtra("imageSeq", imageSeq);
                intent.putExtra("imageArraySize", imageArraySize);
                startActivityForResult(intent, TAKE_PICTURE_FOR_ADD_IMAGE);
            }
        });

        //카메라 버튼 기능 구현
        molde_camera_capture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                        } else {

                        }
                    }
                });
            }
        });

        molde_camera_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPreview.setCheckCameraUse(true);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
    }

    // 생명주기를 통해
    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.stopPreview();
            cameraPreview.setCamera(null);
            camera.release();
            camera = null;
        }
        molde_camera_layout.removeView(cameraPreview);
        cameraPreview = null;
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "onPictureTaken - shutterCallback");
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken - rawCallback");
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken - jpegCallback");
            //이미지의 너비와 높이 결정
            int w = camera.getParameters().getPictureSize().width;
            int h = camera.getParameters().getPictureSize().height;

            int orientation = setCameraDisplayOrientation(MoldeReportCameraActivity.this,
                    CAMERA_FACING, camera);

            //byte array를 bitmap으로 변환
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            //int w = bitmap.getWidth();
            //int h = bitmap.getHeight();

            //이미지를 디바이스 방향으로 회전
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);

            //bitmap을 byte array로 변환
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] currentData = stream.toByteArray();

            //파일로 저장
            new SaveImageTask().execute(currentData);
            //resetCam();
        }
    };

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/molde");
                dir.mkdirs();

                long systemTime = System.currentTimeMillis();

                String fileName = String.format("%d.jpg", systemTime);
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to "
                        + outFile.getAbsolutePath());

                refreshGallery(outFile);
            } catch (IOException  e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void refreshGallery(File file) {

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MoldeReportCheckImageActivity.class);
        intent.putExtra("imageSeq", imageSeq);
        intent.putExtra("imagePath", Uri.fromFile(file));
        startActivityForResult(intent, TAKE_PICTURE_FOR_ADD_IMAGE);
    }

    public static int setCameraDisplayOrientation(Activity activity,
                                                  int cameraId, android.hardware.Camera camera) {

        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();

        camera.getCameraInfo(cameraId, info);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == TAKE_PICTURE_FOR_ADD_IMAGE) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}

