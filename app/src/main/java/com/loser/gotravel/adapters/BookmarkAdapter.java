package com.loser.gotravel.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loser.gotravel.R;
import com.loser.gotravel.objects.Attractions;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by DucAnhZ on 29/11/2015.
 */
public class BookmarkAdapter extends ArrayAdapter<Attractions> {
    private ArrayList<Attractions> listAttractions;
    private int layoutId;
    private Activity activity;
    private TextView tvBookmarkName;
    private TextView tvBookmarkAddress;
    private TextView tvBookmarkClassify;

    public BookmarkAdapter(Activity activity, int layoutId, ArrayList<Attractions> listAttractions) {
        super(activity, layoutId, listAttractions);
        this.activity = activity;
        this.layoutId = layoutId;
        this.listAttractions = listAttractions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = this.activity.getLayoutInflater();
            convertView = inflater.inflate(layoutId, null);
        }
        tvBookmarkName = (TextView) convertView.findViewById(R.id.tvBookmarkName);
        tvBookmarkAddress = (TextView) convertView.findViewById(R.id.tvBookmarkAddress);
        tvBookmarkClassify = (TextView) convertView.findViewById(R.id.tvBookmarkClassify);

        tvBookmarkName.setText(listAttractions.get(position).name);
        tvBookmarkAddress.setText(listAttractions.get(position).address);
        tvBookmarkClassify.setText(listAttractions.get(position).classify);

        return convertView;
    }
}
