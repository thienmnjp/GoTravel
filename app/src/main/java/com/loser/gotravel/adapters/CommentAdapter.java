package com.loser.gotravel.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loser.gotravel.R;
import com.loser.gotravel.common.RoundedImageView;
import com.loser.gotravel.objects.Comment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by DucAnhZ on 28/11/2015.
 */
public class CommentAdapter extends ArrayAdapter<Comment> {
    private ArrayList<Comment> listComments;
    private int layoutId;
    private Activity activity;
    private DisplayImageOptions option;
    private RoundedImageView imvAvatar;
    private TextView tvUsername;
    private TextView tvContent;
    private TextView tvTime;

    public CommentAdapter(Activity activity, int layoutId, ArrayList<Comment> listComments) {
        super(activity, layoutId, listComments);
        this.activity = activity;
        this.layoutId = layoutId;
        this.listComments = listComments;
        option = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_avatar_default)
                .showImageOnFail(R.mipmap.ic_avatar_default)
                .showImageOnLoading(R.mipmap.ic_avatar_default)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .cacheInMemory(true).cacheOnDisk(true).build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = this.activity.getLayoutInflater();
            convertView = inflater.inflate(layoutId, null);
        }
        imvAvatar = (RoundedImageView) convertView.findViewById(R.id.imvAvatar);
        tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        tvContent = (TextView) convertView.findViewById(R.id.tvContent);
        tvTime = (TextView) convertView.findViewById(R.id.tvTime);

        try {
            ImageLoader.getInstance().displayImage(listComments.get(position).avatar, imvAvatar, option);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvUsername.setText(listComments.get(position).username);
        tvContent.setText(listComments.get(position).content);
        tvTime.setText(convertTime(listComments.get(position).createdTime));

        return convertView;
    }

    private String convertTime(String createdTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        SimpleDateFormat formatVN = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date =  format.parse(createdTime);
            format.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            Long dateInVN = format.parse(format.format(date)).getTime();
            Calendar calendar = Calendar.getInstance();
            Long time = calendar.getTimeInMillis() - dateInVN;
            if (time < 60000) {
                time = (calendar.getTimeInMillis() - dateInVN)/1000;
                return "Cách đây " + time + " giây";
            } else if (60000 <= time && time < 60000 * 60) {
                time = (calendar.getTimeInMillis() - dateInVN)/60000;
                return "Cách đây " + time + " phút";
            } else if (60000 * 60 <= time && time < 60000 * 60 * 24) {
                time = (calendar.getTimeInMillis() - dateInVN)/(60000 * 60);
                return "Cách đây " + time + " giờ";
            } else if (60000 * 60 * 24 <= time && time < 60000 * 60 * 24 * 3) {
                time = (calendar.getTimeInMillis() - dateInVN)/(60000 * 60 * 24);
                return "Cách đây " + time + " ngày";
            } else {
                return formatVN.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
