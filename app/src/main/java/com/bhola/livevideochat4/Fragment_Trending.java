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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


public class Fragment_Trending extends Fragment {

    Context context;
    View view;
    public static DrawerLayout drawerLayout;
    public static String selectedCountry = "All";
    public static CircleImageView flagIcon;
    public static TextView countryName;


    public static GirlsCardAdapter adapter;
    SliderAdapter sliderAdapter;
    public static ArrayList<Model_Profile> girlsList;
    ArrayList<Model_Profile> girlsList_slider;
    ArrayList<Model_Profile> girlsList_nearBy;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    final int NOTIFICATION_REQUEST_CODE = 112;
    Boolean isScrolling = false;
    GridLayoutManager layoutManager;
    public static SwipeRefreshLayout swipeRefreshLayout;
    NearByAdapter nearByAdapter;
    RecyclerView recyclerview_NearBy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        getUserLocation();

        view = inflater.inflate(R.layout.fragment_trending, container, false);

        context = getContext();
    
        TextView HotTextview = view.findViewById(R.id.HotTextview);
        ImageView HotTextview_line = view.findViewById(R.id.HotTextview_line);

        TextView NearbyTextview = view.findViewById(R.id.NearbyTextview);
        ImageView NearbyTextview_line = view.findViewById(R.id.NearbyTextview_line);


        HotTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerview_NearBy.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setEnabled(true);

                float textSizeInDp = 25; // Replace with your desired text size in dp
                float scale = getResources().getDisplayMetrics().density;
                int textSizeInPixels = (int) (textSizeInDp * scale + 0.5f);
                int textSizeInPixels2 = (int) (16 * scale + 0.5f);

// Set the text size in pixels
                NearbyTextview.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPixels2);
                NearbyTextview_line.setVisibility(View.INVISIBLE);

                HotTextview.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPixels);
                HotTextview_line.setVisibility(View.VISIBLE);

                int lightgray = getResources().getColor(com.google.android.ads.mediationtestsuite.R.color.gmts_light_gray); // Replace with your color resource or a specific color value
                int semiblack = getResources().getColor(R.color.semiblack); // Replace with your color resource or a specific color value

                NearbyTextview.setTextColor(lightgray);
                HotTextview.setTextColor(semiblack);

            }
        });

        NearbyTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SplashScreen.App_updating.equals("active")) {
                    return;
                }


                recyclerview_NearBy.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setEnabled(false);

                float textSizeInDp = 25; // Replace with your desired text size in dp
                float scale = getResources().getDisplayMetrics().density;
                int textSizeInPixels = (int) (textSizeInDp * scale + 0.5f);
                int textSizeInPixels2 = (int) (16 * scale + 0.5f);

// Set the text size in pixels
                NearbyTextview.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPixels);
                NearbyTextview_line.setVisibility(View.VISIBLE);

                HotTextview.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPixels2);
                HotTextview_line.setVisibility(View.INVISIBLE);

                int lightgray = getResources().getColor(com.google.android.ads.mediationtestsuite.R.color.gmts_light_gray); // Replace with your color resource or a specific color value
                int semiblack = getResources().getColor(R.color.semiblack); // Replace with your color resource or a specific color value

                NearbyTextview.setTextColor(semiblack);
                HotTextview.setTextColor(lightgray);
            }
        });

        updateFlagIconButton();
        sideLayout_Countries();

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (SplashScreen.App_updating.equals("active")) {
                    return;
                }
                Fragment_Trending.selectedCountry = "All";
                girlsList.clear();
                loadDatabase();
                updateFlagButton();
            }
        });
        setUpSlider();
        setupRecycerView();
        showNearBy();
        
        return view;
    }

    private void updateFlagIconButton() {
        flagIcon = view.findViewById(R.id.flagIcon);
        countryName = view.findViewById(R.id.countryName);

    }

    private void sideLayout_Countries() {

        LinearLayout openDrawer = view.findViewById(R.id.openDrawer);
        drawerLayout = view.findViewById(R.id.drawerLayout);

        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SplashScreen.App_updating.equals("active")) {
                    return;
                }
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });


        ArrayList<CountryInfo_Model> newList = new ArrayList<>(SplashScreen.countryList);


        CountryInfo_Model countryInfoModel = new CountryInfo_Model();
        countryInfoModel.setNationality("All");
        countryInfoModel.setFlagUrl("All");
        countryInfoModel.setCountry("All");
        countryInfoModel.setSelected(true);

        newList.add(0, countryInfoModel);


        CountryRecyclerViewAdapter adapter = new CountryRecyclerViewAdapter(requireContext(), newList);
        getLocation(newList, adapter); // this is to get current current country location and move that country to top of the list in slider layout

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);


    }


    private void getLocation(ArrayList<CountryInfo_Model> newList, CountryRecyclerViewAdapter adapter) {
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

    private void getAddressFromLocation(double latitude, double longitude, ArrayList<CountryInfo_Model> newList, CountryRecyclerViewAdapter adapter) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                SplashScreen.currentCity = addresses.get(0).getLocality();
                SplashScreen.currentCountry = addresses.get(0).getCountryName();
                // Now you have the city and country information

                int fromIndex = -1;
                for (int i = 0; i < newList.size(); i++) {
                    CountryInfo_Model countryInfoModel1 = newList.get(i);
                    if (SplashScreen.currentCountry.equals(countryInfoModel1.getCountry())) {
                        fromIndex = i; // Index of the item to move
                    }
                }
                if (fromIndex != -1) {
                    CountryInfo_Model countryInfoModel2 = newList.remove(fromIndex);
                    newList.add(1, countryInfoModel2);
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showNearBy() {
        girlsList_nearBy = new ArrayList<>();


         recyclerview_NearBy = view.findViewById(R.id.recyclerview_NearBy);


        if (SplashScreen.currentCountry.length() != 0) {
            loadDatabase_Country_NearBy(SplashScreen.currentCountry);
        } else {
            loadDatabase_NearBy();
        }

        nearByAdapter = new NearByAdapter(context, girlsList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerview_NearBy.setLayoutManager(layoutManager);
        recyclerview_NearBy.setAdapter(nearByAdapter);

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
                        Model_Profile model_profile = new Model_Profile(Username, Name, Country, Languages, Age, InterestedIn, BodyType, Specifics, Ethnicity, Hair, EyeColor, Subculture, profilePhoto, coverPhoto, Interests, images, videos, censored, like);
                        girlsList_slider.add(model_profile);
                    } while (cursor.moveToNext());

                }
                cursor.close();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (SplashScreen.App_updating.equals("active")) {
                            if (girlsList_slider.size() > 4) {
                                girlsList_slider.subList(4, girlsList_slider.size()).clear();
                            }
                        }
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
        getLocation();

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
                        Model_Profile model_profile = new Model_Profile(Username, Name, Country, Languages, Age, InterestedIn, BodyType, Specifics, Ethnicity, Hair, EyeColor, Subculture, profilePhoto, coverPhoto, Interests, images, videos, censored, like);
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
                                if (SplashScreen.App_updating.equals("active")) {
                                    if (girlsList.size() > 6) {
                                        girlsList.subList(6, girlsList.size()).clear();
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }, 200);

                    }
                });
            }
        }).start();


    }


    private void getLocation() {

        if (SplashScreen.App_updating.equals("active")) {
            return;
        }
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
                        getAddressFromLocation(latitude, longitude);
                    }
                });


    }

    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                SplashScreen.currentCity = addresses.get(0).getLocality();
                SplashScreen.currentCountry = addresses.get(0).getCountryName();
                // Now you have the city and country information

                for (int i = 0; i < SplashScreen.countryList.size(); i++) {
                    CountryInfo_Model countryInfo_model = SplashScreen.countryList.get(i);
                    if (SplashScreen.currentCountry.equalsIgnoreCase(countryInfo_model.getCountry().trim())) {
                        loadDatabase_Country(countryInfo_model.getCountry().trim());
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadDatabase_Country(String selectedCountry) {

       girlsList.clear();

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
                        Model_Profile model_profile = new Model_Profile(Username, Name, Country, Languages, Age, InterestedIn, BodyType, Specifics, Ethnicity, Hair, EyeColor, Subculture, profilePhoto, coverPhoto, Interests, images, videos, censored, like);
                        if (model_profile.getImages().size() != 0) {
                           girlsList.add(0, model_profile);
                        } else {
                          girlsList.add(model_profile);
                        }
                    } while (cursor.moveToNext());

                }
                cursor.close();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       swipeRefreshLayout.setRefreshing(false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (SplashScreen.App_updating.equals("active")) {
                                    if (girlsList.size() > 6) {
                                        girlsList.subList(6, girlsList.size()).clear();
                                    }
                                }
                              adapter.notifyDataSetChanged();
                            }
                        }, 200);

                    }
                });
            }
        }).start();


    }

    private void askForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_REQUEST_CODE);

            }
        }
    }

    private void getUserLocation() {

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            // Permission not granted, request it
//            ActivityCompat.requestPermissions((Activity) requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

        }
    }


    private void loadDatabase_Country_NearBy(String selectedCountry) {


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
                        Model_Profile model_profile = new Model_Profile(Username, Name, Country, Languages, Age, InterestedIn, BodyType, Specifics, Ethnicity, Hair, EyeColor, Subculture, profilePhoto, coverPhoto, Interests, images, videos, censored, like);
                        girlsList_nearBy.add(model_profile);

                    } while (cursor.moveToNext());

                }
                cursor.close();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Collections.shuffle(girlsList_nearBy);
                                if (girlsList_nearBy.size() > 10) {
                                    // Remove elements beyond the first 10
                                    girlsList_nearBy.subList(10, girlsList_nearBy.size()).clear();
                                }

                                nearByAdapter.notifyDataSetChanged();
                            }
                        }, 200);

                    }
                });
            }
        }).start();


    }

    private void loadDatabase_NearBy() {

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
                        Model_Profile model_profile = new Model_Profile(Username, Name, Country, Languages, Age, InterestedIn, BodyType, Specifics, Ethnicity, Hair, EyeColor, Subculture, profilePhoto, coverPhoto, Interests, images, videos, censored, like);
                        girlsList_nearBy.add(model_profile);
                    } while (cursor.moveToNext());

                }
                cursor.close();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Collections.shuffle(girlsList_nearBy);
                                if (girlsList_nearBy.size() > 10) {
                                    // Remove elements beyond the first 10
                                    girlsList_nearBy.subList(10, girlsList_nearBy.size()).clear();
                                }
                                nearByAdapter.notifyDataSetChanged();
                            }
                        }, 200);

                    }
                });
            }
        }).start();


    }

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    Log.d(SplashScreen.TAG, "requestCode: " + result);
                    if (result) {
                        getLocation();
                        askForNotificationPermission();
                    } else {
                        // PERMISSION NOT GRANTED
                    }
                }
            }
    );


}


class GirlsCardAdapter extends RecyclerView.Adapter<GirlsCardAdapter.GridViewHolder> {

    private final List<Model_Profile> girlsList;
    private final Context context;
    private ObjectAnimator breatheAnimator;


    public GirlsCardAdapter(Context context, ArrayList<Model_Profile> girlsList) {
        this.context = context;
        this.girlsList = girlsList;
    }


    @androidx.annotation.NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.girlcard, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull GridViewHolder holder, int position) {
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
                intent.putExtra("online", false);
                context.startActivity(intent);
            }
        });

        censoredBtn(item.getCensored(), item.getUsername(), holder.censoredBtn, holder.getAbsoluteAdapterPosition(), item);


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

    private void censoredBtn(int censored, String username, Button censoredBtn, int absoluteAdapterPosition, Model_Profile item) {
        if (censored == 0) {
            censoredBtn.setBackgroundColor(context.getResources().getColor(R.color.themeColor));
            censoredBtn.setText("Not Censored");
        } else {
            censoredBtn.setBackgroundColor(context.getResources().getColor(R.color.green)); // Assumes you have a green color defined in your resources
            censoredBtn.setText("Censored");

        }

        censoredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (censored == 0) {
                    String res = new DatabaseHelper(context, SplashScreen.DB_NAME, SplashScreen.DB_VERSION, "GirlsProfile").updateCensored(username, 1);

                    censoredBtn.setBackgroundColor(context.getResources().getColor(R.color.green));
                    censoredBtn.setText("Censored");
                    for (int i = 0; i < Fragment_Trending.girlsList.size(); i++) {
                        if (Fragment_Trending.girlsList.get(i).getUsername().equals(username)) {
                            Fragment_Trending.girlsList.get(i).setCensored(1);
                        }
                    }
                } else {
                    String res = new DatabaseHelper(context, SplashScreen.DB_NAME, SplashScreen.DB_VERSION, "GirlsProfile").updateCensored(username, 0);

                    censoredBtn.setBackgroundColor(context.getResources().getColor(R.color.themeColor)); // Assumes you have a green color defined in your resources
                    censoredBtn.setText("Not Censored");

                    for (int i = 0; i < Fragment_Trending.girlsList.size(); i++) {
                        if (Fragment_Trending.girlsList.get(i).getUsername().equals(username)) {
                            Fragment_Trending.girlsList.get(i).setCensored(0);
                        }
                    }

                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Fragment_Trending.adapter.notifyItemChanged(absoluteAdapterPosition);

                    }
                }, 500);

            }
        });
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
        Button censoredBtn;

        public GridViewHolder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.name);
            hello = itemView.findViewById(R.id.hello);
            flag = itemView.findViewById(R.id.flag);
            cardView1 = itemView.findViewById(R.id.cardView1);
            censoredBtn = itemView.findViewById(R.id.censoredBtn);


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

    @androidx.annotation.NonNull
    @Override
    public viewholder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.slider_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onViewRecycled(viewholder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull viewholder holder, int position) {
        Model_Profile item = girllist.get(position);
        holder.title.setText(item.getName());
        Picasso.get().load(item.getProfilePhoto()).into(holder.thumbnail);

        holder.sliderlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Profile.class);
                intent.putExtra("userName", item.getUsername());
                intent.putExtra("online", true);
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return girllist.size();
    }


    public class viewholder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title;
        LinearLayout sliderlayout;

        public viewholder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.imageview);
            title = itemView.findViewById(R.id.categorytextview);
            sliderlayout = itemView.findViewById(R.id.sliderlayout);
        }
    }
}


class NearByAdapter extends RecyclerView.Adapter<NearByAdapter.ViewHolder> {

    ArrayList<Model_Profile> girlsList;
    Context context;

    public NearByAdapter(Context context, ArrayList<Model_Profile> girlsList) {
        this.girlsList = girlsList;
        this.context = context;
    }


    @android.support.annotation.NonNull
    @Override
    public ViewHolder onCreateViewHolder(@android.support.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_girl_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@android.support.annotation.NonNull ViewHolder holder, int position) {
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

class CountryRecyclerViewAdapter extends RecyclerView.Adapter<CountryRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CountryInfo_Model> itemList;

    public CountryRecyclerViewAdapter(Context context, ArrayList<CountryInfo_Model> countryList) {
        this.context = context;
        this.itemList = countryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.country_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CountryInfo_Model countryInfoModel = itemList.get(position);
        holder.countryName.setText(countryInfoModel.getCountry());

        if (countryInfoModel.getCountry().equals("All")) {
            Drawable drawable = context.getResources().getDrawable(R.drawable.earth);
            holder.flagurl.setImageDrawable(drawable);
        } else {

            loadImageview(holder.flagurl, countryInfoModel.getCountry(), context);

        }

        if (countryInfoModel.isSelected()) {
            holder.counrtyCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.themeColorExtralight));
            holder.countryName.setTextColor(Color.WHITE);
        } else {
            holder.counrtyCard.setCardBackgroundColor(Color.parseColor("#F1FBFF"));
            int colorResource = R.color.semiblack; // Replace with your color resource
            int textColor = ContextCompat.getColor(context, colorResource);
            holder.countryName.setTextColor(textColor);


        }

        holder.counrtyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CountryInfo_Model otherItem : itemList) {
                    otherItem.setSelected(false);
                }

                // Select the clicked item
                countryInfoModel.setSelected(true);
                Fragment_Trending.selectedCountry = countryInfoModel.getCountry();
                notifyDataSetChanged(); // Notify adapter to refresh the UI
                loadDatabase_Country(countryInfoModel.getCountry(), holder.counrtyCard);


                if (Fragment_Trending.selectedCountry.equals("All")) {
                    Drawable drawable = context.getResources().getDrawable(R.drawable.earth);
                    Fragment_Trending.flagIcon.setImageDrawable(drawable);
                    Fragment_Trending.countryName.setText("Region");
                } else {
                    loadImageview(Fragment_Trending.flagIcon, Fragment_Trending.selectedCountry, context);
                    Fragment_Trending.countryName.setText(countryInfoModel.getCountry());

                }
            }
        });

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


    private void loadDatabase_Country(String selectedCountry, CardView counrtyCard) {

        Fragment_Trending.girlsList.clear();

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
                        Model_Profile model_profile = new Model_Profile(Username, Name, Country, Languages, Age, InterestedIn, BodyType, Specifics, Ethnicity, Hair, EyeColor, Subculture, profilePhoto, coverPhoto, Interests, images, videos, censored, like);
                        if (model_profile.getImages().size() != 0) {
                            Fragment_Trending.girlsList.add(0, model_profile);
                        } else {
                            Fragment_Trending.girlsList.add(model_profile);
                        }
                    } while (cursor.moveToNext());

                }
                cursor.close();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Fragment_Trending.swipeRefreshLayout.setRefreshing(false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Fragment_Trending.adapter.notifyDataSetChanged();
                                Fragment_Trending.drawerLayout.closeDrawer(GravityCompat.END);

                            }
                        }, 200);

                    }
                });
            }
        }).start();


    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView countryName;
        CircleImageView flagurl;
        CardView counrtyCard;

        public ViewHolder(View itemView) {
            super(itemView);
            countryName = itemView.findViewById(R.id.countryName);
            flagurl = itemView.findViewById(R.id.flagurl);
            counrtyCard = itemView.findViewById(R.id.counrtyCard);
        }
    }
}





