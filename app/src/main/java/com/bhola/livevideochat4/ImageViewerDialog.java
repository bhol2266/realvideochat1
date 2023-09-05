package com.bhola.livevideochat4;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.viewpager2.widget.ViewPager2;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;


public class ImageViewerDialog extends Dialog {
    private Context context;
    private ArrayList<Map<String, String>> imageUrls;
    private int selectedIndex;
    int screenWidth;
    int screenHeight;

    public ImageViewerDialog(Context context, ArrayList<Map<String, String>> imageUrls, int selectedIndex, int screenWidth, int screenHeight) {
        super(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        this.context = context;
        this.imageUrls = imageUrls;
        this.selectedIndex = selectedIndex;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullscreenMode();
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_image_viewer);

        // Initialize ViewPager and its adapter
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        ImageView cross = findViewById(R.id.cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss(); // Close the dialog
            }
        });
        ImagePagerAdapter pagerAdapter = new ImagePagerAdapter(context, imageUrls, screenWidth, screenHeight);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(selectedIndex);

        // Add any other logic or UI customization as needed
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

}

class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder> {

    private Context context;
    private ArrayList<Map<String, String>> imageUrls;
    private int screenWidth;
    private int screenHeight;


    public ImagePagerAdapter(Context context, ArrayList<Map<String, String>> imageUrls, int screenWidth, int screenHeight) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image2, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        private PhotoView imageView;
        private LinearLayout lock;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            lock = itemView.findViewById(R.id.lock);
        }

        public void bind(int position) {


            Picasso.get()
                    .load(imageUrls.get(position).get("url"))
                    .resize(screenWidth, 0)
                    .into(imageView);

            if (SplashScreen.coins == 0) {
                if (imageUrls.get(position).get("type").equals("premium")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        imageView.setRenderEffect(RenderEffect.createBlurEffect(100, 100, Shader.TileMode.MIRROR));
                    }
                    lock.setVisibility(View.VISIBLE);
                    lock.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(new Intent(context, VipMembership.class));
                        }
                    });
                } else {
                    lock.setVisibility(View.GONE);
                }
            } else {
                lock.setVisibility(View.GONE);
            }
        }
    }

}