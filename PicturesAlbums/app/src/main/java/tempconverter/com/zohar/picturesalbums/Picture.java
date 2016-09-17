package tempconverter.com.zohar.picturesalbums;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

public class Picture extends Activity {
    ImageView image;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        bindUI();
    }

    public void bindUI(){
        try {
//            Bitmap imageBitmap = (Bitmap) getIntent().getExtras().get(String.valueOf(R.string.image));
            image = (ImageView) findViewById(R.id.act_picture_image);
            fileUri = (Uri) getIntent().getExtras().get(getString(R.string.image));
//            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
            Bitmap bm = setOrientation();
            image.setImageBitmap(bm);
//            image.setImageURI(fileUri);
        }
        catch (Exception e){
            Log.e("Put image", e.getMessage());
        }
    }

    public Bitmap setOrientation(){
        try {
            ExifInterface ei = new ExifInterface(fileUri.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);

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

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent albumIntent = new Intent(this, Album.class);
        startActivity(albumIntent);
        finish();
    }
}
