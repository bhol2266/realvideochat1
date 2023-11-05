package com.bhola.realvideochat1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bhola.realvideochat1.ZegoCloud.CustomView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.zegocloud.uikit.components.audiovideo.ZegoAvatarViewProvider;
import com.zegocloud.uikit.components.audiovideo.ZegoForegroundViewProvider;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.config.DurationUpdateListener;
import com.zegocloud.uikit.prebuilt.call.config.ZegoCallDurationConfig;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoCallInvitationData;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallConfigProvider;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ClickListener;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallUser;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.ArrayList;
import java.util.List;


public class ZegoCloudActivity extends AppCompatActivity {

    private String userName;
    private SharedPreferences sp;

    private ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zego_cloud);

        sp = getSharedPreferences("offline", Context.MODE_PRIVATE);

        TextView yourUserID = findViewById(R.id.your_user_id);
        String userID = String.valueOf(SplashScreen.userModel.getUserId());

        yourUserID.setText("Your User ID :" + userID);

        initCallInviteService();

        initVoiceButton();

        initVideoButton();



    }



    private void initVideoButton() {
        ZegoSendCallInvitationButton newVideoCall = findViewById(R.id.new_video_call);
        newVideoCall.setIsVideoCall(true);
        //resourceID can be used to specify the ringtone of an offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
        newVideoCall.setResourceID("zegouikit_call");
        newVideoCall.setOnClickListener(v -> {
            TextInputLayout inputLayout = findViewById(R.id.target_user_id);
            String targetUserID = inputLayout.getEditText().getText().toString();
            String[] split = targetUserID.split(",");
            List<ZegoUIKitUser> users = new ArrayList<>();
            for (String userID : split) {
                String userName = userID + "_name";
                users.add(new ZegoUIKitUser(userID, userName));
            }
            newVideoCall.setInvitees(users);
        });
    }

    private void initVoiceButton() {
        ZegoSendCallInvitationButton newVoiceCall = findViewById(R.id.new_voice_call);
        newVoiceCall.setIsVideoCall(false);
        //resourceID can be used to specify the ringtone of an offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
        newVoiceCall.setResourceID("zegouikit_call");
        newVoiceCall.setOnClickListener(v -> {
            TextInputLayout inputLayout = findViewById(R.id.target_user_id);
            String targetUserID = inputLayout.getEditText().getText().toString();
            String[] split = targetUserID.split(",");
            List<ZegoUIKitUser> users = new ArrayList<>();
            for (String userID : split) {
                String userName = userID + "_name";
                users.add(new ZegoUIKitUser(userID, userName));
            }
            newVoiceCall.setInvitees(users);
        });

        newVoiceCall.setOnClickListener(new ClickListener() {
            @Override
            public void onClick(int errorCode, String errorMessage, List<ZegoCallUser> errorInvitees) {

            }
        });
    }

    public void initCallInviteService() {
        long appID = 1889863973;
        String appSign = "71d102ab65161df9f2d2ef11fa98dad3b87513f0df1673892e644cef90b96e75";

        String userID = String.valueOf(SplashScreen.userModel.getUserId());
        userName = SplashScreen.userModel.getFullname();

        callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
        callInvitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true;


        callInvitationConfig.provider = new ZegoUIKitPrebuiltCallConfigProvider() {
            @Override
            public ZegoUIKitPrebuiltCallConfig requireConfig(ZegoCallInvitationData invitationData) {
                ZegoUIKitPrebuiltCallConfig config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall();
                config.avatarViewProvider = new ZegoAvatarViewProvider() {
                    @Override
                    public View onUserIDUpdated(ViewGroup parent, ZegoUIKitUser uiKitUser) {
                        ImageView imageView = new ImageView(parent.getContext());

                        String avatarUrl = SplashScreen.databaseURL_images+ "RealVideoChat1/profilePic/"+String.valueOf(uiKitUser.userID)+".jpg";
                        Log.d("SpaceError", "onUserIDUpdated: "+avatarUrl);

                        if (!TextUtils.isEmpty(avatarUrl)) {
                            RequestOptions requestOptions = new RequestOptions().circleCrop();
                            Glide.with(parent.getContext()).load(avatarUrl).apply(requestOptions).into(imageView);
                        }
                        return imageView;
                    }
                };


                config.durationConfig = new ZegoCallDurationConfig();
                config.durationConfig.isVisible = true;
                config.durationConfig.durationUpdateListener = new DurationUpdateListener() {
                    @Override
                    public void onDurationUpdate(long seconds) {
                        Log.d("onDurationUpdate", "onDurationUpdate() called with: seconds = [" + seconds + "]");
                        if (seconds == 60 * 5) {
                            ZegoUIKitPrebuiltCallInvitationService.endCall();
                        }
                    }
                };

                // Modify your custom calling configurations here.
                config.audioVideoViewConfig.videoViewForegroundViewProvider = (ZegoForegroundViewProvider) (parent, uiKitUser) -> {
                    CustomView customView = new CustomView(parent.getContext(), uiKitUser.userID);
                    return customView;
                };


                return config;
            }
        };
        //This property needs to be set when you are building an Android app and when the notifyWhenAppRunningInBackgroundOrQuit is true.
        //androidNotificationConfig.channelID must be the same as the FCM Channel ID in [ZEGOCLOUD Admin Console|_blank]https://console.zegocloud.com),
        // and the androidNotificationConfig.channelName can be an arbitrary value.
        ZegoNotificationConfig notificationConfig = new ZegoNotificationConfig();
        notificationConfig.sound = "zego_uikit_sound_call";
        notificationConfig.channelID = "CallInvitation";
        notificationConfig.channelName = "CallInvitation";
        callInvitationConfig.notificationConfig = notificationConfig;
        ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, userName,
                callInvitationConfig);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }

}