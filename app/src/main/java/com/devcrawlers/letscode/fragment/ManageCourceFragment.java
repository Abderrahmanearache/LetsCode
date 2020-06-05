package com.devcrawlers.letscode.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.devcrawlers.letscode.R;
import com.devcrawlers.letscode.modeles.Course;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageCourceFragment extends Fragment {

    @BindView(R.id.next_cource_list)
    RecyclerView recyclerView;
    Adapter recyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manage_cource_fragment, container, false);
        ButterKnife.bind(this, view);


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
            if (cours.isNew() && cours.isWaitingForConfirmation())
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_cource, parent, false);

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

            @BindView(R.id.manage_item_cource_teacher)
            TextView textViewTeacher;

            @BindView(R.id.manage_item_cource_content)
            TextView textViewContent;

            @BindView(R.id.manage_item_cource_date)
            TextView textViewDate;

            @BindView(R.id.manage_item_cource_label)
            TextView textViewLabel;

            @BindView(R.id.manage_item_cource_accept)
            MaterialButton buttonAccept;

            @BindView(R.id.manage_item_cource_refuse)
            MaterialButton buttonRefuse;

            @BindView(R.id.manage_item_cource_progress)
            ProgressBar progressBar;

            @BindView(R.id.manage_item_cource_linearlayout_bg)
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
                buttonAccept.setOnClickListener(v -> update(cource, true));
                buttonRefuse.setOnClickListener(v -> update(cource, false));
                int from = CourceImage.from(getContext(), cource);
                if (from != 0)
                    linearLayoutBackground.setBackgroundResource(CourceImage.from(getContext(), cource));
            }

            private void update(Course cource, boolean b) {

                cource.setState(b ? 1 : -1);

                buttonAccept.setEnabled(false);
                buttonRefuse.setEnabled(false);

                progressBar.setVisibility(View.VISIBLE);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                        Constants.URL_COURCE_CONFIRM,
                        cource.toJson(),
                        response -> {
                            if (b) {
                                buttonAccept.setText(R.string.accepted);
                                buttonRefuse.setText(R.string.refuse);
                                buttonRefuse.setEnabled(true);
                            } else {
                                buttonAccept.setEnabled(true);
                                buttonAccept.setText(R.string.accept);
                                buttonRefuse.setText(R.string.refused);
                            }
                            notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        },
                        e -> {
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                        });

                Volley.newRequestQueue(getContext()).add(jsonObjectRequest);

            }
        }
    }


}
