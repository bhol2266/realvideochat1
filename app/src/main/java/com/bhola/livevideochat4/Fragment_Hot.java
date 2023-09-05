package com.bhola.livevideochat4;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


public class Fragment_Hot extends Fragment {

    View view;
    Context context;
    public static GirlsCardAdapter adapter;
    SliderAdapter sliderAdapter;
    public static ArrayList<Model_Profile> girlsList;
    ArrayList<Model_Profile> girlsList_slider;
    int currentItems, totalItems, scrollOutItems;
    Boolean isScrolling = false;
    GridLayoutManager layoutManager;
    public static SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment__hot_page, container, false);
        context = getActivity();

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Fragment_Trending.selectedCountry = "All";
                girlsList.clear();
                loadDatabase();
                updateFlagButton();
            }
        });
        setUpSlider();
        setupRecycerView();
        return view;
    }

    private void updateFlagButton() {
        if (Fragment_Trending.selectedCountry.equals("All")) {
            Drawable drawable = context.getResources().getDrawable(R.drawable.earth);
            Fragment_Trending.flagIcon.setImageDrawable(drawable);
            Fragment_Trending.countryName.setText("Region");
        } else {
            loadImageview(Fragment_Trending.flagIcon, Fragment_Trending.selectedCountry, context);
            Fragment_Trending.countryName.setText(Fragment_Trending.selectedCountry);

        }
    }

    private void loadImageview(CircleImageView flagurl, String country, Context context) {
        try {
            // Replace "image.jpg" with the actual filename and path within the assets folder
            InputStream inputStream = context.getAssets().open("countryFlag/" + country.replaceAll(" ", "-") + ".png");

            // Decode the input stream into a Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Set the Bitmap to the ImageView
            flagurl.setImageBitmap(bitmap);

            // Close the input stream when done
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setUpSlider() {
        girlsList_slider = new ArrayList<>();
        loadDatabase_slider();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        CustomRecyclerView recyclerView_slider = view.findViewById(R.id.recyclerView_slider);

        recyclerView_slider.setLayoutManager(layoutManager);
        sliderAdapter = new SliderAdapter(context, girlsList_slider);
        recyclerView_slider.setAdapter(sliderAdapter);
        sliderAdapter.notifyDataSetChanged();
    }

    private void loadDatabase_slider() {
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
                        girlsList_slider.add(model_profile);
                    } while (cursor.moveToNext());

                }
                cursor.close();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sliderAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

    }

    private void setupRecycerView() {
        girlsList = new ArrayList<>();


        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.VISIBLE);

        loadDatabase();


        adapter = new GirlsCardAdapter(context, girlsList);
        getLocation(girlsList, adapter); // this is to get current current country location and move that country to top of the list in slider layout


        layoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        NestedScrollView nestedScrollview = view.findViewById(R.id.nestedScrollview);

        nestedScrollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    // Scrolled to the end of the NestedScrollView
                    // You can load more data here
                    if (Fragment_Trending.selectedCountry.equals("All")) {
                        swipeRefreshLayout.setRefreshing(true);
                        loadDatabase();
                    }

                }
            }
        });


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
                cursor.close();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        }, 200);

                    }
                });
            }
        }).start();


    }


    private void getLocation(ArrayList<Model_Profile> newList, GirlsCardAdapter adapter) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity) context, location -> {
                    if (location != null) {
                        // Use the location data
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        // Call reverse geocoding to get the city and country
                        getAddressFromLocation(latitude, longitude, newList, adapter);
                    }
                });


    }

    private void getAddressFromLocation(double latitude, double longitude, ArrayList<Model_Profile> newList, GirlsCardAdapter adapter) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                SplashScreen.currentCity = addresses.get(0).getLocality();
                SplashScreen.currentCountry = addresses.get(0).getCountryName();
                // Now you have the city and country information

                for (int i = 0; i < newList.size(); i++) {
                    Model_Profile modelProfile = newList.get(i);
                    if (SplashScreen.currentCountry.equals(modelProfile.getFrom())) {

                        loadDatabase_Country(modelProfile.getFrom());
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadDatabase_Country(String selectedCountry) {

        Fragment_Hot.girlsList.clear();

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
                        Fragment_Hot.girlsList.add(model_profile);
                    } while (cursor.moveToNext());

                }
                cursor.close();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Fragment_Hot.swipeRefreshLayout.setRefreshing(false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Fragment_Hot.adapter.notifyDataSetChanged();

                            }
                        }, 200);

                    }
                });
            }
        }).start();


    }

}


class GirlsCardAdapter extends RecyclerView.Adapter<GirlsCardAdapter.GridViewHolder> {

    private List<Model_Profile> girlsList;
    private Context context;
    private ObjectAnimator breatheAnimator;


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

        for (CountryInfo_Model countryMap : SplashScreen.countryList) {
            if (item.getFrom().equals(countryMap.getCountry())) {
                loadImageview(holder.flag, countryMap.getCountry());
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation breathingAnimation = AnimationUtils.loadAnimation(context, R.anim.breathing_animation);
                holder.hello.startAnimation(breathingAnimation);
            }
        }, getRandomNumber());

        holder.cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, Profile.class);
                intent.putExtra("userName", item.getUsername());
                context.startActivity(intent);
            }
        });


    }

    private void loadImageview(ImageView imageView, String country) {
        try {
            // Replace "image.jpg" with the actual filename and path within the assets folder
            InputStream inputStream = context.getAssets().open("countryFlag/" + country.replaceAll(" ", "-") + ".png");

            // Decode the input stream into a Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Set the Bitmap to the ImageView
            imageView.setImageBitmap(bitmap);

            // Close the input stream when done
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private long getRandomNumber() {

        Random random = new Random();
        int minValue = 100;
        int maxValue = 700;
        int increment = 100;

        // Calculate the range of possible values
        int range = (maxValue - minValue) / increment + 1;

        // Generate a random index within the range
        int randomIndex = random.nextInt(range);

        int randomNumber = minValue + randomIndex * increment;
        return randomNumber;
    }


    @Override
    public int getItemCount() {
        return girlsList.size();
    }


    public static class GridViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView profile, hello, flag;
        CardView cardView1;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.name);
            hello = itemView.findViewById(R.id.hello);
            flag = itemView.findViewById(R.id.flag);
            cardView1 = itemView.findViewById(R.id.cardView1);


        }
    }

}


class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.viewholder> {

    Context context;
    ArrayList<Model_Profile> girllist;


    public SliderAdapter(Context context, ArrayList<Model_Profile> girllist) {
        this.context = context;
        this.girllist = girllist;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.slider_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onViewRecycled(viewholder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        Model_Profile item = girllist.get(position);
        holder.title.setText(item.getName());
        Picasso.get().load(item.getProfilePhoto()).into(holder.thumbnail);

    }


    @Override
    public int getItemCount() {
        return girllist.size();
    }


    public class viewholder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.imageview);
            title = itemView.findViewById(R.id.categorytextview);
        }
    }
}


