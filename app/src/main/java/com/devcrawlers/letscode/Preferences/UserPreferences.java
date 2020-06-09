package com.devcrawlers.letscode.Preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.devcrawlers.letscode.modeles.User;
import com.google.gson.Gson;

public class UserPreferences {

    private static Context context;

    private static User user;

    public static void init(Context c) {
        context = c;
    }

    public static User getCurrentUser() {
        if (user == null)
            return readUser();
        return user;
    }

    public static void setCurrentUser(User userx) {
        user = userx;

        SharedPreferences.Editor sharedPrefEditor = context.getSharedPreferences("USER", Context.MODE_PRIVATE).edit();
        if (user == null)
            sharedPrefEditor.putString("currentUser", null);
        else
            sharedPrefEditor.putString("currentUser", new Gson().toJson(userx));
        sharedPrefEditor.apply();
    }

    private static User readUser() {

        if (context == null) {
            System.err.println("UserPreferences not initialized");
            return null;
        }

        SharedPreferences userSharedPreferences = context.getSharedPreferences("USER", Context.MODE_PRIVATE);

        String userjson = userSharedPreferences.getString("currentUser", null);

        if (userjson != null)
            user = new Gson().fromJson(userjson, User.class);
        else
            user = null;
        return user;

    }


}
