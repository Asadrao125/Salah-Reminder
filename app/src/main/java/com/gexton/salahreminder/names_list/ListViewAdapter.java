package com.gexton.salahreminder.names_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gexton.salahreminder.R;

public class ListViewAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;

    public ListViewAdapter(Context context) {
        this.context = context;
    }

    public int getCount() {
        return Integer.valueOf(99).intValue();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = this.inflater.inflate(R.layout.listview_item, parent, false);

        TextView Meaning = (TextView) itemView.findViewById(R.id.LVmeaning);
        ImageView nameImage = itemView.findViewById(R.id.LVimg);
        TextView name = itemView.findViewById(R.id.LVname);

        name.setText(HelperClass.names[position]);
        Meaning.setText(HelperClass.meaning[position]);
        nameImage.setImageResource(HelperClass.imagesSmall[position]);
        return itemView;
    }
}

