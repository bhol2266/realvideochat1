package com.bhola.livevideochat;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Vibrator;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.Collections;


public class CameraActivity extends Activity {

    VideoView videoView;


    MediaPlayer mediaPlayer;
    MediaPlayer mediaPlayerRingtone;
    TextView messageTextView;

    //Camera stuffs
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 123;
    private TextureView textureView;
    CameraManager cameraManager;
    Size previewSize;
    String cameraId;
    Handler backgroundHandler;
    HandlerThread handlerThread;
    CameraDevice cameraDevice;
    CameraCaptureSession cameraCaptureSession;
    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevicee) {
            cameraDevice = cameraDevicee;
            try {
                createPreviewSession();
            } catch (CameraAccessException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
            CameraActivity.this.cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            CameraActivity.this.cameraDevice = null;
        }
    };
    String currentCameraSide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        fullscreenMode();

        textureView = findViewById(R.id.textureView);
        playRinging();
        dragCamera();
        controlCamera();

    }

    private void playRinging() {
        messageTextView = findViewById(R.id.message);
        mediaPlayerRingtone = MediaPlayer.create(CameraActivity.this, R.raw.ringback_tone);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                messageTextView.setText("ringing...");
                mediaPlayerRingtone.start();
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                messageTextView.setVisibility(View.GONE);
                LinearLayout controlsLayout = findViewById(R.id.controlsLayout);
                ImageView warningSign = findViewById(R.id.warningSign);
                VideoView videoView = findViewById(R.id.videoView);
                videoView.setVisibility(View.VISIBLE);
                warningSign.setVisibility(View.VISIBLE);
                controlsLayout.setVisibility(View.VISIBLE);
                mediaPlayerRingtone.stop();
                playVideoinTheBackground();


// Set desired width and height in dp
                int desiredWidthDp = 150;
                int desiredHeightDp = 250;
// Convert dp values to pixels
                float scale = getResources().getDisplayMetrics().density;
                int desiredWidthPx = (int) (desiredWidthDp * scale + 0.5f);
                int desiredHeightPx = (int) (desiredHeightDp * scale + 0.5f);
// Get the layout parameters of the TextureView
                ViewGroup.LayoutParams layoutParams = textureView.getLayoutParams();

// Set the width and height
                layoutParams.width = desiredWidthPx;
                layoutParams.height = desiredHeightPx;

// Apply the updated layout parameters to the TextureView
                textureView.setLayoutParams(layoutParams);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAGG", "textureView.getWidth(): " + textureView.getWidth());
                        Log.d("TAGG", "textureView.getHeight(): " + textureView.getHeight());
                        closeCamera();
                        setUpCamera(currentCameraSide);
                        try {
                            openCamera();
                        } catch (CameraAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, 200);



            }
        }, 8000);


    }


    private void playVideoinTheBackground() {
        videoView = findViewById(R.id.videoView);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.sample_video;
        Uri videoUri = Uri.parse(videoPath);
        videoView.setVideoURI(videoUri);
        videoView.start();


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer = mp;
                mp.setVolume(0f, 0f);
                mp.setLooping(true);
            }
        });
    }

    private void fullscreenMode() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }


    private void setUpCamera(String cameraSide) {
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);

                if (cameraSide.equals("Front")) {
                    currentCameraSide = "Front";
                    if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraMetadata.LENS_FACING_FRONT) {
                        StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(
                                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                        previewSize = streamConfigurationMap.getOutputSizes(SurfaceTexture.class)[0];
                        previewSize = chooseOptimalSize(streamConfigurationMap.getOutputSizes(SurfaceTexture.class), textureView.getWidth(), textureView.getHeight());
                        this.cameraId = cameraId;
                    }
                } else {
                    currentCameraSide = "Back";
                    if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraMetadata.LENS_FACING_BACK) {
                        StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(
                                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                        previewSize = streamConfigurationMap.getOutputSizes(SurfaceTexture.class)[0];
                        previewSize = chooseOptimalSize(streamConfigurationMap.getOutputSizes(SurfaceTexture.class), textureView.getWidth(), textureView.getHeight());
                        this.cameraId = cameraId;
                    }
                }

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() throws CameraAccessException {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(cameraId, stateCallback, backgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


    }

    private void openBackgroundHandler() {
        handlerThread = new HandlerThread("camera_app");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
    }

    private void closeBackgroundHandler() {
        if (handlerThread != null) {
            handlerThread.quitSafely();
            handlerThread = null;
        }
        backgroundHandler = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                openBackgroundHandler();
                setUpCamera("Front");
                try {
                    openCamera();
                } catch (CameraAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeBackgroundHandler();
    }


    private void closeCamera() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    private void createPreviewSession() throws CameraAccessException {

        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);
        CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        captureRequestBuilder.addTarget(previewSurface);
        cameraDevice.createCaptureSession(Collections.singletonList(previewSurface), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                if (cameraDevice == null) {
                    return;
                }
                try {
                    CaptureRequest captureRequest = captureRequestBuilder.build();
                    CameraActivity.this.cameraCaptureSession = cameraCaptureSession;
                    cameraCaptureSession.setRepeatingRequest(captureRequest,
                            null, backgroundHandler);
                } catch (CameraAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

            }
        }, backgroundHandler);
    }


    private Size chooseOptimalSize(Size[] outputSizes, int width, int height) {


        double preferredRatio = height / (double) width;
        Size currentOptimalSize = outputSizes[0];
        double currentOptimalRatio = currentOptimalSize.getWidth() / (double) currentOptimalSize.getHeight();
        for (Size currentSize : outputSizes) {
            double currentRatio = currentSize.getWidth() / (double) currentSize.getHeight();
            if (Math.abs(preferredRatio - currentRatio) <
                    Math.abs(preferredRatio - currentOptimalRatio)) {
                currentOptimalSize = currentSize;
                currentOptimalRatio = currentRatio;
            }
        }

        Log.d(SplashScreen.TAG, "chooseOptimalSize: " + outputSizes[1]);
        Log.d(SplashScreen.TAG, "width: " + width);
        Log.d(SplashScreen.TAG, "height: " + height);
        Log.d(SplashScreen.TAG, "currentOptimalSize: " + currentOptimalSize);
        return currentOptimalSize;
    }

    private void dragCamera() {
        View draggableView = findViewById(R.id.draggableView);
        draggableView.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private float startY;
            private float offsetX;
            private float offsetY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Record the initial touch position and view position
                        startX = event.getRawX();
                        startY = event.getRawY();
                        offsetX = view.getX();
                        offsetY = view.getY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        // Calculate the new position based on the touch offset
                        float newX = offsetX + event.getRawX() - startX;
                        float newY = offsetY + event.getRawY() - startY;

                        // Update the view's position
                        view.setX(newX);
                        view.setY(newY);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void controlCamera() {
        ImageView speaker = findViewById(R.id.speaker);
        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final Bitmap currentImage = ((BitmapDrawable) speaker.getDrawable()).getBitmap();
                Drawable myDrawable = getResources().getDrawable(R.drawable.speaker);
                final Bitmap speaker_on = ((BitmapDrawable) myDrawable).getBitmap();

                if (currentImage.sameAs(speaker_on)) {
                    mediaPlayer.setVolume(0f, 0f);
                    speaker.setImageResource(R.drawable.speaker_off);
                } else {


                    mediaPlayer.setVolume(1f, 1f);
                    speaker.setImageResource(R.drawable.speaker);

                }


            }
        });
        ImageView microphone = findViewById(R.id.microphone);
        microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Bitmap currentImage = ((BitmapDrawable) microphone.getDrawable()).getBitmap();
                Drawable myDrawable = getResources().getDrawable(R.drawable.microphone);
                final Bitmap microphone_on = ((BitmapDrawable) myDrawable).getBitmap();

                if (currentImage.sameAs(microphone_on)) {
                    microphone.setImageResource(R.drawable.microphone_off);
                } else {
                    microphone.setImageResource(R.drawable.microphone);
                }

            }
        });
        ImageView camera = findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
                closeCamera();
                if (currentCameraSide.equals("Front")) {
                    setUpCamera("Back");
                } else {
                    setUpCamera("Front");
                }
                try {
                    openCamera();
                } catch (CameraAccessException e) {
                    throw new RuntimeException(e);
                }


            }
        });


        ImageView end_call = findViewById(R.id.end_call);
        end_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayerRingtone.stop();
                onBackPressed();
            }
        });

    }


}
