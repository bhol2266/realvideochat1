package com.bhola.realvideochat1.ZegoCloud;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bhola.realvideochat1.SplashScreen;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

public class ZegoCloud_Utils {


    public void initVideoButton(String userName, String targetUserID, ZegoSendCallInvitationButton newVideoCall) {
        newVideoCall.setIsVideoCall(true);
        //resourceID can be used to specify the ringtone of an offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
        newVideoCall.setResourceID("zegouikit_call");
        newVideoCall.setOnClickListener(v -> {
            List<ZegoUIKitUser> users = new ArrayList<>();
            users.add(new ZegoUIKitUser(targetUserID, userName));

            newVideoCall.setInvitees(users);
        });
    }

    public void initVoiceButton(String userName, String targetUserID, ZegoSendCallInvitationButton newVoiceCall) {
        newVoiceCall.setIsVideoCall(false);
        //resourceID can be used to specify the ringtone of an offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
        newVoiceCall.setResourceID("zegouikit_call");
        newVoiceCall.setOnClickListener(v -> {
            List<ZegoUIKitUser> users = new ArrayList<>();
            users.add(new ZegoUIKitUser(targetUserID, userName));

            newVoiceCall.setInvitees(users);
        });

        newVoiceCall.setOnClickListener(new ClickListener() {
            @Override
            public void onClick(int errorCode, String errorMessage, List<ZegoCallUser> errorInvitees) {

            }
        });
    }




    public void initCallInviteService(Context context, int userId, String fullname) {
        long appID = SplashScreen.Zegocloud_appID;
        String appSign = SplashScreen.Zegocloud_appSign;

        String userID = String.valueOf(userId);
        String userName = fullname;
        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
        callInvitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true;


        callInvitationConfig.provider = new ZegoUIKitPrebuiltCallConfigProvider() {
            @Override
            public ZegoUIKitPrebuiltCallConfig requireConfig(ZegoCallInvitationData invitationData) {
                ZegoUIKitPrebuiltCallConfig config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall();
                config.avatarViewProvider = new ZegoAvatarViewProvider() {
                    @Override
                    public View onUserIDUpdated(ViewGroup parent, ZegoUIKitUser uiKitUser) {
                        ImageView imageView = new ImageView(parent.getContext());

                        String avatarUrl = SplashScreen.databaseURL_images + "RealVideoChat1/profilePic/" + String.valueOf(uiKitUser.userID) + ".jpg";
                        Log.d("SpaceError", "onUserIDUpdated: " + avatarUrl);

                        if (!TextUtils.isEmpty(avatarUrl)) {
                            RequestOptions requestOptions = new RequestOptions().circleCrop();
                            Glide.with(parent.getContext()).load(avatarUrl).apply(requestOptions).into(imageView);
                        }

                        Log.d("onUserIDUpdated", "onUserIDUpdated: "+avatarUrl);
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
        ZegoUIKitPrebuiltCallInvitationService.init((Application) context, appID, appSign, userID, userName,
                callInvitationConfig);


    }


//    public void initChat(Context context) {
//        long appID = 1889863973;
//        String appSign = "71d102ab65161df9f2d2ef11fa98dad3b87513f0df1673892e644cef90b96e75";
//
//        ZIMKit.initWith((Application) context, appID, appSign);
//        // Online notification for the initialization (use the following code if this is needed).
//        ZIMKit.initNotifications();
//
//    }




}
