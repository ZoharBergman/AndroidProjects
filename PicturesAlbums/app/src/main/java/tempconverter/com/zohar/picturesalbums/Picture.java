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
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);
            imageBitmap = setOrientation(imageBitmap);
            image.setImageBitmap(imageBitmap);
        }
        catch (Exception e){
            Log.e("Put image", e.getMessage());
        }
    }

    public Bitmap setOrientation(Bitmap bitmap){
        try {
            ExifInterface ei = new ExifInterface(fileUri.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotateImage(bitmap, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    break;
            }
        }
        catch (Exception ex){
            Log.e("URI", ex.getMessage());
        }

        return bitmap;
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
