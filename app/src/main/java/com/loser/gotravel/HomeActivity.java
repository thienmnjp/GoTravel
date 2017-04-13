package com.loser.gotravel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.loopj.android.http.TextHttpResponseHandler;
import com.loser.gotravel.common.CommonUtils;
import com.loser.gotravel.common.Constant;
import com.loser.gotravel.common.GoTravelUtils;
import com.loser.gotravel.common.RippleEffect;
import com.loser.gotravel.common.RoundedImageView;
import com.loser.gotravel.fragments.HomeFragment;
import com.loser.gotravel.fragments.MapFragment;
import com.loser.gotravel.objects.Attractions;
import com.loser.gotravel.services.CustomAsyncHttpClient;
import com.loser.gotravel.services.ServiceUrls;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HomeActivity extends FragmentActivity implements OnClickListener {

    public RelativeLayout btnLeftMenu;
    public RelativeLayout btnBack;
    public RelativeLayout btnMap;
    private LinearLayout btnLogin;
    public ImageView imgMap;
    public ImageView imgHome;
    public DrawerLayout drawerLayout;
    private View viewStatusBar;
    private LinearLayout linearlayoutHome;
    private ActionBarDrawerToggle drawerToggle;
    private float lastScale = 1.0f;
    public boolean checkLayoutMap = false;
    public Fragment mContent;
    private DisplayImageOptions options;
    private RoundedImageView imvAvatar;
    public ArrayList<Attractions> arrayAttractions = new ArrayList<>();
    private String jStrMember;
    private String avatar = "";
    private String name = "";
    private TextView tvLogin;
    private String token = "";
    private TextView tvLeftmenuNameAccount;
    public TextView tvActionbarTitle;
    private LinearLayout btnBookmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        if (savedInstanceState != null) {
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        }
        if (mContent == null) {
            mContent = new HomeFragment();
        }
        setContentView(R.layout.act_gotravel_home);
        getSupportFragmentManager().beginTransaction().replace(R.id.contentChild, mContent).commit();
        viewStatusBar = (View) findViewById(R.id.viewStatusBar);
        tvActionbarTitle = (TextView) findViewById(R.id.tvActionbarTitle);

        AppEventsLogger.activateApp(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            viewStatusBar.setVisibility(View.VISIBLE);
            viewStatusBar.getLayoutParams().height = CommonUtils.getStatusBarHeight(this);
            viewStatusBar.setBackgroundColor(getResources().getColor(R.color.action_bar));
        }

        linearlayoutHome = (LinearLayout) findViewById(R.id.linearlayoutHome);
        btnLogin = (LinearLayout) findViewById(R.id.btnLogin);
        btnLeftMenu = (RelativeLayout) findViewById(R.id.btnLeftMenu);
        btnMap = (RelativeLayout) findViewById(R.id.btnMap);
        imgHome = (ImageView) findViewById(R.id.imgHome);
        imgMap = (ImageView) findViewById(R.id.imgMap);
        btnBack = (RelativeLayout) findViewById(R.id.btnBack);
        imvAvatar = (RoundedImageView) findViewById(R.id.imvAvatar);
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        tvLeftmenuNameAccount = (TextView) findViewById(R.id.tvLeftmenuNameAccount);
        btnBookmark = (LinearLayout) findViewById(R.id.btnBookmark);

        btnBookmark.setOnClickListener(this);
        btnBack.setEnabled(false);
        btnBack.setVisibility(View.GONE);
        btnBack.setOnClickListener(this);
        btnLeftMenu.setOnClickListener(this);
        btnMap.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        setScaleBackground(drawerLayout);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_avatar_default)
                .showImageOnFail(R.mipmap.ic_avatar_default)
                .showImageOnLoading(R.mipmap.ic_avatar_default)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .cacheInMemory(true).cacheOnDisk(true).build();

        IntentFilter filter = new IntentFilter("login");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onResume();
            }
        };
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        token = getSharedPreferences(Constant.nameSharedPreferences, MODE_PRIVATE).getString(Constant.TOKEN, "");
        Log.d("token", token);
        if (CommonUtils.stringIsValid(token)) {
            jStrMember = getSharedPreferences(Constant.nameSharedPreferences, MODE_PRIVATE).getString(Constant.MEMBER, "");
            try {
                JSONObject jMember = new JSONObject(jStrMember);
                avatar = CommonUtils.getStringValid(jMember.getString("avatar"));
                name = CommonUtils.getStringValid(jMember.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                ImageLoader.getInstance().displayImage(avatar, imvAvatar, options);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvLeftmenuNameAccount.setText(name);
            tvLogin.setText(getResources().getString(R.string.left_menu_logout));
        } else {
            tvLogin.setText(getResources().getString(R.string.left_menu_login));
            tvLeftmenuNameAccount.setText("");
        }
    }

    private void setScaleBackground(DrawerLayout drawerLayout) {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, null, 0, 0) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float min = 0.9f;
                float max = 1.0f;
                float scaleFactor = (max - ((max - min) * slideOffset));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    linearlayoutHome.setScaleX(scaleFactor);
                    linearlayoutHome.setScaleY(scaleFactor);
                } else {
                    ScaleAnimation anim = new ScaleAnimation(lastScale, scaleFactor, lastScale, scaleFactor, linearlayoutHome.getWidth()/2, linearlayoutHome.getHeight()/2);
                    anim.setDuration(1000);
                    anim.setFillAfter(true);
                    linearlayoutHome.startAnimation(anim);

                    lastScale = scaleFactor;
                }
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    public void onClick(View v) {
        if (v == btnLeftMenu) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        if (v == btnLogin) {
            if (CommonUtils.stringIsValid(getSharedPreferences(Constant.nameSharedPreferences, MODE_PRIVATE).getString(Constant.TOKEN, ""))) {
                drawerLayout.closeDrawers();
                LoginManager.getInstance().logOut();
                logout(token);
            } else {
                drawerLayout.closeDrawers();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }

        }
        if (v == btnMap) {
            if (!checkLayoutMap) {
                imgHome.setVisibility(View.VISIBLE);
                imgMap.setVisibility(View.GONE);
                MapFragment mapFragment = new MapFragment();
                getSupportFragmentManager().beginTransaction().addToBackStack(Constant.HOME).replace(R.id.contentChild, mapFragment, Constant.MAP).commit();
                checkLayoutMap = true;
            } else {
                imgHome.setVisibility(View.GONE);
                imgMap.setVisibility(View.VISIBLE);
                getSupportFragmentManager().popBackStack(Constant.HOME, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                checkLayoutMap = false;
            }
        }
        if (v == btnBack) {
            btnLeftMenu.setEnabled(true);
            btnLeftMenu.setVisibility(View.VISIBLE);
            btnBack.setEnabled(false);
            btnBack.setVisibility(View.GONE);
            getSupportFragmentManager().popBackStack(Constant.LIST_ATTRACTIONS, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if (v == btnBookmark) {
            Intent intent = new Intent(this, BookmarkActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        if (getSupportFragmentManager().findFragmentByTag(Constant.DETAIL_ATTRACTIONS) != null) {
            btnLeftMenu.setEnabled(true);
            btnLeftMenu.setVisibility(View.VISIBLE);
            btnBack.setEnabled(false);
            btnBack.setVisibility(View.GONE);
           getSupportFragmentManager().popBackStack(Constant.LIST_ATTRACTIONS, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else if (getSupportFragmentManager().findFragmentByTag(Constant.LIST_ATTRACTIONS) != null) {
            arrayAttractions = new ArrayList<>();
            tvActionbarTitle.setText("GO TRAVEL");
            btnMap.setVisibility(View.VISIBLE);
            getSupportFragmentManager().popBackStack(Constant.HOME, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else if (getSupportFragmentManager().findFragmentByTag(Constant.MAP) != null) {
            imgMap.setVisibility(View.VISIBLE);
            imgHome.setVisibility(View.GONE);
            checkLayoutMap = false;
            getSupportFragmentManager().popBackStack(Constant.HOME, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    private void logout(String token) {
        if (!GoTravelUtils.networkConnected(this)) {
            return;
        }

        CustomAsyncHttpClient client = new CustomAsyncHttpClient(this, token);
        String url = ServiceUrls.SERVER_URL + "/user/logout";
        client.post(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                CommonUtils.showOkDialog(HomeActivity.this, getResources().getString(R.string.dialog_title_error), responseString, null);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (statusCode == 200) {
                    try {
                        JSONObject jObject = new JSONObject(responseString);
                        String success = CommonUtils.getStringValid(jObject.getString("success"));
                        String message = CommonUtils.getStringValid(jObject.getString("message"));
                        if ("1".equals(success)) {
                            tvLogin.setText(getResources().getString(R.string.left_menu_login));
                            tvLeftmenuNameAccount.setText("");
                            imvAvatar.setImageDrawable(getDrawable(R.mipmap.ic_avatar_default));
                            SharedPreferences.Editor editor = getSharedPreferences(Constant.nameSharedPreferences, MODE_PRIVATE).edit();
                            editor.clear();
                            editor.commit();
                        } else {
                            CommonUtils.showOkDialog(HomeActivity.this, getResources().getString(R.string.dialog_title_error), message, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();;
                    }
                } else {
                    CommonUtils.showOkDialog(HomeActivity.this, getResources().getString(R.string.dialog_title_error), String.valueOf(statusCode), null);
                }
            }
        });

    }
}
