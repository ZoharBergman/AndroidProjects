package tempconverter.com.zohar.picturesalbums;

import android.content.Context;
import android.widget.ImageButton;

/**
 * Created by Zohar on 23/09/2016.
 */
public class MyImageButton extends ImageButton{
    private String filePath;

    public MyImageButton(Context context, String filePath){
        super(context);
        this.filePath = filePath;
    }

    public String getFilePath(){
        return this.filePath;
    }
}
