package com.example.zemljopis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    Hashtable<String,Integer> pList = new Hashtable<String,Integer>();
    Hashtable<String,Integer> pListIds = new Hashtable<String,Integer>();
    JSONObject historyData = new JSONObject();
    Iterator<String> pListKeys = null;
    List<String> roundValuesList = null;
    Button resultBtn;
    Spinner ddlRounds = null;
    Spinner spinnerPlayer = null;
    ArrayAdapter roundAdapter = null;
    ArrayAdapter adapterPlayer = null;
    List<String> userList = null;
    String[] dataDictKeys = new String[8];
    Button btnFor = null;
    Button btnAgainst = null;
    Button btnKick = null;
    void predlozi(String text, String slovo, String kat,View v){
        if(text.startsWith(slovo)){
            if(Pattern.matches(regData,text)){
                JSONObject obj = new JSONObject();
                try {
                    obj.put("predlog",text);
                    obj.put("slovo",slovo);
                    obj.put("kategorija",kat);
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
                Snackbar.make(findViewById(R.id.coordinatorID),"Uspesno predlozeno!",Snackbar.LENGTH_SHORT).show();
            }
            else
                Snackbar.make(findViewById(R.id.coordinatorID),"Predlog sadrzi nedozvoljene karaktere!",Snackbar.LENGTH_SHORT).show();
        }else
            Snackbar.make(findViewById(R.id.coordinatorID),"Drzava ne pocinje na slovo " + currentLetter,Snackbar.LENGTH_SHORT).show();
    }
    void disableHistoryButton(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultBtn.setClickable(false);
            }
        });
    }

    void enableHistoryButton(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultBtn.setClickable(true);
            }
        });
    }
    void showVoteButtons(){
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                btnAgainst.setVisibility(View.VISIBLE);
                btnFor.setVisibility(View.VISIBLE);
            }
        });

    }
    void hideVoteButtons(){
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                btnAgainst.setVisibility(View.INVISIBLE);
                btnFor.setVisibility(View.INVISIBLE);
            }
        });

    }
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
                inputDrzava.setFocusable(true);
                inputGrad.setFocusable(true);
                inputIme.setFocusable(true);
                inputBiljka.setFocusable(true);
                inputZivotinja.setFocusable(true);
                inputPlanina.setFocusable(true);
                inputReka.setFocusable(true);
                inputPredmet.setFocusable(true);
                // Stuff that updates the UI

            }
        });

    }
    void disableAllInputs(){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                inputDrzava.setFocusable(false);
                inputGrad.setFocusable(false);
                inputIme.setFocusable(false);
                inputBiljka.setFocusable(false);
                inputZivotinja.setFocusable(false);
                inputPlanina.setFocusable(false);
                inputReka.setFocusable(false);
                inputPredmet.setFocusable(false);
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
    RelativeLayout left;
    LinearLayout mainLeft;
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //left  = findViewById(R.id.navRelativeLayout);
        mainLeft = findViewById(R.id.navLinearLayout);
        drawerLayout = findViewById(R.id.drawer_layout);
        //drawerLayout.openDrawer(left);

        URI uri = URI.create("http://46.40.27.131:3000/");
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
        resultBtn = findViewById(R.id.resultsButton);
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
        ddlRounds = findViewById(R.id.roundSpinner);
        spinnerPlayer = findViewById(R.id.playerSpinner);
        btnAgainst =  findViewById(R.id.btnAgainst);
        btnFor = findViewById(R.id.btnFor);
        btnKick = findViewById(R.id.btnKick);
       // socket.emit("test","test");

        Bundle extras = gameActivity.this.getIntent().getExtras();
        if(extras == null){
            Snackbar.make(findViewById(R.id.coordinatorID),"Nedostaju potrebni parametri!",Snackbar.LENGTH_LONG).show();
        }else{
            //Snackbar.make(findViewById(R.id.coordinatorID),extras.getString("USERNAME") + " " + extras.getString("ROOMCODE"),Snackbar.LENGTH_LONG).show();
            username = extras.getString("USERNAME");
            roomCode = extras.getString("ROOMCODE");
            //vidi da ovo convertujes u xml nekako
            TextView lblLocal = new TextView(this);
            Integer id = View.generateViewId();
            pListIds.put(username,id);

            mainLeft.addView(lblLocal);
            lblLocal.setId(id);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lblLocal.setText(username);
                    lblLocal.setBackgroundColor(Color.rgb(187,134,252));
                    lblLocal.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                    lblLocal.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    lblLocal.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30);
                    lblLocal.setPadding(10,10,10,10);
                    lblLocal.setGravity(Gravity.CENTER);
                }
            });

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

        btnKick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userN = spinnerPlayer.getSelectedItem().toString();
                JSONObject obj = new JSONObject();
                try {
                    obj.put("usernameM",userN);
                    obj.put("roomCode",roomCode);
                    socket.emit("voteKickStartM",obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


        btnAgainst.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("mode", "AGAINST");
                    obj.put("roomCode", roomCode);
                    obj.put("username", username);
                    socket.emit("voteKickCounterM",obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        btnFor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                JSONObject obj = new JSONObject();
                try {
                    obj.put("mode", "FOR");
                    obj.put("roomCode", roomCode);
                    obj.put("username", username);
                    socket.emit("voteKickCounterM",obj);
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
                    if(result.getBoolean("Success")){
                        if(result.getString("username").equals(username)){
                            if(ready)
                                socket.emit("playerUnReady",roomCode);
                            gameActivity.this.finish();
                        }
                    }else{
                        //ovo ne radi
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lblPlayerCount.setText(String.valueOf((Integer.valueOf(lblPlayerCount.getText().toString()) -1)));
                                try {
                                    mainLeft.removeView(findViewById(pListIds.get(result.getString("username"))));
                                    userList.remove(result.getString("username"));
                                    adapterPlayer.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        pList.remove(result.getString("username"));
                        pListIds.remove(result.getString("username"));
                        Set<String> keys = pList.keySet();
                        pListKeys = keys.iterator();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        socket.on("startVoteKickResponse", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    if(result.getBoolean("Success")){
                        Snackbar.make(findViewById(R.id.coordinatorID),"Izbacivanje za igrača : "+ result.getString("username"),Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        resultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String targetRound = ddlRounds.getSelectedItem().toString();
                String player = spinnerPlayer.getSelectedItem().toString();
                disableAllPButtons();
                disableAllInputs();
                disableHistoryButton();
                try {
                if(!targetRound.equals(roundNumber)) {
                    if (historyData.has(player)) {
                        if (historyData.getJSONObject(player).has(targetRound)) {
                            JSONArray arr = historyData.getJSONObject(player).getJSONArray(targetRound);
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

                        } else {
                            Snackbar.make(findViewById(R.id.coordinatorID),"HistReq",Snackbar.LENGTH_SHORT);
                            JSONObject obj = new JSONObject();
                            obj.put("roomCode", roomCode);
                            obj.put("player", player);
                            obj.put("targetRound", targetRound);
                            socket.emit("historyReqM", obj);
                        }
                    }else{
                        Snackbar.make(findViewById(R.id.coordinatorID),"HistReq",Snackbar.LENGTH_SHORT);
                        historyData.put(player,new JSONObject());
                        JSONObject obj = new JSONObject();
                        obj.put("roomCode",roomCode);
                        obj.put("player",player);
                        obj.put("targetRound",targetRound);
                        socket.emit("historyReqM",obj);
                    }

                }else{
                    Snackbar.make(findViewById(R.id.coordinatorID),"Trenutna runda jos nije završena",Snackbar.LENGTH_SHORT);
                    enableHistoryButton();
                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        socket.on("historyReqResponse", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Snackbar.make(findViewById(R.id.coordinatorID),"HistUspeh",Snackbar.LENGTH_SHORT);
                enableHistoryButton();
                disableAllInputs();
                disableAllPButtons();
                JSONObject result = (JSONObject)args[0];

                try {
                    if(result.getBoolean("Success")){

                        JSONObject data = result.getJSONObject("Data");
                        JSONArray arr = new JSONArray();
                        String username = result.getString("username");
                        String round = result.getString("round");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for(int i=0;i<dataDictKeys.length;i++){
                                    try {
                                        String temp = data.getString(dataDictKeys[i]);
                                        fields[i].setText(data.getString(dataDictKeys[i]));
                                        arr.put(i,temp);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        historyData.getJSONObject(username).put(round,arr);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        socket.on("load",new Emitter.Listener(){


            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    if(result.getBoolean("Success")){
                        roundNumber = result.getString("roundNumber");
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
                        adapterPlayer = new ArrayAdapter(gameActivity.this,android.R.layout.simple_spinner_item,userList);
                        adapterPlayer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        historyData.put(username,new JSONObject());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ddlRounds.setAdapter(roundAdapter);
                                roundAdapter.notifyDataSetChanged();
                                ddlRounds.setSelection(roundValuesList.size() -1);
                                spinnerPlayer.setAdapter(adapterPlayer);
                                adapterPlayer.notifyDataSetChanged();
                            }
                        });



                        pList.put(username,points);
                        Set<String> keys = pList.keySet();
                        pListKeys = keys.iterator();
                        Snackbar.make(findViewById(R.id.coordinatorID),result.getString("MSG"),Snackbar.LENGTH_SHORT).show();
                        if(result.getBoolean("roundActive")){
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    readyBtn.setClickable(false);
                                    readyBtn.setText("Sačekajte");
                                    // Stuff that updates the UI

                                }
                            });



                            Snackbar.make(findViewById(R.id.coordinatorID),"Runda u toku! Sačekajte kraj!",Snackbar.LENGTH_SHORT).show();
                        }
                    }else{
                        Snackbar.make(findViewById(R.id.coordinatorID),result.getString("ERR_MSG"),Snackbar.LENGTH_LONG).show();
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

                Snackbar.make(findViewById(R.id.coordinatorID),"Problem pri kreiranju runde , pokusajte ponovo!",Snackbar.LENGTH_LONG).show();

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
                    Snackbar.make(findViewById(R.id.coordinatorID),result.getString("ERR_MSG"),Snackbar.LENGTH_LONG).show();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            try {
                                lblPlayersReady.setText(result.getString("playersReady"));
                                roundNumber = result.getString("roundNumber");
                                lblRunda.setText(roundNumber);
                                roundValuesList.add(roundNumber);
                                roundAdapter.notifyDataSetChanged();
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
                Snackbar.make(findViewById(R.id.coordinatorID),"Doslo je do problema prilikom unosenja podataka, podaci nevažeci!",Snackbar.LENGTH_LONG).show();
            }
        });
        socket.on("gameStartNotification", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject result = (JSONObject)args[0];
                try {
                    if(result.getBoolean("Success")){
                        Snackbar.make(findViewById(R.id.coordinatorID),result.getString("MSG"), Snackbar.LENGTH_SHORT).show();
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {

                                    currentLetter = result.getString("currentLetter");
                                    lblSlovo.setText(currentLetter);
                                    lblPlayersReady.setText("0");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                readyBtn.setText("Gotovo");
                                // Stuff that updates the UI

                            }
                        });
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
                                    timer = new Timer();
                                    timer.cancel();
                                }
                            }
                        },1000,1000);

                        gameStarted = true;
                        ready = false;
                        enableAllInputs();
                        clearAllInputs();
                        disableAllPButtons();

                    }else{
                        //reset game logic

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
                    JSONObject players = result.getJSONObject("players");
                    //ovo ne radi


                    Iterator<String> keys = players.keys();
                    String mode = result.getString("MODE");


                    if( mode.equals("START")){

                        while(keys.hasNext()){
                            String username = keys.next();

                            if(!pList.containsKey(username)){

                                pList.put(username,players.getInt(username));
                                userList.add(username);
                                TextView p = new TextView(gameActivity.this);
                                Integer id = View.generateViewId();
                                pListIds.put(username,id);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mainLeft.addView(p);
                                    }
                                });


                                p.setId(id);
                                String points = String.valueOf(players.getInt(username));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapterPlayer.notifyDataSetChanged();
                                        p.setText(username + " | " + points);
                                        p.setBackgroundColor(Color.rgb(187,134,252));
                                        p.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                                        p.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                        p.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30);
                                        p.setGravity(Gravity.CENTER);
                                        p.setPadding(10,10,10,10);
                                        //p.setVisibility(View.INVISIBLE); overlapuje fixuj top.of constrains
                                    }
                                });
                            }

                        }
                    }else if(mode.equals("UPDATE")){
                        Log.d("test","Update");
                        while(keys.hasNext()){
                            String usernameM = keys.next();
                            if(pList.containsKey(usernameM) && usernameM != username){
                                Integer curr =pList.get(usernameM);
                                curr += players.getInt(usernameM);
                                pList.put(usernameM,curr);
                                TextView t = findViewById(pListIds.get(usernameM));
                                Integer finalCurr = curr;
                                String text = (String) t.getText();
                                String[] splitted = text.split("|");
                                splitted[1] = String.valueOf(finalCurr);
                                t.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Snackbar.make(findViewById(R.id.coordinatorID),"TEST",Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        t.setText(usernameM +" | " + splitted[1]);
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
                            Snackbar.make(findViewById(R.id.coordinatorID),"Vrednosti polja nisu validna!",Snackbar.LENGTH_SHORT).show();
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
                            Snackbar.make(findViewById(R.id.coordinatorID),result.getString("MSG"),Snackbar.LENGTH_LONG).show();
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
                Snackbar.make(findViewById(R.id.coordinatorID),args[0].toString(),Snackbar.LENGTH_LONG).show();
            }
        });
        socket.on("playerJoinMsg", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Snackbar.make(findViewById(R.id.coordinatorID),args[0].toString(),Snackbar.LENGTH_SHORT).show();

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
                Snackbar.make(findViewById(R.id.coordinatorID),args[0].toString(),Snackbar.LENGTH_LONG).show();
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
                        Snackbar.make(findViewById(R.id.coordinatorID),result.getString("MSG"),Snackbar.LENGTH_SHORT).show();
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
                        Snackbar.make(findViewById(R.id.coordinatorID),"Problem sa rezultatima",Snackbar.LENGTH_LONG).show();
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
                predlozi(text, currentLetter, "d", v);
            }
        });
        predloziGrad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputGrad.getText().toString().trim().toLowerCase();
                predlozi(text,currentLetter,"g",v);
            }
        });
        predloziIme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputIme.getText().toString().trim().toLowerCase();
                predlozi(text,currentLetter,"i",v);
            }
        });
        predloziBiljka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputBiljka.getText().toString().trim().toLowerCase();
                predlozi(text,currentLetter,"b",v);
            }
        });
        predloziZivotinja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputZivotinja.getText().toString().trim().toLowerCase();
                predlozi(text,currentLetter,"z",v);
            }
        });
        predloziPlanina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputPlanina.getText().toString().trim().toLowerCase();
                predlozi(text,currentLetter,"p",v);
            }
        });
        predloziReka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputReka.getText().toString().trim().toLowerCase();
                predlozi(text,currentLetter,"r",v);
            }
        });
        predloziPredmet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputPredmet.getText().toString().trim().toLowerCase();
                predlozi(text,currentLetter,"pr",v);
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
                        Snackbar.make(findViewById(R.id.coordinatorID),"Problem prilikom evaluacije , runda ponistena!",Snackbar.LENGTH_LONG).show();
                    }else{
                        Snackbar.make(findViewById(R.id.coordinatorID),"Evaluacija završena",Snackbar.LENGTH_SHORT).show();
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
                                roundValuesList.add(roundNumber);
                                roundAdapter.notifyDataSetChanged();
                                ddlRounds.setSelection(roundValuesList.size() -1);
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