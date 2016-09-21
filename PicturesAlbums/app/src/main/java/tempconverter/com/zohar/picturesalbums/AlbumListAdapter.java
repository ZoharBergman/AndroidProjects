package tempconverter.com.zohar.picturesalbums;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Zohar on 21/09/2016.
 */
public class AlbumListAdapter extends BaseAdapter {
    ArrayList<File> data;
    LayoutInflater inflater;

    public  AlbumListAdapter(ArrayList<File> fileData,Context mContext){
        this.data = fileData;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (data != null)
            return data.size();

        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (data != null)
            return  data.get(i);

        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View cellView = inflater.inflate(R.layout.activity_album_cell,null);
        TextView txtAlbumName = (TextView) cellView.findViewById(R.id.act_album_cell_txtAlbumName);

        File fileAlbum = (File) getItem(i);
        txtAlbumName.setText(fileAlbum.getName());

        return cellView;
    }
}
