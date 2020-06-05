package com.devcrawlers.letscode.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.devcrawlers.letscode.Constants;
import com.devcrawlers.letscode.CreateNewRequestDialog;
import com.devcrawlers.letscode.Preferences.MyPrefs;
import com.devcrawlers.letscode.R;
import com.devcrawlers.letscode.modeles.Request;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RequestCourceFragment extends Fragment {


    @BindView(R.id.request_list)
    RecyclerView recyclerView;

    @BindView(R.id.request_floating_add)
    FloatingActionButton floatingActionButton;

    Adapter recyclerAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.request_cource_fragment, container, false);
        ButterKnife.bind(this, view);


        workWith(view);


        return view;
    }


    private void workWith(View view) {

        ProgressDialog progress = ProgressDialog.show(getContext(), null, null, true);

        progress.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                com.android.volley.Request.Method.GET,
                Constants.URL_REQUEST_GET,
                null,
                response -> {
                    try {
                        parseJsonAndShowRequests(response);
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


        floatingActionButton.setOnClickListener(v -> {

            showCreateFragment();

        });
    }

    private void showCreateFragment() {

        System.out.println("creating fragment");

        CreateNewRequestDialog dialog = new CreateNewRequestDialog(getContext());

        System.out.println("trying to show fragment");
        dialog.show();


        System.out.println("after showing fragment");
        dialog.setOnDismissListener(dialog1 -> {
            workWith(null);
        });

    }

    private void parseJsonAndShowRequests(JSONObject response) throws Exception {


        if (response.getString("message").equalsIgnoreCase("ok")) {
            JSONArray jsonArray = response.getJSONArray("requests");

            ArrayList<Request> requests = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++)
                requests.add(Request.fromJson(jsonArray.getJSONObject(i)));

            updateListView(requests);
        }
    }

    private void updateListView(ArrayList<Request> requests) {


        recyclerAdapter = new Adapter(requests);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.AdapterHolder> {

        List<Request> requests;


        public Adapter(List<Request> requests) {

            this.requests = requests;
        }

        @NonNull
        @Override
        public AdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_cource, parent, false);

            return new AdapterHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterHolder holder, int position) {
            holder.setRequest(requests.get(position));
        }

        @Override
        public int getItemCount() {
            return requests.size();
        }


        class AdapterHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.request_item_date)
            TextView textViewDate;

            @BindView(R.id.request_item_title)
            TextView textViewTitle;

            @BindView(R.id.request_item_username)
            TextView textViewUsername;

            @BindView(R.id.request_item_description)
            TextView textViewDescription;

            @BindView(R.id.request_item_icon)
            ImageView imageViewIcon;

            @BindView(R.id.request_item_nbrvotes)
            TextView textViewNbrVote;

            @BindView(R.id.request_item_likedbutton)
            LikeButton likeButton;

            MyPrefs myPrefs;

            public AdapterHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void setRequest(Request request) {

                textViewDate.setText(request.getTimestap());
                textViewDescription.setText(request.getDescription());
                textViewUsername.setText(request.getOwner().getFullname());
                textViewTitle.setText(request.getTitle());
                textViewNbrVote.setText(String.valueOf(request.getNbrVote()));

                Picasso.get().load(request.getOwner().getImage()).placeholder(R.drawable.acount_ic).into(imageViewIcon);

                myPrefs = MyPrefs.init(getActivity());

                if (myPrefs.getLikedRequest().contains(request.getId()))
                    likeButton.setLiked(true);


                likeButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        like(request, true);
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        like(request, false);
                    }
                });
            }

            private void like(Request request, boolean b) {


                if (myPrefs.getLikedRequest().contains(request.getId()))
                    return;

                Volley.newRequestQueue(getContext()).add(new JsonObjectRequest(
                        com.android.volley.Request.Method.POST,
                        Constants.URL_REQUEST_VOTE, request.toJsonNew(),
                        response -> {
                            try {
                                request.setNbrVote(response.getJSONObject("request").getInt("nbrvote"));
                            } catch (JSONException e) {
                                System.out.println(response);
                                e.printStackTrace();
                            }
                            notifyDataSetChanged();
                            List<String> likedRequest = myPrefs.getLikedRequest();
                            likedRequest.add(request.getId());
                            myPrefs.saveLikedRequest(likedRequest);
                        },
                        Throwable::printStackTrace
                ));
            }
        }

    }

}
