package com.example.zemljopis;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Button b = findViewById(R.id.button3);

        ImageView imgKick = findViewById(R.id.imgKickNotification);


        imgKick.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v ){
                AlertDialog.Builder builder = new AlertDialog.Builder(test.this);
                View layout_dialog = LayoutInflater.from(test.this).inflate(R.layout.kick_layout,null);
                builder.setView(layout_dialog);



                //Show dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.setCancelable(true);
                dialog.getWindow().setGravity(Gravity.CENTER);
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(test.this);
                View layout_dialog = LayoutInflater.from(test.this).inflate(R.layout.test_dialog,null);
                builder.setView(layout_dialog);

                //AppCompatButton btnRetry = layout_dialog.findViewById(R.id.btnRetry);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                //Show dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.setCancelable(true);

                // ovaj deo mora nakon sto ide dialog.show
                lp.copyFrom(dialog.getWindow().getAttributes());
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                lp.width = (int) (displayMetrics.widthPixels * 0.9);
                lp.height = (int) (displayMetrics.heightPixels * 0.85);
                Log.v("WIDTH", String.valueOf(lp.width));
                Log.v("HEIGHT", String.valueOf(lp.height));
                dialog.getWindow().setAttributes(lp);
                dialog.getWindow().setGravity(Gravity.CENTER);
            }
        });
    }
}