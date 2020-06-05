package com.devcrawlers.letscode.Preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.devcrawlers.letscode.R;
import com.devcrawlers.letscode.modeles.Settings;
import com.devcrawlers.letscode.modeles.User;
import com.google.gson.Gson;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class SettingsPreferences {
    private static Context context;

    private static Settings settings;

    public static Settings getCurrentSettings() {
        if (settings == null)
            readSettings();
        return settings;
    }

    public static void init(Context c) {
        context = c;
    }

    private static void readSettings() {
        if (context == null) {
            System.err.println("UserPreferences not initialized");
            return;
        }
        SharedPreferences userSharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);

        String settings1 = userSharedPreferences.getString("settings", null);

        if (settings1 != null)
            settings = new Gson().fromJson(settings1, Settings.class);
        else
            settings = new Settings(context);
    }

    public static void setCurrentSettings(Settings settings1) {
        settings = settings1;

        SharedPreferences.Editor sharedPrefEditor = context.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        if (settings1 == null)
            sharedPrefEditor.putString("Settings", null);
        else
            sharedPrefEditor.putString("Settings", new Gson().toJson(settings1));
        sharedPrefEditor.apply();
    }

}
