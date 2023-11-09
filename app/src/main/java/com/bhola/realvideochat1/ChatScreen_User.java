package com.bhola.realvideochat1;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bhola.realvideochat1.Models.ChatMessageModel;
import com.bhola.realvideochat1.Models.ChatroomModel;
import com.bhola.realvideochat1.Models.GiftItemModel;
import com.bhola.realvideochat1.Models.UserModel;
import com.bhola.realvideochat1.ZegoCloud.ZegoCloud_Utils;
import com.bhola.realvideochat1.adapter.ChatRecyclerAdapter;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatScreen_User extends Activity {


    public static TextView send;
    public static UserModel otherUser;
    RecyclerView recyclerView;
    AlertDialog block_user_dialog = null;
    AlertDialog report_user_dialog = null;
    AlertDialog report_userSucessfully_dialog = null;
    private AudioRecorder audioRecorder;
    private File recordFile;
    String chatroomId;
    ChatroomModel chatroomModel;
    EditText newMessage;
    ChatRecyclerAdapter adapter;
    MediaPlayer mediaPlayer;


    EditText messageInput;
    CardView sendbtnn;
    ImageButton backBtn;
    TextView otherUsername;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        


        init();
        getProfileDetail();
        bottomBtns();
        actionbar();

        getOrCreateChatroomModel();
        setupChatRecyclerView();

        ZegocloudBtns();
    }

    private void ZegocloudBtns() {


        ImageView videoCall = findViewById(R.id.videoCall);
        ImageView voiceCall = findViewById(R.id.voiceCall);

        videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SplashScreen.userModel.getCoins() < 100) {
                    rechargeDialog(view.getContext());
                    return;
                }
                ZegoSendCallInvitationButton newVideoCall = findViewById(R.id.new_video_call);
                new ZegoCloud_Utils().initVideoButton(otherUser.getFullname(), String.valueOf(otherUser.getUserId()), newVideoCall);
                newVideoCall.performClick();
                SplashScreen.calleeId = String.valueOf(otherUser.getUserId());
                if (otherUser.isStreamer()) {
                    SplashScreen.isCalleeIdStreamer = true;
                } else {
                    SplashScreen.isCalleeIdStreamer = false;
                }
                FirebaseUtil.getOrCreateChatroomModel(String.valueOf(otherUser.getUserId()));

            }
        });
        voiceCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SplashScreen.userModel.getCoins() < 100) {
                    rechargeDialog(view.getContext());
                    return;

                }
                ZegoSendCallInvitationButton newVoiceCall = findViewById(R.id.new_voice_call);
                new ZegoCloud_Utils().initVoiceButton(otherUser.getFullname(), String.valueOf(otherUser.getUserId()), newVoiceCall);
                newVoiceCall.performClick();
                SplashScreen.calleeId = String.valueOf(otherUser.getUserId());
                if (otherUser.isStreamer()) {
                    SplashScreen.isCalleeIdStreamer = true;
                } else {
                    SplashScreen.isCalleeIdStreamer = false;
                }
                FirebaseUtil.getOrCreateChatroomModel(String.valueOf(otherUser.getUserId()));

            }
        });

    }


    private void init() {

        recyclerView = findViewById(R.id.chat_recycler_view);
    }

    private void getProfileDetail() {
        String userModelJson = getIntent().getStringExtra("userModelJson");
        otherUser = new Gson().fromJson(userModelJson, UserModel.class); // Using Gson for JSON deserialization

    }

    void getOrCreateChatroomModel() {
        chatroomId = FirebaseUtil.getChatroomId(String.valueOf(SplashScreen.userModel.getUserId()), String.valueOf(otherUser.getUserId()));
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel == null) {
                    //first time chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(String.valueOf(SplashScreen.userModel.getUserId()), String.valueOf(otherUser.getUserId())),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

    private void bottomBtns() {

        EditText newMessage = findViewById(R.id.newMessage);
        newMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int textLength = charSequence.length();
                CardView sendbtnn = findViewById(R.id.sendbtnn);
                RecordButton record_button = findViewById(R.id.record_button);
                if (textLength != 0) {
                    sendbtnn.setVisibility(View.VISIBLE);
                    record_button.setVisibility(View.GONE);
                } else {
                    sendbtnn.setVisibility(View.GONE);
                    record_button.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        CardView sendbtnn = findViewById(R.id.sendbtnn);
        sendbtnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = newMessage.getText().toString();
                if (msg.length() == 0) {
                    return;
                }
                if (SplashScreen.userModel.getCoins() >= 20) {
                    sendMessageToUser(msg, "text", "");
                } else {
                    rechargeDialog(ChatScreen_User.this);
                    return;
                }
//                insertCustomMsginChats(msg, "mimeType/text", "premium"); //this function handles the custom msg from user and updates the userlistTemp and all
                newMessage.setText("");
            }
        });

        ImageView sendImage = findViewById(R.id.sendImage); // this is option for sending extra images
        ImageView lottiegift = findViewById(R.id.lottiegift);

        lottiegift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheetDialog();
            }
        });
        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SplashScreen.userModel.getCoins() >= 20) {
                    loadImageFromGallery();
                } else {
                    rechargeDialog(ChatScreen_User.this);
                }
            }
        });


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted; proceed with audio recording or other functionality.
            handleVoiceMessage();
        } else {
            // Permission is not granted; request it.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 123);
        }
    }

    void setupChatRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(50);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, ChatScreen_User.this, String.valueOf(SplashScreen.userModel.getUserId()), SplashScreen.userModel.getProfilepic(), otherUser.getProfilepic(), mediaPlayer);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessageToUser(String message, String type, String extraMessage) {
        MediaPlayer mediaPlayer = MediaPlayer.create(ChatScreen_User.this, R.raw.msg_sent_sound);
        mediaPlayer.start();

        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(String.valueOf(SplashScreen.userModel.getUserId()));
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);


        ChatMessageModel chatMessageModel = new ChatMessageModel(message, String.valueOf(SplashScreen.userModel.getUserId()), Timestamp.now(), type, extraMessage);
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            EditText newMessage = findViewById(R.id.newMessage);
                            newMessage.setText("");
                            sendNotification(message);
                            if (!SplashScreen.userModel.isStreamer()) {
                                FirebaseUtil.decreaseUserCoins(20);
                            }
                        }
                    }
                });
    }


    private void actionbar() {
        ImageView backArrow = findViewById(R.id.backArrow);
        ImageView warningSign = findViewById(R.id.warningSign);
        ImageView menuDots = findViewById(R.id.menuDots);
        RelativeLayout alertBar = findViewById(R.id.alertBar);
        TextView profileName = findViewById(R.id.profileName);


        profileName.setText(otherUser.getFullname());
        profileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userModelJson = new Gson().toJson(otherUser); // Using Google's Gson library for JSON serialization
                Intent intent = new Intent(view.getContext(), Profile.class);
                intent.putExtra("userModelJson", userModelJson);
                startActivity(intent);
            }
        });


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        warningSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockUserDialog();
            }
        });

        menuDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportUserDialog();
            }
        });

        alertBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertBar.setVisibility(View.INVISIBLE);
            }
        });


    }

    @Override
    public void onBackPressed() {
        // Get the fragment manager
        FragmentManager fragmentManager = ((Activity) ChatScreen_User.this).getFragmentManager();

        // Check if there are any fragments in the back stack
        if (fragmentManager.getBackStackEntryCount() > 0) {
            // If there are fragments in the back stack, pop the top one (close it)
            fragmentManager.popBackStack();
        } else {
            // If there are no fragments in the back stack, perform the default back button behavior
            super.onBackPressed();
        }
    }


    private void blockUserDialog() {

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ChatScreen_User.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View promptView = inflater.inflate(R.layout.dialog_block_user, null);
        builder.setView(promptView);
        builder.setCancelable(true);

        TextView confirm = promptView.findViewById(R.id.confirm);
        TextView cancel = promptView.findViewById(R.id.cancel);


        block_user_dialog = builder.create();
        block_user_dialog.show();


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatScreen_User.this, "User blocked succesfully", Toast.LENGTH_SHORT).show();
                block_user_dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                block_user_dialog.dismiss();
            }
        });

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        block_user_dialog.getWindow().setBackgroundDrawable(inset);

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


    private void reportUserDialog() {

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ChatScreen_User.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View promptView = inflater.inflate(R.layout.dialog_report_user, null);
        builder.setView(promptView);
        builder.setCancelable(true);

        TextView report = promptView.findViewById(R.id.reportBtn);
        ImageView cross = promptView.findViewById(R.id.cross);


        report_user_dialog = builder.create();
        report_user_dialog.show();


        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                report_user_dialog.dismiss();
                reportUserSucessfullDialog();
            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                report_user_dialog.dismiss();
            }
        });


        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        report_user_dialog.getWindow().setBackgroundDrawable(inset);

    }

    private void reportUserSucessfullDialog() {

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ChatScreen_User.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View promptView = inflater.inflate(R.layout.dialog_report_user_sucessfull, null);
        builder.setView(promptView);
        builder.setCancelable(true);

        TextView confirm = promptView.findViewById(R.id.confirm);


        report_userSucessfully_dialog = builder.create();
        report_userSucessfully_dialog.show();


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatScreen_User.this, "User Reported", Toast.LENGTH_SHORT).show();
                report_userSucessfully_dialog.dismiss();
            }
        });


        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        report_userSucessfully_dialog.getWindow().setBackgroundDrawable(inset);

    }


    private void openBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog;

        bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottomsheetdialog_gifts, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        send = view.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rechargeDialog(view.getContext());


            }
        });
        TextView coinCount = view.findViewById(R.id.coin);
        coinCount.setText(String.valueOf(SplashScreen.userModel.getCoins()));
        TextView topup = view.findViewById(R.id.topup);
        topup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatScreen_User.this, VipMembership.class));
            }
        });
        TextView problem = findViewById(R.id.problem);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        String[] items = {"Rose", "Penghua", "TeddyBear", "Ring", "CrystalShoes", "LaserBall", "Crown", "Ferrari", "Motorcycle", "Yacht", "Bieshu", "Castle"};

        List<GiftItemModel> itemList = new ArrayList<>();

        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            int coin = 99 + (i * 100); // Calculate the "coin" value based on the index

            GiftItemModel giftItemModel = new GiftItemModel(item, coin, false);
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("gift", item);
            itemMap.put("coin", coin);

            itemList.add(giftItemModel);
        }

        GiftItemAdapter giftItemAdapter = new GiftItemAdapter(ChatScreen_User.this, itemList);
        GridLayoutManager layoutManager = new GridLayoutManager(ChatScreen_User.this, 4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(giftItemAdapter);

    }


    private void handleVoiceMessage() {

        RecordView recordView = (RecordView) findViewById(R.id.record_view);
        RecordButton recordButton = (RecordButton) findViewById(R.id.record_button);
        audioRecorder = new AudioRecorder();


//IMPORTANT
        recordButton.setRecordView(recordView);
//        recordView.setRecordButtonGrowingAnimationEnabled(false);


        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                ChangeVisiblityMic(true);
                recordFile = new File(getFilesDir(), UUID.randomUUID().toString() + ".3gpp");
                try {
                    audioRecorder.start(recordFile.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                ChangeVisiblityMic(false);
                stopRecording(true);

            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                //Stop Recording..
                ChangeVisiblityMic(false);
                stopRecording(false);

                if (SplashScreen.userModel.getCoins() >= 20) {
                    uploadVoiceMessageFirebase(recordFile);
                } else {
                    rechargeDialog(ChatScreen_User.this);
                }

            }

            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                ChangeVisiblityMic(false);
                stopRecording(true);


            }

            @Override
            public void onLock() {
                //When Lock gets activated
            }

        });


        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                Log.d("RecordView", "Basket Animation Finished");
            }
        });

    }

    private void loadImageFromGallery() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 777);
        }

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 111);

    }


    private void uploadImageToFirebaseStorage(Bitmap bitmap, Uri imageUri) {
        Utils utils = new Utils();
        utils.showLoadingDialog(ChatScreen_User.this, "sending image..");

        int orientation = ImageResizer.getImageOrientation(imageUri, ChatScreen_User.this);

        // Rotate the image to its default orientation
        Bitmap rotatedBitmap = ImageResizer.rotateBitmap(bitmap, orientation);
        //Resize image
        Bitmap redusedBitmap = ImageResizer.reduceBitmapSize(rotatedBitmap, 400000);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        redusedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();


        // Upload the rotated image to Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("Users/" + SplashScreen.userModel.getUserId() + "/chatImages/" + Timestamp.now().toString());

        imageRef.putBytes(data).addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully
            // You can get the download URL of the image
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                sendMessageToUser("[Image]", "image", downloadUrl);
                utils.dismissLoadingDialog();
            });
        }).addOnFailureListener(exception -> {
            // Handle any errors that may occur during the upload
            Toast.makeText(this, "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    private Uri saveImageTOAppDirectory(Uri selectedImageUri) {
        Uri savedImageUri = null;
        try {
            // Create a directory for your app if it doesn't exist.
            File appDirectory = new File(getFilesDir(), "Images");
            if (!appDirectory.exists()) {
                appDirectory.mkdirs();
            }

            // Create a file in your app's directory.
            String fileName = otherUser.getUserId() + System.currentTimeMillis() + ".jpg"; // You can choose any file name.
            File imageFile = new File(appDirectory, fileName);

            // Copy the selected image to your app's directory.
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            OutputStream outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();

            // The image is now saved in your app's directory, and you have its URI.
            savedImageUri = Uri.fromFile(imageFile);

            // You can use 'savedImageUri' as needed.

        } catch (IOException e) {
            e.printStackTrace();
        }
        return savedImageUri;
    }


    private void ChangeVisiblityMic(boolean micOn) {
        CardView edittextCardView = findViewById(R.id.edittextCardView);
        CardView sendbtnn = findViewById(R.id.sendbtnn);
        RecordView record_view = findViewById(R.id.record_view);
        RecordButton record_button = findViewById(R.id.record_button);
        if (micOn) {
            edittextCardView.setVisibility(View.GONE);
            record_view.setVisibility(View.VISIBLE);
        }
        if (!micOn) {
            edittextCardView.setVisibility(View.VISIBLE);
            record_view.setVisibility(View.GONE);
        }
    }

    private void stopRecording(boolean deleteFile) {
        audioRecorder.stop();
        if (recordFile != null && deleteFile) {
            recordFile.delete();
        }
    }


    private void uploadVoiceMessageFirebase(File audioFile) {

        Utils utils = new Utils();
        utils.showLoadingDialog(this, "sending audio..");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {


                // Start the task on the new thread
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("Users/" + SplashScreen.userModel.getUserId() + "/VoiceRecordings/" + SplashScreen.userModel.getUserId() + Timestamp.now().toString());
                Uri audioUri = Uri.fromFile(audioFile);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        insertCustomMsginChats(audioUri.toString(), "mimeType/audio", "premium");
                    }
                });

                storageReference.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot success) {
                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> path) {
                                if (path.isSuccessful()) {
                                    String url = path.getResult().toString();
                                    utils.dismissLoadingDialog();
                                    sendMessageToUser("[Audio]", "audio", url);
                                }
                            }
                        });
                    }
                });
            }
        });

        thread.start();


    }

    private File createTempImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        return imageFile;
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

//            Uri saveFiledURI = saveImageTOAppDirectory(imageUri);
//            insertCustomMsginChats(saveFiledURI.toString(), "mimeType/image", "premium");

            Bitmap bitmap = null;
            try {
                // Open an input stream from the URI and decode it into a Bitmap
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                // Handle exceptions
            }


            uploadImageToFirebaseStorage(bitmap, imageUri);
        }
    }


    void sendNotification(String message) {

        try {
            JSONObject jsonObject = new JSONObject();

            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", SplashScreen.userModel.getFullname());
            notificationObj.put("body", message);

            JSONObject dataObj = new JSONObject();
            dataObj.put("userId", String.valueOf(SplashScreen.userModel.getUserId()));

            jsonObject.put("notification", notificationObj);
            jsonObject.put("data", dataObj);
            jsonObject.put("to", otherUser.getFcmToken());

            callApi(jsonObject);


        } catch (Exception e) {

        }
    }

    void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer " + SplashScreen.fcmAPI_KEY)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("dsfsdfdfgdfsg", "onFailure: " + e.getMessage());
                Toast.makeText(ChatScreen_User.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("dsfsdfdfgdfsg", "Sucess: Sucess" + SplashScreen.fcmAPI_KEY);

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}



