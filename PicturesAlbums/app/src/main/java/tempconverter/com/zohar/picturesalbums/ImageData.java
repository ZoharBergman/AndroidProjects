package tempconverter.com.zohar.picturesalbums;

/**
 * Created by Zohar on 24/09/2016.
 */
public class ImageData {
    private String image_name;
    private String location;
    private String comment;

    public ImageData(String image_name, String location, String comment) {
        this.image_name = image_name;
        this.location = location;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }
}
