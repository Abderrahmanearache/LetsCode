package com.devcrawlers.letscode.modeles;

import android.text.TextUtils;

import com.devcrawlers.letscode.Preferences.UserPreferences;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {

    String content;
    String timestap;
    User owner;
    Course course;



    public static Feedback fromJson(JSONObject jsonObject) {


        Feedback feedback = new Feedback();


        try {
            feedback.content = jsonObject.getString("content");
            feedback.timestap = jsonObject.getString("timestap");

            feedback.owner = new Gson().fromJson(jsonObject.getJSONObject("user").toString(), User.class);


        } catch (JSONException e) {
            System.err.println(" error in reading feedback properties");
            e.printStackTrace();
        }


        return feedback;
    }

    public JSONObject toJsonNew() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content", this.content);
            jsonObject.put("timestap", this.timestap);
            jsonObject.put("id_course", this.course.getId());
            jsonObject.put("id_user", UserPreferences.getCurrentUser().getId());
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
