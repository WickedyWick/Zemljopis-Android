package com.example.zemljopis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
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
import java.util.regex.Pattern;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import com.example.zemljopis.StaticSocket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class gameActivity extends AppCompatActivity {
    Socket socket;
    Button readyBtn = null;
    TextView lblVreme = null;
    TextView lblPoeni = null;
    TextView lblRunda = null;
    TextView lblRoom = null;
    TextView lblPlayersReady = null;
    TextView lblPlayerCount = null;
    TextView lblSlovo = null;
    EditText inputDrzava = null;
    EditText inputGrad = null;
    EditText inputIme = null;
    EditText inputBiljka = null;
    EditText inputZivotinja = null;
    EditText inputPlanina = null;
    EditText inputReka = null;
    EditText inputPredmet = null;;
    ListView playerList = null;
    EditText[] fields = new EditText[8];
    String username = "";
    String roomCode = "";
    Boolean ready =false;
    Boolean gameStarted = false;
    Boolean allValid = true;
    String roundNumber ="0";
    Integer points =0;
    String sessionToken = "";
    Timer timer;
    Timer btnTimer;
    Integer duration;
    List<String> users;
    void enableAllInputs(){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                inputDrzava.setClickable(true);
                inputGrad.setClickable(true);
                inputIme.setClickable(true);
                inputBiljka.setClickable(true);
                inputZivotinja.setClickable(true);
                inputPlanina.setClickable(true);
                inputReka.setClickable(true);
                inputPredmet.setClickable(true);
                // Stuff that updates the UI

            }
        });

    }
    void disableAllInputs(){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                inputDrzava.setClickable(false);
                inputGrad.setClickable(false);
                inputIme.setClickable(false);
                inputBiljka.setClickable(false);
                inputZivotinja.setClickable(false);
                inputPlanina.setClickable(false);
                inputReka.setClickable(false);
                inputPredmet.setClickable(false);
                // Stuff that updates the UI

            }
        });

    }
    void clearAllInputs(){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                inputDrzava.setText("");
                inputGrad.setText("");
                inputIme.setText("");
                inputBiljka.setText("");
                inputZivotinja.setText("");
                inputPlanina.setText("");
                inputReka.setText("");
                inputPredmet.setText("");
                // Stuff that updates the UI

            }
        });


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        /*URI uri = URI.create("http://46.40.27.131:3000/");
        socket = IO.socket(uri);
        socket.connect();

         */
        readyBtn = findViewById(R.id.button2);
        lblVreme = findViewById(R.id.lblVreme);
        lblPoeni = findViewById(R.id.lblPoeni);
        lblRunda = findViewById(R.id.lblRunda);
        lblRoom = findViewById(R.id.lblRoomCode);
        lblPlayersReady = findViewById(R.id.lblPlayersReady);
        lblPlayerCount = findViewById(R.id.lblPlayerCount);
        lblSlovo = findViewById(R.id.lblSlovo);
        inputDrzava = findViewById(R.id.inputDrzava);
        inputGrad = findViewById(R.id.inputGrad);
        inputIme =  findViewById(R.id.inputIme);
        inputBiljka = findViewById(R.id.inputBiljka);
        inputZivotinja = findViewById(R.id.inputZivotinja);
        inputPlanina = findViewById(R.id.inputPlanina);
        inputReka = findViewById(R.id.inputReka);
        inputPredmet = findViewById(R.id.inputPredmet);
        fields[0] = inputDrzava;
        fields[1] = inputGrad;
        fields[2] = inputIme;
        fields[3] = inputBiljka;
        fields[4] = inputZivotinja;
        fields[5] = inputPlanina;
        fields[6] = inputReka;
        fields[7] = inputPredmet;
        socket = StaticSocket.socket;
        timer = new Timer();
        btnTimer = new Timer();
       // socket.emit("test","test");

        Bundle extras = gameActivity.this.getIntent().getExtras();
        if(extras == null){
            Snackbar.make(findViewById(R.id.coordinatorID),"Nedostaju potrebni parametri!",Snackbar.LENGTH_LONG).show();
        }else{
            //Snackbar.make(findViewById(R.id.coordinatorID),extras.getString("USERNAME") + " " + extras.getString("ROOMCODE"),Snackbar.LENGTH_LONG).show();
            username = extras.getString("USERNAME");
            roomCode = extras.getString("ROOMCODE");

            users = new ArrayList<String>(Collections.singleton(username));
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_list_item_1, users);


            if(Pattern.matches("^[A-Za-zčČćĆžŽšŠđĐ ]{4,30}$", username) && Pattern.matches("^[A-Za-z0-9]{8}$",roomCode)) {
                Context context = gameActivity.this;
                SharedPreferences sharedPref = context.getSharedPreferences("zemljopisData", Context.MODE_PRIVATE);
                sessionToken = sharedPref.getString("sessionToken", "");
                if (Pattern.matches("^[A-Za-z0-9+/]{48}$", sessionToken)) {
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
                    Snackbar.make(findViewById(R.id.coordinatorID),"Session token nije validan",Snackbar.LENGTH_LONG).show();
                }



            }
        }


        socket.on("load",new Emitter.Listener(){


            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    if(result.getBoolean("Success")){
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                try {
                                    lblPoeni.setText(result.getString("points"));
                                    lblRoom.setText(roomCode);
                                    lblPlayersReady.setText(result.getString("playersReady"));
                                    lblPlayerCount.setText(result.getString("playerCount"));
                                    lblRunda.setText(result.getString("roundNumber"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                // Stuff that updates the UI

                            }
                        });

                        Snackbar.make(findViewById(R.id.coordinatorID),result.getString("MSG"),Snackbar.LENGTH_SHORT);
                        if(result.getBoolean("roundActive")){
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    readyBtn.setClickable(false);
                                    readyBtn.setText("Sačekajte");
                                    // Stuff that updates the UI

                                }
                            });



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
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        readyBtn.setText("Nisi spreman!");
                        // Stuff that updates the UI

                    }
                });

                Snackbar.make(findViewById(R.id.coordinatorID),"Problem pri kreiranju runde , pokusajte ponovo!",Snackbar.LENGTH_LONG);

            }
        });
        socket.on("playerCount", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        lblPlayersReady.setText(args[0].toString());
                        // Stuff that updates the UI

                    }
                });

            }
        });
        socket.on("evaluationResponse",new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    Snackbar.make(findViewById(R.id.coordinatorID),result.getString("ERR_MSG"),Snackbar.LENGTH_LONG);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                lblPlayersReady.setText(result.getString("playersReady"));
                                lblRunda.setText(result.getString("roundNumber"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // Stuff that updates the UI

                        }
                    });

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
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    lblSlovo.setText(result.getString("currentLetter"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                readyBtn.setText("Gotovo");
                                // Stuff that updates the UI

                            }
                        });

                        duration =61;
                        //timer.cancel(); ponistava

                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                duration--;
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        lblVreme.setText(duration.toString());
                                        // Stuff that updates the UI

                                    }
                                });

                                if(duration == 0){
                                    timer.cancel();
                                }
                            }
                        },1000,1000);

                        gameStarted = true;
                        enableAllInputs();
                        clearAllInputs();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        socket.on("playerList",new Emitter.Listener(){
            @Override
            public void call(Object... args){
                JSONObject result = (JSONObject)args[0];
                try {
                    JSONArray players = result.getJSONArray("players");
                    for(int i=0;i<players.length();i++){
                        if(!users.contains(players.getString(i))){
                            users.add(players.getString(i));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        readyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gameStarted){
                    if(!ready){
                        socket.emit("playerReady",roomCode);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                readyBtn.setClickable(false);
                                // Stuff that updates the UI
                            }
                        });

                        ready = true;

                    }else{
                        socket.emit("playerUnReady",roomCode);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                readyBtn.setClickable(false);
                                // Stuff that updates the UI

                            }
                        });
                        ready =  false;

                    }
                    btnTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    readyBtn.setClickable(true);
                                    // Stuff that updates the UI

                                }
                            });

                        }
                    },1500);

                }else{
                    String[] data = new String[8];
                    allValid = true;
                    for(int i=0;i<fields.length;i++){
                        //dodaj regex ovde
                        if(Pattern.matches("^[A-Za-zčČćĆžŽšŠđĐ ]{2,42}$",fields[i].getText().toString().trim())){
                            allValid = false;
                            data[i] = "";
                        }else{
                            data[i] = fields[i].getText().toString().toLowerCase().trim();
                        }
                        if(allValid){
                            JSONObject obj = new JSONObject();
                            try {
                                timer.cancel();
                                obj.put("username",username);
                                obj.put("roomCode",roomCode);
                                obj.put("data",data);
                                obj.put("roundNumber",roundNumber);
                                socket.emit("clientEndRoundM",obj);
                                disableAllInputs();
                                gameStarted = false;
                                ready = false;
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        // Stuff that updates the UI
                                        readyBtn.setText("Nisi spreman");
                                        readyBtn.setClickable(false);
                                        lblVreme.setText("0");
                                    }
                                });

                                //NA POINTS EVENT ukljuci
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else{
                            Snackbar.make(findViewById(R.id.coordinatorID),"Vrednosti polja nisu validna!",Snackbar.LENGTH_SHORT);
                        }
                    }
                }
            }
        });
        socket.on("playerReadyResponse", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    if(!result.getBoolean("Success")){
                        if(result.getInt("ERR_CODE") == 1){
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    readyBtn.setText("Nisi spreman!");
                                    lblPlayersReady.setText("0");
                                    // Stuff that updates the UI

                                }
                            });


                        }
                    }else{
                        if(result.getInt("CODE") == 1){
                            if(result.getString("STATE") == "Ready"){
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        readyBtn.setText("Spreman!");
                                        // Stuff that updates the UI

                                    }
                                });

                            }else{
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        readyBtn.setText("Nisi spreman!");
                                        // Stuff that updates the UI

                                    }
                                });

                            }
                            Snackbar.make(findViewById(R.id.coordinatorID),result.getString("MSG"),Snackbar.LENGTH_LONG);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        socket.on("discMessage", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Snackbar.make(findViewById(R.id.coordinatorID),args[0].toString(),Snackbar.LENGTH_LONG);
            }
        });
        socket.on("playerJoinMsg", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Snackbar.make(findViewById(R.id.coordinatorID),args[0].toString(),Snackbar.LENGTH_SHORT);

            }
        });
        socket.on("roundNumberUpdate", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                roundNumber =  args[0].toString();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        lblRunda.setText(roundNumber);
                        // Stuff that updates the UI

                    }
                });

            }
        });
        socket.on("pointsErr", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Snackbar.make(findViewById(R.id.coordinatorID),args[0].toString(),Snackbar.LENGTH_LONG);
                //vidi ovde reset da uradis ili vratis poene na proslo?
            }
        });
        //roundENd i points eventovi da se prevedu
        socket.on("roundEnd", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    if(result.getBoolean("Success")){
                        Snackbar.make(findViewById(R.id.coordinatorID),result.getString("MSG"),Snackbar.LENGTH_SHORT);
                        String[] data = new String[8];
                        allValid = true;
                        for(int i=0;i<fields.length;i++){
                            //dodaj regex ovde
                            if(Pattern.matches("^[A-Za-zčČćĆžŽšŠđĐ ]{2,42}$",fields[i].getText().toString().trim())){
                                allValid = false;
                                data[i] = "";
                            }else{
                                data[i] = fields[i].getText().toString().toLowerCase().trim();
                            }
                            if(allValid){
                                JSONObject obj = new JSONObject();

                                    timer.cancel();
                                    obj.put("username",username);
                                    obj.put("roomCode",roomCode);
                                    obj.put("data",data);
                                    obj.put("roundNumber",roundNumber);
                                    socket.emit("roundDataM",obj);
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            readyBtn.setText("Nisi spreman");
                                            readyBtn.setClickable(false);
                                            lblVreme.setText("0");
                                            // Stuff that updates the UI

                                        }
                                    });
                                    disableAllInputs();
                                    gameStarted = false;
                                    ready = false;

                            }
                        }
                    }
                }catch (JSONException e) {
                            e.printStackTrace();
                }
            }
        });

    }
    

}