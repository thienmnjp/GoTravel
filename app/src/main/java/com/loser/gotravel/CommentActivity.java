package com.loser.gotravel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.loser.gotravel.adapters.CommentAdapter;
import com.loser.gotravel.common.CommonUtils;
import com.loser.gotravel.common.Constant;
import com.loser.gotravel.common.GoTravelUtils;
import com.loser.gotravel.objects.Comment;
import com.loser.gotravel.services.CustomAsyncHttpClient;
import com.loser.gotravel.services.ServiceUrls;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by DucAnhZ on 28/11/2015.
 */
public class CommentActivity extends FragmentActivity implements View.OnClickListener {
    private View viewStatusBar;
    private ArrayList<Comment> listComments;
    private CommentAdapter commentAdapter;
    private ListView listViewComments;
    private Long attractionsId;
    private RelativeLayout btnBackComment;
    private TextView tvTitleComment;
    private ImageView btnSend;
    private EditText edtComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_comment);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        viewStatusBar = (View) findViewById(R.id.viewStatusBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            viewStatusBar.setVisibility(View.VISIBLE);
            viewStatusBar.getLayoutParams().height = CommonUtils.getStatusBarHeight(this);
            viewStatusBar.setBackgroundColor(getResources().getColor(R.color.action_bar));
        }

        btnBackComment = (RelativeLayout) findViewById(R.id.btnBackComment);
        tvTitleComment = (TextView) findViewById(R.id.tvTitleComment);
        listViewComments = (ListView) findViewById(R.id.listviewComment);
        tvTitleComment.setText(getResources().getString(R.string.btnComment));
        edtComment = (EditText) findViewById(R.id.edtComment);
        btnSend = (ImageView) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        listComments = new ArrayList<>();
        btnBackComment.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        attractionsId = bundle.getLong(Constant.kAttractionsId);

        if (listComments.size() == 0) {
            parselistComments(attractionsId);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == btnBackComment) {
            finish();
        }
        if (v == btnSend) {
            if (this.getSharedPreferences(Constant.nameSharedPreferences, MODE_PRIVATE).getString(Constant.TOKEN, "").equals("")) {
                CommonUtils.showOkDialog(this, getResources().getString(R.string.dialog_title_common), getResources().getString(R.string.dialog_message_login_for_comment), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CommentActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
            } else {
                if (edtComment.getText().toString().length() < 10) {
                    CommonUtils.showOkDialog(this, getResources().getString(R.string.dialog_title_common), "Bình luận phải lớn hơn 10 ký tự.", null);
                } else {
                    postComment(edtComment.getText().toString());
                    edtComment.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtComment.getWindowToken(), 0);
                }
            }

        }
    }

    private void parselistComments(Long attractionsId) {
        if (!GoTravelUtils.networkConnected(this)) {
            return;
        }

        CustomAsyncHttpClient client = new CustomAsyncHttpClient(this, "");
        String url = ServiceUrls.SERVER_URL + "/attractions/comment/list";
        RequestParams params = new RequestParams();
        params.put("attractionsId", attractionsId);
        client.get(url, params, new TextHttpResponseHandler() {
            private ProgressDialog progressBar;

            @Override
            public void onStart() {
                super.onStart();
                try {
                    progressBar = new ProgressDialog(CommentActivity.this);
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
                        String success = CommonUtils.getStringValid(jObject.getString("success"));
                        if ("1".equals(success)) {
                            JSONArray jArrData = new JSONArray(jObject.getString("data"));
                            for (int i = 0; i < jArrData.length(); i++) {
                                JSONObject jData = jArrData.getJSONObject(i);
                                Comment comment = Comment.getComment(jData);
                                listComments.add(comment);
                            }
                            commentAdapter = new CommentAdapter(CommentActivity.this, R.layout.item_comment, listComments);
                            listViewComments.setAdapter(commentAdapter);
                        } else {
                            String message = CommonUtils.getStringValid(jObject.getString("message"));
                            CommonUtils.showOkDialog(CommentActivity.this, getResources().getString(R.string.dialog_title_error), message, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    CommonUtils.showOkDialog(CommentActivity.this, getResources().getString(R.string.dialog_title_error), "statusCode: " + statusCode, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                CommonUtils.showOkDialog(CommentActivity.this, getResources().getString(R.string.dialog_title_error), responseString, null);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progressBar.dismiss();
            }
        });

    }

    private void postComment(String content) {
        if (!GoTravelUtils.networkConnected(this)) {
            return;
        }

        CustomAsyncHttpClient client = new CustomAsyncHttpClient(this, this.getSharedPreferences(Constant.nameSharedPreferences, MODE_PRIVATE).getString(Constant.TOKEN, ""));
        String url = ServiceUrls.SERVER_URL + "/attractions/comment/post";
        RequestParams params = new RequestParams();
        params.put("attractionsId", attractionsId);
        params.put("content", content);
        client.post(url, params, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                CommonUtils.showOkDialog(CommentActivity.this, getResources().getString(R.string.dialog_title_error), responseString, null);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (statusCode == 200) {
                    try {
                        JSONObject jObject = new JSONObject(responseString);
                        String success = CommonUtils.getStringValid(jObject.getString("success"));
                        if ("1".equals(success)) {
                            JSONArray jArrData = new JSONArray(jObject.getString("data"));
                            listComments = new ArrayList<Comment>();
                            for (int i = 0; i < jArrData.length(); i++) {
                                JSONObject jData = jArrData.getJSONObject(i);
                                Comment comment = Comment.getComment(jData);
                                listComments.add(comment);
                            }
                            commentAdapter = new CommentAdapter(CommentActivity.this, R.layout.item_comment, listComments);
                            listViewComments.setAdapter(commentAdapter);
                        } else {
                            String message = CommonUtils.getStringValid(jObject.getString("message"));
                            CommonUtils.showOkDialog(CommentActivity.this, getResources().getString(R.string.dialog_title_error), message, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    CommonUtils.showOkDialog(CommentActivity.this, getResources().getString(R.string.dialog_title_error), "statusCode: " + statusCode, null);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                listViewComments.setSelection(0);
            }
        });
    }
}
