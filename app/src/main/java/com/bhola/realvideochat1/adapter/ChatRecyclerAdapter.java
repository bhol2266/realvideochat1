package com.bhola.realvideochat1.adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bhola.realvideochat1.ChatScreen_User;
import com.bhola.realvideochat1.Fragment_LargePhotoViewer;
import com.bhola.realvideochat1.Models.ChatMessageModel;
import com.bhola.realvideochat1.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;
    String currentUserId;
    String currentProfile;
    String otherProfile;
    MediaPlayer mediaPlayer;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context, String currentUserId, String currentProfile, String otherProfile, MediaPlayer mediaPlayer) {
        super(options);
        this.context = context;
        this.currentUserId = currentUserId;
        this.currentProfile = currentProfile;
        this.otherProfile = otherProfile;
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel chatMessageModel) {


        if (chatMessageModel.getSenderId().equals(currentUserId)) {
            holder.rightSideChat.setVisibility(View.VISIBLE);
            holder.leftSideChat.setVisibility(View.GONE);
            holder.timeStamp_right.setText(getTimeStampFormat(chatMessageModel.getTimestamp()));

            if (chatMessageModel.getMessagetype().equals("text")) {
                holder.message_right.setText(chatMessageModel.getMessage());
                holder.picMsgLayout_right.setVisibility(View.GONE);
                holder.audioMsg_right.setVisibility(View.GONE);
            }
            if (chatMessageModel.getMessagetype().equals("audio")) {
                holder.picMsgLayout_right.setVisibility(View.GONE);
                holder.message_right.setVisibility(View.GONE);
                holder.audioMsgLayout_right.setVisibility(View.VISIBLE);
                holder.audioMsg_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            return;
                        }

                        try {
                            holder.audioProgressBar_right.setVisibility(View.VISIBLE);
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build());

                            String audioSource = chatMessageModel.getExtraMessage(); // Get the audio source (URL or URI as a string)

                            // Check if the audio source is a URL or a local file URI
                            if (audioSource.startsWith("http")) {
                                // It's an audio URL
                                mediaPlayer.setDataSource(audioSource);
                            } else {
                                // It's a local file URI as a string, so convert it back to a Uri
                                Uri audioUri = Uri.parse(audioSource);
                                mediaPlayer.setDataSource(context, audioUri);
                            }

                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    holder.audioProgressBar_right.setVisibility(View.GONE);
                                    holder.playAudiolottie_right.playAnimation();
                                    mediaPlayer.start();
                                }
                            });
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    holder.playAudiolottie_right.cancelAnimation();
                                    mediaPlayer.stop();
                                }
                            }); // Set the OnCompletionListener
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
            if (chatMessageModel.getMessagetype().equals("image")) {

                holder.picMsgLayout_right.setVisibility(View.VISIBLE);

                Log.d("asdf", "onBindViewHolder: " + chatMessageModel.getExtraMessage());
                if (chatMessageModel.getExtraMessage().startsWith("http")) {
                    Picasso.get().load(chatMessageModel.getExtraMessage()).into(holder.picMsg_right);

                } else {
                    try {
                        Bitmap bitmap = checkOrientation(Uri.parse(chatMessageModel.getExtraMessage())); //change orientation to default
                        holder.playAudiolottie_right.setImageBitmap(bitmap);

                    } catch (Exception e) {
                    }
                }

                holder.message_right.setVisibility(View.GONE);
                holder.audioMsg_right.setVisibility(View.GONE);

                holder.picMsgLayout_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<Map<String, String>> imageList = new ArrayList<>();
                        Map<String, String> stringMap2 = new HashMap<>();
                        stringMap2.put("url", chatMessageModel.getExtraMessage());
                        stringMap2.put("type", "free");
                        imageList.add(stringMap2);

                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        int originalScreenWidth = displayMetrics.widthPixels;
                        int screenHeight = displayMetrics.heightPixels;


                        Log.d("SDfsd", "onClick: " + imageList.size());
                        // Decrease the screen width by 15%
                        int screenWidth = (int) (originalScreenWidth * 0.85);
                        Fragment_LargePhotoViewer fragment = Fragment_LargePhotoViewer.newInstance(context, (ArrayList<Map<String, String>>) imageList, 0, screenWidth, screenHeight);

                        FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment) // Replace with your container ID
                                .addToBackStack(null) // Optional, for back navigation
                                .commit();

                    }
                });


            }
            Picasso.get().load(currentProfile).into(holder.profileImage_right);


        } else {
            holder.leftSideChat.setVisibility(View.VISIBLE);
            holder.rightSideChat.setVisibility(View.GONE);
            holder.timeStamp_left.setText(getTimeStampFormat(chatMessageModel.getTimestamp()));


            if (chatMessageModel.getMessagetype().equals("text")) {
                holder.message_left.setText(chatMessageModel.getMessage());
                holder.picMsgLayout_left.setVisibility(View.GONE);
                holder.audioMsg_left.setVisibility(View.GONE);
            }
            if (chatMessageModel.getMessagetype().equals("audio")) {
                holder.picMsgLayout_left.setVisibility(View.GONE);
                holder.audioMsg_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            return;
                        }

                        try {
                            holder.audioProgressBar_left.setVisibility(View.VISIBLE);
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build());

                            String audioSource = chatMessageModel.getExtraMessage(); // Get the audio source (URL or URI as a string)

                            // Check if the audio source is a URL or a local file URI
                            if (audioSource.startsWith("http")) {
                                // It's an audio URL
                                mediaPlayer.setDataSource(audioSource);
                            } else {
                                // It's a local file URI as a string, so convert it back to a Uri
                                Uri audioUri = Uri.parse(audioSource);
                                mediaPlayer.setDataSource(context, audioUri);
                            }

                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    holder.audioProgressBar_left.setVisibility(View.GONE);
                                    holder.playAudiolottie_left.playAnimation();
                                    mediaPlayer.start();
                                }
                            });
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    holder.playAudiolottie_left.cancelAnimation();
                                    mediaPlayer.stop();
                                }
                            }); // Set the OnCompletionListener
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                holder.message_left.setVisibility(View.GONE);
                holder.audioMsgLayout_left.setVisibility(View.VISIBLE);
            }
            if (chatMessageModel.getMessagetype().equals("image")) {

                holder.picMsgLayout_left.setVisibility(View.VISIBLE);

                if (chatMessageModel.getExtraMessage().startsWith("http")) {
                    Picasso.get().load(chatMessageModel.getExtraMessage()).into(holder.picMsg_left);

                } else {
                    try {
                        Bitmap bitmap = checkOrientation(Uri.parse(chatMessageModel.getExtraMessage())); //change orientation to default
                        holder.playAudiolottie_left.setImageBitmap(bitmap);

                    } catch (Exception e) {
                    }
                }

                holder.message_left.setVisibility(View.GONE);
                holder.audioMsg_left.setVisibility(View.GONE);

                holder.picMsgLayout_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<Map<String, String>> imageList = new ArrayList<>();
                        Map<String, String> stringMap2 = new HashMap<>();
                        stringMap2.put("url", chatMessageModel.getExtraMessage());
                        stringMap2.put("type", "free");
                        imageList.add(stringMap2);

                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        int originalScreenWidth = displayMetrics.widthPixels;
                        int screenHeight = displayMetrics.heightPixels;


                        // Decrease the screen width by 15%
                        int screenWidth = (int) (originalScreenWidth * 0.85);
                        Fragment_LargePhotoViewer fragment = Fragment_LargePhotoViewer.newInstance(context, (ArrayList<Map<String, String>>) imageList, 0, screenWidth, screenHeight);

                        FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment) // Replace with your container ID
                                .addToBackStack(null) // Optional, for back navigation
                                .commit();

                    }
                });


            }


            Picasso.get().load(otherProfile).into(holder.profileImage_left);

        }
        updateErrorIcon(holder.errorLayout_right, holder.errorIcon_right, chatMessageModel.getMessagetype());
    }

    private void updateErrorIcon(FrameLayout errorLayout, ImageView errorIcon, String chatType) {

        errorLayout.setVisibility(View.GONE);

//        if (!chatType.equals("premium") || SplashScreen.coins >0) {
//            errorLayout.setVisibility(View.GONE);
//        }
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                errorIcon.setVisibility(View.VISIBLE);
//                errorIcon.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        ChatScreen_User.rechargeDialog(view.getContext());
//                    }
//                });
//            }
//        }, 3000);
    }


    private String getTimeStampFormat(Timestamp timestamp) {


        Date date = timestamp.toDate();

        // Create a SimpleDateFormat object with the desired pattern
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm MMM dd", Locale.ENGLISH);

        // Format the Date using the SimpleDateFormat
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }


    private Bitmap checkOrientation(Uri imageUri) {

        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(imageUri);
            ExifInterface exif = new ExifInterface(inputStream);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            Bitmap rotatedBitmap = ChatScreen_User.rotateBitmap(originalBitmap, orientation);

            return rotatedBitmap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatModelViewHolder(view);
    }


    //    class ChatModelViewHolder extends RecyclerView.ViewHolder{
//
//        LinearLayout leftChatLayout,rightChatLayout;
//        TextView leftChatTextview,rightChatTextview;
//
//        public ChatModelViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
//            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
//            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
//            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
//        }
//    }
    class ChatModelViewHolder extends RecyclerView.ViewHolder {

        //LeftSide
        RelativeLayout leftSideChat;
        CircleImageView profileImage_left;
        TextView message_left;
        TextView timeStamp_left;
        ImageView picMsg_left;
        CardView audioMsg_left;
        FrameLayout picMsgLayout_left;
        LottieAnimationView playAudiolottie_left;
        ProgressBar audioProgressBar_left;
        LinearLayout audioMsgLayout_left;


        // RightSide
        RelativeLayout rightSideChat;
        CircleImageView profileImage_right;
        TextView message_right;
        TextView timeStamp_right;
        ImageView picMsg_right;
        CardView audioMsg_right;
        FrameLayout picMsgLayout_right;
        LottieAnimationView playAudiolottie_right;
        ProgressBar audioProgressBar_right;
        FrameLayout errorLayout_right;
        ImageView errorIcon_right;
        LinearLayout audioMsgLayout_right;


        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            //LeftSide
            leftSideChat = itemView.findViewById(R.id.leftSideChat);
            profileImage_left = itemView.findViewById(R.id.profileImage_left);
            message_left = itemView.findViewById(R.id.message_left);
            timeStamp_left = itemView.findViewById(R.id.timeStamp_left);

            picMsg_left = itemView.findViewById(R.id.picMsg_left);
            audioMsg_left = itemView.findViewById(R.id.audioMsg_left);
            picMsgLayout_left = itemView.findViewById(R.id.picMsgLayout_left);
            playAudiolottie_left = itemView.findViewById(R.id.playAudiolottie_left);
            audioProgressBar_left = itemView.findViewById(R.id.audioProgressBar_left);
            audioMsgLayout_left = itemView.findViewById(R.id.audioMsgLayout_left);


            //RightSide
            rightSideChat = itemView.findViewById(R.id.rightSideChat);
            profileImage_right = itemView.findViewById(R.id.profileImage_right);
            message_right = itemView.findViewById(R.id.message_right);
            timeStamp_right = itemView.findViewById(R.id.timeStamp_right);

            picMsg_right = itemView.findViewById(R.id.picMsg_right);
            audioMsg_right = itemView.findViewById(R.id.audioMsg_right);
            picMsgLayout_right = itemView.findViewById(R.id.picMsgLayout_right);
            playAudiolottie_right = itemView.findViewById(R.id.playAudiolottie_right);
            audioProgressBar_right = itemView.findViewById(R.id.audioProgressBar_right);
            errorLayout_right = itemView.findViewById(R.id.errorLayout_right);
            errorIcon_right = itemView.findViewById(R.id.errorIcon_right);
            audioMsgLayout_right = itemView.findViewById(R.id.audioMsgLayout_right);
        }
    }

}
