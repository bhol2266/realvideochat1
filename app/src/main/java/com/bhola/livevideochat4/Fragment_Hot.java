package com.bhola.livevideochat4;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Fragment_Hot extends Fragment {

    View view;
    Context context;
    GirlsCardAdapter adapter;
    ArrayList<Model_Profile> girlsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment__hot_page, container, false);
        context = getActivity();

        setupRecycerView();
        return view;
    }

    private void setupRecycerView() {
        girlsList = new ArrayList<>();


        ProgressBar proressbarRecycleview = view.findViewById(R.id.proressbarRecycleview);


        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadDatabase();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        proressbarRecycleview.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();


        adapter = new GirlsCardAdapter(context, girlsList);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.setAdapter(adapter);

    }

    private void loadDatabase() {

        Cursor cursor = new DatabaseHelper(context, SplashScreen.DB_NAME, SplashScreen.DB_VERSION, "GirlsProfile").readRandomGirls();

        if (cursor.moveToFirst()) {
            do {
                // Extract data from the cursor and populate the Model_Profile object
                String Username = SplashScreen.decryption(cursor.getString(0));
                String Name = SplashScreen.decryption(cursor.getString(1));
                String Country = cursor.getString(2);
                String Languages = cursor.getString(3);
                String Age = cursor.getString(4);
                String InterestedIn = cursor.getString(5);
                String BodyType = cursor.getString(6);
                String Specifics = SplashScreen.decryption(cursor.getString(7));
                String Ethnicity = cursor.getString(8);
                String Hair = cursor.getString(9);
                String EyeColor = cursor.getString(10);
                String Subculture = cursor.getString(11);
                String profilePhoto = SplashScreen.decryption(cursor.getString(13));
                String coverPhoto = SplashScreen.decryption(cursor.getString(14));


                // Convert JSON strings back to arrays/lists using Gson
                Gson gson = new Gson();


                String interestsJson = SplashScreen.decryption(cursor.getString(12));
                List<Map<String, String>> Interests = gson.fromJson(interestsJson, new TypeToken<List<Map<String, String>>>() {
                }.getType());

                String imagesJson = SplashScreen.decryption(cursor.getString(15));
                List<String> images = gson.fromJson(imagesJson, new TypeToken<List<String>>() {
                }.getType());

                String videosJson = SplashScreen.decryption(cursor.getString(16));
                List<Map<String, String>> videos = gson.fromJson(videosJson, new TypeToken<List<Map<String, String>>>() {
                }.getType());

                // Create a new Model_Profile object and populate it
                Model_Profile model_profile = new Model_Profile(Username, Name, Country, Languages, Age, InterestedIn, BodyType, Specifics, Ethnicity, Hair, EyeColor, Subculture, profilePhoto, coverPhoto, Interests, images, videos);
                girlsList.add(model_profile);
            } while (cursor.moveToNext());

        }
        Log.d("dsfads", "loadData_DB: " + girlsList.size());

        cursor.close();
    }
}


class GirlsCardAdapter extends RecyclerView.Adapter<GirlsCardAdapter.GridViewHolder> {

    private List<Model_Profile> girlsList;
    private Context context;


    public GirlsCardAdapter(Context context, ArrayList<Model_Profile> girlsList) {
        this.context = context;
        this.girlsList = girlsList;
    }


    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.girlcard, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        Model_Profile item = girlsList.get(position);

        holder.name.setText(item.getName());
        Picasso.get().load(item.getProfilePhoto()).into(holder.profile);
        Log.d("SDAfsdf", "onBindViewHolder: "+item.getProfilePhoto());
    }

    @Override
    public int getItemCount() {
        return girlsList.size();
    }


    public static class GridViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView profile, hello;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.name);
            hello = itemView.findViewById(R.id.hello);

        }
    }


}
