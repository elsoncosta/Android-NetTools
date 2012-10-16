package com.pc.nettools.example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.pc.nettools.image.ImageDownloader;

/**
 * Created by Pietro Caselani
 */
public class ImageAdapter extends ArrayAdapter<String> {
    private LayoutInflater mInflater;
    private ImageDownloader mImageDownloader;

    public ImageAdapter(Context context, String[] objects) {
        super(context, R.layout.image_item_layout, objects);

        mInflater = LayoutInflater.from(context);
        mImageDownloader = new ImageDownloader();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.image_item_layout, parent, false);

            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);

            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        String link = getItem(position);

        mImageDownloader.download(link, holder.imageView);

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}
