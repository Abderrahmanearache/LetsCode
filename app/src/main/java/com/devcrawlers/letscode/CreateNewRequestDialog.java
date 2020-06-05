package com.devcrawlers.letscode;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.devcrawlers.letscode.Preferences.UserPreferences;
import com.devcrawlers.letscode.modeles.Request;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateNewRequestDialog extends Dialog {


    @BindView(R.id.newrequest_description)
    TextInputLayout descriptionInputLayout;

    @BindView(R.id.newrequest_titletext)
    TextInputLayout titleInputLayout;

    @BindView(R.id.newrequest_savebtn)
    MaterialButton saveMaterialButton;

    @BindView(R.id.newrequest_progress)
    ProgressBar progressBar;

    public CreateNewRequestDialog(@NonNull Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_request_fragment);
        ButterKnife.bind(this);

        workWith(null);

    }


    private void workWith(View view) {

        saveMaterialButton.setOnClickListener(this::save);


    }

    private void save(View view) {
        if (titleInputLayout.getEditText().getText().toString().trim().length() < 6) {

            titleInputLayout.setError(getContext().getString(R.string.sixcontentenough));
            return;
        }
        titleInputLayout.setError(null);
        if (descriptionInputLayout.getEditText().getText().toString().trim().length() < 6) {

            descriptionInputLayout.setError(getContext().getString(R.string.sixcontentenough));
            return;
        }
        descriptionInputLayout.setError(null);

        String timestap = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

        progressBar.setVisibility(View.VISIBLE);

        Request request = new Request(
                UserPreferences.getCurrentUser(),
                "0",
                titleInputLayout.getEditText().getText().toString(),
                descriptionInputLayout.getEditText().getText().toString(),
                timestap,
                1
        );


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                Constants.URL_REQUEST_CREATE,
                request.toJsonNew(),
                response -> {
                    try {
                        if (response.getString("message").equalsIgnoreCase("ok")) {
                            //finishWith(Request.fromJson(response.getJSONObject("request")));
                            dismiss();
                        } else
                            System.out.println(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace
        );

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);


    }


}
