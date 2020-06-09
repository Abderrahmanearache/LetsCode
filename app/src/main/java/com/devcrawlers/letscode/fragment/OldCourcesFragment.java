package com.devcrawlers.letscode.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import com.devcrawlers.letscode.Preferences.UserPreferences;
import com.devcrawlers.letscode.R;
import com.devcrawlers.letscode.modeles.Course;
import com.devcrawlers.letscode.modeles.Feedback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.mateware.snacky.Snacky;

public class OldCourcesFragment extends Fragment {

    @BindView(R.id.old_cource_list)
    RecyclerView recyclerView;
    Adapter recyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.old_cources_fragment, container, false);
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

        ArrayList<Course> oldCources = new ArrayList<>();


        for (Course cours : courses)
            if (!cours.isNew() && cours.isConfirmed())
                oldCources.add(cours);


        recyclerAdapter = new Adapter(oldCources);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public class Adapter extends RecyclerView.Adapter<AdapterHolder> {

        List<Course> courses;

        public Adapter(List<Course> courses) {

            this.courses = courses;
        }

        @NonNull
        @Override
        public AdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_old_cource, parent, false);

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
    }


    class AdapterHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.oldcource_teacher)
        TextView textViewTeacher;

        @BindView(R.id.oldcource_content)
        TextView textViewContent;

        @BindView(R.id.oldcource_date)
        TextView textViewDate;

        @BindView(R.id.oldcource_label)
        TextView textViewLabel;

        @BindView(R.id.oldcource_signupbutton)
        MaterialButton buttonCollabse;

        @BindView(R.id.oldcource_linearlayout_bg)
        LinearLayout linearLayoutBackground;

        @BindView(R.id.old_constraint_container)
        ExpandableLayout constraintLayoutList;

        @BindView(R.id.old_list_comment)
        ListView listViewFeedbacks;

        @BindView(R.id.old_newcomment)
        EditText newCommentEditText;


        @BindView(R.id.feedback_progress)
        ProgressBar progressBar;


        @BindView(R.id.old_newcomment_send)
        FloatingActionButton newCommentSendButton;

        private boolean isExpended = false;

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
            listViewFeedbacks.setAdapter(new SimpleAdapterFeeds(cource));
            updateListWidth();
            buttonCollabse.setOnClickListener(v -> collabse());

            newCommentSendButton.setOnClickListener(v -> {
                try {
                    if (UserPreferences.getCurrentUser().isGuest()) {

                        Snacky.builder().setActivity(getActivity()).setDuration(Snacky.LENGTH_SHORT)
                                .setText(R.string.guest_permission_denied)
                                .warning()
                                .show();
                        return;
                    }


                    createNewFeedback(cource);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
            int from = CourceImage.from(getContext(), cource);
            if (from != 0)
                linearLayoutBackground.setBackgroundResource(CourceImage.from(getContext(), cource));

        }

        private void createNewFeedback(Course cource) throws JSONException {
            String s = newCommentEditText.getText().toString();
            if (s.isEmpty()) {
                Snacky.builder().setActivity(getActivity()).setDuration(Snacky.LENGTH_SHORT)
                        .setText(R.string.empty_field)
                        .warning()
                        .setDuration(1000)
                        .show();
                return;
            }
            progress(true);
            Feedback feedback = new Feedback();

            feedback.setCourse(cource);
            feedback.setOwner(UserPreferences.getCurrentUser());
            feedback.setContent(s);
            feedback.setTimestap(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date()));

            System.out.println(feedback.toJsonNew());
            System.out.println(UserPreferences.getCurrentUser());

            Volley.newRequestQueue(getContext()).add(new JsonObjectRequest(
                            Constants.URL_FEEDBACK_CREATE,
                            feedback.toJsonNew(),
                            response -> {
                                try {
                                    if (response.getString("message").equals("ok"))
                                        addFeedback(cource, feedback);
                                    else
                                        Snacky.builder().setActivity(getActivity()).setDuration(Snacky.LENGTH_SHORT)
                                                .setText(R.string.network_problem)
                                                .error()
                                                .show();
                                    System.out.println(response);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                progress(false);
                            },
                            error -> {
                                progress(false);
                                Snacky.builder().setActivity(getActivity()).setDuration(Snacky.LENGTH_SHORT)
                                        .setText(R.string.network_problem)
                                        .error()
                                        .show();
                                error.printStackTrace();
                            }
                    )
            );

        }

        private void progress(boolean b) {
            progressBar.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        private void addFeedback(Course c, Feedback feedback) {

            c.getFeedbacks().add(feedback);
            listViewFeedbacks.setAdapter(new SimpleAdapterFeeds(c));
            listViewFeedbacks.deferNotifyDataSetChanged();
            updateListWidth();

        }


        private void collabse() {


            isExpended = !isExpended;
            constraintLayoutList.setExpanded(isExpended);
            buttonCollabse.setText(isExpended ? R.string.oldcource_hidebutton : R.string.oldcource_showbutton);

        }

        public void updateListWidth() {

            ListAdapter myListAdapter = listViewFeedbacks.getAdapter();
            if (myListAdapter == null) {
                return;
            }
            // get listview height
            int totalHeight = 0;
            int adapterCount = myListAdapter.getCount();
            for (int size = 0; size < adapterCount; size++) {
                View listItem = myListAdapter.getView(size, null, listViewFeedbacks);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            // Change Height of ListView
            ViewGroup.LayoutParams params = listViewFeedbacks.getLayoutParams();

            int maxh = getResources().getDisplayMetrics().heightPixels;

            params.height = (totalHeight
                    + (listViewFeedbacks.getDividerHeight() * (adapterCount)));
            if (params.height < maxh)
                listViewFeedbacks.setLayoutParams(params);

        }

        private class SimpleAdapterFeeds extends BaseAdapter {
            private Course cource;

            public SimpleAdapterFeeds(Course cource) {
                this.cource = cource;
            }

            @Override
            public int getCount() {
                return cource.getFeedbacks().size();
            }

            @Override
            public Object getItem(int position) {
                return cource.getFeedbacks().get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View inflate = LayoutInflater.from(getContext()).inflate(R.layout.item_old_cource_feedback, parent, false);

                Feedback feedback = cource.getFeedbacks().get(position);

                ((TextView) inflate.findViewById(R.id.feedback_username)).setText(feedback.getOwner().getFullname());
                ((TextView) inflate.findViewById(R.id.feedback_date)).setText(feedback.getTimestap());
                ((TextView) inflate.findViewById(R.id.feedback_userfeedback)).setText(feedback.getContent());
                Picasso.get()
                        .load(feedback.getOwner().getImage())
                        .placeholder(R.drawable.acount_ic)
                        .into(((ImageView) inflate.findViewById(R.id.feedback_userbadge)));
                return inflate;
            }
        }


    }


}
