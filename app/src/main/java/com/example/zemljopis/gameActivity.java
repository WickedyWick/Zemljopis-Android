package com.example.zemljopis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class gameActivity extends AppCompatActivity {

    Socket socket;
    String username = "";
    String roomCode = "";
    String usernameReg = "^[A-Za-zа-шА-ШčČćĆžŽšŠđĐђјљњћџЂЈЉЊЋЏ ]{4,20}$";
    String roomReg = "^[A-Za-z0-9]{8}$";
    String sessionReg = "^[A-Za-z0-9/+]{48}$";
    String roundNumber = "1";
    TextView lblVreme = null;
    TextView lblPoeni = null;
    TextView lblRunda = null;
    TextView lblRoom = null;
    TextView lblPlayersReady = null;
    TextView lblPlayerCount = null;
    TextView lblSlovo = null;
    Button readyBtn = null;
    ImageView playerInfoImage = null;
    ImageView kickNotificationImage = null;
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
    ImageView predloziDrzava = null;
    ImageView predloziGrad = null;
    ImageView predloziIme = null;
    ImageView predloziBiljka = null;
    ImageView predloziZivotinja = null;
    ImageView predloziPlanina = null;
    ImageView predloziReka = null;
    ImageView predloziPredmet = null;
    EditText[] fields = new EditText[8];
    TextView[] pointFields = new TextView[8];
    String[] data = new String[8];
    Boolean ready =false;
    Boolean gameStarted = false;
    Boolean allValid = true;
    Integer points =0;
    String sessionToken = "";
    String currentLetter = "";
    Timer timer;
    Timer btnTimer;
    String regData;
    Integer duration;
    List<String> users;
    Hashtable<String,Integer> pList = new Hashtable<String,Integer>();
    Hashtable<String,Integer> pListIds = new Hashtable<String,Integer>();
    Hashtable<String,Integer> playerInfoIds = new Hashtable<String,Integer>();
    Hashtable<String,TextView> playerPointFields = new Hashtable<String,TextView>();
    Hashtable<String,LinearLayout> playerLayouts = new Hashtable<String,LinearLayout>();
    JSONObject historyData = new JSONObject();
    Iterator<String> pListKeys = null;
    List<String> roundValuesList = null;
    Button resultBtn;
    Spinner ddlRounds = null;
    Spinner spinnerPlayer = null;
    ArrayAdapter roundAdapter = null;
    ArrayAdapter adapterPlayer = null;
    Integer vreme = 0;
    Boolean kicked = false;
    List<String> userList = null;
    String[] dataDictKeys = new String[8];
    AlertDialog.Builder playerBuilder;
    AlertDialog.Builder kickBuilder;
    View playerInfoDialogLayout;
    View kickDialogLayout;
    AlertDialog playerInfoDialog;
    AlertDialog kickDialog;
    LinearLayout linearLayout;
    String personToBeKicked = "";
    Button voteFor = null;
    Button voteAgainst = null;
    TextView txbToBeKicked = null;
    Boolean kickInProgress = false;
    Button btnOK = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        URI uri = URI.create("http://46.40.27.131:3000/");
        socket = IO.socket(uri);
        socket.connect();
        readyBtn = findViewById(R.id.button3);
        lblVreme = findViewById(R.id.lblVremeN);
        lblPoeni = findViewById(R.id.lblPoeniN);
        lblRunda = findViewById(R.id.lblRundaN);
        lblRoom = findViewById(R.id.lblRoomCodeN);
        lblPlayersReady = findViewById(R.id.lblPlayersReadyN);
        lblPlayerCount = findViewById(R.id.lblPlayerCountN);
        lblSlovo = findViewById(R.id.lblSlovoN);
        inputDrzava = findViewById(R.id.inputDrzavaN);
        inputGrad = findViewById(R.id.inputGrad);
        inputIme =  findViewById(R.id.inputIme);
        inputBiljka = findViewById(R.id.inputBiljka);
        inputZivotinja = findViewById(R.id.inputZivotinja);
        inputPlanina = findViewById(R.id.inputPlanina);
        inputReka = findViewById(R.id.inputReka);
        inputPredmet = findViewById(R.id.inputPredmet);
        predloziDrzava = findViewById(R.id.imgDrzava);
        predloziGrad = findViewById(R.id.imgGrad);
        predloziIme = findViewById(R.id.imgIme);
        predloziBiljka = findViewById(R.id.imgBiljka);
        predloziZivotinja = findViewById(R.id.imgZivotinja);
        predloziPlanina = findViewById(R.id.imgPlanina);
        predloziReka = findViewById(R.id.imgReka);
        predloziPredmet = findViewById(R.id.imgPredmet);

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
        dataDictKeys[0] = "drzava";
        dataDictKeys[1] = "grad";
        dataDictKeys[2] = "ime";
        dataDictKeys[3] = "biljka";
        dataDictKeys[4] = "zivotinja";
        dataDictKeys[5] = "planina";
        dataDictKeys[6] = "reka";
        dataDictKeys[7] = "predmet";
        timer = new Timer();
        btnTimer = new Timer();
        playerInfoImage = findViewById(R.id.imgPlayerInfo);
        kickNotificationImage = findViewById(R.id.imgKickNotification);

        playerBuilder = new AlertDialog.Builder(this);
        playerInfoDialogLayout = LayoutInflater.from(this).inflate(R.layout.player_info_dialog,null);
        playerBuilder.setView(playerInfoDialogLayout);
        playerInfoDialog = playerBuilder.create();

        kickBuilder = new AlertDialog.Builder(this);
        kickDialogLayout = LayoutInflater.from(this).inflate(R.layout.kick_layout,null);
        kickBuilder.setView(kickDialogLayout);
        kickDialog = kickBuilder.create();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        playerInfoImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                playerInfoDialog.show();
                playerInfoDialog.setCancelable(true);

                // ovaj deo mora nakon sto ide dialog.show
                lp.copyFrom(playerInfoDialog.getWindow().getAttributes());
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                lp.width = (int) (displayMetrics.widthPixels * 0.9);
                lp.height = (int) (displayMetrics.heightPixels * 0.85);
                //Log.v("WIDTH", String.valueOf(lp.width));
               // Log.v("HEIGHT", String.valueOf(lp.height));
                playerInfoDialog.getWindow().setAttributes(lp);
                playerInfoDialog.getWindow().setGravity(Gravity.CENTER);
            }
        });
        ddlRounds =  playerInfoDialogLayout.findViewById(R.id.spinner2);
        linearLayout = (LinearLayout) playerInfoDialogLayout.findViewById(R.id.mainLinearLayout);
        btnOK = playerInfoDialogLayout.findViewById(R.id.btnOK);

        btnOK.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                playerInfoDialog.dismiss();
            }
        });

        //WindowManager.LayoutParams klp = new WindowManager.LayoutParams();
        kickNotificationImage.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               kickDialog.show();
               kickDialog.setCancelable(true);
            }
        });
        voteFor = kickDialogLayout.findViewById(R.id.btnFor);
        voteAgainst = kickDialogLayout.findViewById(R.id.btnAgainst);
        txbToBeKicked = kickDialogLayout.findViewById(R.id.txbToBeKicked);

        voteFor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String mode = "FOR";
                JSONObject obj = new JSONObject();
                try {
                    obj.put("roomCode",roomCode);
                    obj.put("username",username);
                    obj.put("mode",mode);
                    socket.emit("voteKickCounterM",obj);
                    kickDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        voteAgainst.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String mode = "AGAINST";
                JSONObject obj = new JSONObject();
                try {
                    obj.put("roomCode",roomCode);
                    obj.put("username",username);
                    obj.put("mode",mode);
                    socket.emit("voteKickCounterM",obj);
                    kickDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        String sessionToken = "";
        Bundle extras = gameActivity.this.getIntent().getExtras();
        if(extras == null){
            Snackbar.make(findViewById(R.id.coordinatorView),"Nedostaju parametri za pokretanje igre, pokušajte ponovo!",Snackbar.LENGTH_LONG);
        }else{
            username = extras.getString("USERNAME");
            roomCode = extras.getString("ROOMCODE");

            users = new ArrayList<String>(Collections.singleton(username));
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_list_item_1, users);

            if(Pattern.matches(usernameReg,username) && Pattern.matches(roomReg,roomCode)){

                Context context = gameActivity.this;
                SharedPreferences sharedPref = context.getSharedPreferences("zemljopisData", Context.MODE_PRIVATE);
                sessionToken = sharedPref.getString("sessionToken", "");
                sharedPref.edit().putString("username",username).apply();
                sharedPref.edit().putString("roomCode",roomCode).apply();

                if(Pattern.matches(sessionReg,sessionToken)){
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("username", username);
                        obj.put("roomCode", roomCode);
                        obj.put("sessionToken", sessionToken);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    socket.emit("joinRoomReqM",obj);
                }else {
                    Snackbar.make(findViewById(R.id.coordinatorView),"Došlo je do problema ,probajte da napravite novu sobu!",Snackbar.LENGTH_LONG).show();
                }
            }else{
                Snackbar.make(findViewById(R.id.coordinatorView),"Format sobe ili korisnickog imena nisu validni!",Snackbar.LENGTH_LONG).show();
            }
        }

        socket.on("load",new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    if(result.getBoolean("Success")){
                        roundNumber = result.getString("roundNumber");
                        vreme = Integer.valueOf(result.getString("vreme"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    lblPoeni.setText(result.getString("points"));
                                    lblRoom.setText(roomCode);
                                    lblPlayersReady.setText(result.getString("playersReady"));
                                    lblPlayerCount.setText(result.getString("playerCount"));
                                    lblRunda.setText(result.getString("roundNumber"));
                                    lblVreme.setText(String.valueOf(vreme));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
                        //ddlRounds.setOnItemSelectedListener(gameActivity.this);
                        points = Integer.valueOf(result.getString("points")) ;
                        String[] roundValues = new String[Integer.valueOf(roundNumber)];
                        for(int i =1;i<= Integer.valueOf(roundNumber);i++){
                            roundValues[i-1] = String.valueOf(i);
                        }

                        roundValuesList = new ArrayList<String>(Arrays.asList(roundValues));

                        roundAdapter = new ArrayAdapter(gameActivity.this,android.R.layout.simple_spinner_item,roundValuesList);
                        roundAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        String[] locUser = new String[1];
                        locUser[0] = username;
                        userList = new ArrayList<String>(Arrays.asList(locUser));
                        /*
                        adapterPlayer = new ArrayAdapter(gameActivity.this,android.R.layout.simple_spinner_item,userList);
                        adapterPlayer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                         */
                        historyData.put(username,new JSONObject());

                        pList.put(username,points);
                        addPlayerInLayout(linearLayout,ddlRounds,username);
                        Set<String> keys = pList.keySet();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ddlRounds.setAdapter(roundAdapter);
                                ddlRounds.setSelection(roundValuesList.size() -1);
                                roundAdapter.notifyDataSetChanged();
                                roundAdapter.setNotifyOnChange(true);
                            }
                        });
                        pListKeys = keys.iterator();
                        Snackbar.make(findViewById(android.R.id.content),result.getString("MSG"),Snackbar.LENGTH_SHORT).show();
                        if(result.getBoolean("roundActive")){
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    readyBtn.setClickable(false);
                                    readyBtn.setText("Sačekajte");
                                    // Stuff that updates the UI

                                }
                            });



                            Snackbar.make(findViewById(android.R.id.content),"Runda u toku! Sačekajte kraj!",Snackbar.LENGTH_SHORT).show();
                        }
                    }else{
                        Snackbar.make(findViewById(android.R.id.content),result.getString("ERR_MSG"),Snackbar.LENGTH_LONG).show();
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

                Snackbar.make(findViewById(android.R.id.content),"Problem pri kreiranju runde , pokusajte ponovo!",Snackbar.LENGTH_LONG).show();

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
                    Snackbar.make(findViewById(android.R.id.content),result.getString("ERR_MSG"),Snackbar.LENGTH_LONG).show();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                lblPlayersReady.setText(result.getString("playersReady"));
                                roundNumber = result.getString("roundNumber");
                                lblRunda.setText(roundNumber);
                                roundValuesList.add(roundNumber);
                                //roundAdapter.notifyDataSetChanged();
                                ddlRounds.setSelection(roundValuesList.size() -1);

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
                JSONObject result = (JSONObject)args[0];
                try {
                    Snackbar.make(findViewById(android.R.id.content),result.getString("ERR_MSG"),Snackbar.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        socket.on("gameStartNotification", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    if(result.getBoolean("Success")){
                        Snackbar.make(findViewById(android.R.id.content),"Svi su spremni , runda počinje",Snackbar.LENGTH_SHORT).show();
                        gameStarted = true;
                        ready = false;
                        currentLetter = result.getString("currentLetter");
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                lblSlovo.setText(currentLetter);
                                lblPlayersReady.setText("0");
                                readyBtn.setText("Gotovo");
                                // Stuff that updates the UI

                            }
                        });
                        duration = vreme;

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
                                    //String[] data = new String[8];

                                    timer.cancel();
                                    timer = new Timer();
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

                                    try {
                                        obj.put("username",username);
                                        obj.put("roomCode",roomCode);
                                        obj.put("data",arr);
                                        obj.put("roundNumber",roundNumber);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if(!kicked)
                                        socket.emit("roundDataM",obj);
                                    try {
                                        historyData.getJSONObject("username").put(roundNumber,arr);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.v("VREME","VREME");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            readyBtn.setText("Nisi Spreman!");
                                            lblVreme.setText("0");

                                        }
                                    });
                                    gameStarted = false;
                                    ready = false;
                                }
                            }
                        },1000,1000);
                        String cirilicaLetter = result.getString("cirilicaLetter");
                        if(currentLetter == "ć")
                            regData = "^(c|ć|ћ)[A-Za-zа-шА-ШčČćĆžŽšŠđĐђјљњћџЂЈЉЊЋЏ ]{1,41}$";
                        else if(currentLetter == "č")
                            regData = "^(c|č|ч)[A-Za-zа-шА-ШčČćĆžŽšŠđĐђјљњћџЂЈЉЊЋЏ ]{1,41}$";
                        else if(currentLetter == "lj" || currentLetter == "nj")
                            regData = "^("+currentLetter+"|"+cirilicaLetter+")"+"[A-Za-zа-шА-ШčČćĆžŽšŠđĐђјљњћџЂЈЉЊЋЏ ]{1,40}$";
                        else if(currentLetter == "dž")
                            regData = "^(dz|dž|џ)[A-Za-zа-шА-ШčČćĆžŽšŠđĐђјљњћџЂЈЉЊЋЏ ]{1,40}$";
                        else if(currentLetter == "đ")
                            regData = "^(đ|dj|ђ)[A-Za-zа-шА-ШčČćĆžŽšŠđĐђјљњћџЂЈЉЊЋЏ ]{1,40}$";
                        else if(currentLetter == "ž")
                            regData = "^(z|ž|ж)[A-Za-zа-шА-ШčČćĆžŽšŠđĐђјљњћџЂЈЉЊЋЏ ]{1,41}$";
                        else if(currentLetter == "š")
                            regData = "^(s|š|ш)[A-Za-zа-шА-ШčČćĆžŽšŠđĐђјљњћџЂЈЉЊЋЏ ]{1,41}$";
                        else
                            regData = "^("+currentLetter+"|"+cirilicaLetter+")"+"[A-Za-zа-шА-ШčČćĆžŽšŠđĐђјљњћџЂЈЉЊЋЏ ]{1,41}$";

                    }else{
                        Snackbar.make(findViewById(android.R.id.content),"Došlo je do problema , probajte ponovo da udjete u sobu!",Snackbar.LENGTH_SHORT).show();
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        socket.on("playerLeft", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Snackbar.make(findViewById(android.R.id.content),(String)args[0],Snackbar.LENGTH_SHORT).show();
            }
        });

        socket.on("playerReadyResponse", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    if(!result.getBoolean("Success")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                readyBtn.setText("Nisi Spreman!");
                                lblPlayersReady.setText("0");

                            }
                        });
                        Snackbar.make(findViewById(android.R.id.content),result.getString("ERR_MSG"),Snackbar.LENGTH_LONG).show();

                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    readyBtn.setText(result.getString("STATE"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Snackbar.make(findViewById(android.R.id.content),result.getString("MSG"),Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        socket.on("pointsErr", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    Snackbar.make(findViewById(android.R.id.content),result.getString("MSG"),Snackbar.LENGTH_LONG).show();
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        socket.on("roundEnd", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    if(result.getBoolean("Success")) {

                        timer.cancel();
                        timer = new Timer();
                        JSONArray arr = new JSONArray();

                        for (int i = 0; i < fields.length; i++) {
                            //dodaj regex ovde
                            if (Pattern.matches(regData, fields[i].getText().toString().toLowerCase())) {
                                arr.put(fields[i].getText().toString().toLowerCase().trim().toLowerCase());

                            } else {
                                arr.put("");
                            }
                        }
                        JSONObject obj = new JSONObject();

                        try {
                            obj.put("username", username);
                            obj.put("roomCode", roomCode);
                            obj.put("data", arr);
                            obj.put("roundNumber", roundNumber);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!kicked)
                            socket.emit("roundDataM", obj);
                        try {
                            historyData.getJSONObject("username").put(roundNumber, arr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        gameStarted = false;
                        ready = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                readyBtn.setText("Nisi spreman!");
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        readyBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.v("BCLICK","BCLICK");
                if (!gameStarted) {
                    Log.v("GAMESTARTED","FALSE");
                    if (!ready) {
                        socket.emit("playerReady", roomCode);
                        Log.v("READY","FALSE");
                        ready = true;
                    } else {
                        socket.emit("playerUnReady", roomCode);
                        Log.v("READY","TRUE");
                        ready = false;
                    }
                } else {
                    Log.v("GAMESTARTED","TRUE");
                    JSONArray arr = new JSONArray();

                    allValid = true;
                    for (int i = 0; i < fields.length; i++) {
                        if (Pattern.matches(regData, fields[i].getText().toString().trim().toLowerCase())) {
                            arr.put(fields[i].getText().toString().toLowerCase().trim().toLowerCase());
                        } else {
                            allValid = false;
                            arr.put("");
                        }
                    }
                    if (allValid) {
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
                            timer = new Timer();
                            obj.put("username", username);
                            obj.put("roomCode", roomCode);
                            obj.put("data", arr);
                            obj.put("roundNumber", roundNumber);
                            socket.emit("clientEndRoundM", obj);
                            gameStarted = false;
                            ready = false;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        });
        socket.on("startVoteKickResponse", new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    if(result.getBoolean("Success")){
                        personToBeKicked = result.getString("username");
                        playerInfoDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txbToBeKicked.setText(personToBeKicked);
                                kickNotificationImage.setImageResource(R.drawable.ic_baseline_notifications_active_24);
                            }
                        });
                    }else{
                        kickInProgress = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        socket.on("voteKickCounterResponse", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    if(result.getBoolean("Success")){
                        Snackbar.make(findViewById(android.R.id.content),"Upešno glasanje",Snackbar.LENGTH_SHORT).show();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                kickNotificationImage.setImageResource(R.drawable.ic_baseline_notifications_24);
                            }
                        });
                    }else{
                        Snackbar.make(findViewById(android.R.id.content),"Neupešno glasanje",Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        socket.on("kickResult", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    kickInProgress = false;
                    if(result.getBoolean("Success")){
                        String userLoc =result.getString("username");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                kickNotificationImage.setImageResource(R.drawable.ic_baseline_notifications_24);
                            }
                        });
                        if(userLoc.equals(username)){
                            if(ready)
                                socket.emit("playerUnReady",roomCode);
                            Snackbar.make(findViewById(android.R.id.content),"Izbačeni ste iz sobe",Snackbar.LENGTH_LONG).show();
                            finish();
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    linearLayout.removeView(playerLayouts.get(userLoc));
                                }
                            });
                            Snackbar.make(findViewById(android.R.id.content),"Igrač " + userLoc + "je izbačen",Snackbar.LENGTH_LONG).show();
                            pList.remove(userLoc);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        socket.on("playerList", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    if(result.getString("MODE").equals("START")){
                        JSONObject players = result.getJSONObject("players");
                        Iterator<String> keys = players.keys();
                        while(keys.hasNext()){
                            String usernameL = keys.next();
                            if(!pList.containsKey(usernameL)){
                                pList.put(usernameL,players.getInt(usernameL));
                                addPlayerInLayout(linearLayout,ddlRounds,usernameL);
                            }
                        }
                        if(gameStarted){
                            //do somehting
                        }
                    }else if(result.getString("UPDATE").equals("UPDATE")){
                        JSONObject players = result.getJSONObject("players");
                        Iterator<String> keys = players.keys();
                        while (keys.hasNext()) {
                            String usernameL = keys.next();
                            if(pList.containsKey(usernameL)){
                                pList.put(usernameL,pList.get(usernameL) + players.getInt(usernameL));
                                runOnUiThread(new Runnable(){
                                    @Override
                                    public void run(){
                                        playerPointFields.get(usernameL).setText(String.valueOf(pList.get(usernameL)));
                                    }
                                });
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    void addPlayerInLayout(LinearLayout linearLayout,Spinner roundSpinner,String player){
        LinearLayout l = new LinearLayout(this);
        l.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setWeightSum(1);
        playerLayouts.put(player,l);
        TextView tName = new TextView(this);
        tName.setId(View.generateViewId());
        l.addView(tName);
        TextView tPoints = new TextView(this);
        tPoints.setId(View.generateViewId());
        playerPointFields.put(player,tPoints);
        l.addView(tPoints);
        ImageView img = new ImageView(this);
        img.setId(View.generateViewId());
        l.addView(img);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tName.setText(player);
                tName.setPadding(5,0,0,0);
                tName.setTextSize(13);
                tName.setGravity(Gravity.CENTER_HORIZONTAL);
                tName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT,0.8f));
                tPoints.setText(String.valueOf(pList.get(player)));
                tPoints.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT,0.1f));
                img.setImageResource(R.drawable.ic_baseline_person_off_24);
               // img.setColorFilter(ContextCompat.getColor(gameActivity.this, R.color.teal_700), android.graphics.PorterDuff.Mode.SRC_IN);
                img.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT,0.1f));
            }
        });
        tName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String targetRound = roundSpinner.getSelectedItem().toString();
                try {
                    if (!targetRound.equals(roundNumber)) {
                        if (historyData.has(player)) {
                            if(historyData.getJSONObject(player).has(targetRound)) {
                                JSONArray arr = historyData.getJSONObject(player).getJSONArray(targetRound);
                                playerInfoDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (int i = 0; i < fields.length; i++) {
                                            try {
                                                fields[i].setText(arr.getString(i));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });

                            }else {
                                JSONObject obj = new JSONObject();
                                obj.put("roomCode", roomCode);
                                obj.put("player", player);
                                obj.put("targetRound", targetRound);
                                socket.emit("historyReqM", obj);
                            }
                        }else{
                            historyData.put(player,new JSONObject());
                            JSONObject obj = new JSONObject();
                            obj.put("roomCode",roomCode);
                            obj.put("player",player);
                            obj.put("targetRound",targetRound);
                            socket.emit("historyReqM",obj);
                        }
                    }else{
                        Snackbar.make(findViewById(android.R.id.content), "Trenutna runda još nije završena.", Snackbar.LENGTH_SHORT).show();
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!kickInProgress){
                    kickInProgress = true;
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("roomCode",roomCode);
                        obj.put("usernameM",player);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    socket.emit("voteKickStartM",obj);
                }else{
                    Snackbar.make(findViewById(android.R.id.content),"Glasanje je već u toku", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        linearLayout.addView(l);
    }

}