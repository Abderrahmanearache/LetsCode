package com.devcrawlers.letscode.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.devcrawlers.letscode.Constants;
import com.devcrawlers.letscode.Preferences.UserPreferences;
import com.devcrawlers.letscode.R;
import com.devcrawlers.letscode.modeles.Course;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.mateware.snacky.Snacky;
import fr.ganfra.materialspinner.MaterialSpinner;

public class NewCourceFragment extends Fragment {


    @BindView(R.id.newcource_contenttext)
    TextInputLayout contentTextInputLayout;

    @BindView(R.id.newcource_titletext)
    TextInputLayout titleTextInputLayout;

    @BindView(R.id.newcource_contentbtn)
    MaterialButton addcontentMaterialButton;

    @BindView(R.id.newcource_date)
    MaterialButton dateMaterialButton;

    @BindView(R.id.newcource_time)
    MaterialButton timeMaterialButton;

    @BindView(R.id.newcource_channel)
    MaterialSpinner channelSpinner;

    @BindView(R.id.newcource_contentlist)
    ListView contentListView;

    @BindView(R.id.newcource_progress)
    ProgressBar progressBar;

    @BindView(R.id.newcource_savebtn)
    MaterialButton saveMaterialButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_cource_fragment, container, false);
        ButterKnife.bind(this, view);


        workWith(view);


        return view;
    }

    ArrayList<String> contents;

    private void updateListWidth() {

        ListAdapter myListAdapter = contentListView.getAdapter();
        if (myListAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int adapterCount = myListAdapter.getCount();
        for (int size = 0; size < adapterCount; size++) {
            View listItem = myListAdapter.getView(size, null, contentListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = contentListView.getLayoutParams();

        params.height = (totalHeight
                + (contentListView.getDividerHeight() * (adapterCount)));

    }

    private void workWith(View view) {
        contents = new ArrayList<>();

        String[] ITEMS = getResources().getStringArray(R.array.channels);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        channelSpinner.setAdapter(adapter);

        SimpleListAdapter listAdapter = new SimpleListAdapter(contents) {
            @Override
            public void delete(int s) {
                contents.remove(s);
                notifyDataSetChanged();
                updateListWidth();
            }
        };
        contentListView.setAdapter(listAdapter);


        addcontentMaterialButton.setOnClickListener(v -> {
            if (contents.size() == 6) {
                contentTextInputLayout.setError(getContext().getString(R.string.sixcontentenough));
                return;
            }
            if (contentTextInputLayout.getEditText().getText().length() < 5) {
                contentTextInputLayout.setError(getContext().getString(R.string.lenghtnotpermitted));
                return;
            }

            contents.add(contentTextInputLayout.getEditText().getText().toString());
            contentTextInputLayout.getEditText().setText("");
            contentTextInputLayout.setError(null);

            listAdapter.setItems(contents);
            listAdapter.notifyDataSetChanged();
            updateListWidth();


        });

        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        dateMaterialButton.setText(date);
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        timeMaterialButton.setText(time);

        dateMaterialButton.setOnClickListener(v -> {
            Calendar instance = Calendar.getInstance();
            new DatePickerDialog(getContext(),
                    (view1, year, month, dayOfMonth) -> {
                        instance.set(year, month, dayOfMonth);
                        String Ca = new SimpleDateFormat("dd/MM/yyyy").format(instance.getTime());
                        dateMaterialButton.setText(Ca);
                    },
                    instance.get(Calendar.YEAR),
                    instance.get(Calendar.MONTH),
                    instance.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        timeMaterialButton.setOnClickListener(v -> {
            Calendar instance = Calendar.getInstance();
            new TimePickerDialog(getContext(),
                    (view1, hourOfDay, minute) -> {
                        instance.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        instance.set(Calendar.MINUTE, minute);
                        String Ca = new SimpleDateFormat("HH:mm").format(instance.getTime());
                        timeMaterialButton.setText(Ca);
                    },
                    instance.get(Calendar.HOUR_OF_DAY),
                    instance.get(Calendar.MINUTE), true)
                    .show();

        });


        saveMaterialButton.setOnClickListener(v -> {

            if (titleTextInputLayout.getEditText().getText().toString().trim().length() < 5) {
                titleTextInputLayout.setError(getContext().getString(R.string.sixcontentenough));
                return;
            }
            if (channelSpinner.getSelectedItem() == null) {
                channelSpinner.setError(getContext().getString(R.string.choosechannel));
                return;
            }
            if (contents.size() < 1) {
                Snacky.builder().setActivity(getActivity()).setDuration(Snacky.LENGTH_SHORT)
                        .setText(R.string.nocontent)
                        .warning()
                        .show();
                return;
            }


            progressBar.setVisibility(View.VISIBLE);

            Course course = new Course("0",
                    titleTextInputLayout.getEditText().getText().toString(),
                    channelSpinner.getSelectedItem().toString(),
                    dateMaterialButton.getText().toString(),
                    timeMaterialButton.getText().toString(),
                    contents,
                    UserPreferences.getCurrentUser(),
                    0,
                    new ArrayList<>()
            );
            System.out.println(course.toJson());
            progressBar.setVisibility(View.VISIBLE);


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    Constants.URL_COURCE_CREATE,
                    course.toJson(),
                    response -> {
                        progressBar.setVisibility(View.GONE);
                        Snacky.builder().setActivity(getActivity()).setDuration(Snacky.LENGTH_SHORT)
                                .setText(R.string.cource_created_succesfully)
                                .success()
                                .show();
                        //redirection

                        getActivity().getSupportFragmentManager().
                                beginTransaction().replace(R.id.home_fragment_contaner,
                                new OldCourcesFragment()).commit();
                    }
                    ,
                    error -> {
                        progressBar.setVisibility(View.GONE);
                        Snacky.builder().setActivity(getActivity()).setDuration(Snacky.LENGTH_SHORT)
                                .setText(R.string.network_problem)
                                .error()
                                .show();
                        error.printStackTrace();
                    }
            );
            Volley.newRequestQueue(getContext()).add(jsonObjectRequest);

        });


















/*
        ProgressDialog progress = ProgressDialog.show(getContext(), null, null, true);

        progress.show();*/


        /*
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Constants.URL_COURCE_GET,
                null,
                response -> {
                    try {
                        parseJsonAndShowCources(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progress.dismiss();
                }
                ,
                error -> {
                    progress.dismiss();
                    error.printStackTrace();
                }
        );

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
*/
    }


    private abstract class SimpleListAdapter extends BaseAdapter {
        private ArrayList<String> contents__;

        public SimpleListAdapter(ArrayList<String> contents) {
            this.contents__ = contents;
        }

        @Override
        public int getCount() {
            return contents__.size();
        }

        @Override
        public Object getItem(int position) {
            return contents__.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.item_content, parent, false);

            ((TextView) inflate.findViewById(R.id.content_item_name)).setText(contents__.get(position));
            inflate.findViewById(R.id.content_item_delete).setOnClickListener(v -> delete(position));

            return inflate;

        }

        public abstract void delete(int s);

        public void setItems(ArrayList<String> contents) {
            contents__ = contents;
        }
    }
}
