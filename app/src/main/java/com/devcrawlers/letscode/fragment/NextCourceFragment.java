package com.devcrawlers.letscode.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.devcrawlers.letscode.Constants;
import com.devcrawlers.letscode.Preferences.CourceImage;
import com.devcrawlers.letscode.Preferences.MyPrefs;
import com.devcrawlers.letscode.R;
import com.devcrawlers.letscode.modeles.Course;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.mateware.snacky.Snacky;

public class NextCourceFragment extends Fragment {

    @BindView(R.id.next_cource_list)
    RecyclerView recyclerView;
    Adapter recyclerAdapter;
    MyPrefs myPrefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.next_cource_fragment, container, false);
        ButterKnife.bind(this, view);

        myPrefs = MyPrefs.init(getActivity());

        workWith(view);
        return view;
    }

    private void workWith(View view) {

        ProgressDialog progress = ProgressDialog.show(getContext(), null, null, true);

        progress.show();

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

    }

    private void parseJsonAndShowCources(JSONObject response) throws Exception {


        if (response.getString("message").equalsIgnoreCase("ok")) {
            JSONArray jsonArray = response.getJSONArray("cources");

            ArrayList<Course> courses = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++)
                courses.add(Course.fromJson(jsonArray.getJSONObject(i)));

            updateListView(courses);
        }
    }

    private void updateListView(ArrayList<Course> courses) {

        ArrayList<Course> newCources = new ArrayList<>();


        for (Course cours : courses)
            if (cours.isNew() && cours.isConfirmed())
                newCources.add(cours);


        recyclerAdapter = new Adapter(newCources);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.AdapterHolder> {

        List<Course> courses;


        public Adapter(List<Course> courses) {
            this.courses = courses;
        }

        @NonNull
        @Override
        public AdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_next_cource, parent, false);

            return new AdapterHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterHolder holder, int position) {
            holder.setCource(courses.get(position));
        }

        @Override
        public int getItemCount() {
            return courses.size();
        }


        class AdapterHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.nextcource_teacher)
            TextView textViewTeacher;

            @BindView(R.id.nextcource_content)
            TextView textViewContent;

            @BindView(R.id.nextcource_date)
            TextView textViewDate;

            @BindView(R.id.nextcource_label)
            TextView textViewLabel;

            @BindView(R.id.nextcource_signupbutton)
            MaterialButton buttonSingin;

            @BindView(R.id.nextcource_linearlayout_bg)
            LinearLayout linearLayoutBackground;

            public AdapterHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }


            public void setCource(Course cource) {

                ArrayList<String> list = new ArrayList<>();
                for (String content : cource.getContents())
                    list.add("+ " + content);
                TextUtils.join("\n", list);
                textViewContent.setText(TextUtils.join("\n", list));

                textViewTeacher.setText("By " + cource.getTeacher().getFullname());
                textViewDate.setText(cource.getDate() + "\n" + cource.getHeure());
                textViewLabel.setText(cource.getTitle());
                buttonSingin.setOnClickListener(v -> signMe(cource));

                if (myPrefs.getSavedInteressedCources().contains(cource.getId())) {
                    buttonSingin.setAlpha(0.6f);
                    buttonSingin.setText(R.string.enrolled);
                }
                int from = CourceImage.from(getContext(), cource);
                if (from != 0)
                    linearLayoutBackground.setBackgroundResource(CourceImage.from(getContext(), cource));


            }

            private void signMe(Course cource) {

                List<String> savedInteressedCources = myPrefs.getSavedInteressedCources();

                if (myPrefs.getSavedInteressedCources().contains(cource.getId())) {
                    buttonSingin.setAlpha(1f);
                    buttonSingin.setText(R.string.sign_me);
                    savedInteressedCources.remove(cource.getId());
                    myPrefs.saveInteressedCources(savedInteressedCources);
                } else {
                    buttonSingin.setAlpha(.6f);
                    buttonSingin.setText(R.string.enrolled);
                    savedInteressedCources.add(cource.getId());
                    myPrefs.saveInteressedCources(savedInteressedCources);
                    Snacky.builder().setActivity(getActivity()).setDuration(Snacky.LENGTH_SHORT)
                            .setText(R.string.enrolled)
                            .success()
                            .show();
                }
            }
        }
    }


}
