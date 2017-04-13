package com.loser.gotravel.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loser.gotravel.R;
import com.loser.gotravel.objects.RelationLocation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

/**
 * Created by DucAnhZ on 22/10/2015.
 */
public class RelationLocationAdapter extends ArrayAdapter<RelationLocation> {
    private int layoutId;
    private Activity activity;
    private ArrayList<RelationLocation> relationLocations;
    private TextView tvLocation;
    private TextView tvLike;
    private TextView tvUnlike;
    private TextView tvAddressLocation;
    private TextView tvDescriptionLocation;
    private ImageView imvItemLocation;
    private DisplayImageOptions option;

    public RelationLocationAdapter(Activity activity, int layoutId, ArrayList<RelationLocation> relationLocations) {
        super(activity, layoutId, relationLocations);
        this.activity = activity;
        this.layoutId = layoutId;
        this.relationLocations = relationLocations;
        option = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .showImageOnLoading(R.mipmap.ic_launcher)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .cacheInMemory(true).cacheOnDisk(true).build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = this.activity.getLayoutInflater();
            convertView = inflater.inflate(layoutId, null);
        }
        tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);
        tvAddressLocation = (TextView) convertView.findViewById(R.id.tvAddressLocation);
        tvDescriptionLocation = (TextView) convertView.findViewById(R.id.tvDescriptionLocation);
        tvLike = (TextView) convertView.findViewById(R.id.tvLike);
        tvUnlike = (TextView) convertView.findViewById(R.id.tvUnLike);
        imvItemLocation = (ImageView) convertView.findViewById(R.id.imvItemLocation);

        try {
            ImageLoader.getInstance().displayImage(relationLocations.get(position).listPhotos.get(0).photoUrl, imvItemLocation, option);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvLocation.setText(relationLocations.get(position).name);
        tvAddressLocation.setText(relationLocations.get(position).listContacts.get(0).contactAddress);
        tvLike.setText(relationLocations.get(position).totalLike + "");
        tvUnlike.setText(relationLocations.get(position).totalUnlike + "");
        tvDescriptionLocation.setText(relationLocations.get(position).description + "");

        return convertView;
    }
}
