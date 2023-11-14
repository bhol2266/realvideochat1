package com.bhola.realvideochat1.ZegoCloud;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bhola.realvideochat1.FirebaseUtil;
import com.bhola.realvideochat1.Fragment_Trending;
import com.bhola.realvideochat1.Models.CallogModel;
import com.bhola.realvideochat1.Models.UserModel;
import com.bhola.realvideochat1.SplashScreen;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.Timestamp;
import com.zegocloud.uikit.components.audiovideo.ZegoAvatarViewProvider;
import com.zegocloud.uikit.components.audiovideo.ZegoForegroundViewProvider;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig;
import com.zegocloud.uikit.prebuilt.call.config.DurationUpdateListener;
import com.zegocloud.uikit.prebuilt.call.config.ZegoCallDurationConfig;
import com.zegocloud.uikit.prebuilt.call.config.ZegoHangUpConfirmDialogInfo;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoCallInvitationData;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallConfigProvider;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ClickListener;
import com.zegocloud.uikit.prebuilt.call.invite.internal.IncomingCallButtonListener;
import com.zegocloud.uikit.prebuilt.call.invite.internal.OutgoingCallButtonListener;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallType;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallUser;
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoInvitationCallListener;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                        handleCoinEvents(seconds, context);

                    }
                };


                config.hangUpConfirmDialogInfo = new ZegoHangUpConfirmDialogInfo();
                config.hangUpConfirmDialogInfo.title = "Hangup confirm";
                config.hangUpConfirmDialogInfo.message = "Do you want to end call?";
                config.hangUpConfirmDialogInfo.cancelButtonName = "Cancel";
                config.hangUpConfirmDialogInfo.confirmButtonName = "Confirm";

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
        notificationConfig.sound = "zego_uikit_call";
        notificationConfig.channelID = "CallInvitation";
        notificationConfig.channelName = "CallInvitation";
        callInvitationConfig.notificationConfig = notificationConfig;
        ZegoUIKitPrebuiltCallInvitationService.init((Application) context, appID, appSign, userID, userName,
                callInvitationConfig);

        invitationListners(context);


    }

    private void handleCoinEvents(long seconds, Context context) {

        if (SplashScreen.isOutgoing) {
            if (!SplashScreen.userModel.isStreamer()) {
                if (seconds != 0 && seconds % 60 == 0) {
                    FirebaseUtil.addStreamerCoins();
                    if (SplashScreen.userModel.getCoins() < 100) {
                        ZegoUIKitPrebuiltCallInvitationService.endCall();
                        Toast.makeText(context, "Coins Finished! Please Recharge", Toast.LENGTH_LONG).show();
                    } else {
                        FirebaseUtil.decreaseUserCoins(100);
                    }
                }

            } else {
                //isStreamer is calling
                if (seconds == 30) {
                    ZegoUIKitPrebuiltCallInvitationService.endCall();
                    Toast.makeText(context, "Free limit 30 sec", Toast.LENGTH_LONG).show();
                }
            }

        } else {
            //inComing call : we dont have to ahandle any incomingcall cases , because everthing is handler from outGoing Call or outgoing devices
        }
    }


    public static void checkUserOnlineStatus(List<UserModel> users, Context context) {


        ArrayList<String> userlist_Online = new ArrayList<>();
        ZegoCloud_Signature zegoCloudSignature = new ZegoCloud_Signature();
        Map<String, Object> data = zegoCloudSignature.getSignature();

        String signature = (String) data.get("signature");
        long unixTimestampSeconds = (long) data.get("timestamp");
        String SignatureNonce = (String) data.get("signatureNonce");

        String API_URL = "https://zim-api.zego.im/?Action=QueryUserOnlineState&AppId=1889863973" + "&Timestamp=" + unixTimestampSeconds + "&Signature=" + signature + "&SignatureVersion=2.0" + "&SignatureNonce=" + SignatureNonce;

        for (UserModel userModel : users) {

            String id = String.valueOf(userModel.getUserId());
            API_URL = API_URL + "&UserId[]=" + id;
        }


        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray results = jsonObject.getJSONArray("Result");

                            for (int i = 0; i < results.length(); i++) {
                                JSONObject jsonObject1 = (JSONObject) results.get(i);
                                String userID = jsonObject1.getString("UserId");
                                String status = jsonObject1.getString("Status");
                                boolean isOnline = false;
                                if (status.equals("online")) {
                                    isOnline = true;
                                }

                                if (isOnline) {
                                    userlist_Online.add(userID);

                                }
                            }
                            Fragment_Trending.Onlineuserslist.clear();
                            for (int i = 0; i < userlist_Online.size(); i++) {
                                for (int j = 0; j < users.size(); j++) {
                                    UserModel userModel = users.get(j);
                                    if (userlist_Online.get(i).equals(String.valueOf(userModel.getUserId()))) {
                                        Fragment_Trending.Onlineuserslist.add(userModel);
                                    }
                                }

                            }
                            Log.d("onResponse", "onResponse: "+ Fragment_Trending.Onlineuserslist.toString());
                            Fragment_Trending.sliderAdapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("onResponse", "JSONException: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("sadfsadf", "onResponse: " + error.getMessage());
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);


    }

    private void invitationListners(Context context) {
        //this
        ZegoUIKitPrebuiltCallInvitationService.addIncomingCallButtonListener(new IncomingCallButtonListener() {
            @Override
            public void onIncomingCallDeclineButtonPressed() {

            }

            @Override
            public void onIncomingCallAcceptButtonPressed() {

            }
        });


        ZegoUIKitPrebuiltCallInvitationService.addOutgoingCallButtonListener(new OutgoingCallButtonListener() {
            @Override
            public void onOutgoingCallCancelButtonPressed() {
                CallogModel callogModel = new CallogModel(SplashScreen.calleeId, "outgoing_canceled", Timestamp.now(), false, 0);
                FirebaseUtil.addDocumentToCallLog(callogModel);
            }
        });

        ZegoUIKitPrebuiltCallInvitationService.addInvitationCallListener(new ZegoInvitationCallListener() {
            @Override
            public void onIncomingCallReceived(String callID, ZegoCallUser caller, ZegoCallType callType, List<ZegoCallUser> callees) {
                SplashScreen.isOutgoing = false;
            }

            @Override
            public void onIncomingCallCanceled(String callID, ZegoCallUser caller) {

            }

            @Override
            public void onIncomingCallTimeout(String callID, ZegoCallUser caller) {

            }

            @Override
            public void onOutgoingCallAccepted(String callID, ZegoCallUser callee) {
                CallogModel callogModel = new CallogModel(String.valueOf(callee.getId()), "outgoing_accepted", Timestamp.now(), true, 0);
                FirebaseUtil.addDocumentToCallLog(callogModel);
                SplashScreen.isOutgoing = true;
                if (!SplashScreen.userModel.isStreamer()) {
                    FirebaseUtil.decreaseUserCoins(100);
                }


            }

            @Override
            public void onOutgoingCallRejectedCauseBusy(String callID, ZegoCallUser callee) {
                CallogModel callogModel = new CallogModel(String.valueOf(callee.getId()), "outgoing_busy", Timestamp.now(), false, 0);
                FirebaseUtil.addDocumentToCallLog(callogModel);
            }


            @Override
            public void onOutgoingCallDeclined(String callID, ZegoCallUser callee) {
                CallogModel callogModel = new CallogModel(String.valueOf(callee.getId()), "outgoing_rejected", Timestamp.now(), false, 0);
                FirebaseUtil.addDocumentToCallLog(callogModel);
            }

            @Override
            public void onOutgoingCallTimeout(String callID, List<ZegoCallUser> callee) {
                CallogModel callogModel = new CallogModel(String.valueOf(callee.get(0).getId()), "outgoing_canceled", Timestamp.now(), false, 0);
                FirebaseUtil.addDocumentToCallLog(callogModel);
            }
        });


    }

    private void requestAnotherVideocall(Context context) {

    }


}
