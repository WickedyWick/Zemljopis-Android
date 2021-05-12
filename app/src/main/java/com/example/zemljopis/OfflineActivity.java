package com.example.zemljopis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import io.realm.Realm;

public class OfflineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);
    }
}