package com.devcrawlers.letscode.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.devcrawlers.letscode.Constants;
import com.devcrawlers.letscode.HomeActivity;
import com.devcrawlers.letscode.Preferences.UserPreferences;
import com.devcrawlers.letscode.Provider;
import com.devcrawlers.letscode.R;
import com.devcrawlers.letscode.modeles.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.mateware.snacky.Snacky;

public class FragmentSignIn extends Fragment {


    @BindView(R.id.signin_fullname_field)
    TextInputLayout fullNameTextInputEditText;

    @BindView(R.id.signin_username_field)
    TextInputLayout usernameTextInputEditText;

    @BindView(R.id.signin_password_field)
    TextInputLayout passwordTextInputEditText;

    @BindView(R.id.signin_password2_field)
    TextInputLayout password2TextInputEditText;

    @BindView(R.id.signin_email_field)
    TextInputLayout emailTextInputEditText;


    @BindView(R.id.signin_loginbutton)
    MaterialButton signin_button;

    @BindView(R.id.signin_progress)
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.signin_fragment, container, false);

        ButterKnife.bind(this, v);


        workWith(v);
        return v;
    }

    private void workWith(View v) {

        signin_button.setOnClickListener(v1 -> {
            if (!validate()) return;

            progress(true);

            User user = new User(-1,
                    fullNameTextInputEditText.getEditText().getText().toString(),
                    usernameTextInputEditText.getEditText().getText().toString(),
                    passwordTextInputEditText.getEditText().getText().toString(),
                    emailTextInputEditText.getEditText().getText().toString(),
                    null, "user", Provider.letscode.name()
            );

            try {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Constants.URL_SIGNIN,
                        new JSONObject(new Gson().toJson(user)),
                        response -> {
                            progress(false);
                            try {
                                if (response.getString("message").equals("ok")) {

                                    String user1 = response.getJSONObject("user").toString();
                                    connected(new Gson().fromJson(user1, User.class));

                                } else
                                    Snacky.builder().setActivity(getActivity()).setDuration(Snacky.LENGTH_SHORT)
                                            .setText("Username or email already taken")
                                            .warning()
                                            .show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        },
                        error -> {
                            progress(false);
                            Snacky.builder().setActivity(getActivity()).setDuration(Snacky.LENGTH_SHORT)
                                    .setText("Network problems")
                                    .error()
                                    .show();
                            error.printStackTrace();
                        }
                );
                Volley.newRequestQueue(getContext()).add(jsonObjectRequest);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void connected(User user) {
        UserPreferences.init(getActivity());
        UserPreferences.setCurrentUser(user);
        startActivity(new Intent(getContext(), HomeActivity.class));
        getActivity().finish();
    }

    Pattern fullnamePattern = Pattern.compile("^[a-zA-Z ]{4,35}$");
    Pattern userNamePattern = Pattern.compile("^[a-zA-Z]+[a-zA-Z.0-9]{4,35}$");
    Pattern emailPattern = Patterns.EMAIL_ADDRESS;
    Pattern passwordPattern = Pattern.compile("^[a-zA-Z 1-9._\\-]{8,45}$");

    private boolean validate() {

        emailTextInputEditText.setError(null);
        usernameTextInputEditText.setError(null);
        fullNameTextInputEditText.setError(null);
        passwordTextInputEditText.setError(null);
        password2TextInputEditText.setError(null);

        if (!fullnamePattern.matcher(fullNameTextInputEditText.getEditText().getText()).matches()) {
            fullNameTextInputEditText.setError("Fullname format not allowed");
            return false;
        }
        if (!userNamePattern.matcher(usernameTextInputEditText.getEditText().getText()).matches()) {
            usernameTextInputEditText.setError("Name format not allowed");
            return false;
        }
        if (!emailPattern.matcher(emailTextInputEditText.getEditText().getText()).matches()) {
            emailTextInputEditText.setError("Email format not allowed");
            return false;
        }
        if (!passwordPattern.matcher(passwordTextInputEditText.getEditText().getText()).matches()) {
            passwordTextInputEditText.setError("password format not allowed");
            return false;
        }
        if (!password2TextInputEditText.getEditText().getText().toString()
                .equals(passwordTextInputEditText.getEditText().getText().toString())) {
            password2TextInputEditText.setError("Password does not matches");
            return false;
        }
        return true;
    }

    private void progress(boolean b) {
        progressBar.setVisibility(b ? View.VISIBLE : View.GONE);
    }
}
