package com.bhola.realvideochat1.ZegoCloud;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.zegocloud.uikit.components.audiovideo.ZegoBaseAudioVideoForegroundView;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.HashMap;

public class CustomView extends ZegoBaseAudioVideoForegroundView {

    public CustomView(@NonNull Context context, String userID) {
        super(context, userID);
    }

    public CustomView(@NonNull Context context, @Nullable AttributeSet attrs,
                          String userID) {
        super(context, attrs, userID);
    }

    protected void onForegroundViewCreated(ZegoUIKitUser uiKitUser) {

        // init your custom view
    }

    protected void onCameraStateChanged(boolean isCameraOn) {
        // will be called when camera changed
    }

    protected void onMicrophoneStateChanged(boolean isMicrophoneOn) {
        // will be called when microphone changed
    }

    protected void onInRoomAttributesUpdated(HashMap<String, String> inRoomAttributes) {
        // will be called when inRoomAttributes changed
    }
}