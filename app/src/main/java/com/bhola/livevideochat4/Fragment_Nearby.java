package com.bhola.livevideochat4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class Fragment_Nearby extends Fragment {

    Context context;
    View view;
    ArrayList<Model_Profile> girlsList;
    NearByAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment__nearby, container, false);
        context = getContext();

        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        girlsList = new ArrayList<>();


        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);


        if (SplashScreen.currentCountry.length() != 0) {
            loadDatabase_Country(SplashScreen.currentCountry);
        } else {
            loadDatabase();
        }

        adapter = new NearByAdapter(context, girlsList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


    }

    private void loadDatabase_Country(String selectedCountry) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = new DatabaseHelper(context, SplashScreen.DB_NAME, SplashScreen.DB_VERSION, "GirlsProfile").readGirls_Country(selectedCountry);
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
                        int censored = cursor.getInt(17);
                        int like = cursor.getInt(18);

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
                        Model_Profile model_profile = new Model_Profile(Username, Name, Country, Languages, Age, InterestedIn, BodyType, Specifics, Ethnicity, Hair, EyeColor, Subculture, profilePhoto, coverPhoto, Interests, images, videos,censored,like);
                        girlsList.add(model_profile);

                    } while (cursor.moveToNext());

                }
                cursor.close();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Collections.shuffle(girlsList);
                                if (girlsList.size() > 10) {
                                    // Remove elements beyond the first 10
                                    girlsList.subList(10, girlsList.size()).clear();
                                }

                                adapter.notifyDataSetChanged();
                            }
                        }, 200);

                    }
                });
            }
        }).start();


    }

    private void loadDatabase() {

        new Thread(new Runnable() {
            @Override
            public void run() {
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
                        int censored = cursor.getInt(17);
                        int like = cursor.getInt(18);

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
                        Model_Profile model_profile = new Model_Profile(Username, Name, Country, Languages, Age, InterestedIn, BodyType, Specifics, Ethnicity, Hair, EyeColor, Subculture, profilePhoto, coverPhoto, Interests, images, videos,censored,like);
                        girlsList.add(model_profile);
                    } while (cursor.moveToNext());

                }
                cursor.close();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Collections.shuffle(girlsList);
                                if (girlsList.size() > 10) {
                                    // Remove elements beyond the first 10
                                    girlsList.subList(10, girlsList.size()).clear();
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }, 200);

                    }
                });
            }
        }).start();


    }


}


class NearByAdapter extends RecyclerView.Adapter<NearByAdapter.ViewHolder> {

    ArrayList<Model_Profile> girlsList;
    Context context;

    public NearByAdapter(Context context, ArrayList<Model_Profile> girlsList) {
        this.girlsList = girlsList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_girl_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Model_Profile model_profile = girlsList.get(position);
        Picasso.get().load(model_profile.getProfilePhoto()).into(holder.profileImage);

        holder.age.setText(model_profile.getAge().replace("years old", "").trim());
        holder.location.setText(model_profile.getFrom());
        holder.name.setText(model_profile.getName());

        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Profile.class);
                intent.putExtra("userName", model_profile.getUsername());
                intent.putExtra("online", true);
                context.startActivity(intent);
            }
        });
        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomToast();
            }
        });

        holder.videocall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, VipMembership.class));
            }
        });
    }


    private void showCustomToast() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast_layout, null);

        TextView secondMessage = layout.findViewById(R.id.secondMessage);
        secondMessage.setVisibility(View.GONE);
        TextView profileName = layout.findViewById(R.id.profileName);
        profileName.setText("nice to meet you");
        // You can customize the text and other properties of the view elements here

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 130); // Adjust the margin (bottom) here
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

    }

    @Override
    public int getItemCount() {
        return girlsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView name, age, location;
        CardView message;
        ImageView videocall;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            name = itemView.findViewById(R.id.name);
            age = itemView.findViewById(R.id.age);
            location = itemView.findViewById(R.id.location);
            message = itemView.findViewById(R.id.message);
            videocall = itemView.findViewById(R.id.videocall);
        }
    }
}