package com.example.juliod07_laptop.firebasecurso.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.juliod07_laptop.firebasecurso.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }


    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity.this,MainActivity.class));
        finish();

    }
}
