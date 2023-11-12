package com.bhola.realvideochat1;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bhola.realvideochat1.Models.StreamerEarningModel;
import com.bhola.realvideochat1.Models.StreamerModel;
import com.bhola.realvideochat1.adapter.StreamerEarning_Adapter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StreamerEarning extends AppCompatActivity {

    RecyclerView recyclerView;
    public static StreamerEarning_Adapter adapter;
    ArrayList<StreamerEarningModel> mlist;
    ArrayList<StreamerModel> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streamer_earning);
        setupRecyclerView();

        ImageView back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(view -> {
            onBackPressed();
        });
        TextView streamerName = findViewById(R.id.streamerName);
        streamerName.setText(SplashScreen.userModel.getFullname());

    }

    void setupRecyclerView() {
        getData();

        recyclerView = findViewById(R.id.recyclerview);
        adapter = new StreamerEarning_Adapter(StreamerEarning.this, mlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(StreamerEarning.this));
        recyclerView.setAdapter(adapter);


    }

    private void getData() {
        mlist = new ArrayList<>();
        dataList = new ArrayList<>();

        Utils utils = new Utils();
        utils.showLoadingDialog(StreamerEarning.this, "loading");
        // Define the Firestore collection reference
        CollectionReference collectionRef = FirebaseFirestore.getInstance().collection("Streamers").document(String.valueOf(SplashScreen.userModel.getUserId())).collection("Logs");
        Query query = collectionRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(30);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    StreamerModel streamerModel = document.toObject(StreamerModel.class);
                    dataList.add(streamerModel);

//                    adapter.notifyDataSetChanged();
                }
                compareDate(dataList);
                utils.dismissLoadingDialog();
            } else {
                // Handle errors
            }
        });


    }

    private void compareDate(ArrayList<StreamerModel> timestamps) {
        HashMap<String, Integer> dayCountMap = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (StreamerModel streamerModel : timestamps) {
            // Convert timestamp to Date
            Timestamp firebaseTimestamp = streamerModel.getTimestamp();
            Date date = firebaseTimestamp.toDate();

            // Format the date to yyyy-MM-dd
            String formattedDate = dateFormat.format(date);
            // Update count in the map
            dayCountMap.put(formattedDate, dayCountMap.getOrDefault(formattedDate, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : dayCountMap.entrySet()) {
            StreamerEarningModel streamerEarningModel = new StreamerEarningModel(entry.getKey(), entry.getValue());
            Log.d("getData", "compareDate: " + streamerEarningModel.getDate());
            Log.d("getData", "compareDate: " + streamerEarningModel.getCoins());

            mlist.add(streamerEarningModel);
        }
        Collections.sort(mlist, new Comparator<StreamerEarningModel>() {
            @Override
            public int compare(StreamerEarningModel o1, StreamerEarningModel o2) {
                // Assuming getDate returns a Date object
                // For Java 8+, you can use o2.getDate().compareTo(o1.getDate()) directly
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        for (StreamerEarningModel streamerEarningModel : mlist) {
            Log.d("getData", "compareDate: " +streamerEarningModel.getDate()+"  coins: " +streamerEarningModel.getCoins());

        }
        adapter.notifyDataSetChanged();


    }
}