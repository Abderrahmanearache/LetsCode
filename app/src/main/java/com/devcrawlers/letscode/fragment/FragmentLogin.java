package com.devcrawlers.letscode.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.devcrawlers.letscode.LoginActivity;
import com.devcrawlers.letscode.Preferences.UserPreferences;
import com.devcrawlers.letscode.Provider;
import com.devcrawlers.letscode.R;
import com.devcrawlers.letscode.modeles.User;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.mateware.snacky.Snacky;

public class FragmentLogin extends Fragment {
    GoogleSignInClient googleSignInClient;

    private static final int GOOGLE_FLAG = 100;

    CallbackManager facebookCallbackManager;


    @BindView(R.id.fb_login_botton)
    LoginButton fbLoginButton;

    @BindView(R.id.google_login_button)
    SignInButton googleSignInButton;

    @BindView(R.id.login_progress)
    ProgressBar progressBar;


    @BindView(R.id.login_email_field)
    TextInputLayout emailTextInputLayout;

    @BindView(R.id.login_password_field)
    TextInputLayout passwTextInputLayout;

    @BindView(R.id.login_loginbutton)
    MaterialButton loginMaterialButton;

    @BindView(R.id.login_signinbutton)
    MaterialButton signinMaterialButton;

    @BindView(R.id.login_guestbutton)
    MaterialButton guestMaterialButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.login_fragment, container, false);

        ButterKnife.bind(this, v);


        workWith(v);

        return v;
    }

    private void workWith(View v) {


        initFacebook();
        initGoogle();
        loginMaterialButton.setOnClickListener(e -> {
            User user = new User();

            user.setEmail(emailTextInputLayout.getEditText().getText().toString());
            user.setUsername(emailTextInputLayout.getEditText().getText().toString());
            user.setPassword(passwTextInputLayout.getEditText().getText().toString());

            try {
                auth(user);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

        });

        signinMaterialButton.setOnClickListener(e ->
                ((LoginActivity) getActivity()).showSignIn());
        guestMaterialButton.setOnClickListener(e ->
                connected(User.guest()));

    }

    private void auth(User user) throws JSONException {

        Volley.newRequestQueue(getContext()).add(new JsonObjectRequest(
                        Constants.URL_LOGIN,
                        new JSONObject(new Gson().toJson(user)),
                        response -> {
                            progress(false);
                            try {
                                if (response.getString("message").equals("ok")) {
                                    String user1 = response.getJSONObject("user").toString();
                                    connected(new Gson().fromJson(user1, User.class));
                                } else
                                    Snacky.builder().setActivity(getActivity()).setDuration(Snacky.LENGTH_SHORT)
                                            .setText("Mail or password incorrect !")
                                            .error()
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
                )
        );
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppEventsLogger.activateApp(getActivity().getApplication());
        facebookCallbackManager = CallbackManager.Factory.create();

    }

    private void initFacebook() {

        fbLoginButton.setPermissions("public_profile", "user_friends", "user_photos", "email", "user_birthday", "public_profile", "contact_email");
        fbLoginButton.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        (json, response) -> {
                            try {
                                User user = new User();
                                if (json.has("email"))
                                    user.setEmail(json.getString("email"));
                                else
                                    user.setEmail("");
                                user.setUsername(json.getString("id"));
                                user.setFullname(json.getString("first_name") + " " + json.getString("last_name").toUpperCase());
                                user.setImage("https://graph.facebook.com/" + json.getString("id") + "/picture?type=large");
                                user.setProvider(Provider.facebook.name());

                                user.setRole("user");
                                saveOrRecover(user);
                            } catch (JSONException e) {
                                progress(false);
                                e.printStackTrace();
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                progress(false);
            }

            @Override
            public void onError(FacebookException exception) {
                progress(false);
                exception.printStackTrace();
            }
        });
    }

    private void saveOrRecover(User user) {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Constants.URL_SIGNIN,
                    new JSONObject(new Gson().toJson(user)),
                    response -> {
                        progress(false);
                        System.out.println(response);
                        try {
                            connected(new Gson().fromJson(response.getJSONObject("user").toString(), User.class));
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
    }

    private void initGoogle() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        googleSignInButton.setOnClickListener(this::signInWithGoogle);

    }

    public void signInWithGoogle(View v) {
        System.out.println("login in with google");
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_FLAG);
    }

    private void progress(boolean b) {
        progressBar.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progress(true);
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_FLAG) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            User user = new User(0,
                    task.getResult().getDisplayName(),
                    task.getResult().getId(),
                    "",
                    task.getResult().getEmail(),
                    task.getResult().getPhotoUrl().toString(),
                    "user",
                    Provider.google.name()
            );
            saveOrRecover(user);
        }
    }

    private void connected(User user) {
        System.out.println(user);
        UserPreferences.init(getActivity());
        UserPreferences.setCurrentUser(user);
        startActivity(new Intent(getContext(), HomeActivity.class));
        getActivity().finish();
    }


}
