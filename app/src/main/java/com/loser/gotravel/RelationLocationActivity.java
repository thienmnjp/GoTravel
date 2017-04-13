package com.loser.gotravel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.loser.gotravel.adapters.RelationLocationAdapter;
import com.loser.gotravel.common.CommonUtils;
import com.loser.gotravel.common.Constant;
import com.loser.gotravel.common.GoTravelUtils;
import com.loser.gotravel.common.LoadMoreListView;
import com.loser.gotravel.objects.RelationLocation;
import com.loser.gotravel.services.CustomAsyncHttpClient;
import com.loser.gotravel.services.ServiceUrls;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by DucAnhZ on 21/10/2015.
 */
public class RelationLocationActivity extends FragmentActivity implements View.OnClickListener{
    private RelativeLayout btnBackLocation;
    private View viewStatusBarLocation;
    private LoadMoreListView listViewLocation;
    private RelationLocationAdapter relationLocationAdapter;
    private ArrayList<RelationLocation> listHotels;
    private ArrayList<RelationLocation> listRestaurents;
    private ArrayList<RelationLocation> listShops;
    private ArrayList<RelationLocation> listEntertainments;
    private Long attractionsId;
    private RadioGroup rgTabBarListItem;
    private View viewStatus;
    private TextView tvTitleLocation;
    private int pageHotel = 1;
    private int pageRestaurent = 1;
    private int pageShop = 1;
    private int pageEntertainment = 1;
    private final int limit = 5;
    private String page = "hotel";
    private int totalHotel = 0;
    private int totalRestaurent = 0;
    private int totalShop = 0;
    private int totalEntertainment = 0;
    private int checkTabId;
    private Button btnCloseDialog;
    private boolean dialogTurnOn = false;
    private FrameLayout layoutLocationDetail;
    private SliderLayout sliderLocation;
    private TextView tvLocationName;
    private TextView tvPrice;
    private TextView tvStatus;
    private TextView tvPhone;
    private TextView tvLikeDetail;
    private TextView tvUnlikeDetail;
    private TextView tvLocationDetail;
    private TextView tvDescriptionDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_relation_location);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        btnBackLocation = (RelativeLayout) findViewById(R.id.btnBackLocation);
        viewStatusBarLocation = (View) findViewById(R.id.viewStatusBarLocation);
        viewStatus = (View) findViewById(R.id.viewStatus);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            viewStatusBarLocation.setVisibility(View.VISIBLE);
            viewStatusBarLocation.getLayoutParams().height = CommonUtils.getStatusBarHeight(this);
            viewStatusBarLocation.setBackgroundColor(getResources().getColor(R.color.action_bar));
            viewStatus.setVisibility(View.VISIBLE);
            viewStatus.getLayoutParams().height = CommonUtils.getStatusBarHeight(this);
            viewStatus.setBackgroundColor(getResources().getColor(R.color.action_bar));
        }
        Bundle bundle = getIntent().getExtras();
        attractionsId = bundle.getLong(Constant.kAttractionsId);
        listViewLocation = (LoadMoreListView) findViewById(R.id.listViewLocation);
        listHotels = new ArrayList<>();
        listRestaurents = new ArrayList<>();
        listShops = new ArrayList<>();
        listEntertainments = new ArrayList<>();
        tvTitleLocation = (TextView) findViewById(R.id.tvTitleReletionLocation);
        rgTabBarListItem = (RadioGroup) findViewById(R.id.rgTabBarListItem);
        btnCloseDialog = (Button) findViewById(R.id.btnCloseDialog);
        relationLocationAdapter = new RelationLocationAdapter(this, R.layout.item_relation_location, listHotels);
        layoutLocationDetail = (FrameLayout) findViewById(R.id.layoutLocationDetail);
        sliderLocation = (SliderLayout) findViewById(R.id.sliderLocation);
        tvLocationName = (TextView) findViewById(R.id.tvLocationName);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvLikeDetail = (TextView) findViewById(R.id.tvLikeDetail);
        tvUnlikeDetail = (TextView) findViewById(R.id.tvUnlikeDetail);
        tvLocationDetail = (TextView) findViewById(R.id.tvLocationDetail);
        tvDescriptionDetail = (TextView) findViewById(R.id.tvDescriptionDetail);

        listViewLocation.setAdapter(relationLocationAdapter);
        parseLocation(this, Constant.HOTEL, attractionsId, pageHotel, limit);
        rgTabBarListItem.check(R.id.btnHotel);
        checkTabId = R.id.btnHotel;
        btnBackLocation.setOnClickListener(this);
        btnCloseDialog.setOnClickListener(this);


        rgTabBarListItem.setOnCheckedChangeListener(new CheckedChangeListenerTabs(this));
        listViewLocation.setOnItemClickListener(new RLItemClickListener());
        listViewLocation.setOnLoadMoreListener(new LocationLoadMore(this));
    }

    @Override
    protected void onResume() {
        tvTitleLocation.setText(R.string.tvHotel);
        super.onResume();
    }

    private void parseLocation(final Activity activity, final String type, Long attractionsId, int page, int limit) {
        if (!GoTravelUtils.networkConnected(this)) {
            return;
        }

        String token = "";
        CustomAsyncHttpClient client = new CustomAsyncHttpClient(this, token);
        String url = ServiceUrls.SERVER_URL + "/relation-location/" + type + "/list";
        RequestParams requestParams = new RequestParams();
        requestParams.put("attractionsId", attractionsId);
        requestParams.put("page", page);
        requestParams.put("limit", limit);
        client.get(url, requestParams, new TextHttpResponseHandler() {
            private ProgressDialog progressBar;
            private int positionListView = 0;

            @Override
            public void onStart() {
                super.onStart();
                try {
                    progressBar = new ProgressDialog(activity);
                    progressBar.setCancelable(true);
                    progressBar.setMessage("Loading ...");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    if (pageHotel == 1 && checkTabId == R.id.btnHotel
                            || pageRestaurent == 1 && checkTabId == R.id.btnRestaurent
                            || pageShop == 1 && checkTabId == R.id.btnShop
                            || pageEntertainment == 1 && checkTabId == R.id.btnEntertainment) {
                        progressBar.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                GoTravelUtils.showDialogServerProblem(activity);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (statusCode == 200) {
                    try {
                        JSONObject jObject = new JSONObject(responseString);
                        String success = CommonUtils.getStringValid(jObject.getString("success"));
                        if (type.equals(Constant.HOTEL)) {
                            totalHotel = jObject.getInt("totalItem");
                            positionListView = listHotels.size() - 1;
                        } else if (type.equals(Constant.RESTAURENT)) {
                            positionListView = listRestaurents.size() - 1;
                            totalEntertainment = jObject.getInt("totalItem");
                        } else if (type.equals(Constant.SHOP)) {
                            positionListView = listShops.size() - 1;
                            totalShop = jObject.getInt("totalItem");
                        } else if (type.equals(Constant.ENTERTAINMENT)) {
                            positionListView = listEntertainments.size() - 1;
                            totalEntertainment = jObject.getInt("totalItem");
                        }

                        if ("1".equals(success)) {
                            JSONArray jArrData = new JSONArray(jObject.getString("data"));
                            for (int i = 0; i < jArrData.length(); i++) {
                                RelationLocation relationLocation = RelationLocation.getReLationLocation(jArrData.getJSONObject(i));
                                if (relationLocation != null) {
                                    if (type.equals(Constant.HOTEL)) {
                                        listHotels.add(relationLocation);
                                    } else if (type.equals(Constant.RESTAURENT)) {
                                        listRestaurents.add(relationLocation);
                                    } else if (type.equals(Constant.SHOP)) {
                                        listShops.add(relationLocation);
                                    } else if (type.equals(Constant.ENTERTAINMENT)) {
                                        listEntertainments.add(relationLocation);
                                    }
                                }
                            }
                            if (type.equals(Constant.HOTEL)) {
                                relationLocationAdapter = new RelationLocationAdapter(activity, R.layout.item_relation_location, listHotels);
                                listViewLocation.setAdapter(relationLocationAdapter);
                                listViewLocation.onLoadMoreComplete();
                                listViewLocation.setSelection(positionListView);
                            } else if (type.equals(Constant.RESTAURENT)) {
                                relationLocationAdapter = new RelationLocationAdapter(activity, R.layout.item_relation_location, listRestaurents);
                                listViewLocation.setAdapter(relationLocationAdapter);
                                listViewLocation.onLoadMoreComplete();
                                listViewLocation.setSelection(positionListView);
                            } else if (type.equals(Constant.SHOP)) {
                                relationLocationAdapter = new RelationLocationAdapter(activity, R.layout.item_relation_location, listShops);
                                listViewLocation.setAdapter(relationLocationAdapter);
                                listViewLocation.onLoadMoreComplete();
                                listViewLocation.setSelection(positionListView);
                            } else if (type.equals(Constant.ENTERTAINMENT)) {
                                relationLocationAdapter = new RelationLocationAdapter(activity, R.layout.item_relation_location, listEntertainments);
                                listViewLocation.setAdapter(relationLocationAdapter);
                                listViewLocation.onLoadMoreComplete();
                                listViewLocation.setSelection(positionListView);
                            }
                        } else {
                            GoTravelUtils.showDialogServerProblem(activity);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progressBar.dismiss();
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == btnBackLocation) {
            if (dialogTurnOn) {
                dialogClose();
            } else {
                finish();
            }
        }
        if (v == btnCloseDialog) {
           dialogClose();
        }
    }

    private class LocationLoadMore implements LoadMoreListView.OnLoadMoreListener {
        private Activity activity;

        public LocationLoadMore(Activity activity) {
            this.activity = activity;
        }
        @Override
        public void onLoadMore() {
            switch (checkTabId) {
                case R.id.btnHotel:
                    if (listHotels.size() == totalHotel) {
                        listViewLocation.onLoadMoreComplete();
                    } else {
                        pageHotel++;
                        parseLocation(activity, Constant.HOTEL, attractionsId, pageHotel, limit);
                    }
                    break;
                case R.id.btnEntertainment:
                    if (listEntertainments.size() == totalEntertainment) {
                        listViewLocation.onLoadMoreComplete();
                    } else {
                        pageEntertainment++;
                        parseLocation(activity, Constant.ENTERTAINMENT, attractionsId, pageHotel, limit);
                    }
                    break;
                case  R.id.btnShop:
                    if (listShops.size() == totalShop) {
                        listViewLocation.onLoadMoreComplete();
                    } else {
                        pageShop++;
                        parseLocation(activity, Constant.SHOP, attractionsId, pageHotel, limit);
                    }
                    break;
                case R.id.btnRestaurent:
                    if (listRestaurents.size() == totalRestaurent) {
                        listViewLocation.onLoadMoreComplete();
                    } else {
                        pageRestaurent++;
                        parseLocation(activity, Constant.RESTAURENT, attractionsId, pageHotel, limit);
                    }
                    break;
            }
        }
    }

    private class CheckedChangeListenerTabs implements RadioGroup.OnCheckedChangeListener {

        private Activity activity;

        public CheckedChangeListenerTabs(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.btnHotel:
                    checkTabId = checkedId;
                    tvTitleLocation.setText(R.string.tvHotel);
                    if (listHotels.size() == 0) {
                        parseLocation(activity, Constant.HOTEL, attractionsId, pageHotel, limit);
                    } else {
                        relationLocationAdapter = new RelationLocationAdapter(activity, R.layout.item_relation_location, listHotels);
                        listViewLocation.setAdapter(relationLocationAdapter);
                    }
                    break;
                case R.id.btnRestaurent:
                    checkTabId = checkedId;
                    tvTitleLocation.setText(R.string.tvRestaurent);
                    if (listRestaurents.size() == 0){
                        parseLocation(activity, Constant.RESTAURENT, attractionsId, pageRestaurent, limit);
                    } else {
                        relationLocationAdapter = new RelationLocationAdapter(activity, R.layout.item_relation_location, listRestaurents);
                        listViewLocation.setAdapter(relationLocationAdapter);
                    }
                    break;
                case R.id.btnShop:
                    checkTabId = checkedId;
                    tvTitleLocation.setText(R.string.tvShop);
                    if (listShops.size() == 0) {
                        parseLocation(activity, Constant.SHOP, attractionsId, pageShop, limit);
                    } else {
                        relationLocationAdapter = new RelationLocationAdapter(activity, R.layout.item_relation_location, listShops);
                        listViewLocation.setAdapter(relationLocationAdapter);
                    }
                    break;
                case R.id.btnEntertainment:
                    checkTabId = checkedId;
                    tvTitleLocation.setText(R.string.tvEntertainment);
                    if (listEntertainments.size() == 0) {
                        parseLocation(activity, Constant.ENTERTAINMENT, attractionsId, pageEntertainment, limit);
                    } else {
                        relationLocationAdapter = new RelationLocationAdapter(activity, R.layout.item_relation_location, listEntertainments);
                        listViewLocation.setAdapter(relationLocationAdapter);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private class RLItemClickListener implements AdapterView.OnItemClickListener {
        private RelationLocation relationLocation;
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //TODO
            dialogTurnOn = true;
            layoutLocationDetail.setVisibility(View.VISIBLE);
            switch (checkTabId) {
                case R.id.btnHotel:
                    relationLocation = listHotels.get(position);
                    break;
                case R.id.btnRestaurent:
                    relationLocation = listRestaurents.get(position);
                    break;
                case R.id.btnShop:
                    relationLocation = listShops.get(position);
                    break;
                case R.id.btnEntertainment:
                    relationLocation = listEntertainments.get(position);
                    break;
            }

            for (int i = 0; i < relationLocation.listPhotos.size(); i++) {
                DefaultSliderView sliderView = new DefaultSliderView(getApplicationContext());
                sliderView
                        .description(relationLocation.listPhotos.get(i).description)
                        .image(relationLocation.listPhotos.get(i).photoUrl)
                        .setScaleType(BaseSliderView.ScaleType.Fit);
                sliderLocation.addSlider(sliderView);
            }

            sliderLocation.setPresetTransformer(SliderLayout.Transformer.Default);
            sliderLocation.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            sliderLocation.setCustomAnimation(new DescriptionAnimation());
            sliderLocation.setDuration(4000);
            sliderLocation.stopAutoCycle();

            tvLocationName.setText(relationLocation.name);
            tvUnlikeDetail.setText(String.valueOf(relationLocation.totalUnlike));
            tvLikeDetail.setText(String.valueOf(relationLocation.totalLike));
            tvStatus.setText(relationLocation.status);
            tvPrice.setText(relationLocation.price);
            tvLocationDetail.setText(relationLocation.listContacts.get(0).contactAddress);
            tvPhone.setText(relationLocation.listContacts.get(0).contactPhone);
            tvDescriptionDetail.setText(relationLocation.description);
        }
    }

    @Override
    public void onBackPressed() {
        if (dialogTurnOn) {
            dialogClose();
        } else {
            super.onBackPressed();
        }
    }

    private void dialogClose() {
        dialogTurnOn = false;
        layoutLocationDetail.setVisibility(View.GONE);
        sliderLocation.removeAllSliders();
    }
}
