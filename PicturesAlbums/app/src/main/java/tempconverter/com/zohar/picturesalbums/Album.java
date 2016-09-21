package tempconverter.com.zohar.picturesalbums;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Album extends Activity implements View.OnClickListener{
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int REQUEST_CAMERA_RESULT=201;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri;
    Button btnCamera;
    static TextView txtAlbumName;
    static String albumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        bindUI();
    }

    public void bindUI(){
        albumName = getString(R.string.album_name);

        // Getting the widgets
        txtAlbumName = (TextView) findViewById(R.id.act_album_txtAlbumName);
        btnCamera = (Button) findViewById(R.id.act_album_btnStartCamera);

        // Setting the album name from shared preference
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE);
        String albumName = sharedPref.getString(getString(R.string.album_name), "");
        txtAlbumName.setText(albumName);
        txtAlbumName.setPaintFlags(txtAlbumName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Setting click listener
        btnCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.act_album_btnStartCamera:{
                startCamera();
                break;
            }
        }
    }

    public void startCamera(){
        if(checkPermission())
            getPic();
        else {
            // Asking a permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, REQUEST_CAMERA_RESULT);
        }
    }

    public boolean checkPermission() {
        // Checking if the version of the cellphone is 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // In case the version is 23+, we should ask from the user a permission to use the camera
            // Checking if the permission was already granted
            if (Album.this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                Album.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                Album.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public void getPic(){
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // create a file to save the image
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Going to the Picture activity
            Intent intentPicture = new Intent(this, Picture.class);
            intentPicture.putExtra(getString(R.string.image), fileUri);
            startActivity(intentPicture);
            finish();
        }
    }

    private static File getOutputMediaFile(int type){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // Getting the external storage directory
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES), albumName);

            // Create the storage directory (if it does not exist) of the application album
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("Create directory", "failed to create directory");
                    return null;
                }
            }

            mediaStorageDir = new File(mediaStorageDir.getPath(), txtAlbumName.getText().toString());

            // Create the storage directory of the new album
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("Create directory", "failed to create directory");
                    return null;
                }
            }

            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile;
            if (type == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + "_" + timeStamp + ".jpg");
            } else if (type == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "VID_" + txtAlbumName.getText().toString() + "_" + timeStamp + ".mp4");
            } else {
                return null;
            }

            return mediaFile;
        }

        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}

