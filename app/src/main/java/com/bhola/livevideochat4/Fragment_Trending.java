package com.bhola.livevideochat4;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class Fragment_Trending extends Fragment {

    Context context;
    View view;
    public static DrawerLayout drawerLayout;
    public static String selectedCountry = "All";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_trending, container, false);

        context = getContext();
        // Inflate the layout for context fragment
        Fragment_Hot fragmentA1 = new Fragment_Hot();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentA1, "HOT").addToBackStack(null).commit();

        TextView HotTextview = view.findViewById(R.id.HotTextview);
        ImageView HotTextview_line = view.findViewById(R.id.HotTextview_line);

        TextView NearbyTextview = view.findViewById(R.id.NearbyTextview);
        ImageView NearbyTextview_line = view.findViewById(R.id.NearbyTextview_line);


        HotTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Hot fragment = new Fragment_Hot();
                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "HOT").addToBackStack(null).commit();


                float textSizeInDp = 23; // Replace with your desired text size in dp
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
                Fragment_Nearby fragment = new Fragment_Nearby();
                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "NEARBY").addToBackStack(null).commit();


                float textSizeInDp = 23; // Replace with your desired text size in dp
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


        sideLayout_Countries();
        return view;
    }

    private void sideLayout_Countries() {

        LinearLayout openDrawer = view.findViewById(R.id.openDrawer);
        drawerLayout = view.findViewById(R.id.drawerLayout);

        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);


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
            new DownloadAndDisplaySvgTask(holder.flagurl).execute(countryInfoModel.getFlagUrl());
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
                loadDatabase_Country(countryInfoModel.getCountry(),holder.counrtyCard);
            }
        });

    }

    private void loadDatabase_Country(String selectedCountry, CardView counrtyCard) {

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

class DownloadAndDisplaySvgTask extends AsyncTask<String, Void, Bitmap> {
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




