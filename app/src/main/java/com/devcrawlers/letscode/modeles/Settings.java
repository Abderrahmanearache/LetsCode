package com.devcrawlers.letscode.modeles;

import android.content.Context;

import com.devcrawlers.letscode.R;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Settings {
    boolean notificationForCreated = true;
    boolean notificationForStarted = true;
    String language;
    String theme;

    public Settings(Context context) {
        language = context.getResources().getStringArray(R.array.language)[0];
        theme = context.getResources().getStringArray(R.array.theme)[0];
        notificationForCreated = true;
        notificationForStarted = true;
    }


}
