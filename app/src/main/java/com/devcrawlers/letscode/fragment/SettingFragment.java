package com.devcrawlers.letscode.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.devcrawlers.letscode.Preferences.SettingsPreferences;
import com.devcrawlers.letscode.R;
import com.devcrawlers.letscode.modeles.Settings;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ganfra.materialspinner.MaterialSpinner;

public class SettingFragment extends Fragment {


    @BindView(R.id.setting_cource_notif_oncecreated)
    RadioGroup radioGroupOnCreated;

    @BindView(R.id.setting_cource_notif_oncestarted)
    RadioGroup radioGroupOnStarted;

    @BindView(R.id.setting_cource_language)
    MaterialSpinner materialSpinnerLanguage;

    @BindView(R.id.setting_cource_theme)
    MaterialSpinner materialSpinnerTheme;

    @BindView(R.id.settings_savebtn)
    MaterialButton materialsave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        ButterKnife.bind(this, view);


        workWith(view);
        return view;
    }

    private void workWith(View view) {

        SettingsPreferences.init(getActivity());
        Settings settings = SettingsPreferences.getCurrentSettings();

        List<String> langues = new ArrayList<>(Arrays.asList(getContext().getResources().getStringArray(R.array.language)));
        List<String> themes = new ArrayList<>(Arrays.asList(getContext().getResources().getStringArray(R.array.theme)));

        materialSpinnerLanguage.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, langues));
        materialSpinnerTheme.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, themes));

        // to edit
        materialSpinnerLanguage.setSelection(langues.indexOf(settings.getLanguage()));
        materialSpinnerTheme.setSelection(themes.indexOf(settings.getTheme()));

        radioGroupOnCreated.check(settings.isNotificationForCreated() ? R.id.setting_cource_notif_oncecreated_yes : R.id.setting_cource_notif_oncecreated_no);
        radioGroupOnStarted.check(settings.isNotificationForStarted() ? R.id.setting_cource_notif_oncestarted_yes : R.id.setting_cource_notif_oncestarted_no);

        materialsave.setOnClickListener(this::save);

    }

    private void save(View view) {

        Settings settings = new Settings(getContext());

        settings.setTheme(materialSpinnerTheme.getSelectedItem().toString());
        settings.setLanguage(materialSpinnerLanguage.getSelectedItem().toString());

        settings.setNotificationForCreated(radioGroupOnCreated.getCheckedRadioButtonId() == R.id.setting_cource_notif_oncecreated_yes);
        settings.setNotificationForStarted(radioGroupOnStarted.getCheckedRadioButtonId() == R.id.setting_cource_notif_oncestarted_yes);


        updateUI(settings);

        SettingsPreferences.setCurrentSettings(settings);
    }

    private void updateUI(Settings settings) {

        //set theme
        // set langhage


    }
}
