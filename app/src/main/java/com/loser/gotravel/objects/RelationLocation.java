package com.loser.gotravel.objects;

import com.loser.gotravel.common.CommonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by DucAnhZ on 21/10/2015.
 */
public class RelationLocation {

    public String type;
    public String name;
    public String price;
    public String description;
    public String status;
    public String photoMapUrl;
    public Double lat;
    public Double lng;
    public Integer totalLike;
    public Integer totalUnlike;
    public ArrayList<Photo> listPhotos = new ArrayList<>();
    public ArrayList<Contact> listContacts = new ArrayList<>();

    public static RelationLocation getReLationLocation(JSONObject jData) {
        try {
            RelationLocation relationLocation = new RelationLocation();
            try {
                relationLocation.type = CommonUtils.getStringValid(jData.getString("type"));
                relationLocation.name = CommonUtils.getStringValid(jData.getString("name"));
                relationLocation.price = CommonUtils.getStringValid(jData.getString("price"));
                relationLocation.description = CommonUtils.getStringValid(jData.getString("description"));
                relationLocation.status = CommonUtils.getStringValid(jData.getString("status"));
                relationLocation.photoMapUrl = CommonUtils.getStringValid(jData.getString("photoMapUrl"));
                relationLocation.totalLike = 0;
                relationLocation.totalUnlike = 0;
                relationLocation.lat = jData.getDouble("lat");
                relationLocation.lng = jData.getDouble("lng");
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                JSONArray jArrPhotos = new JSONArray(jData.getString("photos"));
                for (int i = 0; i < jArrPhotos.length(); i++) {
                    Photo photo = new Photo();
                    JSONObject jPhoto = jArrPhotos.getJSONObject(i);
                    photo.photoUrl = CommonUtils.getStringValid(jPhoto.getString("photoUrl"));
                    photo.description = CommonUtils.getStringValid(jPhoto.getString("description"));
                    relationLocation.listPhotos.add(photo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                JSONArray jArrContacts = new JSONArray(jData.getString("contacts"));
                for (int i = 0; i < jArrContacts.length(); i++) {
                    Contact contact = new Contact();
                    JSONObject jContact = jArrContacts.getJSONObject(i);
                    contact.contactAddress = CommonUtils.getStringValid(jContact.getString("contactAddress"));
                    contact.contactEmail = CommonUtils.getStringValid(jContact.getString("contactEmail"));
                    contact.contactName = CommonUtils.getStringValid(jContact.getString("contactName"));
                    contact.contactPhone = CommonUtils.getStringValid(jContact.getString("contactPhone"));
                    relationLocation.listContacts.add(contact);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return relationLocation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
