package com.example.zemljopis;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class NetworkChangeListener extends BroadcastReceiver{
    Socket socket;
    public NetworkChangeListener(Socket socket){
        this.socket = socket;
    }
    @Override
    public void onReceive(Context context, Intent intent){
        if(!Connection.isConnectedToInternet(context)) {
            //Internet is not connected
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View layout_dialog = LayoutInflater.from(context).inflate(R.layout.check_internet_dialog,null);
            builder.setView(layout_dialog);

            AppCompatButton btnRetry = layout_dialog.findViewById(R.id.btnRetry);

            //Show dialog
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(true);

            dialog.getWindow().setGravity(Gravity.CENTER);

            //pokrece ponovo
            btnRetry.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    dialog.dismiss();
                    onReceive(context,intent);
                }
            });
        }else{


            socket.connect();
            SharedPreferences sharedPref = context.getSharedPreferences("zemljopisData", Context.MODE_PRIVATE);

            String sessionToken = sharedPref.getString("sessionToken", "");
            String username = sharedPref.getString("username","");
            String roomCode = sharedPref.getString("roomCode","");
            if (Pattern.matches("^[A-Za-z0-9+/]{48}$", sessionToken) && Pattern.matches("^[A-Za-zčČćĆžŽšŠđĐ ]{4,30}$", username) && Pattern.matches("^[A-Za-z0-9]{8}$",roomCode)) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("username", username);
                    obj.put("roomCode", roomCode);
                    obj.put("sessionToken", sessionToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("joinRoomReqM", obj);
            }
        }
    }
}
