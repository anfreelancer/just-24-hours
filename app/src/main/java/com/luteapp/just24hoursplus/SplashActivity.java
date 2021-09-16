package com.luteapp.just24hoursplus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                startActivity(new Intent(SplashActivity.this, theClockActivity.class));
                finish();
            }
        }.start();
    }

    public boolean isUpgraded() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppUpgrade", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("Upgraded", false);
    }

    public boolean isFirst() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppUpgrade", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("first", false);
    }

    public static void setFirst(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppUpgrade", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("first", true);
        editor.apply();
    }
}