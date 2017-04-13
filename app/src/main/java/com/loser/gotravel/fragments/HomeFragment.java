package com.loser.gotravel.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.loser.gotravel.HomeActivity;
import com.loser.gotravel.R;
import com.loser.gotravel.common.Constant;
import com.loser.gotravel.common.RippleEffect;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.HashMap;

/**
 * Created by DucAnhZ on 04/10/2015.
 */
public class HomeFragment extends Fragment implements OnClickListener {
    private SliderLayout sliderLayoutBanner;
    private RippleEffect btnDongBacBo;
    private RippleEffect btnTayBacBo;
    private RippleEffect btnBacTrungBo;
    private RippleEffect btnTayNguyen;
    private RippleEffect btnNamTrungBo;
    private RippleEffect btnTayNamBo;
    private RippleEffect btnDongNamBo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_home, container, false);

        sliderLayoutBanner = (SliderLayout) view.findViewById(R.id.slider);
        btnDongBacBo = (RippleEffect) view.findViewById(R.id.btnDongBacBo);
        btnTayBacBo = (RippleEffect) view.findViewById(R.id.btnTayBacBo);
        btnBacTrungBo = (RippleEffect) view.findViewById(R.id.btnBacTrungBo);
        btnTayNguyen = (RippleEffect) view.findViewById(R.id.btnTayNguyen);
        btnNamTrungBo = (RippleEffect) view.findViewById(R.id.btnNamTrungBo);
        btnTayNamBo = (RippleEffect) view.findViewById(R.id.btnTayNamBo);
        btnDongNamBo = (RippleEffect) view.findViewById(R.id.btnDongNamBo);

        btnTayBacBo.setOnClickListener(this);
        btnDongBacBo.setOnClickListener(this);
        btnNamTrungBo.setOnClickListener(this);
        btnTayNguyen.setOnClickListener(this);
        btnBacTrungBo.setOnClickListener(this);
        btnTayNamBo.setOnClickListener(this);
        btnDongNamBo.setOnClickListener(this);

        setSliderLayoutBanner();

        return view;
    }

    private void setSliderLayoutBanner() {
        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Vịnh Hạ Long", R.mipmap.banner_background_default);
        file_maps.put("Mù Căng Chải", R.mipmap.banner_background_default2);
        file_maps.put("Hà Nội mùa thu", R.mipmap.banner_background_default3);
        file_maps.put("Đà Nẵng", R.mipmap.banner_background_default4);
        file_maps.put("Cà Mau", R.mipmap.banner_background_default5);
        file_maps.put("Tây nguyên buổi chiều", R.mipmap.banner_background_default6);

        for (String name : file_maps.keySet()) {
            DefaultSliderView sliderView = new DefaultSliderView(getContext());
            sliderView
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            sliderLayoutBanner.addSlider(sliderView);
        }

        sliderLayoutBanner.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayoutBanner.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayoutBanner.setCustomAnimation(new DescriptionAnimation());
        sliderLayoutBanner.setDuration(4000);
    }

    @Override
    public void onResume() {
        sliderLayoutBanner.startAutoCycle();
        super.onResume();
    }

    @Override
    public void onPause() {
        sliderLayoutBanner.stopAutoCycle();
        super.onPause();
    }

    @Override
    public void onStop() {
        sliderLayoutBanner.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        if (v == btnTayBacBo) {
            ListFragment attractionsFragment = new ListFragment();
            clickCategory(attractionsFragment, 1);
        }
        if (v == btnDongBacBo) {
            ListFragment attractionsFragment = new ListFragment();
            clickCategory(attractionsFragment, 2);
        }
        if (v == btnBacTrungBo) {
            ListFragment attractionsFragment = new ListFragment();
            clickCategory(attractionsFragment, 3);
        }

        if (v == btnNamTrungBo) {
            ListFragment attractionsFragment = new ListFragment();
            clickCategory(attractionsFragment, 4);
        }

        if (v == btnTayNamBo) {
            ListFragment attractionsFragment = new ListFragment();
            clickCategory(attractionsFragment, 5);
        }

        if (v == btnDongNamBo) {
            ListFragment attractionsFragment = new ListFragment();
            clickCategory(attractionsFragment, 6);
        }

        if (v == btnTayNguyen) {
            ListFragment attractionsFragment = new ListFragment();
            clickCategory(attractionsFragment, 7);
        }
    }

    private void clickCategory(Fragment fragment, Integer categoryId) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.btnMap.setVisibility(View.GONE);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.kCategoryId, categoryId);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.contentChild, fragment, Constant.LIST_ATTRACTIONS);
        fragmentTransaction.addToBackStack(Constant.HOME);
        fragmentTransaction.commit();
    }
}
