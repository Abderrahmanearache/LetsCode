package com.devcrawlers.letscode.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyPrefs {


    private Context context;
    private static MyPrefs instance;

    private MyPrefs(Context c) {
        context = c;
    }


    public static MyPrefs init(Context c) {
        if (instance == null)
            instance = new MyPrefs(c);
        else
            instance.context = c;
        return instance;
    }


    public List<String> getSavedInteressedCources() {

        SharedPreferences savedInfo = context.getSharedPreferences("SavedInfo", Context.MODE_PRIVATE);

        String interessedCources = savedInfo.getString("cources", null);

        if (interessedCources == null)
            return new ArrayList<>();

        ArrayList<String> list = new ArrayList<>(Arrays.asList(interessedCources.split(",")));

        return list;

    }


    public void saveInteressedCources(List<String> listId) {

        SharedPreferences.Editor editor = context.getSharedPreferences("SavedInfo", Context.MODE_PRIVATE).edit();


        if (listId.size() > 1)
            editor.putString("cources", TextUtils.join(",", listId));
        else if (listId.size() == 1)
            editor.putString("cources", listId.get(0));
        else
            editor.putString("cources", null);
        editor.apply();
    }


    public List<String> getLikedRequest() {


        SharedPreferences savedInfo = context.getSharedPreferences("SavedInfo", Context.MODE_PRIVATE);

        String interessedCources = savedInfo.getString("LikedRequest", null);

        if (interessedCources == null)
            return new ArrayList<>();


        return new ArrayList<>(Arrays.asList(interessedCources.split(",")));

    }


    public void saveLikedRequest(List<String> listId) {

        SharedPreferences.Editor editor = context.getSharedPreferences("SavedInfo", Context.MODE_PRIVATE).edit();

        TextUtils.join(",", listId);

        editor.putString("LikedRequest", TextUtils.join(",", listId));
        editor.apply();
    }


}
