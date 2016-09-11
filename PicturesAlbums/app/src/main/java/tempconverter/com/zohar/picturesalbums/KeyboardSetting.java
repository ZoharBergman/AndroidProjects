package tempconverter.com.zohar.picturesalbums;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Zohar on 01/09/2016.
 */
public class KeyboardSetting {
    Context context;

    public KeyboardSetting(Context context, View view){
        this.context = context;
        setupKeyBoard(view);
    }

    public void setupKeyBoard(View view) {
        try {
            // Set up touch listener for non-text box views to hide keyboard.
            if (!(view instanceof EditText)) {
                view.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        hideSoftKeyboard((Activity) context);
                        return false;
                    }
                });
            }

            //If a layout container, iterate over children and seed recursion.
            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    View innerView = ((ViewGroup) view).getChildAt(i);
                    setupKeyBoard(innerView);
                }
            }
        }
        catch (Exception ex){
            Log.e("setupKeyBoard", ex.getMessage());
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception ex){
            Log.e("hideSoftKeyboard", ex.getMessage());
        }
    }
}
