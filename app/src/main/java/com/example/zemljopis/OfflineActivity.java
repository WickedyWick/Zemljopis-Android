package com.example.zemljopis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import com.example.zemljopis.Utils.*;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;

public class OfflineActivity extends AppCompatActivity {

    private String copyBundledRealmFile(InputStream inputStream, String outFileName) {
        try {
            File file = new File(this.getFilesDir(), outFileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    void disablePretraziButton(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnPretrazi.setClickable(false);
            }
        });
    }void enablePretraziButton(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnPretrazi.setClickable(true);
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

    void evaluation(String mode){


        String realmName = "zemljopis.realm";
        RealmConfiguration config = new RealmConfiguration.Builder().name(realmName).build();
        //Realm backgroundThreadRealm = Realm.getInstance(config);
        Realm realm = Realm.getInstance(config);
        RealmQuery<referencedata> dataRealmQuery = realm.where(referencedata.class);

        if(mode == "FORCE") {
            for (int i = 0; i < arr.length; i++) {
                try {
                    String results = dataRealmQuery.equalTo("naziv", arr[i]).equalTo("kategorija", i).equalTo("slovo",currentLetter).findFirst().getNaziv();
                    int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fields[finalI].setText(fields[finalI].getText() + " + 10");
                            poeni +=10;
                        }
                    });
                }catch(NullPointerException e){
                    int finalI1 = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fields[finalI1].setText(fields[finalI1].getText() + " + 0");
                        }
                    });
                }
            }
        }else if(mode == "TIMER"){
            for (int i = 0; i < fields.length; i++) {
                arr[i] = fields[i].getText().toString().toLowerCase().trim().toLowerCase();

                try {
                    String results = dataRealmQuery.equalTo("naziv", arr[i]).equalTo("kategorija", i).findFirst().getNaziv();
                    int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                fields[finalI].setText(fields[finalI].getText() + " + 10");
                                poeni +=10;
                        }
                    });
                }catch(NullPointerException e){
                    int finalI1 = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fields[finalI1].setText(fields[finalI1].getText() + " + 0");
                        }
                    });
                }
            }
        }

        historyData.put(currentRound,arr);
        currentRound +=1;
        gameActive = false;
        disableAllInputs();
        enablePretraziButton();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listRound.add(String.valueOf(currentRound));
                spinnerRound.setSelection(listRound.size() -1);
                btnReady.setText("Počni");
                btnReady.setClickable(true);
                lblPoeni.setText(String.valueOf(poeni));
            }
        });
        Snackbar.make(findViewById(R.id.offlineConstraint),"Evaluacija završena!",Snackbar.LENGTH_SHORT).show();
    }
    private void fillDict(){
        letterDict.put("a","а");
        letterDict.put("b","б");
        letterDict.put("v","в");
        letterDict.put("g","г");
        letterDict.put("d","д");
        letterDict.put("đ","ђ");
        letterDict.put("e","е");
        letterDict.put("ž","ж");
        letterDict.put("z","з");
        letterDict.put("i","и");
        letterDict.put("j","ј");
        letterDict.put("k","к");
        letterDict.put("l","л");
        letterDict.put("lj","љ");
        letterDict.put("m","м");
        letterDict.put("n","н");
        letterDict.put("nj","њ");
        letterDict.put("o","о");
        letterDict.put("p","п");
        letterDict.put("r","р");
        letterDict.put("s","с");
        letterDict.put("t","т");
        letterDict.put("ć","ћ");
        letterDict.put("u","у");
        letterDict.put("f","ф");
        letterDict.put("h","х");
        letterDict.put("c","ц");
        letterDict.put("č","ч");
        letterDict.put("dž","џ");
        letterDict.put("š","ш");
    }
    void enableMainButton(){
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                btnReady.setClickable(true);
            }
        });

    }
    void disableMainButton(){
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                btnReady.setClickable(false);
            }
        });

    }

    String[] letters = new String[30];

    Boolean gameActive;
    Button btnReady =null;
    Button btnPretrazi = null;
    EditText inputDrzava = null;
    EditText inputGrad = null;
    EditText inputIme = null;
    EditText inputBiljka = null;
    EditText inputZivotinja = null;
    EditText inputPlanina = null;
    EditText inputReka = null;
    EditText inputPredmet = null;
    EditText[] fields = new EditText[8];
    TextView lblVreme = null;
    TextView lblPoeni = null;
    String currentLetter = "";
    String regData = "";
    String cirilicaLetter = "";
    Timer timer;
    TextView lblSlovo = null;
    int duration;
    int currentRound =1 ;
    Boolean allValid = true;
    String[] arr = new String[8];
    Spinner spinnerRound = null;
    List<String> listRound;
    List<String> letterList;
    ArrayAdapter<String> arrayAdapter;
    Hashtable<Integer,String[]> historyData = new Hashtable<Integer, String[]>();
    Hashtable<String,String> letterDict = new Hashtable<String,String>(){};
    int poeni =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);Realm.init(this);

        //check if there is db on the device
        File f = new File(Environment.getDataDirectory().getAbsolutePath() + "/data/com.example.zemljopis/files/zemljopis.realm");
        copyBundledRealmFile(this.getResources().openRawResource(R.raw.zemljopis), "zemljopis.realm");
        if(!f.exists()){
            //importuje iz rawa

        }
        //Log.v("FXIST",String.valueOf(f.exists()));



        //Log.v("REALMEXAMPLE",dataRealmQuery.equalTo("naziv","srbija").findAll().asJSON());//
        fillDict();
        btnReady = findViewById(R.id.btnReady);
        gameActive = false;
        inputDrzava = findViewById(R.id.inputODrzava);
        inputGrad = findViewById(R.id.inputOGrad);
        inputIme = findViewById(R.id.inputOIme);
        inputBiljka = findViewById(R.id.inputOBiljka);
        inputZivotinja = findViewById(R.id.inputOZivotinja);
        inputPlanina = findViewById(R.id.inputOPlanina);
        inputReka = findViewById(R.id.inputOReka);
        inputPredmet = findViewById(R.id.inputOPredmet);
        lblVreme = findViewById(R.id.labelVreme);
        lblSlovo = findViewById(R.id.labelSlovo);
        spinnerRound = findViewById(R.id.spinnerRunda);
        btnPretrazi = findViewById(R.id.btnPretrazi);
        lblPoeni = findViewById(R.id.lblPoints);
        fields[0]= inputDrzava;
        fields[1] = inputGrad;
        fields[2] = inputIme;
        fields[3] = inputBiljka;
        fields[4] = inputZivotinja;
        fields[5] = inputPlanina;
        fields[6] = inputReka;
        fields[7] = inputPredmet;
        letters[0] = "a";
        letters[1] = "b";
        letters[2] = "v";
        letters[3] = "g";
        letters[4] = "d";
        letters[5] = "đ";
        letters[6] = "e";
        letters[7] = "ž";
        letters[8] = "z";
        letters[9] = "i";
        letters[10] = "j";
        letters[11] = "k";
        letters[12] = "l";
        letters[13] = "lj";
        letters[14] = "m";
        letters[15] = "n";
        letters[16] = "nj";
        letters[17] = "o";
        letters[18] = "p";
        letters[19] = "r";
        letters[20] = "s";
        letters[21] = "t";
        letters[22] = "ć";
        letters[23] = "u";
        letters[24] = "f";
        letters[25] = "h";
        letters[26] = "c";
        letters[27] = "č";
        letters[28] = "dž";
        letters[29] = "š";
        letterList = new ArrayList<String>(Arrays.asList(letters));
        timer= new Timer();
        listRound = new ArrayList<String>(Collections.singleton("1"));
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,listRound);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setNotifyOnChange(true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinnerRound.setAdapter(arrayAdapter);
                spinnerRound.setSelection(0);
            }
        });
        btnPretrazi.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int targetRound =  Integer.valueOf(spinnerRound.getSelectedItem().toString());
                if(targetRound == currentRound){
                    Snackbar.make(findViewById(R.id.offlineConstraint),"Trenutna runda nije završena",Snackbar.LENGTH_SHORT).show();

                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String[] lArr = historyData.get(targetRound);
                            for(int i=0;i<fields.length;i++){
                                fields[i].setText(lArr[i]);
                            }
                        }
                    });
                }
            }
        });
        btnReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameActive){
                    disableMainButton();
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


                    allValid = true;
                    for(int i=0;i<fields.length;i++){
                        //dodaj regex ovde
                        if(Pattern.matches(regData,fields[i].getText().toString().trim().toLowerCase())){
                            arr[i] = fields[i].getText().toString().toLowerCase().trim().toLowerCase();
                        }else {
                            allValid = false;
                            arr[i] = "";
                        }}
                    if(allValid){
                        timer.cancel();
                        evaluation("FORCE");
                        //evaluation
                    }else{
                        enableMainButton();
                        Snackbar.make(findViewById(R.id.offlineConstraint),"Polja mogu da sadrže samo tekst",Snackbar.LENGTH_LONG).show();
                    }

                   //regexuj rezultate

                }else{
                    //startGame
                    gameActive = true;
                    currentLetter = Utils.chooseLetter(letterList);
                    cirilicaLetter = letterDict.get(currentLetter);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnReady.setText("Gotovo");
                        }
                    });

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

                    duration = 61;
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            duration--;
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    lblVreme.setText(String.valueOf(duration));
                                    lblSlovo.setText(currentLetter);
                                    // Stuff that updates the UI

                                }
                            });

                            if(duration == 0){
                                disableMainButton();
                                evaluation("TIMER");
                                timer.cancel();
                                timer = new Timer();
                            }
                        }
                    },1000,1000);
                    disablePretraziButton();
                    clearAllInputs();
                    enableAllInputs();
                }
            }
        });

    }
}