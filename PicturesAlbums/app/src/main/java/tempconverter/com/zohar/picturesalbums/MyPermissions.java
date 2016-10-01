package tempconverter.com.zohar.picturesalbums;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;

/**
 * Created by Zohar on 30/09/2016.
 */
public abstract class MyPermissions implements View.OnClickListener{

    public static boolean checkPermission(Context context, String permission) {
        // Checking if the version of the cellphone is 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // In case the version is 23+, we should ask from the user a permission to use the camera
            // Checking if the permission was already granted
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public static void askPermission(String[] permissions, Context context, int request_result){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            ((Activity)context).requestPermissions(permissions, request_result);
    }
}
