package com.bhola.realvideochat1;


import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.bhola.realvideochat1.Models.UserModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SplashScreen extends AppCompatActivity {

    static String TAG = "TAGA";
    Animation topAnim, bottomAnim;
    TextView textView;
    LottieAnimationView lottie_progressbar;

    public static UserModel userModel;
    public static String calleeId;//this is for setting callee which will be used in zego call listener

    public static boolean isCalleeIdStreamer=false;

    public static boolean isOutgoing=false;
    public static String Notification_Intent_Firebase = "inactive";
    public static String Ad_Network_Name = "facebook";
    public static String Refer_App_url2 = "https://play.google.com/store/apps/developer?id=UK+DEVELOPERS";
    public static String Ads_State = "inactive";
    public static String fcmAPI_KEY = "";
    public static String App_updating = "active";
    public static String databaseURL_video = "https://bhola2266.ap-south-1.linodeobjects.com//"; //default
    public static String databaseURL_images = "https://bucket2266.blr1.digitaloceanspaces.com/"; //default
    public static ArrayList<CountryInfo_Model> countryList;

    public static String exit_Refer_appNavigation = "inactive";
    public static String Notification_ImageURL = "https://hotdesipics.co/wp-content/uploads/2022/06/Hot-Bangla-Boudi-Ki-Big-Boobs-Nangi-Selfies-_002.jpg";
    DatabaseReference url_mref;
    public static int Login_Times = 0;
    com.facebook.ads.InterstitialAd facebook_IntertitialAds;


    public static boolean homepageAdShown = false;
    boolean animationCompleted = false;
    boolean activityChanged = false;

    public static String terms_service_link = "https://sites.google.com/view/desi-girls-live-video-chat/terms_service";
    public static String privacy_policy_link = "https://sites.google.com/view/desi-girls-live-video-chat/privacypolicy";


    //ZegoCloud
    public static Long Zegocloud_appID;
    public static String Zegocloud_appSign = "";
    public static String Zegocloud_serverSecret = "";


    //Google login
    public static boolean userLoggedIn = false;
    public static String userLoggedIAs = "not set";
    public static String authProviderName = "";
    public static String userEmail = "";
    FirebaseUser firebaseUser;
    private FirebaseAnalytics mFirebaseAnalytics;

    //location
    public static String currentCity = "";
    public static String currentCountry = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        fullscreenMode();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        try {
            allUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }


        countryList = loadCountryListFromAsset(this, "countrylist.json");


        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        textView = findViewById(R.id.textView_splashscreen);
        lottie_progressbar = findViewById(R.id.lottie_progressbar);


//        textView.setAnimation(topAnim);
        lottie_progressbar.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animationCompleted = true;

                if (!activityChanged) {
                    handler_forIntent();
                }


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        generateNotification();


//        clearChats();
    }


    private void clearChats() {
        SharedPreferences sharedPreferences = SplashScreen.this.getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

// Clear the SharedPreferences
        editor.clear();
        editor.apply();

    }


    private void allUrl() {
        if (!isInternetAvailable(SplashScreen.this)) {

            Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Login_Times > 5) {
                        App_updating = "inactive";
                        Ads_State = "active";
                        Ad_Network_Name = "admob";
                    }
                    if (!activityChanged) {
                        handler_forIntent();
                    }
                }
            }, 2000);

            return;
        } else {
            url_mref = FirebaseDatabase.getInstance().getReference().child("RealVideoChat1");
            url_mref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Refer_App_url2 = (String) snapshot.child("Refer_App_url2").getValue();
                    exit_Refer_appNavigation = (String) snapshot.child("switch_Exit_Nav").getValue();
                    Ads_State = (String) snapshot.child("Ads").getValue();
                    Ad_Network_Name = (String) snapshot.child("Ad_Network").getValue();
                    App_updating = (String) snapshot.child("App_updating").getValue();
                    Notification_ImageURL = (String) snapshot.child("Notification_ImageURL").getValue();
                    databaseURL_video = (String) snapshot.child("databaseURL_video").getValue();


                    Zegocloud_appID = Long.valueOf((Integer) snapshot.child("Zegocloud_appID").getValue(Integer.class));
                    Zegocloud_appSign = (String) snapshot.child("Zegocloud_appSign").getValue();
                    Zegocloud_serverSecret = (String) snapshot.child("Zegocloud_serverSecret").getValue();
                    fcmAPI_KEY = (String) snapshot.child("fcmAPI_KEY").getValue();

                    sharedPrefrences();

                    if (animationCompleted) {
                        handler_forIntent();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SplashScreen.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void sharedPrefrences() {

        //Reading Login Times and login details
        SharedPreferences sh = getSharedPreferences("UserInfo", MODE_PRIVATE);
        int a = sh.getInt("loginTimes", 0);
        int userId = sh.getInt("userId", 0);
        String loginAs = sh.getString("loginAs", "not set");
        if (!loginAs.equals("not set")) {
            userLoggedIn = true;
            getUserFromFireStore(userId);
            if (loginAs.equals("Google")) {
                userLoggedIAs = "Google";
            } else {
                userLoggedIAs = "Guest";

            }
        }
        Login_Times = a + 1;

        // Updating Login Times data into SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putInt("loginTimes", a + 1);
        myEdit.commit();


    }

    private void getUserFromFireStore(int userId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("Users");

        DocumentReference userRef = usersRef.document(String.valueOf(userId));

        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userModel = documentSnapshot.toObject(UserModel.class);
                        // Use the user data
                        //update user latest login date
                        Utils utils = new Utils();
                        utils.updateDateonFireStore("date", new Date());
                    } else {

                        SplashScreen.userLoggedIn=false;
                        // User document doesn't exist
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });


    }





    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (facebook_IntertitialAds != null) {
            facebook_IntertitialAds.destroy();

        }
    }

    private void generateNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic("all").addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                String msg = "Failed";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void handler_forIntent() {
        activityChanged = true;
        if (!isInternetAvailable(SplashScreen.this)) {
            createSnackBar();
            return;
        }
        if (SplashScreen.userLoggedIn && firebaseUser != null) {

            //user logged in with google now check notification extras
            checkNotificationExtras();

        } else {

            if (SplashScreen.userLoggedIn && SplashScreen.userLoggedIAs.equals("Guest")) {

                //user logged in with guest now check notification extras
                checkNotificationExtras();

            } else {
                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                startActivity(intent);
            }
        }
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        finish();
    }

    private void checkNotificationExtras() {
        if (getIntent().getExtras() != null && getIntent().getExtras().getString("userId") != null) {
            //from notification
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtil.allUserCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            UserModel model = task.getResult().toObject(UserModel.class);

                            Intent mainIntent = new Intent(this, MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainIntent);

                            String userModelJson = new Gson().toJson(model); // Using Google's Gson library for JSON serialization
                            Intent intent = new Intent(this, ChatScreen_User.class);
                            intent.putExtra("userModelJson", userModelJson);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        }
                    });
        } else {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
        }
    }

    private void createSnackBar() {

        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No Internet Connection!", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashScreen.this, SplashScreen.class));
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }


    private List<String> getusers_fromSingleCountry(String path) {
        List<String> filenames = new ArrayList<>();
        AssetManager assetManager = getAssets(); // Get the AssetManager
        try {
            String[] assetFiles = assetManager.list(path); // Replace with the subfolder name you want to list


            for (String fileName : assetFiles) {
                filenames.add(fileName); // Remove the trailing slash
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAGAA", "e: " + e.getMessage());
        }
        return filenames;
    }


    public String loadJSONFromAsset(String path) {
        String json = null;
        try {
            InputStream is = getApplicationContext().getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.d("TAGAA", "IOException: " + ex.getMessage());

            return null;
        }

        return json;
    }

    public static String decryption(String encryptedText) {

        int key = 5;
        String decryptedText = "";

        //Decryption
        char[] chars2 = encryptedText.toCharArray();
        for (char c : chars2) {
            c -= key;
            decryptedText = decryptedText + c;
        }
        return decryptedText;
    }

    public static String encryption(String text) {

        int key = 5;
        char[] chars = text.toCharArray();
        String encryptedText = "";
        String decryptedText = "";

        //Encryption
        for (char c : chars) {
            c += key;
            encryptedText = encryptedText + c;
        }

        //Decryption
        char[] chars2 = encryptedText.toCharArray();
        for (char c : chars2) {
            c -= key;
            decryptedText = decryptedText + c;
        }
        return encryptedText;
    }


    boolean isInternetAvailable(Context context) {
        if (context == null) return false;


        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {


            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            } else {

                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i("update_statut", "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("update_statut", "" + e.getMessage());
                }
            }
        }
        Log.i("update_statut", "Network is available : FALSE ");
        return false;
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


    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sh = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String loginAs = sh.getString("loginAs", "not set");
        if (firebaseUser != null && loginAs.equals("Google")) {
            authProviderName = firebaseUser.getProviderData().get(firebaseUser.getProviderData().size() - 1).getProviderId();
            userLoggedIn = true;
        }
    }


    private ArrayList<CountryInfo_Model> loadCountryListFromAsset(Context context, String fileName) {
        ArrayList<CountryInfo_Model> countryList = new ArrayList<>();

        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);

            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CountryInfo_Model countryInfoModel = new CountryInfo_Model();
                countryInfoModel.setNationality(jsonObject.getString("nationality"));
                countryInfoModel.setFlagUrl(jsonObject.getString("flagUrl"));
                countryInfoModel.setCountry(jsonObject.getString("country"));
                countryInfoModel.setCountryCode(jsonObject.getString("countryCode"));

                countryInfoModel.setSelected(false);

                countryList.add(countryInfoModel);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return countryList;
    }


}