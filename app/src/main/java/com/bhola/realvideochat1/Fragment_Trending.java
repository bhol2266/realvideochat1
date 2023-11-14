package com.bhola.realvideochat1;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bhola.realvideochat1.Models.UserModel;
import com.bhola.realvideochat1.adapter.SliderAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class Fragment_Trending extends Fragment {

    Context context;
    View view;
    UserCardAdapter adapter;
    ArrayList<UserModel> userslist;
  public static   ArrayList<UserModel> Onlineuserslist;
    GridLayoutManager layoutManager;
    public static    SliderAdapter sliderAdapter;
    int page = 1;
    final int NOTIFICATION_REQUEST_CODE = 112;

    public static SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getUserLocation_Permission();

        view = inflater.inflate(R.layout.fragment_trending, container, false);
        context = getContext();

        swipeRefreshLayout_init();

        setupRecycerView();
        getLocation();
        gotoAdminPanel();
        sliderAdapter();

        return view;
    }

    private void sliderAdapter() {
        Onlineuserslist=new ArrayList<>();
        RecyclerView recyclerView_slider = view.findViewById(R.id.recyclerView_slider);
        recyclerView_slider.setVisibility(View.VISIBLE);
        sliderAdapter = new SliderAdapter(context, Onlineuserslist);
        recyclerView_slider.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_slider.setAdapter(sliderAdapter);
    }

    private void gotoAdminPanel() {
        TextView NearbyTextview = view.findViewById(R.id.NearbyTextview);
        NearbyTextview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                context.startActivity(new Intent(context, AdminPanel_Userlist.class));
                return false;
            }
        });
    }


    private void swipeRefreshLayout_init() {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                page = 1;
                if (SplashScreen.App_updating.equals("active")) {
                    return;
                }
                userslist.clear();
                getLatestUsers(page);
            }
        });

    }

    private void getLatestUsers(int page) {
        new Utils().getUserDetails(adapter, swipeRefreshLayout, userslist, page);
    }

    private void setupRecycerView() {

//        Query query = FirebaseUtil.allUserCollectionReference().orderBy("date", Query.Direction.DESCENDING).limit(25);
//
//        FirestoreRecyclerOptions<UserModel> options=new FirestoreRecyclerOptions.Builder<UserModel>().setQuery(query,UserModel.class).build();

        swipeRefreshLayout.setRefreshing(true);

        userslist = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.VISIBLE);

        adapter = new UserCardAdapter(context, userslist);
        layoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);


        getLatestUsers(page);


    }

    private void getLocation() {


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
                SplashScreen.userModel.setLocation(SplashScreen.currentCity);
                if (!SplashScreen.userModel.getLocation().equals(SplashScreen.currentCity + ", " + SplashScreen.currentCountry)) {
                    new Utils().updateProfileonFireStore("location", SplashScreen.currentCity + ", " + SplashScreen.currentCountry);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void askForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_REQUEST_CODE);

            }
        }
    }

    private void getUserLocation_Permission() {

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            // Permission not granted, request it
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

        }
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

class UserCardAdapter extends RecyclerView.Adapter<UserCardAdapter.GridViewHolder> {

    private final List<UserModel> userlist;
    private final Context context;
    private ObjectAnimator breatheAnimator;


    public UserCardAdapter(Context context, ArrayList<UserModel> userlist) {
        this.context = context;
        this.userlist = userlist;
    }


    @androidx.annotation.NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.usercard, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull GridViewHolder holder, int position) {
        UserModel item = userlist.get(position);

        Log.d("getUserFromFireStore", "getUserFromFireStore: " + item.getProfilepic());
        if (item.getProfilepic().length() < 10) {
            if (item.getSelectedGender().equals("male")) {
                holder.profile.setImageResource(R.drawable.male_logo);
            } else {
                holder.profile.setImageResource(R.drawable.female_logo);
            }
        } else {
            Picasso.get().load(item.getProfilepic()).into(holder.profile);
        }
        holder.name.setText(item.getFullname());


        holder.cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userModelJson = new Gson().toJson(item); // Using Google's Gson library for JSON serialization
                Intent intent = new Intent(context, Profile.class);
                intent.putExtra("userModelJson", userModelJson);
                context.startActivity(intent);

            }
        });

        holder.hello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userModelJson = new Gson().toJson(item); // Using Google's Gson library for JSON serialization
                Intent intent = new Intent(context, ChatScreen_User.class);
                intent.putExtra("userModelJson", userModelJson);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation breathingAnimation = AnimationUtils.loadAnimation(context, R.anim.breathing_animation);
                holder.hello.startAnimation(breathingAnimation);
            }
        }, getRandomNumber());


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
        return userlist.size();
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
            cardView1 = itemView.findViewById(R.id.cardView1);


        }
    }

}






