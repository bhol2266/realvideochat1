package com.bhola.livevideochat4;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
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
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
                girlsList.clear();
                girlsList_slider.clear();
                loadDatabase();
                loadDatabase_slider();
            }
        });
        setUpSlider();
        setupRecycerView();
        return view;
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
                new DownloadAndDisplaySvgTask(holder.flag).execute(countryMap.getFlagUrl());
            }
        }
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
        return girlsList.size();
    }


    public static class GridViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView profile, hello, flag;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.name);
            hello = itemView.findViewById(R.id.hello);
            flag = itemView.findViewById(R.id.flag);


        }
    }

    private static class DownloadAndDisplaySvgTask extends AsyncTask<String, Void, Bitmap> {
        private final ImageView imageView;

        DownloadAndDisplaySvgTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String imageUrl = params[0];

            try {
                // Download the SVG image from the URL
                HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl).openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();

                // Load SVG image using AndroidSVG
                SVG svg = SVG.getFromInputStream(inputStream);

                // Render SVG to a Bitmap
                Picture picture = svg.renderToPicture();
                PictureDrawable pictureDrawable = new PictureDrawable(picture);
                Bitmap bitmap = Bitmap.createBitmap(
                        pictureDrawable.getIntrinsicWidth(),
                        pictureDrawable.getIntrinsicHeight(),
                        Bitmap.Config.ARGB_8888
                );
                Canvas canvas = new Canvas(bitmap);
                canvas.drawPicture(picture);

                return bitmap;
            } catch (IOException | SVGParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null && imageView != null) {
                // Display the Bitmap in the ImageView
                imageView.setImageBitmap(result);
            }
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


