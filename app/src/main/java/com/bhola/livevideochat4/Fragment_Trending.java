package com.bhola.livevideochat4;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class Fragment_Trending extends Fragment {

    private RecyclerView recyclerView1, recyclerView2, recyclerView3;
    private ImageAdapter imageAdapter, imageAdapter2, imageAdapter3;
    LinearSmoothScroller smoothScroller1, smoothScroller2, smoothScroller3;
    List<Integer> imageList2;
    private Handler handlerAnimation, blinkhandler, countHandler;
    RelativeLayout btnRelativelayout;
    int randomNumber, current_value;
    TextView onlineCountTextview;
    Context context;
    AlertDialog permissionDialog;


    ActivityResultLauncher<String[]> mPermissionResultLauncher;
    private boolean isCameraPermissionGranted;
    private boolean isMicrophonePermissionGranted;
    private String[] PERMISSIONS;


    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int MICROPHONE_PERMISSION_REQUEST_CODE = 101;


    public Fragment_Trending() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trending, container, false);

        context = getContext();
        // Inflate the layout for context fragment
        Fragment_Hot fragmentA1 = new Fragment_Hot();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentA1, "HOT").addToBackStack(null).commit();

        TextView HotTextview = view.findViewById(R.id.HotTextview);
        HotTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Hot fragment = new Fragment_Hot();
                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "HOT").addToBackStack(null).commit();
            }
        });
        TextView NearbyTextview = view.findViewById(R.id.NearbyTextview);
        NearbyTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_Nearby fragment = new Fragment_Nearby();
                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "NEARBY").addToBackStack(null).commit();
            }
        });


        return view;
    }


    private boolean hasPermissions(Context context, String... PERMISSIONS) {

        if (context != null && PERMISSIONS != null) {

            for (String permission : PERMISSIONS) {

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            boolean permissionGranted = true;
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Camera Permission is granted", Toast.LENGTH_SHORT).show();
            } else {
                permissionGranted = false;
                Toast.makeText(context, "Camera Permission is denied", Toast.LENGTH_SHORT).show();
            }

            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Record Audio is granted", Toast.LENGTH_SHORT).show();
            } else {
                permissionGranted = false;
                Toast.makeText(context, "Record Audio is denied", Toast.LENGTH_SHORT).show();
            }


        }
    }

}


