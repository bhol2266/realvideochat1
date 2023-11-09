package com.bhola.realvideochat1;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bhola.realvideochat1.Models.CallogModel;
import com.bhola.realvideochat1.Models.CallroomModel;
import com.bhola.realvideochat1.Models.StreamerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FirebaseUtil {

    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn() {
        if (currentUserId() != null) {
            return true;
        }
        return false;
    }

    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("Users").document(currentUserId());
    }

    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("Users");
    }

    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("Chatrooms").document(chatroomId);
    }

    public static DocumentReference getCallroomReference(String callroomId) {
        return FirebaseFirestore.getInstance().collection("Call_logs").document(callroomId);
    }

    public static DocumentReference getStreamersReference(String streamerId) {
        return FirebaseFirestore.getInstance().collection("Streamers").document(streamerId);
    }



    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("Chats");
    }

    public static CollectionReference getCallroomLogsReference(String callroomId) {
        return getCallroomReference(callroomId).collection("Logs");
    }

    public static CollectionReference getStreamersLogsReference(String streamerId) {
        return getStreamersReference(streamerId).collection("Logs");
    }

    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("Chatrooms");
    }

    public static CollectionReference allCallogsCollectionReference() {
        return FirebaseFirestore.getInstance().collection("Call_logs");
    }


    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds.get(0).equals(String.valueOf(SplashScreen.userModel.getUserId()))) {
            return allUserCollectionReference().document(userIds.get(1));
        } else {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static DocumentReference getOtherUserFromCallroom(List<String> userIds) {
        if (userIds.get(0).equals(String.valueOf(SplashScreen.userModel.getUserId()))) {
            Log.d("onBindViewHolder", "1: " + userIds.get(1));
            return FirebaseFirestore.getInstance().collection("Users").document(userIds.get(1));

        } else {
            Log.d("onBindViewHolder", "0: " + userIds.get(0));
            return FirebaseFirestore.getInstance().collection("Users").document(userIds.get(0));
        }

    }

    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    public static String getTimeStampFormat(Timestamp timestamp) {


        Date date = timestamp.toDate();

        // Create a SimpleDateFormat object with the desired pattern
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm MMM dd", Locale.ENGLISH);

        // Format the Date using the SimpleDateFormat
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }


    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    public static void getOrCreateChatroomModel(String otherUserId) {

        String callRoomId = FirebaseUtil.getChatroomId(String.valueOf(SplashScreen.userModel.getUserId()), otherUserId);

        FirebaseUtil.getCallroomReference(callRoomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                CallroomModel callroomModel = task.getResult().toObject(CallroomModel.class);
                if (callroomModel == null) {
                    //first time chat
                    callroomModel = new CallroomModel(
                            callRoomId,
                            Arrays.asList(String.valueOf(SplashScreen.userModel.getUserId()), otherUserId),
                            Timestamp.now(),
                            "outgoing", otherUserId, 0);
                    FirebaseUtil.getCallroomReference(callRoomId).set(callroomModel);
                }
            }
        });
    }


    public static void addDocumentToCallLog(CallogModel callogModel) {

        String callRoomId = FirebaseUtil.getChatroomId(String.valueOf(SplashScreen.userModel.getUserId()), callogModel.getCalleeId());


        CallroomModel callroomModel = new CallroomModel();
        callroomModel.setCallroomId(callRoomId);
        callroomModel.setLastCalleeId(callogModel.getCalleeId());
        callroomModel.setLastCallTimestamp(callogModel.getCallTimestamp());
        callroomModel.setLastcallType(callogModel.getCallType());
        callroomModel.setLastcallDuration(callogModel.getCallDurationSeconds());
        callroomModel.setUserIds(Arrays.asList(String.valueOf(SplashScreen.userModel.getUserId()), callogModel.getCalleeId()));

        FirebaseUtil.getCallroomReference(callRoomId).set(callroomModel);


        FirebaseUtil.getCallroomLogsReference(callRoomId).add(callogModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
//                            Log.d("addDocumentToCallLog", "isSuccessful: "+String.valueOf(SplashScreen.userModel.getUserId()) +" ::"+ callogModel.getCalleeId());
                        }
                    }
                });
    }

    public static void decreaseUserCoins(int decreaseNumber) {

        int currentCoin = SplashScreen.userModel.getCoins();
        int coinsAfterDecreased = currentCoin - decreaseNumber;
        SplashScreen.userModel.setCoins(coinsAfterDecreased);
        Log.d("coinsAfterDecreased", "coinsAfterDecreased: "+coinsAfterDecreased);
        Fragment_UserProfile.coins.setText(String.valueOf("Coins: " + SplashScreen.userModel.getCoins()));
        updateUserCoinsonFireStore(coinsAfterDecreased);

    }

    public static void addStreamerCoins() {
        if (SplashScreen.isCalleeIdStreamer) {

            StreamerModel streamerModel = new StreamerModel(SplashScreen.calleeId, String.valueOf(SplashScreen.userModel.getUserId()), Timestamp.now(), 100);

            FirebaseUtil.getStreamersLogsReference(SplashScreen.calleeId).add(streamerModel)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {

                            }
                        }
                    });


        }
    }

    public static void addUserCoins(int newCoins) {

        int currentCoin = SplashScreen.userModel.getCoins();
        int coinsAfterAdding = currentCoin + newCoins;
        SplashScreen.userModel.setCoins(coinsAfterAdding);
        Fragment_UserProfile.coins.setText(String.valueOf("Coins: " + SplashScreen.userModel.getCoins()));
        updateUserCoinsonFireStore(coinsAfterAdding);



    }

    public static void updateUserCoinsonFireStore(int value) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("Users");
        String userId = String.valueOf(SplashScreen.userModel.getUserId()); // Replace with the actual user ID
        DocumentReference userDocRef = usersRef.document(userId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("coins", value);

        userDocRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    // The field(s) were successfully updated
                })
                .addOnFailureListener(e -> {
                    // Handle any errors that might occur during the update
                });

    }

}









