package tempconverter.com.zohar.picturesalbums;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Zohar on 17/09/2016.
 */
public class MyImageView extends ImageButton {
    private String location;
    private String comment;

    public MyImageView(Context context, AttributeSet attrs){
        super(context, attrs);
        setBackground(null);
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public String getComment(){
        return this.comment;
    }
}
