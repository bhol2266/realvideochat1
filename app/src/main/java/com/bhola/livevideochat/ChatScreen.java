package com.bhola.livevideochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatScreen extends AppCompatActivity {
    CircleImageView profile;
    TextView reciverNName;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    CardView sendbtn;
    EditText textmsg;

    String userEmail, reciverRoom;
    RecyclerView recyclerview;
    ArrayList<Message_Modelclass> messagesArrayList;
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);


        messagesArrayList = new ArrayList<>();
        Date date = new Date();
        Message_Modelclass messages1 = new Message_Modelclass("preset", date.getTime(), 2); //viewType 1 is sender 2 is receiver
        messagesArrayList.add(messages1);

        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);
        recyclerview = findViewById(R.id.recylerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerview.setLayoutManager(linearLayoutManager);
        messageAdapter = new MessageAdapter(ChatScreen.this, messagesArrayList);
        recyclerview.setAdapter(messageAdapter);

        if (SplashScreen.userLoggedIAs.equals("Google")) {

            userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            databaseReference = FirebaseDatabase.getInstance().getReference().child(userEmail.replace(".", "_")).child("customer_care");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    messagesArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Message_Modelclass messages = dataSnapshot.getValue(Message_Modelclass.class);
                        messagesArrayList.add(messages);
                    }
                    messageAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = textmsg.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(ChatScreen.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
                    return;
                }
                textmsg.setText("");
                Date date = new Date();
                Message_Modelclass messages = new Message_Modelclass(message, date.getTime(), 1); //viewType 1 is sender 2 is receiver
                if (SplashScreen.userLoggedIAs.equals("Google")) {
                    databaseReference.push().setValue(messages);
                }

                String[] presetsMsg = {"How to be a member?", "Cancel subscription", "Why am I banned", "Report & Complaints"};

                messagesArrayList.add(messages);
                messageAdapter.notifyDataSetChanged();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Message_Modelclass messages1 = new Message_Modelclass("preset", date.getTime(), 2); //viewType 1 is sender 2 is receiver
                        messagesArrayList.add(messages1);
                        linearLayoutManager.smoothScrollToPosition(recyclerview, null, recyclerview.getAdapter().getItemCount() - 1);
                        messageAdapter.notifyDataSetChanged();


                    }
                }, 1000);

                textmsg.setText("");
                linearLayoutManager.smoothScrollToPosition(recyclerview, null, recyclerview.getAdapter().getItemCount() - 1);

            }
        });

    }
}