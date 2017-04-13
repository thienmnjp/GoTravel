package com.loser.gotravel.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loser.gotravel.R;
import com.loser.gotravel.greenDao.AttractionsDao;
import com.loser.gotravel.greenDao.DaoMaster;
import com.loser.gotravel.greenDao.DaoSession;
import com.loser.gotravel.objects.Attractions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

/**
 * Created by DucAnhZ on 06/10/2015.
 */
public class AttractionsAdapter extends ArrayAdapter<Attractions> {
    private int layoutId;
    private Activity activity;
    private ArrayList<Attractions> listAttractions;
    private DisplayImageOptions option;

    private ImageView imvItem;
    private TextView tvAttractionsName;
    private TextView tvRate;
    private TextView tvClassify;
    private TextView tvDescription;
    private ImageView imvBookmark;

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private AttractionsDao attractionsDao;
    private Cursor cursor;

    public AttractionsAdapter(Activity activity, int layoutId, ArrayList<Attractions> listAttractions) {
        super(activity, layoutId, listAttractions);
        this.layoutId = layoutId;
        this.activity = activity;
        this.listAttractions = listAttractions;
        option = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .showImageOnLoading(R.mipmap.ic_launcher)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .cacheInMemory(true).cacheOnDisk(true).build();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(activity,
                "GoTravelManager", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        attractionsDao = daoSession.getAttractions();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = this.activity.getLayoutInflater();
            convertView = inflater.inflate(this.layoutId, null);
        }

        imvItem = (ImageView) convertView.findViewById(R.id.imvItem);
        tvAttractionsName = (TextView) convertView.findViewById(R.id.tvAttractionsName);
        tvRate = (TextView) convertView.findViewById(R.id.tvRate);
        tvClassify = (TextView) convertView.findViewById(R.id.tvClassify);
        tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
        imvBookmark = (ImageView) convertView.findViewById(R.id.imvBookmark);

        try {
            ImageLoader.getInstance().displayImage(listAttractions.get(position).listPhoto.get(0).photoUrl, imvItem, option);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String titleName = listAttractions.get(position).name;
        if (titleName.length() > 20) {
            titleName = titleName.substring(0, 20) + "...";
        }
        tvAttractionsName.setText(titleName);
        tvRate.setText(String.valueOf(listAttractions.get(position).varRate)
                + "/"
                + String.valueOf(listAttractions.get(position).totalRate)
                + " đánh giá");
        tvClassify.setText(listAttractions.get(position).classify);
        tvDescription.setText(listAttractions.get(position).listInformation.get(0).description);


        cursor = db.query(attractionsDao.getTablename(), null, "attractionsId = " + listAttractions.get(position).attractionsId, null, null, null, null);
        if (cursor.getCount() > 0) {
            imvBookmark.setBackground(activity.getResources().getDrawable(R.mipmap.ic_bookmark_yes));
        } else {
            imvBookmark.setBackground(activity.getResources().getDrawable(R.mipmap.ic_bookmark_no));
        }

        imvBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursorCheck = db.query(attractionsDao.getTablename(), null, "attractionsId = " + listAttractions.get(position).attractionsId, null, null, null, null);
                if (cursorCheck.getCount() == 0) {
                    Attractions attractions = new Attractions(null,
                            listAttractions.get(position).attractionsId,
                            listAttractions.get(position).name,
                            listAttractions.get(position).address,
                            listAttractions.get(position).classify);
                    attractionsDao.insert(attractions);
                    notifyDataSetChanged();
                } else {
                    db.delete(attractionsDao.getTablename(), "attractionsId=?", new String[]{ String.valueOf(listAttractions.get(position).attractionsId)});
                    notifyDataSetChanged();
                }
            }
        });
        cursor.close();

        return convertView;
    }
}
