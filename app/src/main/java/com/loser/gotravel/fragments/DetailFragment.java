package com.loser.gotravel.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.loser.gotravel.CommentActivity;
import com.loser.gotravel.HomeActivity;
import com.loser.gotravel.R;
import com.loser.gotravel.RelationLocationActivity;
import com.loser.gotravel.common.Constant;
import com.loser.gotravel.objects.Attractions;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by DucAnhZ on 19/10/2015.
 */
public class DetailFragment extends Fragment implements OnClickListener {

    private HomeActivity homeActivity;
    private int itemPosition;
    private View view;
    private Attractions attractions;
    private TextView tvAttractionsNameDetail;
    private TextView tvRateDetail;
    private LinearLayout layoutContent;
    private SliderLayout sliderLayout;
    private Button btnRelationLocation;
    private ImageView preSlider;
    private ImageView nextSlider;
    private Button btnComment;
    private ImageView btnShareFacebook;
    private LinearLayout layoutRating;
    private RatingBar ratebar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fg_detail_attractions, container, false);
        homeActivity = (HomeActivity) getActivity();
        tvAttractionsNameDetail = (TextView) view.findViewById(R.id.tvAttractionsNameDetail);
        tvRateDetail = (TextView) view.findViewById(R.id.tvRateDetail);
        layoutContent = (LinearLayout) view.findViewById(R.id.layoutContent);
        sliderLayout = (SliderLayout) view.findViewById(R.id.sliderDetail);
        btnRelationLocation = (Button) view.findViewById(R.id.btnRelationLocation);
        preSlider = (ImageView) view.findViewById(R.id.preSlider);
        nextSlider = (ImageView) view.findViewById(R.id.nextSlider);
        btnComment = (Button) view.findViewById(R.id.btnComment);
        btnShareFacebook = (ImageView) view.findViewById(R.id.btnShareFacebook);
        layoutRating = (LinearLayout) view.findViewById(R.id.layoutRating);
        ratebar = (RatingBar) view.findViewById(R.id.ratebar);
        layoutRating.setOnClickListener(this);
        btnShareFacebook.setOnClickListener(this);
        btnComment.setOnClickListener(this);
        preSlider.setOnClickListener(this);
        nextSlider.setOnClickListener(this);
        btnRelationLocation.setOnClickListener(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            btnRelationLocation.setBackgroundColor(getResources().getColor(R.color.button_relation_location));
        }
        homeActivity.btnBack.setEnabled(true);
        homeActivity.btnBack.setVisibility(View.VISIBLE);
        homeActivity.btnLeftMenu.setEnabled(false);
        homeActivity.btnLeftMenu.setVisibility(View.GONE);
        homeActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        Bundle bundle = getArguments();
        if (bundle != null && homeActivity.arrayAttractions.size() > 0) {
            itemPosition = bundle.getInt(Constant.kItemPosition);
            attractions = homeActivity.arrayAttractions.get(itemPosition);
        }
        if (attractions != null) {
            createData(savedInstanceState);
            createSliderLayout(attractions);
        }
        ratebar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                tvRateDetail.setText(rating + "/" + 1 + " đánh giá");
            }
        });
        return view;
    }

    private void createData(Bundle savedInstanceState) {
        tvAttractionsNameDetail.setText(attractions.name);
        tvRateDetail.setText(attractions.varRate + "/" + attractions.totalRate + " đánh giá");
        for (int i = 0; i < attractions.listInformation.size(); i++) {
            TextView title = (TextView) getLayoutInflater(savedInstanceState).inflate(R.layout.title_detail, null);
            title.setText(attractions.listInformation.get(i).title);
            layoutContent.addView(title);
            TextView description = new TextView(getActivity());
            description.setText(attractions.listInformation.get(i).description);
            layoutContent.addView(description);
        }
    }

    private void createSliderLayout(Attractions attractions) {
        HashMap<String, String> url_maps = new HashMap<String, String>();
        for (int i = 0; i < attractions.listPhoto.size(); i++) {
            url_maps.put(attractions.listPhoto.get(i).description, attractions.listPhoto.get(i).photoUrl);
        }
        for (String name : url_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(getActivity());
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);
        sliderLayout.setCustomIndicator((PagerIndicator) view.findViewById(R.id.custom_indicator));
    }

    @Override
    public void onResume() {
        sliderLayout.stopAutoCycle();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v == btnRelationLocation) {
            Intent intent = new Intent(getActivity(), RelationLocationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong(Constant.kAttractionsId, attractions.attractionsId);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        if (v == preSlider) {
            sliderLayout.movePrevPosition();
        }
        if (v == nextSlider) {
            sliderLayout.moveNextPosition();
        }
        if (v == btnComment) {
            Intent intent = new Intent(getActivity(), CommentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong(Constant.kAttractionsId, attractions.attractionsId);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        if (v == btnShareFacebook) {
            sharefacebook("https://www.facebook.com/uetloser");
        }
        if (v == layoutRating) {
            //Show dialog rateting

        }
    }

    @Override
    public void onStop() {
        sliderLayout.stopAutoCycle();
        super.onStop();
    }

    public void sharefacebook(String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.setType("image/*");

        boolean facebookAppFound = false;
        List<ResolveInfo> matches = getActivity().getPackageManager().queryIntentActivities(
                intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith(
                    "com.facebook.katana")) {
                intent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }

        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u="
                    + url;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }

        startActivity(intent);
    }
}
