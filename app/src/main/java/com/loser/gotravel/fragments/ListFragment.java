package com.loser.gotravel.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.loser.gotravel.HomeActivity;
import com.loser.gotravel.R;
import com.loser.gotravel.adapters.AttractionsAdapter;
import com.loser.gotravel.common.CommonUtils;
import com.loser.gotravel.common.Constant;
import com.loser.gotravel.common.GoTravelUtils;
import com.loser.gotravel.common.LoadMoreListView;
import com.loser.gotravel.objects.Attractions;
import com.loser.gotravel.services.CustomAsyncHttpClient;
import com.loser.gotravel.services.ServiceUrls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by DucAnhZ on 05/10/2015.
 */
public class ListFragment extends Fragment {
    //private ArrayList<Attractions> arrayAttractions;
    private AttractionsAdapter attractionsAdapter;
    private LoadMoreListView listViewAttractions;
    private View view;
    private Integer categoryId;
    private Integer totalItem;
    private HomeActivity homeActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fg_list_attractions, container, false);
        homeActivity = (HomeActivity) getActivity();
        listViewAttractions = (LoadMoreListView) view.findViewById(R.id.listViewAttractions);

        attractionsAdapter = new AttractionsAdapter(getActivity(), R.layout.item_attrantions, homeActivity.arrayAttractions);
        Bundle bundle = getArguments();
        if (bundle != null) {
            categoryId = bundle.getInt(Constant.kCategoryId);
        }
        listViewAttractions.setAdapter(attractionsAdapter);
        if (homeActivity.arrayAttractions.size() == 0) {
            parseAttractions(categoryId, 1, 100);
        }
        setTitle();

        listViewAttractions.setOnItemClickListener(new ClickItemAttractions());
        listViewAttractions.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //TODO -> 
            }
        });

        return view;
    }

    private void setTitle() {
        switch (categoryId) {
            case 1:
                homeActivity.tvActionbarTitle.setText("TÂY BẮC BỘ");
                break;
            case 2:
                homeActivity.tvActionbarTitle.setText("ĐÔNG BẮC BỘ");
                break;
            case 3:
                homeActivity.tvActionbarTitle.setText("BẮC TRUNG BỘ");
                break;
            case 4:
                homeActivity.tvActionbarTitle.setText("NAM TRUNG BỘ");
                break;
            case 5:
                homeActivity.tvActionbarTitle.setText("TÂY NAM BỘ");
                break;
            case 6:
                homeActivity.tvActionbarTitle.setText("ĐÔNG NAM BỘ");
                break;
            case 7:
                homeActivity.tvActionbarTitle.setText("TÂY NGUYÊN");
                break;
        }
    }

    private class ClickItemAttractions implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.kItemPosition, position);
            Fragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);
            ft.replace(R.id.contentChild, detailFragment, Constant.DETAIL_ATTRACTIONS);
            ft.addToBackStack(Constant.LIST_ATTRACTIONS);
            ft.commit();
        }
    }

    private void parseAttractions(int categoryId, int page, int limit) {

        if (!GoTravelUtils.networkConnected(getActivity())) {
            return;
        }

        String token = "";
        CustomAsyncHttpClient client = new CustomAsyncHttpClient(getActivity(), token);
        String url = ServiceUrls.SERVER_URL + "/attractions/list";
        RequestParams params = new RequestParams();
        params.put("categoryId", categoryId);
        params.put("page", page);
        params.put("limit", limit);
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
                        String success = CommonUtils.getStringValid(jObject.getString("success"));
                        totalItem = jObject.getInt("totalItem");
                        if ("1".equals(success)) {
                            JSONArray jArrData = new JSONArray(jObject.getString("data"));
                            for (int i = 0; i < jArrData.length(); i++) {
                                Attractions attractions = Attractions.getAttractions(jArrData.getJSONObject(i));
                                if (attractions != null) {
                                    homeActivity.arrayAttractions.add(attractions);
                                }
                            }
                            attractionsAdapter.notifyDataSetChanged();
                        } else {
                            GoTravelUtils.showDialogServerProblem(getActivity());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                GoTravelUtils.showDialogServerProblem(getActivity());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progressBar.dismiss();
            }
        });

    }


}
