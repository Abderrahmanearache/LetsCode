package com.devcrawlers.letscode.modeles;

import android.text.TextUtils;

import com.devcrawlers.letscode.Preferences.UserPreferences;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Course implements Comparable {


    String id;


    String title;
    String channel;
    String date;
    String heure;
    List<String> contents = new ArrayList<>();







    User teacher;
    int state = 0;
    List<Feedback> feedbacks = new ArrayList<>();












    public JSONObject toJson() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("title", title);
            jsonObject.put("channel", channel);
            jsonObject.put("date", date);
            jsonObject.put("heure", heure);
            jsonObject.put("state", state);
            jsonObject.put("contents", TextUtils.join(";", contents));
            jsonObject.put("id_user", UserPreferences.getCurrentUser().getId());
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static Course fromJson(JSONObject jsonObject) {


        Course course = new Course();


        try {
            course.id = jsonObject.getString("id");
            course.title = jsonObject.getString("title");
            course.heure = jsonObject.getString("heure");
            course.date = jsonObject.getString("date");
            course.channel = jsonObject.getString("channel");
            course.state = jsonObject.getInt("state");
            String ctn = jsonObject.getString("contents");
            course.contents = Arrays.asList(ctn.split(";"));

            JSONArray feedbacks = jsonObject.getJSONArray("feedbacks");
            course.feedbacks = new ArrayList<>();
            for (int i = 0; i < feedbacks.length(); i++)
                course.feedbacks.add(Feedback.fromJson(feedbacks.getJSONObject(i)));


            course.teacher = new Gson().fromJson(jsonObject.getJSONObject("teacher").toString(), User.class);


        } catch (JSONException e) {
            System.err.println(" error in reading Cource properties");
            e.printStackTrace();
        }
        return course;
    }

    public boolean isNew() {
        return getCalendar().after(Calendar.getInstance());
    }

    @Override
    public int compareTo(Object o) {
        getCalendar().compareTo(((Course) o).getCalendar());
        return 0;
    }

    public Calendar getCalendar() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
        Calendar c = new GregorianCalendar();

        try {
            c.setTime(simpleDateFormat.parse(date + " " + heure));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(c);
        return c;
    }

    public boolean isConfirmed() {
        return state == 1;
    }

    public boolean isRejected() {
        return state == -1;
    }

    public boolean isWaitingForConfirmation() {
        return state == 0;
    }


}
