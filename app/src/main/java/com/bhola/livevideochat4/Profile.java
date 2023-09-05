package com.bhola.livevideochat4;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile extends AppCompatActivity {

    ChatItem_ModelClass modelClass;
    AlertDialog block_user_dialog = null;
    AlertDialog report_user_dialog = null;
    AlertDialog report_userSucessfully_dialog = null;
    GridLayout gridLayout;
    Model_Profile model_profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SplashScreen.Ads_State.equals("active")) {
//            showAds();
        }
        setContentView(R.layout.activity_profile_girl);

        fullscreenMode();

        getGirlProfile_DB();
     
        actionbar();
    }


    private void bindDetails() {
        ImageView profileImage = findViewById(R.id.profileImage);
        Picasso.get().load(model_profile.getProfilePhoto()).into(profileImage);


        TextView profileName = findViewById(R.id.profileName);
        profileName.setText(model_profile.getName());

        TextView age = findViewById(R.id.age);
        age.setText(model_profile.getAge());

        TextView country = findViewById(R.id.country);
        country.setText(model_profile.getFrom());


        CardView voiceCall = findViewById(R.id.voiceCall);
        voiceCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this, VipMembership.class));
            }
        });

        LottieAnimationView videoCall = findViewById(R.id.videoCall);
        videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this, VipMembership.class));

            }
        });

        CardView chat = findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this, VipMembership.class));
            }
        });

        TextView Languages=findViewById(R.id.Languages);
        Languages.setText(model_profile.getLanguages());

        TextView InterestedIn=findViewById(R.id.InterestedIn);
        InterestedIn.setText(model_profile.getInterestedIn());

        TextView BodyType=findViewById(R.id.BodyType);
        BodyType.setText(model_profile.getBodyType());

        TextView Ethnicity=findViewById(R.id.Ethnicity);
        Ethnicity.setText(model_profile.getEthnicity());

        TextView Hair=findViewById(R.id.Hair);
        Hair.setText(model_profile.getHair());

        TextView EyeColor=findViewById(R.id.EyeColor);
        EyeColor.setText(model_profile.getEyeColor());

        TextView Subculture=findViewById(R.id.Subculture);
        Subculture.setText(model_profile.getSubculture());


    }

    private void actionbar() {
        ImageView backArrow = findViewById(R.id.backArrow);
        ImageView warningSign = findViewById(R.id.warningSign);
        ImageView menuDots = findViewById(R.id.menuDots);


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        warningSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockUserDialog();
            }
        });

        menuDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportUserDialog();
            }
        });


    }

    private void blockUserDialog() {

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Profile.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View promptView = inflater.inflate(R.layout.dialog_block_user, null);
        builder.setView(promptView);
        builder.setCancelable(true);

        TextView confirm = promptView.findViewById(R.id.confirm);
        TextView cancel = promptView.findViewById(R.id.cancel);


        block_user_dialog = builder.create();
        block_user_dialog.show();


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Profile.this, "User blocked succesfully", Toast.LENGTH_SHORT).show();
                block_user_dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                block_user_dialog.dismiss();
            }
        });

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        block_user_dialog.getWindow().setBackgroundDrawable(inset);

    }

    private void reportUserDialog() {

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Profile.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View promptView = inflater.inflate(R.layout.dialog_report_user, null);
        builder.setView(promptView);
        builder.setCancelable(true);

        TextView report = promptView.findViewById(R.id.reportBtn);
        ImageView cross = promptView.findViewById(R.id.cross);


        report_user_dialog = builder.create();
        report_user_dialog.show();


        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                report_user_dialog.dismiss();
                reportUserSucessfullDialog();
            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                report_user_dialog.dismiss();
            }
        });


        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        report_user_dialog.getWindow().setBackgroundDrawable(inset);

    }

    private void reportUserSucessfullDialog() {

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Profile.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View promptView = inflater.inflate(R.layout.dialog_report_user_sucessfull, null);
        builder.setView(promptView);
        builder.setCancelable(true);

        TextView confirm = promptView.findViewById(R.id.confirm);


        report_userSucessfully_dialog = builder.create();
        report_userSucessfully_dialog.show();


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Profile.this, "User Reported", Toast.LENGTH_SHORT).show();
                report_userSucessfully_dialog.dismiss();
            }
        });


        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        report_userSucessfully_dialog.getWindow().setBackgroundDrawable(inset);

    }

    private void getGirlProfile_DB() {

        String userName = getIntent().getStringExtra("userName");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = new DatabaseHelper(Profile.this, SplashScreen.DB_NAME, SplashScreen.DB_VERSION, "GirlsProfile").readSingleGirl(userName);
                if (cursor.moveToFirst()) {
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
                     model_profile = new Model_Profile(Username, Name, Country, Languages, Age, InterestedIn, BodyType, Specifics, Ethnicity, Hair, EyeColor, Subculture, profilePhoto, coverPhoto, Interests, images, videos);
                }                cursor.close();
                ((Activity) Profile.this).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bindDetails();
                                setImageinGridLayout();
                            }
                        }, 200);

                    }
                });
            }
        }).start();


    }



    private void setImageinGridLayout() {

        gridLayout = findViewById(R.id.gridLayout);
        ArrayList<Map<String, String>> imageList = new ArrayList<>();

        for (int i = 0; i < model_profile.getImages().size(); i++) {
            Map<String, String> stringMap1 = new HashMap<>();
            stringMap1.put("url", model_profile.getImages().get(i).replace("thumb","full"));
            stringMap1.put("type", "free");  //premium
            imageList.add(stringMap1);
        }

        for (int i = 0; i < model_profile.getVideos().size(); i++) {
            Map<String, String> stringMap1 = new HashMap<>();
            stringMap1.put("url", model_profile.getVideos().get(i).get("imageUrl"));
            stringMap1.put("type", "free");  //premium
            imageList.add(stringMap1);
        }
        Log.d(SplashScreen.TAG, "setImageinGridLayout: "+imageList);



        for (int i = 0; i < imageList.size(); i++) {


            // Create a new CardView
            CardView cardView = new CardView(this);

// Set corner radius and elevation for the CardView
            int cornerRadius = (int) (20 * getResources().getDisplayMetrics().density); // Set the desired corner radius in dp
            float elevation = 0 * getResources().getDisplayMetrics().density; // Set the desired elevation in dp
            cardView.setRadius(cornerRadius);
            cardView.setElevation(elevation);


            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            int dpValue = 100;
            float density = getResources().getDisplayMetrics().density;
            int pixelWidth = (int) (dpValue * density);

// Load the image with Picasso
            Picasso.get()
                    .load(imageList.get(i).get("url"))
                    .resize(pixelWidth, 0) // Set the width in pixels and let Picasso calculate the height
                    .into(imageView);

            if (SplashScreen.coins == 0) {

                if (imageList.get(i).get("type").equals("premium")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        imageView.setRenderEffect(RenderEffect.createBlurEffect(40, 40, Shader.TileMode.MIRROR));
                    }
                }
            }

// Set layout parameters for width and height
            int widthInPixels = (int) (80 * getResources().getDisplayMetrics().density); // Desired width in dp
            int heightInPixels = (int) ((widthInPixels * 4) / 3.5); // Calculated height to maintain 4:3 ratio

            CardView.LayoutParams layoutParams = new CardView.LayoutParams(widthInPixels, heightInPixels);
            layoutParams.setMargins(10, 10, 10, 10);

// Add the ImageView to the CardView
            cardView.addView(imageView, layoutParams);
            int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int screenWidth = displayMetrics.widthPixels;
                    int screenHeight = displayMetrics.heightPixels;

                    ImageViewerDialog dialog = new ImageViewerDialog(Profile.this, imageList, finalI,screenWidth,screenHeight);
                    dialog.show();
                }
            });


// Add the CardView to the GridLayout
            gridLayout.addView(cardView);
        }


    }


    private void fullscreenMode() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }
    private void showAds() {
        if (SplashScreen.Ad_Network_Name.equals("admob")) {
            ADS_ADMOB.Interstitial_Ad(this);
        } else {
            com.facebook.ads.InterstitialAd facebook_IntertitialAds = null;
            ADS_FACEBOOK.interstitialAd(this, facebook_IntertitialAds, getString(R.string.Facebook_InterstitialAdUnit));
        }
    }

}