package tempconverter.com.zohar.picturesalbums;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Zohar on 23/09/2016.
 */
public class MyFiles {

    public static ArrayList<File> getFiles(File parentDir){
        File[] files = parentDir.listFiles();
        if (files != null)
            return new ArrayList<>(Arrays.asList(files));

        return null;
    }

    public static Bitmap getImageFromFile(String path){
        return setOrientation(path);
    }

    public static Bitmap setOrientation(String path){
        try {
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            opts.inSampleSize = 2;
            opts.inDither = true;
            Bitmap bm = BitmapFactory.decodeFile(path, opts);

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
            Log.e("Read image from file", ex.getMessage());
        }
        return null;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Boolean deleteFile(String path){
        File deletedFile = new File(path);
        if (deletedFile.isDirectory()){
                String[] children = deletedFile.list();
                for (int i = 0; i < children.length; i++)
                {
                    new File(path, children[i]).delete();
                }
        }
        return deletedFile.delete();
    }

    public static String getFileDate(String path){
        File file = new File(path);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date(file.lastModified()));
    }
}
