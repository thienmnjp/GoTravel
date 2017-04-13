package com.loser.gotravel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.loser.gotravel.common.CommonUtils;
import com.loser.gotravel.common.Constant;
import com.loser.gotravel.common.GoTravelUtils;
import com.loser.gotravel.services.CustomAsyncHttpClient;
import com.loser.gotravel.services.ServiceUrls;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

/**
 * Created by DucAnhZ on 16/10/2015.
 */
public class LoginActivity extends Activity implements OnClickListener{

    private static String FacebookSDK = "FacebookSDK";

    private RelativeLayout btnSkip;
    private LoginButton btnLoginFacebook;
    private RelativeLayout btnLoginGmail;
    private RelativeLayout getBtnLoginTwitter;
    private CallbackManager callbackManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.act_login);
        btnSkip = (RelativeLayout) findViewById(R.id.btnSkip);
        btnLoginFacebook = (LoginButton) findViewById(R.id.btnLoginFacebook);
        btnLoginGmail = (RelativeLayout) findViewById(R.id.btnLoginGmail);
        getBtnLoginTwitter = (RelativeLayout) findViewById(R.id.btnLoginTwitter);
        btnLoginFacebook.setOnClickListener(this);
        btnLoginGmail.setOnClickListener(this);
        getBtnLoginTwitter.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
        sharedPreferences = this.getSharedPreferences(Constant.nameSharedPreferences, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        btnLoginFacebook.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends", "user_birthday"));
        btnLoginFacebook.registerCallback(callbackManager, new RegisterFacebook(this));
        btnLoginFacebook.setBackgroundResource(R.mipmap.btn_facebook);
        btnLoginFacebook.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSkip) {
            finish();
        }
    }

    private void loginFacebook(String facebookId, String name, String email) {
        if (!CommonUtils.checkNetwork(this)) {
            return;
        }
        String username = email.replace("@gmail.com", "").replace("@yahoo.com", "");
        String avatar = "http://graph.facebook.com/" + facebookId + "/picture?type=large";
        String token = "";
        CustomAsyncHttpClient client = new CustomAsyncHttpClient(this, token);
        String url = ServiceUrls.SERVER_URL + "/user/login-with-facebook";
        RequestParams params = new RequestParams();
        params.put("facebookId", facebookId);
        params.put("name", name);
        params.put("username", username);
        params.put("email", email);
        params.put("avatar", avatar);
        params.put("deviceId", Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID));
        client.post(url, params, new TextHttpResponseHandler() {

            private ProgressDialog progressBar;

            @Override
            public void onStart() {
                super.onStart();
                try {
                    progressBar = new ProgressDialog(LoginActivity.this);
                    progressBar.setCancelable(true);
                    progressBar.setMessage("Loading ...");
                    progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressBar.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                CommonUtils.showOkDialog(LoginActivity.this, getResources().getString(R.string.dialog_title_common), responseString, null);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (statusCode == 200) {
                    try {
                        JSONObject jObject = new JSONObject(responseString);
                        String success = CommonUtils.getStringValid(jObject.getString("success"));
                        if ("1".equals(success)) {
                            JSONObject jData = new JSONObject(jObject.getString("data"));
                            String token = CommonUtils.getStringValid(jData.getString("token"));
                            editor.putString(Constant.MEMBER, jData.getString("member"));
                            editor.putString(Constant.TOKEN, token);
                            editor.commit();
                            finish();
                            Intent intent = new Intent("login");
                            LoginActivity.this.sendBroadcast(intent);
                        } else {
                            String message = CommonUtils.getStringValid(jObject.getString("message"));
                            CommonUtils.showOkDialog(LoginActivity.this, getResources().getString(R.string.dialog_title_common), message, null);
                        }
                    } catch (Exception e) {
                        CommonUtils.showOkDialog(LoginActivity.this, getResources().getString(R.string.dialog_title_common), "Exception: " + e.getMessage(), null);
                    }

                } else {
                    GoTravelUtils.showDialogServerProblem(LoginActivity.this);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progressBar.dismiss();
            }
        });

    }

    private class RegisterFacebook implements FacebookCallback<LoginResult> {
        private Activity activity;

        public RegisterFacebook(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onSuccess(LoginResult loginResult) {
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {

                        @Override
                        public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                            try {
                                loginFacebook(jsonObject.getString("id"),
                                        jsonObject.getString("name"),
                                        jsonObject.getString("email"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, name, email, gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();

        }

        @Override
        public void onCancel() {
            Log.d(FacebookSDK, "Login attempt canceled.");
        }

        @Override
        public void onError(FacebookException e) {
            Log.d(FacebookSDK, "Login attempt failed.");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
