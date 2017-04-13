package com.loser.gotravel.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.loser.gotravel.R;
import com.loser.gotravel.common.CommonUtils;
import com.loser.gotravel.objects.RelationLocation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by DucAnhZ on 29/11/2015.
 */
public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private View myContentsView;
    private DisplayImageOptions option;

    public MyInfoWindowAdapter(Activity context) {
        try {
            myContentsView = context.getLayoutInflater().inflate(
                    R.layout.custom_info_contents, null);
        } catch (Exception e) {
        }
        option = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .showImageOnLoading(R.mipmap.ic_launcher)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .cacheInMemory(true).cacheOnDisk(true).build();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        ImageView imvMarkerItem = (ImageView) myContentsView.findViewById(R.id.imvMarkerItem);
        TextView tvMarkerName = (TextView) myContentsView.findViewById(R.id.tvMarkerName);
        TextView tvMarkerPhone = (TextView) myContentsView.findViewById(R.id.tvMarkerPhone);
        Log.d("jsonObject", marker.getTitle());
        try {
            JSONObject jData = new JSONObject(marker.getTitle());
            RelationLocation relationLocation = RelationLocation.getReLationLocation(jData);
            tvMarkerName.setText(CommonUtils.getStringValid(jData.getString("name")));
            JSONArray jArrContacts = new JSONArray(jData.getString("listContacts"));
            tvMarkerPhone.setText(CommonUtils.getStringValid(jArrContacts.getJSONObject(0).getString("contactPhone")));
            JSONArray jArrPhotos = new JSONArray(jData.getString("listPhotos"));
            try {
                ImageLoader.getInstance().displayImage(CommonUtils.getStringValid(jArrPhotos.getJSONObject(0).getString("photoUrl")), imvMarkerItem, option);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return myContentsView;
    }
}
