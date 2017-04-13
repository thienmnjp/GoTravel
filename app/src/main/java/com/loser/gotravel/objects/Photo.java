package com.loser.gotravel.objects;

/**
 * Created by DucAnhZ on 06/10/2015.
 */
public class Photo {
    public String photoUrl;
    public String description;

    public Photo(){}

    public Photo(String photoUrl, String description) {
        this.description = description;
        this.photoUrl = photoUrl;
    }
}
