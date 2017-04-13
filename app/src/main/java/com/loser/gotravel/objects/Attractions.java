package com.loser.gotravel.objects;

import android.content.Intent;

import com.loser.gotravel.common.CommonUtils;
import com.loser.gotravel.common.GoTravelUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by DucAnhZ on 05/10/2015.
 */
public class Attractions {
    public Long _id;
    public Long attractionsId;
    public String name;
    public String address;
    public String classify;
    public Double varRate;
    public Integer totalRate;
    public Double yourRate;

    public ArrayList<Information> listInformation = new ArrayList<Information>();
    public ArrayList<Photo> listPhoto = new ArrayList<Photo>();

    public Attractions() {
        super();
    }

    public Attractions(Long attractionsId){
        this.attractionsId = attractionsId;
    }

    public static Attractions getAttractions(JSONObject jSonData) {
        try {
            Attractions attractions = new Attractions();
            try {
                attractions.attractionsId = jSonData.getLong("attractionsId");
                attractions.name = CommonUtils.getStringValid(jSonData.getString("name"));
                attractions.address = CommonUtils.getStringValid(jSonData.getString("address"));
                attractions.varRate = jSonData.getDouble("varRate");
                attractions.totalRate = jSonData.getInt("totalRate");
                if (CommonUtils.getStringValid(jSonData.getString("yourRate")).equals("")) {
                    attractions.yourRate = 0.0;
                } else {
                    attractions.yourRate = Double.parseDouble(CommonUtils.getStringValid(jSonData.getString("yourRate")));
                }

                attractions.classify = CommonUtils.getStringValid(jSonData.getString("classify"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                JSONArray jArrInformation = new JSONArray(CommonUtils.getStringValid(jSonData.getString("informations")));
                for (int i = 0; i < jArrInformation.length(); i++ ) {
                    JSONObject jInformation = jArrInformation.getJSONObject(i);
                    Information information = new Information(
                            CommonUtils.getStringValid(jInformation.getString("title")),
                            CommonUtils.getStringValid(jInformation.getString("description"))
                    );
                    attractions.listInformation.add(information);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                JSONArray jArrPhotos = new JSONArray(CommonUtils.getStringValid(jSonData.getString("photos")));
                for (int i = 0; i < jArrPhotos.length(); i++) {
                    JSONObject jPhoto = jArrPhotos.getJSONObject(i);
                    Photo photo = new Photo(
                            CommonUtils.getStringValid(jPhoto.getString("photoUrl")),
                            CommonUtils.getStringValid(jPhoto.getString("description"))
                    );
                    attractions.listPhoto.add(photo);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        return attractions;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Attractions(Long _id, Long attractionsId, String name, String address, String classify) {
        this._id = _id;
        this.name = name;
        this.attractionsId = attractionsId;
        this.address = address;
        this.classify = classify;
    }

}
