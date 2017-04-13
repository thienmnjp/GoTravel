package com.loser.gotravel.fragments;

import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.loser.gotravel.HomeActivity;
import com.loser.gotravel.R;
import com.loser.gotravel.adapters.MyInfoWindowAdapter;
import com.loser.gotravel.common.GPSTracker;
import com.loser.gotravel.common.GoTravelUtils;
import com.loser.gotravel.objects.RelationLocation;
import com.loser.gotravel.services.CustomAsyncHttpClient;
import com.loser.gotravel.services.ServiceUrls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by DucAnhZ on 04/10/2015.
 */

public class MapFragment extends Fragment implements LocationListener {

    private GPSTracker gpsTracker;
    private View view;
    private Location myLocation;
    private SupportMapFragment fg_map;
    private ArrayList<LatLng> arrLatLngs;
    private ArrayList<Marker> arrMarkers;
    private Marker marker;
    private ArrayList<RelationLocation> listRL;
    private Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fg_map, container, false);
        gpsTracker = new GPSTracker(getActivity(), this);
        fg_map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fg_map);
        if (!gpsTracker.canGetLocation()) {
            gpsTracker.showSettingsAlert((HomeActivity) getActivity(), getActivity().getSupportFragmentManager());
        }
        arrLatLngs = new ArrayList<>();
        arrMarkers = new ArrayList<>();
        listRL = new ArrayList<>();
        View locationButton = fg_map.getView().findViewById(2);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 210);
        parseData();
        return view;
    }

    private void animateCameraMyLocation() {
        fg_map.getMap().setMyLocationEnabled(true);
        myLocation = fg_map.getMap().getMyLocation();
        double dLatitude = gpsTracker.getLatitude();
        double dLongitude = gpsTracker.getLongitude();
        final LatLng myLatLng = new LatLng(dLatitude,
                dLongitude);
        arrLatLngs.add(myLatLng);
        CameraPosition myPosition = new CameraPosition.Builder()
                .target(arrLatLngs.get(0)).zoom(15.0f)
                .build();
        fg_map.getMap().animateCamera(
                CameraUpdateFactory
                        .newCameraPosition(myPosition));
    }


    private void showMap() {
        try {
            for (int i = 0; i < listRL.size(); i++) {
                LatLng local = new LatLng(listRL.get(i).lat, listRL.get(i).lng);
                arrLatLngs.add(local);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                try {
                    for (int i = 0; i < arrLatLngs.size(); i++) {
                        String jsonObject = gson.toJson(listRL.get(i));
                        marker = fg_map.getMap().addMarker(new MarkerOptions()
                                .position(arrLatLngs.get(i))
                                .title(jsonObject)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        arrMarkers.add(marker);
                        builder.include(arrLatLngs.get(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    fg_map.getMap().setInfoWindowAdapter(
                            new MyInfoWindowAdapter(getActivity()));

                    if (gpsTracker.canGetLocation()) {

                        fg_map.getMap().setMyLocationEnabled(true);
                        myLocation = fg_map.getMap().getMyLocation();
                        double dLatitude = gpsTracker.getLatitude();
                        double dLongitude = gpsTracker.getLongitude();
                        final LatLng myLatLng = new LatLng(dLatitude,
                                dLongitude);
                        builder.include(myLatLng);
                        final LatLngBounds bounds = builder.build();
                        fg_map.getMap()
                                .animateCamera(
                                        CameraUpdateFactory.newLatLngBounds(
                                                bounds, 70));
                    } else {
                        if (arrLatLngs.size() == 1) {
                            try {
                                CameraPosition myPosition = new CameraPosition.Builder()
                                        .target(arrLatLngs.get(0)).zoom(15.0f)
                                        .build();
                                fg_map.getMap().animateCamera(
                                        CameraUpdateFactory
                                                .newCameraPosition(myPosition));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                final LatLngBounds bounds = builder.build();
                                fg_map.getMap().animateCamera(
                                        CameraUpdateFactory.newLatLngBounds(
                                                bounds, 70));
                            } catch (Exception e) {
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000);

    }

    private void parseData() {
        if (!GoTravelUtils.networkConnected(getActivity())) {
            return;
        }

        CustomAsyncHttpClient client = new CustomAsyncHttpClient(getActivity(), "");
        String url = ServiceUrls.SERVER_URL + "/relation-location/restaurent/list";
        RequestParams params = new RequestParams();
        params.put("attractionsId", 3);
        client.get(url, params, new TextHttpResponseHandler() {
            private ProgressDialog progressBar;

            @Override
            public void onStart() {
                super.onStart();
                try {
                    progressBar = new ProgressDialog(getActivity());
                    progressBar.setCancelable(true);
                    progressBar.setMessage("Loading ...");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (statusCode == 200) {
                    try {
                        JSONObject jObject = new JSONObject(responseString);
                        JSONArray jArrData = new JSONArray(jObject.getString("data"));
                        for (int i = 0; i < jArrData.length(); i++) {
                            RelationLocation relationLocation = RelationLocation.getReLationLocation(jArrData.getJSONObject(i));
                            listRL.add(relationLocation);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onFinish() {
                super.onFinish();
                progressBar.dismiss();
                showMap();
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
