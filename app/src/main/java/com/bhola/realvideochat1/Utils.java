package com.bhola.realvideochat1;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.bhola.realvideochat1.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Utils {

    private ProgressDialog progressDialog;



    public void showLoadingDialog(Context context, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void updateProfileonFireStore(String key, String value) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("Users");
        String userId = String.valueOf(SplashScreen.userModel.getUserId()); // Replace with the actual user ID
        DocumentReference userDocRef = usersRef.document(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put(key, value);

        userDocRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    // The field(s) were successfully updated
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that might occur during the update
                });

    }

    public void updateDateonFireStore(String key, Date date) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("Users");
        String userId = String.valueOf(SplashScreen.userModel.getUserId()); // Replace with the actual user ID
        DocumentReference userDocRef = usersRef.document(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put(key, date);

        userDocRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    // The field(s) were successfully updated
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that might occur during the update
                });
    }


    public void getUserDetails(UserCardAdapter adapter, SwipeRefreshLayout swipeRefreshLayout, ArrayList<UserModel> userslist, int page) {

        String gender_query = "male";
        if (SplashScreen.userModel.getSelectedGender().equals("male")) {
            gender_query = "female";
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference usersRef = db.collection("Users");
        Query query = usersRef.orderBy("date", Query.Direction.DESCENDING).whereEqualTo("selectedGender", gender_query).whereEqualTo("banned", false).limit(100);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {

                        // Map the document to your UserModel object.
                        UserModel userModel = document.toObject(UserModel.class);
                        Log.d("SpaceError", "size: " + userModel.getUserId());

                        if (userModel.getUserId() != SplashScreen.userModel.getUserId()) {
                            userslist.add(userModel);
                        }

                    }
//                    new ZegoCloud_Utils().checkUserOnlineStatus(userslist, context);
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    Log.d("sdfsdafsdaf", "Error getting documents: ", task.getException());
                }
            }
        });
    }


    public void getUserDetail(int userId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(String.valueOf(userId));

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Document exists, you can access its data and convert it to UserModel
                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                if (userModel != null) {
                    Log.d("onUserIDUpdated", "getUserDetails: " + userModel.getProfilepic());
                }
            } else {
                // Document doesn't exist
            }
        }).addOnFailureListener(e -> {
            // Task failed with an exception
            Log.d("onUserIDUpdated", "exception: " + e.getMessage());

            // Handle the error
        });

    }


    public void readAll_FemaleUserList(Context context) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

// Reference to the "users" collection in Firestore
        CollectionReference usersCollection = db.collection("Users");

// Create a query to filter users by gender
        Query query = usersCollection.whereEqualTo("selectedGender", "female");

// Execute the query and handle the results
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                List<UserModel> femaleUsers = new ArrayList<>();

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    // Convert the Firestore document to a UserModel object
                    UserModel user = document.toObject(UserModel.class);
                    if (!SplashScreen.userModel.getEmail().equals(user.getEmail())) {
                        //excluding self email
                        femaleUsers.add(user);
                    }

                }

//                checkUserOnlineStatus(femaleUsers, context);
            } else {
                // Handle the error
                Exception e = task.getException();
                e.printStackTrace();
                Log.d("Exception", "Exception: " + e.getMessage());

            }
        });


    }


    public static File uriToFile(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            File file = File.createTempFile("temp_image", null, context.getCacheDir());

            // Copy the data from the input stream to the file
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            fos.close();
            inputStream.close();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void uploadImageToSpace(Context context, String uri) {

        File imageFile = uriToFile(context, Uri.parse(uri));


        String key = "DO006P38F8BCL7V3ALVL";
        String secret = "t9DaxKNDoE37wbtQ3tuZk8kDoQB/5jI1czBokGeNyHY";

        BasicAWSCredentials credentials = new BasicAWSCredentials(key, secret);
        AmazonS3Client s3 = new AmazonS3Client(credentials);
        s3.setEndpoint(SplashScreen.databaseURL_images + "RealVideoChat1/profilePic");


        TransferUtility transferUtility = new TransferUtility(s3, context);
        CannedAccessControlList filePermission = CannedAccessControlList.PublicRead;

        TransferObserver observer = transferUtility.upload(
                "", //empty bucket name, included in endpoint
                String.valueOf(SplashScreen.userModel.getUserId()) + ".jpg",
                imageFile, //a File object that you want to upload
                filePermission
        );

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.COMPLETED.equals(observer.getState())) {
//                    Toast.makeText(context, "Space upload completed !!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(context, "Space upload error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void downloadProfile_andGetURI(String image_url, Context context) throws IOException {
        //this method is used to download profle pic from google signIN option and get Uri to upload to digital ocean space

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);


                    URL url = new URL(image_url);
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();

                    InputStream inputStream = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    File internalStorage = new File(context.getFilesDir(), "images");

                    if (!internalStorage.exists()) {
                        internalStorage.mkdir();
                    }
                    File file = new File(internalStorage, "profile.jpg");
                    Uri imageURI = Uri.fromFile(file);
                    SplashScreen.userModel.setProfilepic(SplashScreen.databaseURL_images + "RealVideoChat1/profilePic/" + String.valueOf(SplashScreen.userModel.getUserId()) + ".jpg");
                    if (file.exists()) file.delete();

                    FileOutputStream outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    uploadImageToSpace(context, String.valueOf(imageURI));
                } catch (Exception e) {
                    Log.d("SpaceError", "saveProfileDetails: " + e.getMessage());
                }
            }
        }).start();


    }

    public int calculateAge(String birthDateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            // Parse the birthdate string into a Date object
            Date birthDate = sdf.parse(birthDateString);

            // Get the current date
            Calendar currentDate = Calendar.getInstance();
            Date now = currentDate.getTime();

            // Calculate the age

            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(birthDate);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(currentDate.getTime());

            int age = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);

            // Check if the birthdate has occurred this year or not
            if (cal2.get(Calendar.DAY_OF_YEAR) < cal1.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age;

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }


}
