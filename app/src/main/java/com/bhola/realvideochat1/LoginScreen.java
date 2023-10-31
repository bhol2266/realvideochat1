package com.bhola.realvideochat1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class LoginScreen extends AppCompatActivity {

    AlertDialog dialog;


    //Google
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    LinearLayout googleBtn;


    //Credentials
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        fullscreenMode();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        addUnderlineTerms_privacy();
        loginStuffs();

    }

    private void loginStuffs() {
        TextView loginGoogle = findViewById(R.id.loginWithGoogle);
        loginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignInStuffs();

            }
        });
        TextView loginGuest = findViewById(R.id.loginAsguest);
        loginGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginInComplete("Guest", "Guest", "", "");
            }
        });
    }


    private void addUnderlineTerms_privacy() {
        TextView terms = findViewById(R.id.terms);
        TextView privaciy = findViewById(R.id.privacy);
        terms.setPaintFlags(terms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        privaciy.setPaintFlags(privaciy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginScreen.this, Terms_Conditions.class));
            }
        });
        privaciy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginScreen.this, PrivacyPolicy.class));

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


    private void googleSignInStuffs() {
        progressDialog = new ProgressDialog(this);
        progressDialog.show();


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            //Google
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.cancel();
                            ArrayList<String> keyword = new ArrayList<>();
                            checkUserExist(account.getEmail(), account.getDisplayName(), account.getPhotoUrl().toString());

                        } else {
                            Toast.makeText(LoginScreen.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (ApiException e) {
                Log.d(SplashScreen.TAG, "onActivityResult: " + e.getMessage());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkUserExist(String email, String displayName, String picUrl) {
        showLoadingDialog();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("Users");

// Create a query to find the user with your email
        Query query = usersRef.whereEqualTo("email", email);


        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // No user with the provided email address found and countinue to new login
                        LoginInComplete("Google", displayName, email, picUrl);

                    } else {



                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            SplashScreen.userModel = documentSnapshot.toObject(UserModel.class); // Replace User with your actual user model class
                            Utils utils=new Utils();
                            utils.updateDateonFireStore("date", new java.util.Date());

                            // Use the user data as needed
                            Toast.makeText(this, "Welcome Back!", Toast.LENGTH_SHORT).show();

                            SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();


                            editor.putString("email", email);
                            editor.putString("photoUrl", SplashScreen.userModel.getProfilepic());
                            editor.putString("loginAs", SplashScreen.userModel.getLoggedAs());
                            editor.putString("Bio", SplashScreen.userModel.getLoggedAs());
                            editor.putString("Language", SplashScreen.userModel.getLanguage());

                            editor.putString("nickName", SplashScreen.userModel.getFullname());
                            editor.putString("Gender", SplashScreen.userModel.getSelectedGender());
                            editor.putString("Birthday", SplashScreen.userModel.getBirthday());
                            editor.putInt("userId", SplashScreen.userModel.getUserId());
                            editor.putInt("coins", SplashScreen.userModel.getCoins());
                            editor.apply();

                            dismissLoadingDialog();
                            if (SplashScreen.userModel.getGalleryImages().size() > 1) {
                                saveGalleryImages(SplashScreen.userModel.getGalleryImages()); // save fallery images to local storeage from firebase storage
                            } else {
                                startActivity(new Intent(LoginScreen.this, MainActivity.class));
                            }

                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that might occur during the query
                });


    }

    private void saveGalleryImages(final ArrayList<GalleryModel> galleryImages) {

        DownloadImageTask downloadImageTask = new DownloadImageTask(LoginScreen.this);
        downloadImageTask.execute(galleryImages);

    }

    public static void saveGalleryImagesToSharedPreferences(Context context) {
        // This method is called when all images are downloaded.
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        List<GalleryModel> itemList = new ArrayList<>();
        itemList.addAll(SplashScreen.userModel.getGalleryImages());

        Gson gson = new Gson();
        String json = gson.toJson(itemList);
        editor.putString("galleryImages", json);

        editor.apply(); // Make sure to apply the changes.
    }


    private void showLoadingDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching details...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    private void LoginInComplete(String loggedAs, String displayName, String email, String photoUrl) {
        SplashScreen.userLoggedIn = true;
        SplashScreen.userLoggedIAs = loggedAs;
        finish();
        Intent intent = new Intent(LoginScreen.this, Fill_details.class);
        intent.putExtra("loggedAs", loggedAs);
        intent.putExtra("nickName", displayName);
        intent.putExtra("email", email);
        intent.putExtra("photoUrl", photoUrl);
        startActivity(intent);
    }


}


class DownloadImageTask extends AsyncTask<ArrayList<GalleryModel>, Void, Void> {

    private ProgressDialog progressBarDialog;
    private Context context;

    public DownloadImageTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(ArrayList<GalleryModel>... arrayLists) {

        ArrayList<GalleryModel> imageList = arrayLists[0];

        for (int i = 1; i < imageList.size(); i++) {
            GalleryModel galleryModel = imageList.get(i);
            try {
                URL url = new URL(galleryModel.getDownloadUrl());
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                File internalStorage = new File(context.getFilesDir(), "images");

                if (!internalStorage.exists()) {
                    internalStorage.mkdir();
                }
                File file = new File(internalStorage, galleryModel.getImageFileNAme());
                Uri imageURI = Uri.fromFile(file);
                SplashScreen.userModel.getGalleryImages().get(i).setImage_uri(String.valueOf(imageURI));

                if (file.exists()) file.delete();

                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();

            } catch (Exception e) {
                Log.d("TAGA", "doInBackground: " + e.getMessage());
            }
        }


        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBarDialog = new ProgressDialog(context);
        progressBarDialog.setMessage("Downloading user images...");
        progressBarDialog.setCancelable(false);
        progressBarDialog.show();

    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        progressBarDialog.dismiss();
        LoginScreen.saveGalleryImagesToSharedPreferences(context);
        context.startActivity(new Intent(context, MainActivity.class));

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}

class UserModel {

    String fullname, email, profilepic, loggedAs, selectedGender, birthday, location, language, bio, intrestedIn;
    boolean streamer;
    int coins;
    int userId;
    Date date;
    String memberShipExpiryDate;
    ArrayList<GalleryModel> galleryImages;


    public UserModel() {
    }

    public UserModel(String fullname, String email, String profilepic, String loggedAs, String selectedGender, String birthday, String location, String language, String bio, String intrestedIn, boolean streamer, int coins, int userId, Date date, String memberShipExpiryDate, ArrayList<GalleryModel> galleryImages) {
        this.fullname = fullname;
        this.email = email;
        this.profilepic = profilepic;
        this.loggedAs = loggedAs;
        this.selectedGender = selectedGender;
        this.birthday = birthday;
        this.location = location;
        this.language = language;
        this.bio = bio;
        this.intrestedIn = intrestedIn;
        this.streamer = streamer;
        this.coins = coins;
        this.userId = userId;
        this.date = date;
        this.memberShipExpiryDate = memberShipExpiryDate;
        this.galleryImages = galleryImages;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getLoggedAs() {
        return loggedAs;
    }

    public void setLoggedAs(String loggedAs) {
        this.loggedAs = loggedAs;
    }

    public String getSelectedGender() {
        return selectedGender;
    }

    public void setSelectedGender(String selectedGender) {
        this.selectedGender = selectedGender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getIntrestedIn() {
        return intrestedIn;
    }

    public void setIntrestedIn(String intrestedIn) {
        this.intrestedIn = intrestedIn;
    }

    public boolean isStreamer() {
        return streamer;
    }

    public void setStreamer(boolean streamer) {
        this.streamer = streamer;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMemberShipExpiryDate() {
        return memberShipExpiryDate;
    }

    public void setMemberShipExpiryDate(String memberShipExpiryDate) {
        this.memberShipExpiryDate = memberShipExpiryDate;
    }

    public ArrayList<GalleryModel> getGalleryImages() {
        return galleryImages;
    }

    public void setGalleryImages(ArrayList<GalleryModel> galleryImages) {
        this.galleryImages = galleryImages;
    }
}
