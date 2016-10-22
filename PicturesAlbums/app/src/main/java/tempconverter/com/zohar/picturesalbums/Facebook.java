package tempconverter.com.zohar.picturesalbums;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;

import java.util.Arrays;
import java.util.List;

public class Facebook extends Activity {

    private CallbackManager callbackManager;
    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        //this loginManager helps you eliminate adding a LoginButton to your UI
        LoginManager manager = LoginManager.getInstance();
        manager.logInWithPublishPermissions(this, Arrays.asList("publish_actions"));

        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                sharePhotoToFacebook();
                Toast.makeText(Facebook.this, "The picture was uploaded to Facebook! Check your profile.", Toast.LENGTH_LONG).show();
                onBackPressed();
            }

            @Override
            public void onCancel()
            {
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("onError");
                Toast.makeText(Facebook.this, "There was a problem uploading the picture.", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });
    }

    private void sharePhotoToFacebook(){
        Bundle bundle = getIntent().getExtras();
        String imagePath = (String) bundle.get(getString(R.string.facebook_pic));
        String comment = (String) bundle.get(getString(R.string.pic_comment));
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(MyFiles.getImageFromFile(imagePath))
                .setCaption(comment)
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, null);

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }
}
