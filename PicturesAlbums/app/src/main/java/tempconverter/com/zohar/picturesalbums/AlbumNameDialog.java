package tempconverter.com.zohar.picturesalbums;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.File;

public class AlbumNameDialog extends Dialog implements View.OnClickListener {

    Button btnCreate;
    Button btnCancel;
    EditText etAlbumName;
    Context context;
    LinearLayout llDialogView;

    public AlbumNameDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_name_dialog);

        bindUI();
    }

    public void bindUI(){
        // Getting the widgets that in the dialog
        btnCreate = (Button) findViewById(R.id.act_album_name_dialog_btnCreate);
        btnCancel = (Button) findViewById(R.id.act_album_name_dialog_btnCancel);
        etAlbumName = (EditText) findViewById(R.id.act_album_name_dialog_etAlbumName);
        llDialogView = (LinearLayout) findViewById(R.id.album_name_dialog);

        // Opening keyboard
        etAlbumName.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(etAlbumName, 0);
            }
        },100);

        setCancelable(false);

        // Setting click listeners
        btnCreate.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        etAlbumName.setOnClickListener(this);
        llDialogView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.act_album_name_dialog_btnCreate:{
                String name = etAlbumName.getText().toString().trim();

                // Checking the user entered a valid name
                if (name.isEmpty())
                    etAlbumName.setError("Please enter a name");
                // Checking if the album name does not exist
                else if(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator +
                            context.getString(R.string.album_name) + File.separator + "\u200e" + name).exists())
                    etAlbumName.setError("This album already exists");
                else {
                    // Adding this string to make hebrew text to be LTR
                    name = "\u200e" + name;
                    CreateAlbum(name);
                }
                break;
            }
            case R.id.act_album_name_dialog_btnCancel:{
                dismiss();
                break;
            }
            case R.id.act_album_name_dialog_etAlbumName:{
                etAlbumName.setError(null);
                break;
            }
            case R.id.album_name_dialog:{
                // setting the keyboard to be hidden
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etAlbumName.getWindowToken(), 0);
                break;
            }
        }
    }

    public void CreateAlbum(String name){
        Intent albumIntent = new Intent(context, Album.class);
        MySharedPreferences.saveOnsharedPreference(R.string.album_name, context, name);
        context.startActivity(albumIntent);
        ((Activity) context).finish();
    }
}
