package com.bhola.livevideochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Profile extends AppCompatActivity {

    ImageView profileImage;
    TextView name, coins;
    LinearLayout logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fullscreenMode();
        profileImage = findViewById(R.id.profileUrl);
        name = findViewById(R.id.profileName);
        coins = findViewById(R.id.coins);
        logout = findViewById(R.id.logout);
        SharedPreferences sh = getSharedPreferences("UserInfo", MODE_PRIVATE);


        if (SplashScreen.userLoggedIn) {

            String fullname = sh.getString("name", "not set");
            name.setText(fullname);

            if (SplashScreen.userLoggedIAs.equals("Google")) {
                String urll = sh.getString("photoUrl", "not set");
                Picasso.get()
                        .load(urll)
                        .into(profileImage);
            }

        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInOptions gso;
                GoogleSignInClient gsc;
                if (SplashScreen.userLoggedIAs.equals("Google")) {
                    gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                    gsc = GoogleSignIn.getClient(Profile.this, gso);
                    gsc.signOut().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                            FirebaseAuth.getInstance().signOut();

                            SharedPreferences sh = getSharedPreferences("UserInfo", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sh.edit();
                            editor.putString("name", "not set");
                            editor.putString("email", "not set");
                            editor.putString("photoUrl", "not set");
                            editor.putString("loginAs", "not set");
                            editor.apply();

                        }
                    });

                } else {

                    SharedPreferences sh = getSharedPreferences("UserInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sh.edit();
                    editor.putString("name", "not set");
                    editor.putString("age", "not set");
                    editor.putString("gender", "not set");
                    editor.putString("loginAs", "not set");
                    editor.apply();


                }
                Toast.makeText(Profile.this, "Logged Out!", Toast.LENGTH_SHORT).show();
                finish();

                startActivity(new Intent(Profile.this, LoginScreen.class));

            }
        });

    }

    private void fullscreenMode() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

}