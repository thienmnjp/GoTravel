package com.loser.gotravel.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.loser.gotravel.R;


/**
 * Created by DucAnhZ on 16/10/2015.
 */
public class GoTravelUtils {
    public static void showDialogServerProblem(Activity activity) {
        try {
            CommonUtils.showOkDialog(activity,
                    activity.getString(R.string.dialog_title_error),
                    activity.getString(R.string.dialog_content_server_problem),
                    null);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public static void showDialogNetworkProblem(Activity activity) {
        CommonUtils
                .showOkDialog(
                        activity,
                        activity.getString(R.string.dialog_title_common),
                        activity.getString(R.string.dialog_title_content_network_problem),
                        null);
    }

    public static boolean networkConnected(Activity activity) {
        if (CommonUtils.checkNetwork(activity)) {
            return true;
        } else {
            // Show msg
            showDialogNetworkProblem(activity);
            return false;
        }
    }

    public static void hideKeyboard(Activity activity, View view) {
        final InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static Object getDataFromResponseJsonString(String jsonString) {
        try {
            JSONObject obj = new JSONObject(jsonString);
            Integer code = obj.getInt("code");
            if (code == 0) {// Success
                return obj.get("data");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getDataJSONObjectFromResponseJsonString(
            String jsonString) {
        Object obj = getDataFromResponseJsonString(jsonString);
        if (obj != null && obj instanceof JSONObject) {
            return (JSONObject) obj;
        }
        return null;
    }

    public static JSONArray getDataJSONArrayFromResponseJsonString(
            String jsonString) {
        Object obj = getDataFromResponseJsonString(jsonString);
        if (obj != null && obj instanceof JSONObject) {
            return (JSONArray) obj;
        }
        return null;
    }

    public static String convertStringDate(String strDate, String date1,
                                           String date2) {
        if (strDate == null || strDate.equals("") || strDate.equals("null")) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(date1);
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (new SimpleDateFormat(date2)).format(date);
    }
}
