package tempconverter.com.zohar.picturesalbums;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Picture extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    ImageView image;
    EditText etComment;
    EditText etLocation;
    String imagePath;
    Toolbar toolbar;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    String location;

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int REQ_CODE_LOCATION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        bindUI();
    }

    @Override
    protected void onStop() {
        disconnectGoogleApiClient();
        super.onStop();
    }

    public void bindUI(){
        try {
            // Binding widgets
            image = (ImageView) findViewById(R.id.act_picture_image);
            etComment = (EditText) findViewById(R.id.act_picture_etComment);
            etLocation = (EditText) findViewById(R.id.act_picture_etLocation);
            toolbar = (Toolbar) findViewById(R.id.act_picture_toolbar);

            // Setting toolbar
            toolbar.setTitle("");
            toolbar.setNavigationIcon(R.mipmap.ic_keyboard_return_white_48dp);
            setSupportActionBar(toolbar);

            // Setting image
            imagePath = (String) getIntent().getExtras().get(getString(R.string.image));
            Bitmap bm = drawTextToBitmap(MyFiles.getImageFromFile(imagePath), MyFiles.getFileDate(imagePath));
            image.setImageBitmap(bm);

            // Setting image data (location and comment)
            getImageData();
            etComment.setPaintFlags(etComment.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            // Setting keyboard
            new KeyboardSetting(this, findViewById(R.id.act_picture));
        }
        catch (Exception e){
            Log.e("Put image", e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveImageData();
        disconnectGoogleApiClient();
        Intent albumIntent = new Intent(this, Album.class);
        startActivity(albumIntent);
        finish();
    }

    public void disconnectGoogleApiClient(){
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_comment:{
                promptSpeechInput();
                break;
            }
            case R.id.action_delete:{
                MyFiles.deleteFile(imagePath);
                onBackPressed();
                break;
            }
            case R.id.action_location:{
                getLocation();
                break;
            }
            default:{
                onBackPressed();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void getLocation(){
        // Create a GoogleApiClient instance
        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }
                break;
            }
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(this, getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etComment.setText("\u200e" + result.get(0));
                }
                break;
            }

        }
    }

    public void saveImageData(){
        DB myDB = new DB(this);
        myDB.insertOrUpdateRow(new ImageData(imagePath,
                                             etLocation.getText().toString().replaceAll("\u200E",""),
                                             etComment.getText().toString().replaceAll("\u200E","")));
    }

    public Bitmap drawTextToBitmap(Bitmap bitmap, String gText) {
        Resources resources = this.getResources();
        float scale = resources.getDisplayMetrics().density;

        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null)
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;

        // resource bitmaps are immutable, so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color
        paint.setColor(Color.rgb(230,181,44));
        // text size in pixels
        paint.setTextSize((int) (15 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas left-top
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = 0;
        int y = bounds.height();

        canvas.drawText(gText, x, y, paint);

        return bitmap;
    }

    public void getImageData(){
        DB myDB = new DB(this);
        ImageData data = myDB.select(imagePath);
        if(data != null) {
            if(!data.getComment().replaceAll("\u200E","").trim().isEmpty())
                etComment.setText("\u200e" + data.getComment());
            if(!data.getLocation().replaceAll("\u200E","").trim().isEmpty())
                etLocation.setText("\u200e" + data.getLocation());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Checking if there is a permission to use location
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                MyPermissions.askPermission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, this, REQ_CODE_LOCATION);
                return;
            }
            else
                // Getting the last location
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        else
            // Getting the last location
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        if(mLastLocation == null)
            Toast.makeText(this, "Unable to reach location. Make sure the location is on", Toast.LENGTH_LONG).show();
        else {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<android.location.Address> addresses;
            try {
                // Decoding the cordinates into address
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude(),1);
                android.location.Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                // Fetch the address lines using getAddressLine and join them
                location = "";
                for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    //addressFragments.add(address.getAddressLine(i));
                    location += address.getAddressLine(i);
                }

                etLocation.setText("\u200E" + location);

            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                Toast.makeText(this, "Unable to reach location. Make sure the location is on", Toast.LENGTH_LONG).show();
            } catch (IOException ioException) {
                // Catch network or other I/O problems.
                Toast.makeText(this, "Unable to reach location. Make sure the location is on", Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
