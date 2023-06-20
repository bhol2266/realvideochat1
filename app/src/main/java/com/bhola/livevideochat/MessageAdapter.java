

package com.bhola.livevideochat;


import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
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

    public MessageAdapter(Context context, ArrayList<Message_Modelclass> messagesAdpterArrayList) {
        this.context = context;
        this.messagesAdpterArrayList = messagesAdpterArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new SenderVierwHolder(view);
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


        } else {
            ReciverViewHolder reciverViewHolder = (MessageAdapter.ReciverViewHolder) holder;
            reciverViewHolder.timeStamp.setText(formattedDate);

            if (messages.getMessage().equals("preset")) {
                reciverViewHolder.msgClick0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Date date = new Date();
                        Message_Modelclass messages = new Message_Modelclass((String) reciverViewHolder.msgClick0.getText(), date.getTime(), 1); //viewType 1 is sender 2 is receiver
                        messagesAdpterArrayList.add(messages);
                        notifyDataSetChanged();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Date date = new Date();
                                Message_Modelclass messages = new Message_Modelclass("Click[Profile]->[Membership center], and then purchase the corresponding membership package as needed", date.getTime(), 2); //viewType 1 is sender 2 is receiver
                                messagesAdpterArrayList.add(messages);
                                notifyDataSetChanged();
                            }
                        },1000);



                    }
                });
                reciverViewHolder.msgClick1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Date date = new Date();
                        Message_Modelclass messages = new Message_Modelclass((String) reciverViewHolder.msgClick1.getText(), date.getTime(), 1); //viewType 1 is sender 2 is receiver
                        messagesAdpterArrayList.add(messages);
                        notifyDataSetChanged();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Date date = new Date();
                                Message_Modelclass messages = new Message_Modelclass("You can unsubscribe in Google Pay if necessary", date.getTime(), 2); //viewType 1 is sender 2 is receiver
                                messagesAdpterArrayList.add(messages);
                                notifyDataSetChanged();
                            }
                        },1000);


                    }
                });
                reciverViewHolder.msgClick2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Date date = new Date();
                        Message_Modelclass messages = new Message_Modelclass((String) reciverViewHolder.msgClick2.getText(), date.getTime(), 1); //viewType 1 is sender 2 is receiver
                        messagesAdpterArrayList.add(messages);
                        notifyDataSetChanged();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Date date = new Date();
                                Message_Modelclass messages = new Message_Modelclass("You can unsubscribe in Google Pay if necessary", date.getTime(), 2); //viewType 1 is sender 2 is receiver
                                messagesAdpterArrayList.add(messages);
                                notifyDataSetChanged();
                            }
                        },1000);




                    }
                });

                reciverViewHolder.msgClick3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Date date = new Date();
                        Message_Modelclass messages = new Message_Modelclass((String) reciverViewHolder.msgClick3.getText(), date.getTime(), 1); //viewType 1 is sender 2 is receiver
                        messagesAdpterArrayList.add(messages);
                        notifyDataSetChanged();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Date date = new Date();
                                Message_Modelclass messages = new Message_Modelclass("In order to solve your problem, we need you to provide more detailed information, please <u>click here</u> to submit feedback.", date.getTime(), 2); //viewType 1 is sender 2 is receiver
                                messagesAdpterArrayList.add(messages);
                                notifyDataSetChanged();
                            }
                        },1000);

                    }
                });


            } else {
                reciverViewHolder.msgtxt.setText(messages.getMessage());
                reciverViewHolder.msgClick0.setVisibility(View.GONE);
                reciverViewHolder.msgClick1.setVisibility(View.GONE);
                reciverViewHolder.msgClick2.setVisibility(View.GONE);
                reciverViewHolder.msgClick3.setVisibility(View.GONE);
            }
        }
    }

    public void updateData(ArrayList<Message_Modelclass> newData) {
        messagesAdpterArrayList.clear();
        messagesAdpterArrayList.addAll(newData);
        notifyDataSetChanged();
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
        CircleImageView circleImageView;
        TextView msgtxt;
        TextView msgClick0, msgClick1, msgClick2, msgClick3;
        TextView timeStamp;

        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            msgtxt = itemView.findViewById(R.id.message);
            msgClick0 = itemView.findViewById(R.id.msgClick0);
            msgClick1 = itemView.findViewById(R.id.msgClick1);
            msgClick2 = itemView.findViewById(R.id.msgClick2);
            msgClick3 = itemView.findViewById(R.id.msgClick3);
            timeStamp = itemView.findViewById(R.id.timeStamp);

        }
    }
}
