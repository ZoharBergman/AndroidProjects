package tempconverter.com.zohar.picturesalbums;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

public class Picture extends AppCompatActivity {
    Image image;
    Uri fileUri;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        bindUI();
    }

    public void bindUI(){
        try {
            // Binding widgets
            image = (Image) findViewById(R.id.act_picture_image);
            toolbar = (Toolbar) findViewById(R.id.act_picture_toolbar);
            toolbar.setTitle("");
            toolbar.setNavigationIcon(R.mipmap.ic_keyboard_return_white_48dp);
            setSupportActionBar(toolbar);

            // Get Uri file
            fileUri = (Uri) getIntent().getExtras().get(getString(R.string.image));
            // Setting the orientation of the image and put it in the ImageView
            Bitmap bm = setOrientation();
            image.setImageBitmap(bm);
        }
        catch (Exception e){
            Log.e("Put image", e.getMessage());
        }
    }

    public Bitmap setOrientation(){
        try {
            ExifInterface ei = new ExifInterface(fileUri.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inSampleSize = 2;
            opts.inDither = true;
            Bitmap bm = BitmapFactory.decodeFile(fileUri.getPath(), opts);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bm = rotateImage(bm, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bm = rotateImage(bm, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bm = rotateImage(bm, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    break;
            }

            return bm;
        }
        catch (Exception ex){
            Log.e("URI", ex.getMessage());
        }
        return null;
    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                break;
            }
            case R.id.action_delete:{
                String path = fileUri.getPath();
                File deletedFile = new File(path);
                Boolean isDelete = deletedFile.delete();
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
}
