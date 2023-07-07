package com.bhola.livevideochat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

public class VipMembership extends AppCompatActivity {

    Button btnContinue;
    final int[] selectedCard = {-1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_membership);
        fullscreenMode();

        actionBar();
        addUnderlineTerms_privacy();
        selectCardView();
        btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedCard[0] == -1) {
                    Toast.makeText(VipMembership.this, "Please select any card", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void addUnderlineTerms_privacy() {
        TextView terms = findViewById(R.id.terms);
        TextView privaciy = findViewById(R.id.privacy);
        terms.setPaintFlags(terms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        privaciy.setPaintFlags(privaciy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.terms_service_link)));
                startActivity(intent);
            }
        });
        privaciy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.privacy_policy_link)));
                startActivity(intent);
            }
        });
    }


    private void selectCardView() {
        CardView cardView1, cardView2, cardView3;
        TextView price1, price2, price3;
        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        cardView3 = findViewById(R.id.cardView3);

        price1 = findViewById(R.id.price1);
        price2 = findViewById(R.id.price2);
        price3 = findViewById(R.id.price3);


        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOtherCards_BackgroundWhite(cardView1, cardView2, cardView3, price1, price2, price3);
                int backgroundColor = R.color.themeColor;
                int color = ContextCompat.getColor(VipMembership.this, backgroundColor);
                cardView1.setCardBackgroundColor(color);

                int textColor = Color.parseColor("#FFFFFF"); // Replace "#FF0000" with your desired color
                price1.setTextColor(textColor);
                selectedCard[0] = 0;


            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOtherCards_BackgroundWhite(cardView1, cardView2, cardView3, price1, price2, price3);

                int backgroundColor = R.color.themeColor;
                int color = ContextCompat.getColor(VipMembership.this, backgroundColor);
                cardView2.setCardBackgroundColor(color);

                int textColor = Color.parseColor("#FFFFFF"); // Replace "#FF0000" with your desired color
                price2.setTextColor(textColor);
                selectedCard[0] = 1;


            }
        });

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOtherCards_BackgroundWhite(cardView1, cardView2, cardView3, price1, price2, price3);

                int backgroundColor = R.color.themeColor;
                int color = ContextCompat.getColor(VipMembership.this, backgroundColor);
                cardView3.setCardBackgroundColor(color);

                int textColor = Color.parseColor("#FFFFFF"); // Replace "#FF0000" with your desired color
                price3.setTextColor(textColor);
                selectedCard[0] = 2;


            }
        });


    }

    private void setOtherCards_BackgroundWhite(CardView cardView1, CardView cardView2, CardView cardView3, TextView price1, TextView price2, TextView price3) {
        int backgroundColor = R.color.white;
        int color = ContextCompat.getColor(VipMembership.this, backgroundColor);
        cardView1.setCardBackgroundColor(color);
        cardView2.setCardBackgroundColor(color);
        cardView3.setCardBackgroundColor(color);

        int textColor = Color.parseColor("#000000"); // Replace "#FF0000" with your desired color
        price1.setTextColor(textColor);
        price2.setTextColor(textColor);
        price3.setTextColor(textColor);
    }

    private void fullscreenMode() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    private void actionBar() {
        ImageView back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void exit_dialog() {
        AlertDialog dialog;

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(VipMembership.this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View promptView = inflater.inflate(R.layout.dialog_membership_exit, null);
        builder.setView(promptView);
        builder.setCancelable(true);

        LottieAnimationView lottie = promptView.findViewById(R.id.lottie);
        lottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                lottie.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        dialog = builder.create();
        dialog.show();

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        dialog.getWindow().setBackgroundDrawable(inset);

    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        exit_dialog();
    }
}