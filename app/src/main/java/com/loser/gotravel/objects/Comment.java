package com.loser.gotravel.objects;

import com.loser.gotravel.common.CommonUtils;

import org.json.JSONObject;

/**
 * Created by DucAnhZ on 28/11/2015.
 */
public class Comment {
    public String username;
    public String avatar;
    public String content;
    public String createdTime;

    public Comment() {}

    public static Comment getComment(JSONObject jData) {
        try {
            Comment comment = new Comment();
            comment.username = CommonUtils.getStringValid(jData.getString("username"));
            comment.avatar = CommonUtils.getStringValid(jData.getString("avatar"));
            comment.content = CommonUtils.getStringValid(jData.getString("content"));
            comment.createdTime = CommonUtils.getStringValid(jData.getString("createdTime"));

            return comment;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
