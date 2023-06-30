package com.bhola.livevideochat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


public class Fragment_Messenger extends Fragment {
    RecyclerView recyclerview;
    ArrayList<ChatItem_ModelClass> userList;
    ArrayList<ChatItem_ModelClass> userListTemp;
    LinearLayoutManager layoutManager;
    MessengeItemsAdapter adapter;

    public Fragment_Messenger() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_messenger, container, false);

        Context context = getContext();
        // Inflate the layout for this fragment


        setRecyclerView(view, context);


        return view;


    }

    private void setRecyclerView(View view, Context context) {
        recyclerview = view.findViewById(R.id.recyclerview);

        readDataFromJson(view, context);


    }

    private void readDataFromJson(View view, Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open("chats1.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

// Parse JSON and create ArrayList of Map objects
        userList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray usersArray = jsonObject.getJSONArray("users");

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userObject = usersArray.getJSONObject(i);
                int id = userObject.getInt("id");
                String userName = userObject.getString("userName");
                String userProfile = userObject.getString("userProfile");
                boolean containsQuestion = userObject.getBoolean("containsQuestion");
                String recommendationType = userObject.getString("recommendationType");

                JSONArray userBotMsgArray = userObject.getJSONArray("userBotMsg");
                ArrayList<UserBotMsg> userBotMsgList = new ArrayList<>();

                for (int j = 0; j < userBotMsgArray.length(); j++) {
                    JSONObject userBotMsgObject = userBotMsgArray.getJSONObject(j);
                    int msgId = userBotMsgObject.getInt("id");
                    String msg = userBotMsgObject.getString("msg");
                    String mimeType = userBotMsgObject.getString("mimeType");
                    String dateTime = userBotMsgObject.getString("dateTime");
                    int nextMsgDelay = userBotMsgObject.getInt("nextMsgDelay");

                    UserBotMsg userBotMsg = new UserBotMsg(msgId, msg, mimeType, "", dateTime, nextMsgDelay);
                    userBotMsgList.add(userBotMsg);
                }

                UserQuestionWithAns questionWithAns = null;
                if (containsQuestion) {
                    JSONObject questionWithAnsObject = userObject.getJSONObject("questionWithAns");
                    String question = questionWithAnsObject.getString("question");
                    JSONArray answersArray = questionWithAnsObject.getJSONArray("answers");
                    String action = questionWithAnsObject.getString("action");

                    ArrayList<String> answersList = new ArrayList<>();
                    for (int k = 0; k < answersArray.length(); k++) {
                        String answer = answersArray.getString(k);
                        answersList.add(answer);
                    }

                    questionWithAns = new UserQuestionWithAns(question, answersList, action);
                }

                ChatItem_ModelClass user = new ChatItem_ModelClass(id, userName, userProfile, containsQuestion, recommendationType, userBotMsgList, questionWithAns);
                userList.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendDataToRecyclerview(context, view);
    }

    private void sendDataToRecyclerview(Context context, View view) {

        Log.d("SplashScreen", "sendDataToRecyclerview: " + userList);
        userListTemp = new ArrayList<>();

        adapter = new MessengeItemsAdapter(userListTemp, context);
        layoutManager = new LinearLayoutManager(context);
        recyclerview.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(true);
        recyclerview.setAdapter(adapter);
        userListTemp.add(userList.get(0));

        userList.remove(0);
        Collections.shuffle(userList);

        for (int i = 0; i < 4; i++) {

            int finalI = i;
//            int[] numbers = {3, 6, 10, 12, 15, 20};
//            Random random = new Random();
//            int randomIndex = random.nextInt(numbers.length);

            int delayTime = 0;
            if (i == 0) {
                delayTime = 1500;
            } else if (i == 1 || i == 2) {
                delayTime = i * 8000;
            } else {
                delayTime = 20000;
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    userListTemp.add(0, userList.get(finalI));
                    adapter.notifyItemInserted(0);
                }
            }, delayTime);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
//        if (layoutManager.getChildCount() != 0 && layoutManager.getChildCount()< userList.size()+1) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    userListTemp.add(0, userList.get(layoutManager.getChildCount()));
//                    adapter.notifyItemInserted(0);
//                }
//            }, 1500);
//        }

    }

}


class MessengeItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;

    ArrayList<ChatItem_ModelClass> userList;

    public MessengeItemsAdapter(ArrayList<ChatItem_ModelClass> userList, Context context) {
        this.userList = userList;
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View UserItem_Viewholder = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_items_recyclerview, parent, false);
        return new MessengeItemsAdapter.UserItem_Viewholder(UserItem_Viewholder);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.message_received);
        mediaPlayer.start();

        MessengeItemsAdapter.UserItem_Viewholder userItem_viewholder = (MessengeItemsAdapter.UserItem_Viewholder) holder;
        ChatItem_ModelClass modelClass = userList.get(position);


        userItem_viewholder.userName.setText(modelClass.getUserName());
        userItem_viewholder.recommendationType.setText(modelClass.getRecommendationType());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        Date currentTime = new Date();
        String formattedTime = dateFormat.format(currentTime);
        userItem_viewholder.messageTime.setText(formattedTime);

        Picasso.get().load(modelClass.getUserProfile()).into(userItem_viewholder.profileUrl);

        if (modelClass.isContainsQuestion()) {
            UserQuestionWithAns userQuestionWithAns = modelClass.getQuestionWithAns();
            userItem_viewholder.lastMessage.setText(userQuestionWithAns.getQuestion());
        } else {
            UserBotMsg userBotMsg = modelClass.getUserBotMsg().get(0);
            userItem_viewholder.lastMessage.setText(userBotMsg.getMsg());
        }
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }


    public static class UserItem_Viewholder extends RecyclerView.ViewHolder {

        TextView userName, recommendationType, lastMessage, messageTime, messageCount;
        CircleImageView profileUrl;

        public UserItem_Viewholder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            recommendationType = itemView.findViewById(R.id.recommendationType);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            messageTime = itemView.findViewById(R.id.messageTime);
            messageCount = itemView.findViewById(R.id.messageCount);
            profileUrl = itemView.findViewById(R.id.profileUrl);

        }
    }
}

