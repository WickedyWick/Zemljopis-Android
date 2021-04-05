package com.example.zemljopis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import com.example.zemljopis.StaticSocket;

import org.json.JSONException;
import org.json.JSONObject;

public class gameActivity extends AppCompatActivity {
    Socket socket;
    Button readyBtn = (Button)findViewById(R.id.button2);
    TextView lblVreme = findViewById(R.id.lblVreme);
    TextView lblPoeni = findViewById(R.id.lblPoeni);
    TextView lblRunda = findViewById(R.id.lblRunda);
    TextView lblRoom = findViewById(R.id.lblRoomCode);
    TextView lblPlayersReady = findViewById(R.id.lblPlayersReady);
    TextView lblPlayerCount = findViewById(R.id.lblPlayerCount);
    TextView lblSlovo = findViewById(R.id.lblSlovo);
    EditText inputDrzava = findViewById(R.id.inputDrzava);
    EditText inputGrad = findViewById(R.id.inputDrzava);
    EditText inputIme = findViewById(R.id.inputIme);
    EditText inputBiljka = findViewById(R.id.inputBiljka);
    EditText inputZivotinja = findViewById(R.id.inputZivotinja);
    EditText inputPlanina = findViewById(R.id.inputPlanina);
    EditText inputReka = findViewById(R.id.inputReka);
    EditText inputPredmet = findViewById(R.id.inputPredmet);;
    ListView playerList = findViewById(R.id.playerList);
    String username = "";
    String roomCode = "";
    Boolean ready =false;
    boolean gameStarted = false;
    String roundNumber ="0";
    Integer points =0;
    String sessionToken = "";
    Timer timer;
    Integer duration;
    void enableAllInputs(){
        inputDrzava.setClickable(true);
        inputGrad.setClickable(true);
        inputIme.setClickable(true);
        inputBiljka.setClickable(true);
        inputZivotinja.setClickable(true);
        inputPlanina.setClickable(true);
        inputReka.setClickable(true);
        inputPredmet.setClickable(true);
    }
    void disableAllInputs(){
        inputDrzava.setClickable(false);
        inputGrad.setClickable(false);
        inputIme.setClickable(false);
        inputBiljka.setClickable(false);
        inputZivotinja.setClickable(false);
        inputPlanina.setClickable(false);
        inputReka.setClickable(false);
        inputPredmet.setClickable(false);
    }
    void clearAllInputs(){
        inputDrzava.setText("");
        inputGrad.setText("");
        inputIme.setText("");
        inputBiljka.setText("");
        inputZivotinja.setText("");
        inputPlanina.setText("");
        inputReka.setText("");
        inputPredmet.setText("");

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        /*URI uri = URI.create("http://46.40.27.131:3000/");
        socket = IO.socket(uri);
        socket.connect();

         */


        List<String> users = new ArrayList<String>(Collections.singleton(username));
        socket = StaticSocket.socket;
       // socket.emit("test","test");

        Bundle extras = gameActivity.this.getIntent().getExtras();
        if(extras == null){
            Snackbar.make(findViewById(R.id.coordinatorID),"Nedostaju potrebni parametri!",Snackbar.LENGTH_LONG).show();
        }else{
            //Snackbar.make(findViewById(R.id.coordinatorID),extras.getString("USERNAME") + " " + extras.getString("ROOMCODE"),Snackbar.LENGTH_LONG).show();
            username = extras.getString("USERNAME");
            roomCode = extras.getString("ROOMCODE");


            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_list_item_1, users);


            if(username.length() >=4 && roomCode.length() == 8) {
                Context context = gameActivity.this;
                SharedPreferences sharedPref = context.getSharedPreferences("zemljopisData", Context.MODE_PRIVATE);
                sessionToken = sharedPref.getString("sessionToken", "");
                if (sessionToken != "") {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("username", username);
                        obj.put("roomCode", roomCode);
                        obj.put("sessionToken", sessionToken);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    socket.emit("joinRoomReqM",obj);
                }else{
                    Snackbar.make(findViewById(R.id.coordinatorID),"Invalidan session token!",Snackbar.LENGTH_LONG).show();
                }



            }
        }

        String finalRoomCode = roomCode;
        socket.on("load",new Emitter.Listener(){


            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    if(result.getBoolean("Success")){
                        lblRoom.setText(finalRoomCode);
                        lblPlayersReady.setText(result.getString("playersReady"));
                        lblPlayerCount.setText(result.getString("playerCount"));
                        lblRunda.setText(result.getString("roundNumber"));
                        lblPoeni.setText(result.getString("points"));
                        Snackbar.make(findViewById(R.id.coordinatorID),result.getString("MSG"),Snackbar.LENGTH_SHORT);
                        if(result.getBoolean("roundActive")){
                            readyBtn.setClickable(false);
                            readyBtn.setText("Sačekajte");
                            Snackbar.make(findViewById(R.id.coordinatorID),"Runda u toku! Sačekajte kraj!",Snackbar.LENGTH_SHORT);
                        }
                    }else{
                        Snackbar.make(findViewById(R.id.coordinatorID),result.getString("ERR_MSG"),Snackbar.LENGTH_LONG);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        socket.on("createRoundResponse", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                gameStarted = false;
                ready = false;
                readyBtn.setText("Nisi spreman!");
                Snackbar.make(findViewById(R.id.coordinatorID),"Problem pri kreiranju runde , pokusajte ponovo!",Snackbar.LENGTH_LONG);

            }
        });
        socket.on("playerCount", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                lblPlayersReady.setText(args[0].toString());
            }
        });
        socket.on("evaluationResponse",new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    Snackbar.make(findViewById(R.id.coordinatorID),result.getString("ERR_MSG"),Snackbar.LENGTH_LONG);
                    lblRunda.setText(result.getString("roundNumber"));
                    lblPlayersReady.setText(result.getString("playersReady"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        socket.on("dataCollectorResponse", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Snackbar.make(findViewById(R.id.coordinatorID),"Doslo je do problema prilikom unosenja podataka, podaci nevažeci!",Snackbar.LENGTH_LONG);
            }
        });
        socket.on("gameStartNotification", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    if(result.getBoolean("Success")){
                        Snackbar.make(findViewById(R.id.coordinatorID),result.getString("MSG"), Snackbar.LENGTH_SHORT);
                        lblSlovo.setText(result.getString("currentLetter"));
                        duration =61;
                        //timer.cancel(); ponistava
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                duration--;
                                lblVreme.setText(duration.toString());
                                if(duration == 0){
                                    timer.cancel();
                                }
                            }
                        },1000);
                        readyBtn.setText("Gotovo");
                        gameStarted = true;
                        enableAllInputs();
                        clearAllInputs();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

}