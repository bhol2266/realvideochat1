package com.bhola.realvideochat1;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bhola.realvideochat1.Models.ChatroomModel;
import com.bhola.realvideochat1.adapter.RecentChatRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Fragment_Messenger extends Fragment {




   public static int count=0;

    public Fragment_Messenger() {
        // Required empty public constructor
    }

    View view;
    Context context;
    RecentChatRecyclerAdapter adapterMessenger;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_messenger, container, false);

        context = getContext();
        // Inflate the layout for this fragment



        setup_CustomerCare_Chat();
        setupRecyclerView();

        return view;


    }

    private void setup_CustomerCare_Chat() {

        TextView userName = view.findViewById(R.id.userName);
        TextView lastMessage = view.findViewById(R.id.lastMessage);
        TextView messageTime = view.findViewById(R.id.messageTime);
        TextView messageCount = view.findViewById(R.id.messageCount);
        LinearLayout chatItemClick = view.findViewById(R.id.chatItemClick);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date currentTime = new Date();
        String formattedTime = dateFormat.format(currentTime);
        messageTime.setText(formattedTime);


        chatItemClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.viewPager2.setCurrentItem(3); // Switch to Fragment B
            }
        });


    }

    void setupRecyclerView(){
        Toast.makeText(context, "Imhere", Toast.LENGTH_SHORT).show();
        recyclerView = view.findViewById(R.id.recyclerview);

        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds",String.valueOf(SplashScreen.userModel.getUserId()))
                .orderBy("lastMessageTimestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query,ChatroomModel.class).setLifecycleOwner((LifecycleOwner) context).build();

        adapterMessenger = new RecentChatRecyclerAdapter(options,context,String.valueOf(SplashScreen.userModel.getUserId()));
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapterMessenger);
        adapterMessenger.startListening();



    }


    @Override
    public void onResume() {
        super.onResume();
        if(adapterMessenger!=null)
            adapterMessenger.notifyDataSetChanged();
    }

}





 class WrapContentLinearLayoutManager extends LinearLayoutManager {
     public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
         super(context, orientation, reverseLayout);
     }

     //... constructor
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e("TAG", "meet a IOOBE in RecyclerView");
        }
    }
}