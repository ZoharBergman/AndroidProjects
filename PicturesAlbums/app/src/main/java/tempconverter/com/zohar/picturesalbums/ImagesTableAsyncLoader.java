package tempconverter.com.zohar.picturesalbums;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ImagesTableAsyncLoader extends AsyncTask<Void,Void,TableLayout> {

    TableLayout tableLayout;
    String albumDir;
    Context context;
    IImageAsyncTaskLoaderListener listener;

    public static final int IMAGE_TABLE_COLS_NUM = 3;

    public ImagesTableAsyncLoader(TableLayout tableLayout,String albumDir,Context context,IImageAsyncTaskLoaderListener listener){
        this.tableLayout = tableLayout;
        this.albumDir = albumDir;
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected TableLayout doInBackground(Void... v) {
        return setImagesTable();
    }

    @Override
    protected void onPostExecute(TableLayout tableLayout) {
        super.onPostExecute(tableLayout);
        if (tableLayout != null)
            this.tableLayout.addView(tableLayout);

        if(listener != null){
            listener.didFinishLoadingImage();
        }
    }


    public TableLayout setImagesTable(){
        TableLayout tabImages = new TableLayout(context);

        // Getting the images from directory
        ArrayList<File> alImages = MyFiles.getFiles(new File(albumDir));

        if(alImages == null)
            return null;

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Setting of the bitmap images
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inSampleSize = 2;
        opts.inDither = true;

        int col = 0;
        TableRow row = new TableRow(context);

        for(File image : alImages){
            try {
                if (col%IMAGE_TABLE_COLS_NUM == 0) {
                    // Creating row
                    row = new TableRow(context);
                    row.setLayoutParams(new TableRow.LayoutParams(layoutParams));
                }

                // Getting the image bitmap from file
                Bitmap bm = MyFiles.getImageFromFile(image.getPath());

                // Creating image button
                MyImageButton imageButton = new MyImageButton(context, image.getPath());
                imageButton.setImageBitmap(scaleCenterCrop(bm,dpToPx(100),dpToPx(100)));
                imageButton.setBackground(null);
                imageButton.setOnClickListener((Album)context);

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

        return tabImages;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

// Compute the scaling factors to fit the new height and width, respectively.
// To cover the final image, the final scaling will be the bigger
// of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

// Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

// Let's find out the upper left coordinates if the scaled bitmap
// should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

// The target rectangle for the new, scaled version of the source bitmap will now
// be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

// Finally, we create a new bitmap of the specified size and draw our new,
// scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }
}
