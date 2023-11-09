package com.bhola.realvideochat1;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bhola.realvideochat1.Models.CallroomModel;
import com.bhola.realvideochat1.adapter.RecentCallRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class Fragment_CallLog extends Fragment {


    public static int count = 0;

    public Fragment_CallLog() {
        // Required empty public constructor
    }

    View view;
    Context context;
    RecentCallRecyclerAdapter adapter;
    RecyclerView recyclerView;
    String callRoomId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_callog, container, false);

        context = getContext();
        // Inflate the layout for this fragmentycvn 
        setupRecyclerView();

        return view;


    }


    void setupRecyclerView() {
        recyclerView = view.findViewById(R.id.recyclerview);

        Query query = FirebaseUtil.allCallogsCollectionReference()
                .whereArrayContains("userIds", String.valueOf(SplashScreen.userModel.getUserId()))
                .orderBy("lastCallTimestamp", Query.Direction.DESCENDING);


        TextView noCallsTextView = view.findViewById(R.id.noCalls);
        noCallsTextView.setVisibility(View.GONE);


        FirestoreRecyclerOptions<CallroomModel> options = new FirestoreRecyclerOptions.Builder<CallroomModel>()
                .setQuery(query, CallroomModel.class).setLifecycleOwner((LifecycleOwner) context).build();

        adapter = new RecentCallRecyclerAdapter(options, context, String.valueOf(SplashScreen.userModel.getUserId()), noCallsTextView);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }



}





