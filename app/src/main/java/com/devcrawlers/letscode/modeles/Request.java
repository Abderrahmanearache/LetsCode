package com.devcrawlers.letscode.modeles;


import com.devcrawlers.letscode.Preferences.UserPreferences;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    User owner;

    String id;
    String title, description;
    String timestap;
    int nbrVote = 1;


    public JSONObject toJsonNew() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", this.id);
            jsonObject.put("title", this.title);
            jsonObject.put("description", this.description);
            jsonObject.put("timestap", this.timestap);
            jsonObject.put("nbrvote", this.nbrVote);
            jsonObject.put("id_user", UserPreferences.getCurrentUser().getId());
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    public static Request fromJson(JSONObject jsonObject) {


        Request request = new Request();


        try {
            request.id = jsonObject.getString("id");
            request.title = jsonObject.getString("title");
            request.description = jsonObject.getString("description");
            request.nbrVote = jsonObject.getInt("nbrvote");
            request.timestap = jsonObject.getString("timestap");
            request.owner = new Gson().fromJson(jsonObject.getJSONObject("user").toString(), User.class);

        } catch (JSONException e) {
            System.err.println(" error in reading Request properties");
            e.printStackTrace();
        }
        return request;
    }


}
