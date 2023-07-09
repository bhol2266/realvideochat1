package com.bhola.livevideochat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatScreen_User extends Activity {


    ChatItem_ModelClass modelClass;
    AlertDialog block_user_dialog = null;
    AlertDialog report_user_dialog = null;
    AlertDialog report_userSucessfully_dialog = null;
    ArrayList<Chats_Modelclass> chatsArrayList;
    ChatsAdapter chatAdapter;
    RecyclerView recylerview;
    DatabaseReference chatRef;
    MediaPlayer mediaPlayer;

    ArrayList<ChatItem_ModelClass> userListTemp2;
    private Handler handler;
    private Runnable myRunnable;
    private Thread myThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen_user);


        getModelClassFrom_sharedPreference();
        sendDataRecyclerview();
        actionbar();


    }

    private void getModelClassFrom_sharedPreference() {
        String userName = getIntent().getStringExtra("userName");

        SharedPreferences sharedPreferences = getSharedPreferences("messenger_chats", Context.MODE_PRIVATE);

// Retrieve the JSON string from SharedPreferences
        String json = sharedPreferences.getString("userListTemp", null);


// Convert the JSON string back to ArrayList
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ChatItem_ModelClass>>() {
        }.getType();


        if (json == null) {
            // Handle case when no ArrayList is saved in SharedPreferences
        } else {
            userListTemp2 = gson.fromJson(json, type);
            for (int i = 0; i < userListTemp2.size(); i++) {
                if (userListTemp2.get(i).getUserName().equals(userName)) {
                    modelClass = userListTemp2.get(i);
                }
            }

        }

    }


    private void sendDataRecyclerview() {

        chatsArrayList = new ArrayList<Chats_Modelclass>();
        if (modelClass.isContainsQuestion()) {

        } else {
            for (int i = 0; i < modelClass.getUserBotMsg().size(); i++) {

                if (modelClass.getUserBotMsg().get(i).getSent() == 1) {
                    UserBotMsg userBotMsg = modelClass.getUserBotMsg().get(i);
                    Chats_Modelclass chats_modelclass = new Chats_Modelclass(userBotMsg.getMsg(), userBotMsg.getMimeType(), userBotMsg.getExtraMsg(), modelClass.getUserProfile(), userBotMsg.getDateTime(), 2);
                    chatsArrayList.add(chats_modelclass);

                    Log.d(SplashScreen.TAG, "  userBotMsg.getRead() : " + userBotMsg.getRead());
                    Log.d(SplashScreen.TAG, "  value before: " + MainActivity.unreadMessage_count);


                    if (userBotMsg.getRead() == 0) {
                        userBotMsg.setRead(1);
                        update_userListTemp();
                        Fragment_Messenger.updateUnreadmessageCount(ChatScreen_User.this, "minus");
                        Log.d(SplashScreen.TAG, "  value After: " + MainActivity.unreadMessage_count);

                    }
                }
            }

        }

        recylerview = findViewById(R.id.recylerview);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatScreen_User.this);
        linearLayoutManager.setStackFromEnd(true);
        recylerview.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatsAdapter(ChatScreen_User.this, chatsArrayList, recylerview, mediaPlayer);
        recylerview.setAdapter(chatAdapter);

        scrollrecycelrvewToBottom();

        load_UnsentMessage();

    }

    private void update_userListTemp() {

        for (int i = 0; i < userListTemp2.size(); i++) {
            if (userListTemp2.get(i).getUserName().equals(modelClass.getUserName())) {
                userListTemp2.set(i, modelClass);
            }
        }

        SharedPreferences sharedPreferences = getSharedPreferences("messenger_chats", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


// Convert the ArrayList to JSON string
        Gson gson = new Gson();
        String json = gson.toJson(userListTemp2);

// Save the JSON string to SharedPreferences
        editor.putString("userListTemp", json);
        editor.apply();


    }

    private void scrollrecycelrvewToBottom() {
        NestedScrollView nestedScrollview = findViewById(R.id.nestedScrollview);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (chatsArrayList.size() == 0) {
                    return;
                }
                final float y = recylerview.getChildAt(chatsArrayList.size() - 1).getY();
                nestedScrollview.smoothScrollTo(0, (int) y);

            }
        }, 500);

    }

    private void load_UnsentMessage() {

        handler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                checkForUpdate();
                // Schedule the task to run again after 1 second
                handler.postDelayed(this, 500);
            }
        };

        myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Start the task on the new thread
                handler.post(myRunnable);
            }
        });

        myThread.start();

    }

    private void checkForUpdate() {

        for (int i = 0; i < Fragment_Messenger.userListTemp.size(); i++) {
            if (Fragment_Messenger.userListTemp.get(i).getUserName().equals(modelClass.getUserName())) {

                for (int j = 0; j < Fragment_Messenger.userListTemp.get(i).getUserBotMsg().size(); j++) {
                    UserBotMsg userBotMsg = Fragment_Messenger.userListTemp.get(i).getUserBotMsg().get(j);

                    if (userBotMsg.getSent() == 1) {
                        if (modelClass.getUserBotMsg().get(j).getSent() == 0) {
                            modelClass.getUserBotMsg().get(j).setSent(1);
                            modelClass.getUserBotMsg().get(j).setRead(1);

                            Chats_Modelclass chats_modelclass = new Chats_Modelclass(userBotMsg.getMsg(), userBotMsg.getMimeType(), userBotMsg.getExtraMsg(), modelClass.getUserProfile(), userBotMsg.getDateTime(), 2);
                            chatsArrayList.add(chats_modelclass);
                            chatAdapter.notifyDataSetChanged();
                            Fragment_Messenger.updateUnreadmessageCount(ChatScreen_User.this, "minus");

                        }
                    }
                }
            }
        }
    }


    private void actionbar() {
        ImageView backArrow = findViewById(R.id.backArrow);
        ImageView warningSign = findViewById(R.id.warningSign);
        ImageView menuDots = findViewById(R.id.menuDots);
        RelativeLayout alertBar = findViewById(R.id.alertBar);
        TextView profileName = findViewById(R.id.profileName);
        profileName.setText(modelClass.getUserName());

        ImageView profileImage = findViewById(R.id.profileImage);
        Picasso.get().load(modelClass.getUserProfile()).into(profileImage);
        TextView viewProfile = findViewById(R.id.viewProfile);
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open view profile activity
            }
        });


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        warningSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockUserDialog();
            }
        });

        menuDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportUserDialog();
            }
        });

        alertBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertBar.setVisibility(View.INVISIBLE);
            }
        });


    }


    private void blockUserDialog() {

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ChatScreen_User.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View promptView = inflater.inflate(R.layout.dialog_block_user, null);
        builder.setView(promptView);
        builder.setCancelable(true);

        TextView confirm = promptView.findViewById(R.id.confirm);
        TextView cancel = promptView.findViewById(R.id.cancel);


        block_user_dialog = builder.create();
        block_user_dialog.show();


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatScreen_User.this, "User blocked succesfully", Toast.LENGTH_SHORT).show();
                block_user_dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                block_user_dialog.dismiss();
            }
        });

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        block_user_dialog.getWindow().setBackgroundDrawable(inset);

    }

    private void reportUserDialog() {

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ChatScreen_User.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View promptView = inflater.inflate(R.layout.dialog_report_user, null);
        builder.setView(promptView);
        builder.setCancelable(true);

        TextView report = promptView.findViewById(R.id.reportBtn);
        ImageView cross = promptView.findViewById(R.id.cross);


        report_user_dialog = builder.create();
        report_user_dialog.show();


        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                report_user_dialog.dismiss();
                reportUserSucessfullDialog();
            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                report_user_dialog.dismiss();
            }
        });


        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        report_user_dialog.getWindow().setBackgroundDrawable(inset);

    }

    private void reportUserSucessfullDialog() {

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ChatScreen_User.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View promptView = inflater.inflate(R.layout.dialog_report_user_sucessfull, null);
        builder.setView(promptView);
        builder.setCancelable(true);

        TextView confirm = promptView.findViewById(R.id.confirm);


        report_userSucessfully_dialog = builder.create();
        report_userSucessfully_dialog.show();


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatScreen_User.this, "User Reported", Toast.LENGTH_SHORT).show();
                report_userSucessfully_dialog.dismiss();
            }
        });


        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        report_userSucessfully_dialog.getWindow().setBackgroundDrawable(inset);

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the task and terminate the new thread
        handler.removeCallbacks(myRunnable);
        myThread.interrupt();
    }
}

class ChatsAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Chats_Modelclass> chatsArrayList;
    int SENDER = 1;
    int RECEIVER = 2;
    RecyclerView recyclerview;
    MediaPlayer mediaPlayer;

    public ChatsAdapter(Context context, ArrayList<Chats_Modelclass> chatsArrayList, RecyclerView recyclerview, MediaPlayer mediaPlayer) {
        this.context = context;
        this.chatsArrayList = chatsArrayList;
        this.recyclerview = recyclerview;
        this.mediaPlayer = mediaPlayer;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new SenderVierwHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.userchat_reciver_layout, parent, false);
            return new ReciverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chats_Modelclass chats = chatsArrayList.get(position);


        long timestamp = Long.parseLong(chats.getTimeStamp()); // Example timestamp value

        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm MM-dd");
        String formattedDate = sdf.format(date);


        if (chats.getViewType() == 2) {
            ChatsAdapter.ReciverViewHolder reciverViewHolder = (ReciverViewHolder) holder;
            reciverViewHolder.timeStamp.setText(formattedDate);
            Picasso.get()
                    .load(chats.getProfileUrl())
                    .into(reciverViewHolder.profileImage);

            if (chats.getMessageType().equals("mimeType/text")) {
                reciverViewHolder.textMsg.setText(chats.getMessage());
                reciverViewHolder.picMsgLayout.setVisibility(View.GONE);
                reciverViewHolder.audioMsg.setVisibility(View.GONE);
            }
            if (chats.getMessageType().equals("mimeType/audio")) {
                reciverViewHolder.picMsgLayout.setVisibility(View.GONE);
                reciverViewHolder.audioMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            return;
                        }
                        try {
                            reciverViewHolder.audioProgressBar.setVisibility(View.VISIBLE);
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build());
                            mediaPlayer.setDataSource(chats.getExtraMsg());
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    reciverViewHolder.audioProgressBar.setVisibility(View.GONE);
                                    reciverViewHolder.playAudiolottie.playAnimation();
                                    mediaPlayer.start();
                                }
                            });
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    reciverViewHolder.playAudiolottie.cancelAnimation();
                                    mediaPlayer.stop();

                                }
                            }); // Set the OnCompletionListener


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                reciverViewHolder.textMsg.setVisibility(View.GONE);
            }
            if (chats.getMessageType().equals("mimeType/image")) {
                Picasso.get()
                        .load(chats.getExtraMsg())
                        .into(reciverViewHolder.picMsg);
                reciverViewHolder.textMsg.setVisibility(View.GONE);
                reciverViewHolder.audioMsg.setVisibility(View.GONE);

            }


        }


    }


    @Override
    public int getItemCount() {
        return chatsArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Chats_Modelclass messages = chatsArrayList.get(position);
        if (messages.getViewType() == 1) {
            return SENDER;
        } else {
            return RECEIVER;
        }
    }

    static class SenderVierwHolder extends RecyclerView.ViewHolder {
        CircleImageView profile;
        TextView msgtxt;
        TextView timeStamp;

        public SenderVierwHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.profileImage);
            msgtxt = itemView.findViewById(R.id.message);
            timeStamp = itemView.findViewById(R.id.timeStamp);

        }
    }

    static class ReciverViewHolder extends RecyclerView.ViewHolder {
        TextView textMsg, timeStamp;
        ImageView picMsg, profileImage;
        CardView audioMsg;
        FrameLayout picMsgLayout;
        LottieAnimationView playAudiolottie;
        ProgressBar audioProgressBar;


        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            textMsg = itemView.findViewById(R.id.textMsg);
            picMsg = itemView.findViewById(R.id.picMsg);
            audioMsg = itemView.findViewById(R.id.audioMsg);
            timeStamp = itemView.findViewById(R.id.timeStamp);
            profileImage = itemView.findViewById(R.id.profileImage);
            picMsgLayout = itemView.findViewById(R.id.picMsgLayout);
            playAudiolottie = itemView.findViewById(R.id.playAudiolottie);
            audioProgressBar = itemView.findViewById(R.id.audioProgressBar);

        }
    }


}


class Chats_Modelclass {

    String message;
    String messageType;
    String extraMsg;
    String profileUrl;
    String timeStamp;
    int viewType;//viewType 1 is sender 2 is receiver

    public Chats_Modelclass() {
    }

    public Chats_Modelclass(String message, String messageType, String extraMsg, String profileUrl, String timeStamp, int viewType) {
        this.message = message;
        this.messageType = messageType;
        this.extraMsg = extraMsg;
        this.profileUrl = profileUrl;
        this.timeStamp = timeStamp;
        this.viewType = viewType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getExtraMsg() {
        return extraMsg;
    }

    public void setExtraMsg(String extraMsg) {
        this.extraMsg = extraMsg;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}

class MyThread extends Thread {
    private Handler handler;

    @Override
    public void run() {
        // Prepare the Looper for this thread
        Looper.prepare();

        // Create the Handler associated with this thread's Looper
        handler = new Handler();

        // Start the task with a delay of 1 second
        handler.postDelayed(myRunnable, 1000);

        // Start the Looper loop to process messages
        Looper.loop();
    }

    public Handler getHandler() {
        return handler;
    }

    private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            // Perform your task here
            Log.d("MyRunnable", "Task is running...");

            // Post the task again after a delay
            handler.postDelayed(this, 1000);
        }
    };
}
