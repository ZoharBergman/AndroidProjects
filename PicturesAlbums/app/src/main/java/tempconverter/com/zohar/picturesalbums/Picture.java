package tempconverter.com.zohar.picturesalbums;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

public class Picture extends AppCompatActivity {
    MyImageView image;
    String imagePath;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        bindUI();
    }

    public void bindUI(){
        try {
            // Binding widgets
            image = (MyImageView) findViewById(R.id.act_picture_image);
            toolbar = (Toolbar) findViewById(R.id.act_picture_toolbar);
            toolbar.setTitle("");
            toolbar.setNavigationIcon(R.mipmap.ic_keyboard_return_white_48dp);
            setSupportActionBar(toolbar);

            // Get Uri file
            imagePath = (String) getIntent().getExtras().get(getString(R.string.image));
            Bitmap bm = MyFiles.getImageFromFile(imagePath);
            image.setImageBitmap(bm);
        }
        catch (Exception e){
            Log.e("Put image", e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent albumIntent = new Intent(this, Album.class);
        startActivity(albumIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_comment:{
                break;
            }
            case R.id.action_delete:{
                MyFiles.deleteFile(imagePath);
                onBackPressed();
                break;
            }
            case R.id.action_location:{
                break;
            }
            default:{
                onBackPressed();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
