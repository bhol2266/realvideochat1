package com.bhola.realvideochat1.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bhola.realvideochat1.FirebaseUtil;
import com.bhola.realvideochat1.Models.CallroomModel;
import com.bhola.realvideochat1.Models.UserModel;
import com.bhola.realvideochat1.Profile;
import com.bhola.realvideochat1.R;
import com.bhola.realvideochat1.SplashScreen;
import com.bhola.realvideochat1.VipMembership;
import com.bhola.realvideochat1.ZegoCloud.ZegoCloud_Utils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentCallRecyclerAdapter extends FirestoreRecyclerAdapter<CallroomModel, RecentCallRecyclerAdapter.CallogViewHolder> {

    Context context;
    String currentUserId;
    TextView noCallsTextView;

    public RecentCallRecyclerAdapter(@NonNull FirestoreRecyclerOptions<CallroomModel> options, Context context, String currentUserId, TextView noCallsTextView) {
        super(options);
        this.context = context;
        this.currentUserId = currentUserId;
        this.noCallsTextView = noCallsTextView;

    }

    @Override
    protected void onBindViewHolder(@NonNull CallogViewHolder holder, int position, @NonNull CallroomModel model) {

        noCallsTextView.setVisibility(View.GONE);

        FirebaseUtil.getOtherUserFromCallroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean lastCalledByMe = !model.getLastCalleeId().equals(currentUserId);


                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                        if (otherUserModel.getProfilepic().isEmpty()) {
                            if (otherUserModel.getSelectedGender().equals("male")) {
                                holder.profileImage.setImageResource(R.drawable.male_logo);
                            } else {
                                holder.profileImage.setImageResource(R.drawable.female_logo);
                            }
                        } else {
                            Picasso.get().load(otherUserModel.getProfilepic()).into(holder.profileImage);
                        }
                        holder.profileImage.setOnClickListener(v->{
                            String userModelJson = new Gson().toJson(otherUserModel); // Using Google's Gson library for JSON serialization
                            Intent intent = new Intent(context, Profile.class);
                            intent.putExtra("userModelJson", userModelJson);
                            context.startActivity(intent);
                        });
                        holder.name.setText(otherUserModel.getFullname());
                        holder.timestamp.setText(FirebaseUtil.getTimeStampFormat(model.getLastCallTimestamp()));

                        holder.chatCardView.setOnClickListener(view -> {
                            if(SplashScreen.userModel.getCoins()<100){
                                rechargeDialog(view.getContext());
                                return;
                            }
                            new ZegoCloud_Utils().initVoiceButton(otherUserModel.getFullname(), String.valueOf(otherUserModel.getUserId()), holder.newVoiceCall);
                            holder.newVoiceCall.performClick();
                            SplashScreen.calleeId=String.valueOf(otherUserModel.getUserId());
                            if (otherUserModel.isStreamer()) {
                                SplashScreen.isCalleeIdStreamer = true;
                            } else {
                                SplashScreen.isCalleeIdStreamer = false;
                            }

                        });
                        holder.videocall.setOnClickListener(view -> {
                            if(SplashScreen.userModel.getCoins()<100){
                                rechargeDialog(view.getContext());
                                return;

                            }
                            new ZegoCloud_Utils().initVideoButton(otherUserModel.getFullname(), String.valueOf(otherUserModel.getUserId()), holder.newVideoCall);
                            holder.newVideoCall.performClick();
                            SplashScreen.calleeId=String.valueOf(otherUserModel.getUserId());
                            if (otherUserModel.isStreamer()) {
                                SplashScreen.isCalleeIdStreamer = true;
                            } else {
                                SplashScreen.isCalleeIdStreamer = false;
                            }

                        });

                    }
                });


    }

    @NonNull
    @Override
    public CallogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.callog_recycler_item, parent, false);
        return new CallogViewHolder(view);
    }

    public static void rechargeDialog(Context context) {

        AlertDialog recharge_dialog = null;

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View promptView = inflater.inflate(R.layout.dialog_recharge, null);
        builder.setView(promptView);
        builder.setCancelable(true);

        TextView recharge = promptView.findViewById(R.id.recharge);
        TextView cancel = promptView.findViewById(R.id.cancel);


        recharge_dialog = builder.create();
        recharge_dialog.show();


        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, VipMembership.class));
            }
        });

        AlertDialog finalRecharge_dialog = recharge_dialog;
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalRecharge_dialog.dismiss();
            }
        });

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        recharge_dialog.getWindow().setBackgroundDrawable(inset);

    }


    class CallogViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView name, timestamp;
        ImageView calltypeIcon;
        ZegoSendCallInvitationButton newVoiceCall;
        ZegoSendCallInvitationButton newVideoCall;
        CardView chatCardView;
        ImageView videocall;
//        LinearLayout chatItemClick;


        public CallogViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            name = itemView.findViewById(R.id.name);
            timestamp = itemView.findViewById(R.id.timestamp);
            calltypeIcon = itemView.findViewById(R.id.calltypeIcon);
            newVoiceCall = itemView.findViewById(R.id.new_voice_call);
            newVideoCall = itemView.findViewById(R.id.new_video_call);
            chatCardView = itemView.findViewById(R.id.chatCardView);
            videocall = itemView.findViewById(R.id.videocall);


        }
    }
}
