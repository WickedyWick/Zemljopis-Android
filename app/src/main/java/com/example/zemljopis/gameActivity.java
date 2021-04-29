package com.example.zemljopis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
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
import org.w3c.dom.Text;

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
    TextView lblDrzavaPoeni = null;
    TextView lblGradPoeni = null;
    TextView lblImePoeni = null;
    TextView lblBiljkaPoeni = null;
    TextView lblZivotinjaPoeni = null;
    TextView lblPlaninaPoeni = null;
    TextView lblRekaPoeni = null;
    TextView lblPredmetPoeni = null;
    EditText inputDrzava = null;
    EditText inputGrad = null;
    EditText inputIme = null;
    EditText inputBiljka = null;
    EditText inputZivotinja = null;
    EditText inputPlanina = null;
    EditText inputReka = null;
    EditText inputPredmet = null;;
    ListView playerList = null;
    Button predloziDrzava = null;
    Button predloziGrad = null;
    Button predloziIme = null;
    Button predloziBiljka = null;
    Button predloziZivotinja = null;
    Button predloziPlanina = null;
    Button predloziReka = null;
    Button predloziPredmet = null;
    EditText[] fields = new EditText[8];
    TextView[] pointFields = new TextView[8];
    String username = "";
    String roomCode = "";
    String[] data = new String[8];
    Boolean ready =false;
    Boolean gameStarted = false;
    Boolean allValid = true;
    String roundNumber ="1";
    Integer points =0;
    String sessionToken = "";
    String currentLetter = "";
    Timer timer;
    Timer btnTimer;
    String regData;
    Integer duration;
    List<String> users;

    void enableAllPButtons(){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                predloziDrzava.setClickable(true);
                predloziGrad.setClickable(true);
                predloziIme.setClickable(true);
                predloziBiljka.setClickable(true);
                predloziZivotinja.setClickable(true);
                predloziPlanina.setClickable(true);
                predloziReka.setClickable(true);
                predloziPredmet.setClickable(true);
                // Stuff that updates the UI

            }
        });
    }
    void disableAllPButtons(){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                predloziDrzava.setClickable(false);
                predloziGrad.setClickable(false);
                predloziIme.setClickable(false);
                predloziBiljka.setClickable(false);
                predloziZivotinja.setClickable(false);
                predloziPlanina.setClickable(false);
                predloziReka.setClickable(false);
                predloziPredmet.setClickable(false);
                // Stuff that updates the UI

            }
        });
    }
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

        URI uri = URI.create("adresaServera:3000/");
        socket = IO.socket(uri);
        socket.connect();
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
        lblDrzavaPoeni = findViewById(R.id.lblDrzavaPoeni);
        lblGradPoeni = findViewById(R.id.lblGradPoeni);
        lblImePoeni = findViewById(R.id.lblImePoeni);
        lblBiljkaPoeni = findViewById(R.id.lblBiljkaPoeni);
        lblZivotinjaPoeni = findViewById(R.id.lblZivotinjaPoeni);
        lblPlaninaPoeni = findViewById(R.id.lblPlaninaPoeni);
        lblRekaPoeni = findViewById(R.id.lblRekaPoeni);
        lblPredmetPoeni = findViewById(R.id.lblPredmetPoeni);
        predloziDrzava = findViewById(R.id.btnPredloziDrzava);
        predloziGrad = findViewById(R.id.btnPredloziGrad);
        predloziIme = findViewById(R.id.btnPredloziIme);
        predloziBiljka = findViewById(R.id.btnPredloziBiljka);
        predloziZivotinja = findViewById(R.id.btnPredloziZivotinja);
        predloziPlanina = findViewById(R.id.btnPredloziPlanina);
        predloziReka = findViewById(R.id.btnPredloziReka);
        predloziPredmet = findViewById(R.id.btnPredloziPredmet);
        fields[0] = inputDrzava;
        fields[1] = inputGrad;
        fields[2] = inputIme;
        fields[3] = inputBiljka;
        fields[4] = inputZivotinja;
        fields[5] = inputPlanina;
        fields[6] = inputReka;
        fields[7] = inputPredmet;
        pointFields[0] = lblDrzavaPoeni;
        pointFields[1] = lblGradPoeni;
        pointFields[2] = lblImePoeni;
        pointFields[3] = lblBiljkaPoeni;
        pointFields[4] = lblZivotinjaPoeni;
        pointFields[5] = lblPlaninaPoeni;
        pointFields[6] = lblRekaPoeni;
        pointFields[7] = lblPredmetPoeni;

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

                                    currentLetter = result.getString("currentLetter");
                                    lblSlovo.setText(currentLetter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                readyBtn.setText("Gotovo");
                                // Stuff that updates the UI

                            }
                        });
                        if(currentLetter == "č" || currentLetter == "ć")
                            regData = "^c[A-Za-zčČćĆžŽšŠđĐ ]{1,41}$";
                        else if(currentLetter == "lj" || currentLetter == "nj")
                            regData = "^"+currentLetter+"[A-Za-zčČćĆžŽšŠđĐ ]{1,40}$";
                        else if(currentLetter == "dž")
                            regData = "^dz[A-Za-zčČćĆžŽšŠđĐ ]{1,41}$";
                        else if(currentLetter == "ž")
                            regData = "^z[A-Za-zčČćĆžŽšŠđĐ ]{1,41}$";
                        else if(currentLetter == "š")
                            regData = "^s[A-Za-zčČćĆžŽšŠđĐ ]{1,41}$";
                        else
                            regData = "^"+currentLetter+"[A-Za-zčČćĆžŽšŠđĐ ]{1,41}$";
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
                        disableAllPButtons();

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

                    JSONArray arr = new JSONArray();

                    allValid = true;
                    Log.e("FIELDS",String.valueOf(fields.length));
                    for(int i=0;i<fields.length;i++){
                        //dodaj regex ovde
                        Log.e("DATA",fields[i].getText().toString().trim().toLowerCase());
                        Log.e("REG",regData);
                        if(Pattern.matches(regData,fields[i].getText().toString().trim().toLowerCase())){
                            arr.put(fields[i].getText().toString().toLowerCase().trim().toLowerCase());


                        }else {
                            allValid = false;
                            arr.put("");
                        }}
                        if(allValid){
                            JSONObject obj = new JSONObject();

                            try {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        // Stuff that updates the UI
                                        readyBtn.setText("Nisi spreman");
                                        readyBtn.setClickable(false);
                                        lblVreme.setText("0");
                                    }
                                });
                                timer.cancel();
                                obj.put("username",username);
                                obj.put("roomCode",roomCode);
                                obj.put("data",arr);
                                obj.put("roundNumber",roundNumber);
                                socket.emit("clientEndRoundM",obj);
                                disableAllInputs();
                                gameStarted = false;
                                ready = false;

                                enableAllPButtons();
                                //NA POINTS EVENT ukljuci
                            } catch (JSONException e) {
                                Log.e("ROUNDENDERR",e.getMessage());
                                e.printStackTrace();
                            }

                        }else{
                            Snackbar.make(findViewById(R.id.coordinatorID),"Vrednosti polja nisu validna!",Snackbar.LENGTH_SHORT);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        readyBtn.setClickable(true);
                    }
                });
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
        //points eventovi da se prevedu
        socket.on("roundEnd", new Emitter.Listener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    if(result.getBoolean("Success")){
                        Snackbar.make(findViewById(R.id.coordinatorID),result.getString("MSG"),Snackbar.LENGTH_SHORT);
                        //String[] data = new String[8];
                        JSONArray arr = new JSONArray();

                        for(int i=0;i<fields.length;i++) {
                            //dodaj regex ovde
                            if (Pattern.matches(regData, fields[i].getText().toString().toLowerCase())) {
                                arr.put(fields[i].getText().toString().toLowerCase().trim().toLowerCase());

                            } else {
                                arr.put("");
                            }
                        }
                            JSONObject obj = new JSONObject();

                                //ovaj if je pogresan
                                timer.cancel();
                                obj.put("username",username);
                                obj.put("roomCode",roomCode);
                                obj.put("data",arr);
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
                                enableAllPButtons();
                                gameStarted = false;
                                ready = false;



                    }else{
                        Snackbar.make(findViewById(R.id.coordinatorID),"Problem sa rezultatima",Snackbar.LENGTH_LONG);
                    }
                }catch (JSONException e) {
                            e.printStackTrace();
                }
            }
        });
        predloziDrzava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputDrzava.getText().toString().trim().toLowerCase();
                if(text.startsWith(currentLetter)){
                    if(Pattern.matches(regData,text)){
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("predlog",text);
                            obj.put("slovo",currentLetter);
                            obj.put("kategorija",0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                v.setClickable(false);
                            }
                        });
                        socket.emit("predlagacM",obj);
                        Snackbar.make(findViewById(R.id.coordinatorID),"Uspesno predlozeno!",Snackbar.LENGTH_SHORT);
                    }
                    else
                        Snackbar.make(findViewById(R.id.coordinatorID),"Predlog sadrzi nedozvoljene karaktere!",Snackbar.LENGTH_SHORT);
                }else
                    Snackbar.make(findViewById(R.id.coordinatorID),"Drzava ne pocinje na slovo " + currentLetter,Snackbar.LENGTH_SHORT);
            }
        });
        predloziGrad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputGrad.getText().toString().trim().toLowerCase();
                if(text.startsWith(currentLetter)){
                    if(Pattern.matches(regData,text)){
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("predlog",text);
                            obj.put("slovo",currentLetter);
                            obj.put("kategorija",1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                v.setClickable(false);
                            }
                        });
                        socket.emit("predlagacM",obj);
                        Snackbar.make(findViewById(R.id.coordinatorID),"Uspesno predlozeno!",Snackbar.LENGTH_SHORT);
                    }
                    else
                        Snackbar.make(findViewById(R.id.coordinatorID),"Predlog sadrzi nedozvoljene karaktere!",Snackbar.LENGTH_SHORT);
                }else
                    Snackbar.make(findViewById(R.id.coordinatorID),"Drzava ne pocinje na slovo " + currentLetter,Snackbar.LENGTH_SHORT);
            }
        });
        predloziIme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputIme.getText().toString().trim().toLowerCase();
                if(text.startsWith(currentLetter)){
                    if(Pattern.matches(regData,text)){
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("predlog",text);
                            obj.put("slovo",currentLetter);
                            obj.put("kategorija",2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                v.setClickable(false);
                            }
                        });
                        socket.emit("predlagacM",obj);
                        Snackbar.make(findViewById(R.id.coordinatorID),"Uspesno predlozeno!",Snackbar.LENGTH_SHORT);
                    }
                    else
                        Snackbar.make(findViewById(R.id.coordinatorID),"Predlog sadrzi nedozvoljene karaktere!",Snackbar.LENGTH_SHORT);
                }else
                    Snackbar.make(findViewById(R.id.coordinatorID),"Drzava ne pocinje na slovo " + currentLetter,Snackbar.LENGTH_SHORT);
            }
        });
        predloziBiljka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputBiljka.getText().toString().trim().toLowerCase();
                if(text.startsWith(currentLetter)){
                    if(Pattern.matches(regData,text)){
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("predlog",text);
                            obj.put("slovo",currentLetter);
                            obj.put("kategorija",3);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                v.setClickable(false);
                            }
                        });
                        socket.emit("predlagacM",obj);
                        Snackbar.make(findViewById(R.id.coordinatorID),"Uspesno predlozeno!",Snackbar.LENGTH_SHORT);
                    }
                    else
                        Snackbar.make(findViewById(R.id.coordinatorID),"Predlog sadrzi nedozvoljene karaktere!",Snackbar.LENGTH_SHORT);
                }else
                    Snackbar.make(findViewById(R.id.coordinatorID),"Drzava ne pocinje na slovo " + currentLetter,Snackbar.LENGTH_SHORT);
            }
        });
        predloziZivotinja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputZivotinja.getText().toString().trim().toLowerCase();
                if(text.startsWith(currentLetter)){
                    if(Pattern.matches(regData,text)){
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("predlog",text);
                            obj.put("slovo",currentLetter);
                            obj.put("kategorija",4);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                v.setClickable(false);
                            }
                        });
                        socket.emit("predlagacM",obj);
                        Snackbar.make(findViewById(R.id.coordinatorID),"Uspesno predlozeno!",Snackbar.LENGTH_SHORT);
                    }
                    else
                        Snackbar.make(findViewById(R.id.coordinatorID),"Predlog sadrzi nedozvoljene karaktere!",Snackbar.LENGTH_SHORT);
                }else
                    Snackbar.make(findViewById(R.id.coordinatorID),"Drzava ne pocinje na slovo " + currentLetter,Snackbar.LENGTH_SHORT);
            }
        });
        predloziPlanina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputPlanina.getText().toString().trim().toLowerCase();
                if(text.startsWith(currentLetter)){
                    if(Pattern.matches(regData,text)){
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("predlog",text);
                            obj.put("slovo",currentLetter);
                            obj.put("kategorija",5);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                v.setClickable(false);
                            }
                        });
                        socket.emit("predlagacM",obj);
                        Snackbar.make(findViewById(R.id.coordinatorID),"Uspesno predlozeno!",Snackbar.LENGTH_SHORT);
                    }
                    else
                        Snackbar.make(findViewById(R.id.coordinatorID),"Predlog sadrzi nedozvoljene karaktere!",Snackbar.LENGTH_SHORT);
                }else
                    Snackbar.make(findViewById(R.id.coordinatorID),"Drzava ne pocinje na slovo " + currentLetter,Snackbar.LENGTH_SHORT);
            }
        });
        predloziReka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputReka.getText().toString().trim().toLowerCase();
                if(text.startsWith(currentLetter)){
                    if(Pattern.matches(regData,text)){
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("predlog",text);
                            obj.put("slovo",currentLetter);
                            obj.put("kategorija",6);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                v.setClickable(false);
                            }
                        });
                        socket.emit("predlagacM",obj);
                        Snackbar.make(findViewById(R.id.coordinatorID),"Uspesno predlozeno!",Snackbar.LENGTH_SHORT);
                    }
                    else
                        Snackbar.make(findViewById(R.id.coordinatorID),"Predlog sadrzi nedozvoljene karaktere!",Snackbar.LENGTH_SHORT);
                }else
                    Snackbar.make(findViewById(R.id.coordinatorID),"Drzava ne pocinje na slovo " + currentLetter,Snackbar.LENGTH_SHORT);
            }
        });
        predloziPredmet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputPredmet.getText().toString().trim().toLowerCase();
                if(text.startsWith(currentLetter)){
                    if(Pattern.matches(regData,text)){
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("predlog",text);
                            obj.put("slovo",currentLetter);
                            obj.put("kategorija",7);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               v.setClickable(false);
                            }
                        });
                        socket.emit("predlagacM",obj);
                        Snackbar.make(findViewById(R.id.coordinatorID),"Uspesno predlozeno!",Snackbar.LENGTH_SHORT);
                    }
                    else
                        Snackbar.make(findViewById(R.id.coordinatorID),"Predlog sadrzi nedozvoljene karaktere!",Snackbar.LENGTH_SHORT);
                }else
                    Snackbar.make(findViewById(R.id.coordinatorID),"Drzava ne pocinje na slovo " + currentLetter,Snackbar.LENGTH_SHORT);
            }
        });
        socket.on("playerCountUpdate", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lblPlayersReady.setText(args[0].toString());
                    }
                });
            }
        });
        socket.on("points", new Emitter.Listener(){
            @Override
            public void call(Object... args){
                JSONObject result = (JSONObject)args[0];

                try {
                    if(!result.getBoolean("Success")){
                        Snackbar.make(findViewById(R.id.coordinatorID),"Problem prilikom evaluacije , runda ponistena!",Snackbar.LENGTH_LONG);
                    }else{
                        Snackbar.make(findViewById(R.id.coordinatorID),"Evaluacija završena",Snackbar.LENGTH_SHORT);
                        for(int i=0;i<fields.length;i++){
                            try{
                                //vidi da li je potrebno da keyovi budu string u pointsu ili je ovako ok
                                Integer poen = result.getJSONObject("result").getJSONObject(String.valueOf(i)).getInt(fields[i].getText().toString().trim().toLowerCase());
                                points += poen;
                                int finalI1 = i;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pointFields[finalI1].setText("+" + poen.toString());
                                    }
                                });
                            }catch (JSONException e){
                                int finalI = i;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pointFields[finalI].setText("+0");
                                    }
                                });
                            }

                        }
                        enableAllPButtons();
                        roundNumber = result.getString("roundNumber");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lblPoeni.setText(points.toString());
                                lblRunda.setText(roundNumber);
                                readyBtn.setClickable(true);
                                try {
                                    lblPlayersReady.setText(String.valueOf(result.getInt("playersReady")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    

}