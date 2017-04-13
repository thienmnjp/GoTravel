package com.loser.gotravel;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loser.gotravel.adapters.BookmarkAdapter;
import com.loser.gotravel.common.CommonUtils;
import com.loser.gotravel.greenDao.AttractionsDao;
import com.loser.gotravel.greenDao.DaoMaster;
import com.loser.gotravel.greenDao.DaoSession;
import com.loser.gotravel.objects.Attractions;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by DucAnhZ on 29/11/2015.
 */
public class BookmarkActivity extends FragmentActivity {
    private BookmarkAdapter bookmarkAdapter;
    private ListView listviewBookmark;
    private ArrayList<Attractions> listAttractions;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private AttractionsDao attractionsDao;
    private Cursor cursor;
    private TextView tvTitleBookmark;
    private View viewStatusBar;
    private RelativeLayout btnBackBookmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_bookmark);

        listAttractions = new ArrayList<>();
        listviewBookmark = (ListView) findViewById(R.id.listviewBookmark);
        tvTitleBookmark = (TextView) findViewById(R.id.tvTitleBookmark);
        tvTitleBookmark.setText("ĐÁNH DẤU");
        viewStatusBar = (View) findViewById(R.id.viewStatusBar);
        btnBackBookmark = (RelativeLayout) findViewById(R.id.btnBackBookmark);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            viewStatusBar.setVisibility(View.VISIBLE);
            viewStatusBar.getLayoutParams().height = CommonUtils.getStatusBarHeight(this);
            viewStatusBar.setBackgroundColor(getResources().getColor(R.color.action_bar));
        }
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,
                "GoTravelManager", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        attractionsDao = daoSession.getAttractions();

        cursor = db.query(attractionsDao.getTablename(), null, null, null, null, null, null);
        listAttractions = (ArrayList<Attractions>) attractionsDao.queryBuilder().list();
        bookmarkAdapter = new BookmarkAdapter(this, R.layout.item_bookmark, listAttractions);
        listviewBookmark.setAdapter(bookmarkAdapter);

        btnBackBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
