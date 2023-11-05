package com.bhola.realvideochat1;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;


public class MainActivity extends AppCompatActivity {

    public static TextView badge_text;
    public static int unreadMessage_count;
    public static ViewPager2 viewPager2;
    com.facebook.ads.InterstitialAd facebook_IntertitialAds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        fullscreenMode();


        if (SplashScreen.Ads_State.equals("active")) {
            showAds();
        }


        initializeBottonFragments();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(new Intent(MainActivity.this,ZegoCloudActivity.class));
//            }
//        },3000);


    }


    private void initializeBottonFragments() {
        viewPager2 = findViewById(R.id.viewpager);
        viewPager2.setAdapter(new PagerAdapter(MainActivity.this));

        viewPager2.setOffscreenPageLimit(4);
        TabLayout tabLayout = findViewById(R.id.tabLayout);


        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {

                    case 0:
                        tab.setIcon(R.drawable.trending);
                        View view5 = getLayoutInflater().inflate(R.layout.customtab, null);
                        view5.findViewById(R.id.icon).setBackgroundResource(R.drawable.trending);
                        tab.setCustomView(view5);

                        //By default tab 0 will be selected to change the tint of that tab
                        View tabView = tab.getCustomView();
                        ImageView tabIcon = tabView.findViewById(R.id.icon);
                        tabIcon.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.themeColor));

                        break;

                    case 1:
                        tab.setIcon(R.drawable.chat);


                        View view2 = getLayoutInflater().inflate(R.layout.customtab, null);
                        view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.chat);
                        tab.setCustomView(view2);

                        badge_text = view2.findViewById(R.id.badge_text);
                        badge_text.setVisibility(View.GONE);


                        break;


                    case 2:
                        tab.setIcon(R.drawable.info_2);


                        View view3 = getLayoutInflater().inflate(R.layout.customtab, null);
                        view3.findViewById(R.id.icon).setBackgroundResource(R.drawable.info_2);
                        tab.setCustomView(view3);
                        break;


                    default:
                        tab.setIcon(R.drawable.user2);
                        View view4 = getLayoutInflater().inflate(R.layout.customtab, null);
                        view4.findViewById(R.id.icon).setBackgroundResource(R.drawable.user2);
                        tab.setCustomView(view4);
                        break;
                }
            }
        });
        tabLayoutMediator.attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Get the custom view of the selected tab
                View tabView = tab.getCustomView();
                if (tabView != null) {
                    // Find the ImageView in the custom view
                    ImageView tabIcon = tabView.findViewById(R.id.icon);

                    // Set the background tint color for the selected tab
                    tabIcon.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.themeColor));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Get the custom view of the unselected tab
                View tabView = tab.getCustomView();
                if (tabView != null) {
                    // Find the ImageView in the custom view
                    ImageView tabIcon = tabView.findViewById(R.id.icon);

                    // Set the background tint color for the unselected tab
                    tabIcon.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, com.google.android.ads.mediationtestsuite.R.color.gmts_light_gray));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Tab reselected, no action needed
            }
        });
    }



    @Override
    public void onBackPressed() {
        exit_dialog();
        if (SplashScreen.Ads_State.equals("active")) {
            showAds();
        }
    }

    private void exit_dialog() {

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View promptView = inflater.inflate(R.layout.dialog_exit_app, null);
        builder.setView(promptView);
        builder.setCancelable(true);

        TextView exit = promptView.findViewById(R.id.confirm);
        TextView cancel = promptView.findViewById(R.id.cancel);


        AlertDialog exitDialog = builder.create();
        exitDialog.show();


        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SplashScreen.exit_Refer_appNavigation.equals("active") && SplashScreen.Login_Times < 2 && SplashScreen.Refer_App_url2.length() > 10) {

                    Intent j = new Intent(Intent.ACTION_VIEW);
                    j.setData(Uri.parse(SplashScreen.Refer_App_url2));
                    try {
                        startActivity(j);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finishAffinity();
                    System.exit(0);
                    finish();
                    exitDialog.dismiss();

                } else {

                    finishAffinity();
                    finish();
                    System.exit(0);
                    finish();
                    exitDialog.dismiss();

                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.cancel();
            }
        });


        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        exitDialog.getWindow().setBackgroundDrawable(inset);

    }


    private void showAds() {
        if (SplashScreen.Ad_Network_Name.equals("admob")) {
            if (!SplashScreen.homepageAdShown) {
                ADS_ADMOB.Interstitial_Ad(this);
                SplashScreen.homepageAdShown = true;
            }
        } else {
            if (!SplashScreen.homepageAdShown) {
                ADS_FACEBOOK.interstitialAd(this, facebook_IntertitialAds, getString(R.string.Facebook_InterstitialAdUnit));
                SplashScreen.homepageAdShown = true;
            }
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }


}