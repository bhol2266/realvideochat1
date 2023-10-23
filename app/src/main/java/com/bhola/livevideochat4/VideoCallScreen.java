package com.bhola.livevideochat4;

import static com.bhola.livevideochat4.SplashScreen.TAG;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputLayout;
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment;
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
import java.util.Random;


public class VideoCallScreen extends AppCompatActivity {

    private String userName;
    private SharedPreferences sp;
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS =
            {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
            };

    private boolean checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_screen);

        sp = getSharedPreferences("offline", Context.MODE_PRIVATE);


        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }

        TextView yourUserID = findViewById(R.id.your_user_id);
        String userID = getUserId();

        yourUserID.setText("Your User ID :" + userID);

        initCallInviteService(userID);

        initVoiceButton();

        initVideoButton();

    }


    private void initVideoButton() {
        ZegoSendCallInvitationButton newVideoCall = findViewById(R.id.new_video_call);
        newVideoCall.setIsVideoCall(true);
        //resourceID can be used to specify the ringtone of an offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
        newVideoCall.setResourceID("zego_uikit_call");
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
        newVoiceCall.setResourceID("zego_uikit_call");
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


    public void initCallInviteService(String generateUserID) {
        long appID = 1889863973;
        String appSign = "71d102ab65161df9f2d2ef11fa98dad3b87513f0df1673892e644cef90b96e75";

        String userID = generateUserID;
        userName = generateUserID + "_" + Build.MANUFACTURER;


        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
        callInvitationConfig.provider = new ZegoUIKitPrebuiltCallConfigProvider() {
            @Override
            public ZegoUIKitPrebuiltCallConfig requireConfig(ZegoCallInvitationData invitationData) {
                ZegoUIKitPrebuiltCallConfig config = null;
                boolean isVideoCall = invitationData.type == ZegoInvitationType.VIDEO_CALL.getValue();
                boolean isGroupCall = invitationData.invitees.size() > 1;
                if (isVideoCall && isGroupCall) {
                    config = ZegoUIKitPrebuiltCallConfig.groupVideoCall();
                } else if (!isVideoCall && isGroupCall) {
                    config = ZegoUIKitPrebuiltCallConfig.groupVoiceCall();
                } else if (!isVideoCall) {
                    config = ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall();
                } else {
                    config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall();
                }
                config.durationConfig = new ZegoCallDurationConfig();
                config.durationConfig.isVisible = true;
                config.durationConfig.durationUpdateListener = new DurationUpdateListener() {
                    @Override
                    public void onDurationUpdate(long seconds) {
                        Log.d(TAG, "onDurationUpdate() called with: seconds = [" + seconds + "]");
                        if (seconds == 60*5) {
                            ZegoUIKitPrebuiltCallInvitationService.endCall();
                        }
                    }
                };
                return config;
            }
        };


        callInvitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true;
        ZegoNotificationConfig notificationConfig = new ZegoNotificationConfig();
        notificationConfig.sound = "zego_uikit_sound_call";
        notificationConfig.channelID = "CallInvitation";
        notificationConfig.channelName = "CallInvitation";
        ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, userName, callInvitationConfig);

    }



    private void saveUserId(String userId) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("userId", userId);
        edit.apply();
    }

    private String getUserId() {
        String spValue = sp.getString("userId", "");
        if (spValue.isEmpty()) {
            return "23154";
        } else {
            return spValue;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }

}