package tempconverter.com.zohar.picturesalbums;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {
    Button btnCreateAlbum;
    Button btnWatchAlbum;

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
        AlbumNameDialog dialog = new AlbumNameDialog(this);
        dialog.show();

    }

    public void goAlbumsList(){
        Intent albumsListIntent = new Intent(this, AlbumsList.class);
        this.startActivity(albumsListIntent);
        this.finish();
    }
}
