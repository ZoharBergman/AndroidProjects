package tempconverter.com.zohar.picturesalbums;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {
    Button btnCreateAlbum;
    Button btnWatchAlbum;
    Boolean is_permmited = false;
    final static int REQUEST_READ_WRITE_EXTERNAL_STORAGE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindUI();
    }

    public void bindUI(){
        // Getting the buttons
        btnCreateAlbum = (Button) findViewById(R.id.act_main_btnCreateAlbum);
        btnWatchAlbum = (Button) findViewById(R.id.act_main_btnWatchAlbum);

        // Setting click listeners
        btnCreateAlbum.setOnClickListener(this);
        btnWatchAlbum.setOnClickListener(this);

        // Ask permissions to write and read from external storage
        handlePermissions();
    }

    public void handlePermissions(){
        if (MyPermissions.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                MyPermissions.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE))
            is_permmited = true;
        else {
            MyPermissions.askPermission(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    this, REQUEST_READ_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    is_permmited = true;
                    Toast.makeText(this,"Please enter your action.",Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.act_main_btnCreateAlbum: {
                dialogAlbumName();
                break;
            }
            case R.id.act_main_btnWatchAlbum:
                goAlbumsList();
                break;
        }
    }

    public void dialogAlbumName(){
        if(!is_permmited)
            handlePermissions();
        else {
            AlbumNameDialog dialog = new AlbumNameDialog(this);
            dialog.show();
        }
    }

    public void goAlbumsList(){
        if(!is_permmited)
            handlePermissions();
        else {
            Intent albumsListIntent = new Intent(this, AlbumsList.class);
            this.startActivity(albumsListIntent);
            this.finish();
        }
    }
}
