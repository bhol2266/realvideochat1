package com.bhola.livevideochat4;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class Fill_details extends AppCompatActivity {

    String selectedGender = "";
    EditText nickName;
    String Birthday = "";
    Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_details);

        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nickName.getText().toString().length() > 0 && selectedGender.length() > 0 && Birthday.length() > 0) {

                    SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("nickName", nickName.getText().toString());
                    editor.putString("Gender", selectedGender);
                    editor.putString("Birthday", Birthday);
                    editor.apply();

                    startActivity(new Intent(Fill_details.this, MainActivity.class));
                }
            }
        });
        nickName = findViewById(R.id.nickName);
        nickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnStatus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        TextView dateOfBirth = findViewById(R.id.dateOfBirth);
        CardView selectDate = findViewById(R.id.selectDate);
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContextThemeWrapper themedContext = new ContextThemeWrapper(Fill_details.this, R.style.DatePickerDialogTheme);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        themedContext,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Handle the selected date
                                Birthday = year + "-" + (month + 1) + "-" + dayOfMonth;
                                dateOfBirth.setText(Birthday);
                                btnStatus();
                            }
                        },
                        2023, 0, 1  // Year, Month (0-indexed), Day
                );
                datePickerDialog.show();
            }
        });

        genderSelector();


    }

    private void genderSelector() {
        CardView maleCard = findViewById(R.id.maleCard);
        CardView femaleCard = findViewById(R.id.femaleCard);

        ImageView maleicon = findViewById(R.id.maleicon);
        ImageView femaleIcon = findViewById(R.id.femaleIcon);

        TextView maleText = findViewById(R.id.maleText);
        TextView femaleText = findViewById(R.id.femaleText);

        maleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedGender.equals("male")) {
                    return;
                }

                selectedGender = "male";
                maleCard.setCardBackgroundColor(ContextCompat.getColor(Fill_details.this, R.color.themeColor));
                femaleCard.setCardBackgroundColor(ContextCompat.getColor(Fill_details.this, R.color.cardView_bg));

                maleText.setTextColor(ContextCompat.getColor(Fill_details.this, R.color.white));
                femaleText.setTextColor(ContextCompat.getColor(Fill_details.this, R.color.semiblack));

                maleicon.setColorFilter(ContextCompat.getColor(Fill_details.this, R.color.white));
                femaleIcon.setColorFilter(ContextCompat.getColor(Fill_details.this, R.color.female_icon));

                btnStatus();
            }
        });

        femaleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedGender.equals("female")) {
                    return;
                }

                selectedGender = "female";
                femaleCard.setCardBackgroundColor(ContextCompat.getColor(Fill_details.this, R.color.themeColor));
                maleCard.setCardBackgroundColor(ContextCompat.getColor(Fill_details.this, R.color.cardView_bg));

                femaleText.setTextColor(ContextCompat.getColor(Fill_details.this, R.color.white));
                maleText.setTextColor(ContextCompat.getColor(Fill_details.this, R.color.semiblack));

                femaleIcon.setColorFilter(ContextCompat.getColor(Fill_details.this, R.color.white));
                maleicon.setColorFilter(ContextCompat.getColor(Fill_details.this, R.color.male_icon));

                btnStatus();

            }
        });


    }

    private void btnStatus() {
        if (nickName.getText().toString().length() > 0 && selectedGender.length() > 0 && Birthday.length() > 0) {
            nextBtn.setAlpha(1);
        } else {
            nextBtn.setAlpha(0.5F);
        }

    }
}