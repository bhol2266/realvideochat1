package com.bhola.realvideochat1;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Fragment_Messenger extends Fragment {


    RecyclerView recyclerview;
    public static ArrayList<ChatItem_ModelClass> userList;
    public static ArrayList<ChatItem_ModelClass> userListTemp;
    public static String currentActiveUser = "";

   public static int count=0;

    public Fragment_Messenger() {
        // Required empty public constructor
    }

    View view;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_messenger, container, false);

        context = getContext();
        // Inflate the layout for this fragment



        setup_CustomerCare_Chat();

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



}





