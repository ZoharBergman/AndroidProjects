package tempconverter.com.zohar.picturesalbums;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Album extends AppCompatActivity implements View.OnClickListener, IImageAsyncTaskLoaderListener{
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int REQUEST_CAMERA_RESULT=201;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int IMAGE_TABLE_COLS_NUM = 3;

    private Uri fileUri;
    Button btnCamera;
    TableLayout tabImages;
    Toolbar toolbar;
    ProgressBar progressBar;
    static TextView txtAlbumName;
    static String albumName;
    static String albumDir;

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
        tabImages = (TableLayout) findViewById(R.id.act_album_tabImages);
        toolbar = (Toolbar) findViewById(R.id.act_album_toolbar);
        progressBar = (ProgressBar) findViewById(R.id.act_album_progressBar);

        // Setting the album name from shared preference
        String albumName = MySharedPreferences.getDataFromSharedPreference(R.string.album_name, this);
        txtAlbumName.setText(albumName);
        txtAlbumName.setPaintFlags(txtAlbumName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Creating album directory storage
        createAlbumDirectory();

        // Setting images table asynchronously
        ImagesTableAsyncLoader loader = new ImagesTableAsyncLoader(tabImages, albumDir, this, this);
        loader.execute();

        // Setting click listener
        btnCamera.setOnClickListener(this);

        // Setting toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.act_album_btnStartCamera:{
                startCamera();
                break;
            }
            default:
                // An image was picked
                Intent intentPicture = new Intent(this, Picture.class);
                intentPicture.putExtra(getString(R.string.image), ((MyImageButton)v).getFilePath());
                startActivity(intentPicture);
                finish();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete: {
                //Boolean isDelete = MyFiles.deleteFile(albumDir);
                //onBackPressed();
                dialogIsDelete();
                break;
            }
            default: {
                onBackPressed();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void startCamera(){
        // Checking if there is a permission to use the camera
        if(MyPermissions.checkPermission(this, Manifest.permission.CAMERA))
            getPic();
        else {
            // Asking permission to use the camera
            MyPermissions.askPermission(new String[]{Manifest.permission.CAMERA}, this, REQUEST_CAMERA_RESULT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_RESULT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPic();
                }
                break;
            }
        }
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
            intentPicture.putExtra(getString(R.string.image), fileUri.getPath());
            startActivity(intentPicture);
            finish();
        }
    }

    private static File getOutputMediaFile(int type){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile;
            if (type == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(albumDir + File.separator + "IMG_" + "_" + timeStamp + ".jpg");
            } else if (type == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(albumDir + File.separator +
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

    public void setImagesTable(){
        // Getting the images from directory
        ArrayList<File> alImages = MyFiles.getFiles(new File(albumDir));

        if(alImages == null)
            return;

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Setting of the bitmap images
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inSampleSize = 2;
        opts.inDither = true;

        int col = 0;
        TableRow row = new TableRow(this);

        for(File image : alImages){
            try {
                if (col%IMAGE_TABLE_COLS_NUM == 0) {
                    // Creating row
                    row = new TableRow(this);
                    row.setLayoutParams(new TableRow.LayoutParams(layoutParams));
                }

                // Getting the image bitmap from file
                Bitmap bm = MyFiles.getImageFromFile(image.getPath());

                // Creating image button
                MyImageButton imageButton = new MyImageButton(this, image.getPath());
                imageButton.setImageBitmap(Bitmap.createScaledBitmap(bm, dpToPx(100), dpToPx(110), false));
                imageButton.setBackground(null);
                imageButton.setOnClickListener(this);

                //  Adding image to row
                row.addView(imageButton);

                if((col%IMAGE_TABLE_COLS_NUM == (IMAGE_TABLE_COLS_NUM - 1)) || (col == alImages.size() - 1))
                    tabImages.addView(row);

                col++;
            }
            catch (Exception e){
                Log.e("Reading image from file", e.getMessage());
            }
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public Boolean createAlbumDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // Getting the external storage directory
            File mediaStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);

            // Create the storage directory (if it does not exist) of the application album
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("Create directory", "failed to create directory");
                    return false;
                }
            }

            mediaStorageDir = new File(mediaStorageDir.getPath(), txtAlbumName.getText().toString());

            // Create the storage directory (if it does not exist) of the album
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("Create directory", "failed to create directory");
                    return false;
                }
            }

            albumDir = mediaStorageDir.getPath();
            return true;
        }

        return false;
    }

    public void dialogIsDelete(){
        // Creating a dialog to check if the user is sure
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete '" + txtAlbumName.getText() + "' album?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyFiles.deleteFile(albumDir);
                onBackPressed();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        builder.show();
    }

    @Override
    public void didFinishLoadingImage() {
        // Disappearing the progress bar
        progressBar.setVisibility(View.GONE);
    }
}


