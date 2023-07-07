package com.bhola.livevideochat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


public class Fragment_Messenger extends Fragment {
    RecyclerView recyclerview;
    public static ArrayList<ChatItem_ModelClass> userList;
    public static ArrayList<ChatItem_ModelClass> userListTemp;
    LinearLayoutManager layoutManager;
    public static MessengeItemsAdapter adapter;

    private Dialog alertNotificationDialog;
    private static final long AUTO_DISMISS_DELAY = 4000; // 4 seconds


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
                    String extraMessage = "";
                    try {
                        extraMessage = userBotMsgObject.getString("extraMsg");
                    } catch (Exception e) {
                    }

                    int nextMsgDelay = userBotMsgObject.getInt("nextMsgDelay");
                    int read = userBotMsgObject.getInt("read");
                    int sent = userBotMsgObject.getInt("sent");

                    UserBotMsg userBotMsg = new UserBotMsg(msgId, msg, mimeType, extraMessage, dateTime, nextMsgDelay, read, sent);
                    userBotMsgList.add(userBotMsg);
                }

                UserQuestionWithAns questionWithAns = null;
                if (containsQuestion) {
                    JSONObject questionWithAnsObject = userObject.getJSONObject("questionWithAns");
                    String question = questionWithAnsObject.getString("question");
                    JSONArray answersArray = questionWithAnsObject.getJSONArray("answers");
                    String action = questionWithAnsObject.getString("action");
                    String dateTime = questionWithAnsObject.getString("dateTime");
                    int read = questionWithAnsObject.getInt("read");
                    int sent = questionWithAnsObject.getInt("sent");

                    ArrayList<String> answersList = new ArrayList<>();
                    for (int k = 0; k < answersArray.length(); k++) {
                        String answer = answersArray.getString(k);
                        answersList.add(answer);
                    }

                    questionWithAns = new UserQuestionWithAns(question, answersList, dateTime, action, read, sent);
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

        userListTemp = new ArrayList<>();
        layoutManager = new LinearLayoutManager(context);
        recyclerview.setLayoutManager(layoutManager);
        adapter = new MessengeItemsAdapter(userListTemp, context, adapter, recyclerview);

        recyclerview.setAdapter(adapter);

        if (retreive_sharedPreferences(context)) {
            adapter = new MessengeItemsAdapter(userListTemp, context, adapter, recyclerview);
            recyclerview.setAdapter(adapter);
            userList.remove(0);

            int temp = 0;
            for (int i = userListTemp.size(); i < userList.size(); i++) {
                int delayTime = temp * 120000;
                temp++;
                int finalI = i;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        userListTemp.add(0, userList.get(finalI));
                        adapter.notifyItemInserted(0);
                        Fragment_Messenger.save_sharedPrefrence(context, userListTemp);
                        updateUnreadmessageCount(context);

                    }
                }, delayTime);
            }

            return;
        }

        userListTemp.add(userList.get(0));
        updateUnreadmessageCount(context);
        userList.remove(0);

        for (int i = 0; i < 4; i++) {

            int finalI = i;
            int delayTime = finalI * 16000;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    userListTemp.add(0, userList.get(finalI));
                    adapter.notifyItemInserted(0);
                    Fragment_Messenger.save_sharedPrefrence(context, userListTemp);
                    updateUnreadmessageCount(context);

                }
            }, delayTime);
        }

    }

    public static void save_sharedPrefrence(Context context, ArrayList<ChatItem_ModelClass> userList) {


        SharedPreferences sharedPreferences = context.getSharedPreferences("messenger_chats", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

// Convert the ArrayList to JSON string
        Gson gson = new Gson();
        String json = gson.toJson(userListTemp);

// Save the JSON string to SharedPreferences
        editor.putString("userListTemp", json);
        editor.apply();
        //JHGJHJHGGHJ

    }

    public static boolean retreive_sharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("messenger_chats", Context.MODE_PRIVATE);

// Retrieve the JSON string from SharedPreferences
        String json = sharedPreferences.getString("userListTemp", null);


// Convert the JSON string back to ArrayList
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ChatItem_ModelClass>>() {
        }.getType();


        if (json == null) {
            // Handle case when no ArrayList is saved in SharedPreferences
            return false;
        } else {
            userListTemp = gson.fromJson(json, type);
            return true;
        }


    }

    public static void updateUnreadmessageCount(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("messenger_chats", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.d(SplashScreen.TAG, "updateUnreadmessageCount: " + MainActivity.unreadMessage_count);
        int value = MainActivity.unreadMessage_count + 1; // Replace with your desired integer value
        Log.d(SplashScreen.TAG, "value: " + value);

        editor.putInt("unreadMessage_Count", value);
        editor.apply();
        MainActivity.badge_text.setText(String.valueOf(value));
        MainActivity.badge_text.setVisibility(View.VISIBLE);
        MainActivity.badge_text.setBackgroundResource(R.drawable.badge_background);
        MainActivity.unreadMessage_count = value;

    }
}


class MessengeItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;

    ArrayList<ChatItem_ModelClass> userList;
    MessengeItemsAdapter adapter;
    RecyclerView recyclerview;

    public MessengeItemsAdapter(ArrayList<ChatItem_ModelClass> userList, Context context, MessengeItemsAdapter adapter, RecyclerView recyclerview) {
        this.userList = userList;
        this.context = context;
        this.adapter = adapter;
        this.recyclerview = recyclerview;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View UserItem_Viewholder = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_items_recyclerview, parent, false);
        return new MessengeItemsAdapter.UserItem_Viewholder(UserItem_Viewholder);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        MessengeItemsAdapter.UserItem_Viewholder userItem_viewholder = (MessengeItemsAdapter.UserItem_Viewholder) holder;
        ChatItem_ModelClass modelClass = userList.get(position);


        userItem_viewholder.userName.setText(modelClass.getUserName());
        userItem_viewholder.recommendationType.setText(modelClass.getRecommendationType());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        Date currentTime = new Date();
        String formattedTime = dateFormat.format(currentTime);
        userItem_viewholder.messageTime.setText(formattedTime);

        Picasso.get().load(modelClass.getUserProfile()).into(userItem_viewholder.profileUrl);

        userItem_viewholder.chatItemClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String selectedObjectJson = new Gson().toJson(userList.get(holder.getBindingAdapterPosition()));
                Intent intent = new Intent(context, ChatScreen_User.class);
                intent.putExtra("data", selectedObjectJson);
                intent.putExtra("indexPosition", holder.getBindingAdapterPosition());

                context.startActivity(intent);
            }
        });

        if (modelClass.isContainsQuestion()) {

            UserQuestionWithAns userQuestionWithAns = modelClass.getQuestionWithAns();
            if (userQuestionWithAns.getSent() == 0) {
                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.message_received);
                mediaPlayer.start();
            }

            userItem_viewholder.lastMessage.setText(userQuestionWithAns.getQuestion());
            userQuestionWithAns.setSent(1);
            userQuestionWithAns.setDateTime(String.valueOf(currentTime.getTime()));

            Fragment_Messenger.save_sharedPrefrence(context, userList);

        } else {

            for (int i = 0; i < modelClass.getUserBotMsg().size() - 1; i++) {

                if (modelClass.getUserBotMsg().get(i).getSent() == 0) {

                    if (!modelClass.getUserName().equals("Team Desi Video Chat")) {
                        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.message_received);
                        mediaPlayer.start();
                    }

                    userItem_viewholder.lastMessage.setText(modelClass.getUserBotMsg().get(i).getMsg());
                    modelClass.getUserBotMsg().get(i).setSent(1);
                    modelClass.getUserBotMsg().get(i).setDateTime(String.valueOf(currentTime.getTime()));
                    userItem_viewholder.messageCount.setText(String.valueOf(i + 1));

                    Random random = new Random();
                    int randomNumber = random.nextInt(1001) + 50; // Generate a random number between 0 and 5000, then add 50

                    int nextMegDelay = modelClass.getUserBotMsg().get(i).getNextMsgDelay() + randomNumber;


                    Handler handler = new Handler();
                    Runnable updateTimeRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (holder.getBindingAdapterPosition() == -1) {
                            } else {

                                userList.remove(holder.getBindingAdapterPosition());
                                userList.add(0, modelClass);
                                notifyItemMoved(holder.getBindingAdapterPosition(), 0);
                                notifyItemChanged(0);
                                Fragment_Messenger.save_sharedPrefrence(context, userList);
                                recyclerview.smoothScrollToPosition(0);
                                Fragment_Messenger.updateUnreadmessageCount(context);
                            }

                        }
                    };
                    handler.postDelayed(updateTimeRunnable, nextMegDelay);


                    break;
                }
                if (i == modelClass.getUserBotMsg().size() - 2) { //last loop
                    userItem_viewholder.lastMessage.setText(modelClass.getUserBotMsg().get(i + 1).getMsg());
                    userItem_viewholder.messageCount.setText(String.valueOf(i + 1));
                    modelClass.getUserBotMsg().get(modelClass.getUserBotMsg().size() - 1).setSent(1);
                    Date date = new Date();
                    modelClass.getUserBotMsg().get(modelClass.getUserBotMsg().size() - 1).setDateTime(String.valueOf(currentTime.getTime()));


                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }


    public static class UserItem_Viewholder extends RecyclerView.ViewHolder {

        TextView userName, recommendationType, lastMessage, messageTime, messageCount;
        CircleImageView profileUrl;
        LinearLayout chatItemClick;

        public UserItem_Viewholder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            recommendationType = itemView.findViewById(R.id.recommendationType);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            messageTime = itemView.findViewById(R.id.messageTime);
            messageCount = itemView.findViewById(R.id.messageCount);
            profileUrl = itemView.findViewById(R.id.profileUrl);
            chatItemClick = itemView.findViewById(R.id.chatItemClick);

        }
    }
}

