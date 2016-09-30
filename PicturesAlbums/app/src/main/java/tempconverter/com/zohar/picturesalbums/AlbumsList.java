package tempconverter.com.zohar.picturesalbums;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlbumsList extends ListActivity implements  AdapterView.OnItemClickListener {
    ArrayList<File> fileAlbums;
    AlbumListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums_list);

        bindList();
    }

    public void bindList(){
        // Getting the list view
        ListView list =  getListView();

        // Get all the albums from the storage
        fileAlbums = MyFiles.getFiles(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
                                                                                File.separator + getString(R.string.album_name)));

        // Checking if there are albums
        if (fileAlbums == null || fileAlbums.size() == 0) {
            Toast.makeText(this, "No albums are available", Toast.LENGTH_LONG).show();
            onBackPressed();
        }
        else {

            // Creating the list adapter and setting the list's adapter with it
            adapter = new AlbumListAdapter(fileAlbums, this);
            list.setAdapter(adapter);

            // Setting Listener
            list.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
        File fileAlbum = fileAlbums.get(i);
        goToAlbum(fileAlbum.getName());
    }

    public void goToAlbum(String name){
        Intent albumIntent = new Intent(this, Album.class);
        MySharedPreferences.saveOnsharedPreference(R.string.album_name, this, name);
        this.startActivity(albumIntent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
