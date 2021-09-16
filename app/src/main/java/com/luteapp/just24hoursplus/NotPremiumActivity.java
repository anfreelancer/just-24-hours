package com.luteapp.just24hoursplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NotPremiumActivity extends AppCompatActivity {
    private Button btnPremium;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_premium);
        Toast.makeText(this, "Please Upgrade!", Toast.LENGTH_SHORT).show();
        btnPremium = findViewById(R.id.btnPremium);
        btnPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotPremiumActivity.this, ShopActivity.class));
            }
        });

    }
}
