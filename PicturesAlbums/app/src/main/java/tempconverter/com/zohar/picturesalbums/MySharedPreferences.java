package tempconverter.com.zohar.picturesalbums;

import android.content.Context;

/**
 * Created by Zohar on 23/09/2016.
 */
public class MySharedPreferences {
    public static String getDataFromSharedPreference(int dataId, Context context){
        android.content.SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_pref), Context.MODE_PRIVATE);
        String data = sharedPref.getString(context.getString(dataId), "");
        return data;
    }

    public static void saveOnsharedPreference(int dataId, Context context, String data){
        android.content.SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_pref), Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(dataId), data);
        editor.commit();
    }
}
