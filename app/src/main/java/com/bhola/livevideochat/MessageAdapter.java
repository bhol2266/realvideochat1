

package com.bhola.livevideochat;


import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Message_Modelclass> messagesAdpterArrayList;
    int SENDER = 1;
    int RECEIVER = 2;
    int RECEIVER_Template = 3;
    RecyclerView recyclerview;
    DatabaseReference chatRef;

    public MessageAdapter(Context context, ArrayList<Message_Modelclass> messagesAdpterArrayList, RecyclerView recyclerview, DatabaseReference chatRef) {
        this.context = context;
        this.messagesAdpterArrayList = messagesAdpterArrayList;
        this.recyclerview = recyclerview;
        this.chatRef = chatRef;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new SenderVierwHolder(view);
        } else if (viewType == RECEIVER_Template) {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_template_layout, parent, false);
            return new ReciverTemplateViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout, parent, false);
            return new ReciverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message_Modelclass messages = messagesAdpterArrayList.get(position);

        Date date = new Date(messages.getTimeStamp());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault());
        String formattedDate = sdf.format(date);

        Log.d(SplashScreen.TAG, "onBindViewHolder: " + messages.getMessage() + "  : " + position);

        if (messages.getViewType() == 1) {
            SenderVierwHolder senderVierwHolder = (MessageAdapter.SenderVierwHolder) holder;
            senderVierwHolder.msgtxt.setText(messages.getMessage());
            senderVierwHolder.timeStamp.setText(formattedDate);


            if (SplashScreen.userLoggedIAs.equals("Google")) {
                SharedPreferences sh = context.getSharedPreferences("UserInfo", MODE_PRIVATE);
                String urll = sh.getString("photoUrl", "not set");
                Picasso.get()
                        .load(urll)
                        .into(senderVierwHolder.profile);
            }

        } else if (messages.getViewType() == 3) {
            ReciverTemplateViewHolder reciverTemplateViewHolder = (ReciverTemplateViewHolder) holder;


            reciverTemplateViewHolder.msgClick0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    optionClicked(reciverTemplateViewHolder.msgClick0.getText(), "Click[Profile]->[Membership center], and then purchase the corresponding membership package as needed");

                }
            });
            reciverTemplateViewHolder.msgClick1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    optionClicked(reciverTemplateViewHolder.msgClick1.getText(), "You can unsubscribe in Google Pay if necessary");

                }
            });
            reciverTemplateViewHolder.msgClick2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    optionClicked(reciverTemplateViewHolder.msgClick2.getText(), "You can check the most common reasons to be banned:\na) creating fake account\nb) sending in appropriate texts or picture\nc) asking others for money\nd)teenagers under 18: According to the rules and laws, this app could only be used by adult over 18 years old\\ne) selling or recommending other websites, or introducing users to other sites\nf) a user will be banned when we receive multiple reports from other users");

                }
            });

            reciverTemplateViewHolder.msgClick3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionClicked(reciverTemplateViewHolder.msgClick3.getText(), "In order to solve your problem, we need you to provide more detailed information, please click here to submit feedback.");

                }
            });
        } else {
            ReciverViewHolder reciverViewHolder = (MessageAdapter.ReciverViewHolder) holder;
            reciverViewHolder.msgtxt.setText(messages.getMessage());

            if (messages.getMessage().equals("In order to solve your problem, we need you to provide more detailed information, please click here to submit feedback.")) {
                String fullText =messages.getMessage();
                int startIndex = fullText.indexOf("please");
                int endIndex = startIndex + "feedback".length()+29;
                SpannableString spannableString = new SpannableString(fullText);
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        // Handle click event here
                        context.startActivity(new Intent(context,Feedback.class));
                    }
                };

                spannableString.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Set the modified SpannableString to the TextView
                reciverViewHolder.msgtxt.setText(spannableString);
                reciverViewHolder.msgtxt.setMovementMethod(LinkMovementMethod.getInstance());

            }


        }
    }


    private void optionClicked(CharSequence text, String message) {
        Date date = new Date();
        Message_Modelclass messages = new Message_Modelclass((String) text, date.getTime(), 1); //viewType 1 is sender 2 is receiver
        messagesAdpterArrayList.add(messages);
        insertToDataBase(messages);
        recyclerview.scrollToPosition(messagesAdpterArrayList.size() - 1);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Date date = new Date();
                Message_Modelclass messages = new Message_Modelclass(message, date.getTime(), 2); //viewType 1 is sender 2 is receiver
                messagesAdpterArrayList.add(messages);
                insertToDataBase(messages);
                recyclerview.scrollToPosition(messagesAdpterArrayList.size() - 1);

            }
        }, 1000);

    }


    private void insertToDataBase(Message_Modelclass messages) {
        if (SplashScreen.userLoggedIAs.equals("Google")) {

            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            chatRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userEmail.replace(".", "_")).child("customer_care");

            chatRef.push().setValue(messages, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        // Value was successfully written to the database
                        // Add your success logic here
                        Log.d(SplashScreen.TAG, "Data saved successfully.: ");
                    } else {
                        // Failed to write value to the database
                        // Add your failure logic here
                        Log.d(SplashScreen.TAG, "Data saved Failed.: " + databaseError.getMessage());

                    }
                }
            });
        } else {
            sharedPreferences();
        }

    }

    private void sharedPreferences() {

        Gson gson = new Gson();
        String json = gson.toJson(messagesAdpterArrayList);

        SharedPreferences preferences = context.getSharedPreferences("customer_care_chats", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("messagesArrayList", json);
        editor.apply();


    }

    @Override
    public int getItemCount() {
        return messagesAdpterArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message_Modelclass messages = messagesAdpterArrayList.get(position);
        if (messages.getViewType() == 1) {
            return SENDER;
        } else if (messages.getViewType() == 3) {
            return RECEIVER_Template;
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
        TextView msgtxt;


        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            msgtxt = itemView.findViewById(R.id.message);


        }
    }

    static class ReciverTemplateViewHolder extends RecyclerView.ViewHolder {

        TextView msgClick0, msgClick1, msgClick2, msgClick3;

        public ReciverTemplateViewHolder(@NonNull View itemView) {
            super(itemView);
            msgClick0 = itemView.findViewById(R.id.msgClick0);
            msgClick1 = itemView.findViewById(R.id.msgClick1);
            msgClick2 = itemView.findViewById(R.id.msgClick2);
            msgClick3 = itemView.findViewById(R.id.msgClick3);

        }
    }

}
