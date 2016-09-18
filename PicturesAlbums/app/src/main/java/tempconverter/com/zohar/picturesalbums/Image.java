package tempconverter.com.zohar.picturesalbums;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Zohar on 17/09/2016.
 */
public class Image extends ImageView {
    private TextView location;
    private TextView comment;

    public Image(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public void setLocation(String location){
        this.location.setText(location);
    }

    public void setComment(String comment){
        this.comment.setText(comment);
    }
}
