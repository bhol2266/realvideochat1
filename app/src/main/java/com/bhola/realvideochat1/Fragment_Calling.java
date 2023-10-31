package com.bhola.realvideochat1;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class Fragment_Calling extends Fragment {


    View view;
    Context context;
    String name,profileImage,username;
    MediaPlayer mediaPlayer;
    int videoView_Height;
    ImageView endcall;
    private MediaPlayer mediaPlayer2;
    private Ringtone defaultRingtone;
    Handler mHandler;
    Runnable mRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

      view = inflater.inflate(R.layout.fragment_calling, container, false);
      return  view;

    }

}