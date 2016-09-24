package tempconverter.com.zohar.picturesalbums;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class Picture extends AppCompatActivity {
    MyImageView image;
    EditText etComment;
    EditText etLocation;
    String imagePath;
    Toolbar toolbar;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        bindUI();
    }

    public void bindUI(){
        try {
            // Binding widgets
            image = (MyImageView) findViewById(R.id.act_picture_image);
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
        Intent albumIntent = new Intent(this, Album.class);
        startActivity(albumIntent);
        finish();
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
                break;
            }
            default:{
                onBackPressed();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
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
                    etComment.setText(result.get(0));
                }
                break;
            }

        }
    }

    public void saveImageData(){
        DB myDB = new DB(this);
        myDB.insertOrUpdateRow(new ImageData(imagePath, etLocation.getText().toString(), etComment.getText().toString()));
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
            etComment.setText(data.getComment());
            etLocation.setText(data.getLocation());
        }
    }
}
