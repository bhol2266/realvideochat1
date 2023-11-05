package com.bhola.realvideochat1.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bhola.realvideochat1.ChatScreen_User;
import com.bhola.realvideochat1.FirebaseUtil;
import com.bhola.realvideochat1.Models.ChatroomModel;
import com.bhola.realvideochat1.Models.UserModel;
import com.bhola.realvideochat1.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;
    String currentUserId;


    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context, String currentUserId) {
        super(options);
        this.context = context;
        this.currentUserId = currentUserId;

    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {

        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(currentUserId);

                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                        holder.userName.setText(otherUserModel.getFullname());
                        Picasso.get().load(otherUserModel.getProfilepic()).into(holder.profileUrl);

                        if (lastMessageSentByMe) {
                            holder.lastMessage.setText("You : " + model.getLastMessage());
                        } else {
                            holder.lastMessage.setText(model.getLastMessage());
                        }
                        holder.messageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                        holder.chatItemClick.setOnClickListener(v -> {
                            //navigate to chat activity
                            String userModelJson = new Gson().toJson(otherUserModel); // Using Google's Gson library for JSON serialization
                            Intent intent = new Intent(context, ChatScreen_User.class);
                            intent.putExtra("userModelJson", userModelJson);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                        });

                    }
                });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_items_recyclerview, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        TextView userName, lastMessage, messageTime, messageCount;
        CircleImageView profileUrl;
        LinearLayout chatItemClick;


        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            messageTime = itemView.findViewById(R.id.messageTime);
            messageCount = itemView.findViewById(R.id.messageCount);
            profileUrl = itemView.findViewById(R.id.profileUrl);
            chatItemClick = itemView.findViewById(R.id.chatItemClick);
        }
    }
}