package com.example.zemljopis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    Socket socket;
    Boolean emulator;
    Realm realm;

    //NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    protected void showNoInternetDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout_dialog = LayoutInflater.from(this).inflate(R.layout.no_internet_dialog,null);
        builder.setView(layout_dialog);

        AppCompatButton btnOK = layout_dialog.findViewById(R.id.btnOK);

        //Show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(true);

        dialog.getWindow().setGravity(Gravity.CENTER);

        btnOK.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        emulator = false;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button napravi = (Button) findViewById(R.id.btnNapravi);
        Button pridruzi = findViewById(R.id.btnPridruzi);
        Button vrati = findViewById(R.id.btnVrati);
        Button btnOffline = findViewById(R.id.btnOffline);
        Spinner brojIgracaSpinner = findViewById(R.id.ddlIgraci);
        Spinner vremeSpinner = findViewById(R.id.ddlVreme);
        EditText sobaBox = (EditText) findViewById(R.id.inputSoba);
        EditText imeBox = (EditText) findViewById(R.id.inputKIme);
        String[] igracArraySpinner = new String[]{
                "1", "2", "3", "4", "5", "6", "7", "8"
        };
        ArrayAdapter<String> igracAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, igracArraySpinner);
        igracAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brojIgracaSpinner.setAdapter(igracAdapter);
        String[] vremeArraySpinner = new String[]{
                "60", "90", "120"
        };
        ArrayAdapter<String> vremeAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, vremeArraySpinner);
        vremeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vremeSpinner.setAdapter(vremeAdapter);
        //Snackbar.make(findViewById(R.id.coordinatorLayout),"Test",Snackbar.LENGTH_LONG).show();

        napravi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //napravi
                 /*
               Pattern p = Pattern.compile(".s");//. represents single character
                Matcher m = p.matcher("as");
                boolean b = m.matches();

                //2nd way
                boolean b2=Pattern.compile(".s").matcher("as").matches();

                //3rd way
                boolean b3 = Pattern.matches(".s", "as")
                */
                String username = imeBox.getText().toString().trim();
                //Pattern usernameRegex = Pattern.compile("/^[A-Za-zčČćĆžŽšŠđĐ ]{4,60}$/g");

                if (!emulator) {
                    if (Pattern.matches("^[A-Za-zа-шА-ШčČćĆžŽšŠđĐђјљњћџЂЈЉЊЋЏ ]{4,60}$", username)) {
                        String playerCount = brojIgracaSpinner.getSelectedItem().toString();
                        String roundTimeLimit = vremeSpinner.getSelectedItem().toString();
                        JSONObject body = new JSONObject();
                        try {
                            body.put("username", username);
                            body.put("playerCount", playerCount);
                            body.put("roundTimeLimit", roundTimeLimit);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest objectRequest = new JsonObjectRequest(
                                Request.Method.POST,
                                "http://46.40.27.131:3000/createRoom/",
                                body,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.v("RESPONSE", response.toString());
                                        if (response.has("username")) {
                                            Context context = MainActivity.this;
                                            SharedPreferences sharedPref = context.getSharedPreferences("zemljopisData", Context.MODE_PRIVATE);
                                            Intent i = new Intent(MainActivity.this, gameActivity.class);
                                            try {
                                                sharedPref.edit().putString("sessionToken", response.getString("sessionToken")).apply();
                                                i.putExtra("USERNAME", response.getString("username"));
                                                i.putExtra("ROOMCODE", response.getString("roomCode"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            startActivity(i);
                                        } else {
                                            try {
                                                Snackbar.make(findViewById(R.id.coordinatorLayout), response.getString("ERR_MSG"), Snackbar.LENGTH_LONG).show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if(error instanceof com.android.volley.NoConnectionError){
                                            showNoInternetDialog();
                                        }
                                        //Log.v("Error", error.toString());
                                    }
                                }
                        );
                        requestQueue.add(objectRequest);
                        //socket.emit("createRoomM", obj);
                    } else {
                        Snackbar.make(findViewById(R.id.coordinatorLayout), "Korisnicko ime mora da bude barem 4 karaktera dugacko, dozvoljena pisma su sprska latinica,cirilica i engleski alfabet!", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Intent i = new Intent(MainActivity.this, gameActivity.class);
                    startActivity(i);
                    /*
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    View layout_dialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.player_info_dialog,null);
                    builder.setView(layout_dialog);
                    FrameLayout fl = findViewById(R.id.scrollView2);
                    Dialog d = new Dialog(MainActivity.this);
                    d.setContentView(R.layout.player_info_dialog);
                    d.setTitle("TEST");
                    d.setCancelable(true);
                    d.show();
                    //Show dialog
                    AlertDialog dialog = builder.create();
                   // dialog.show();
                    dialog.setCancelable(true);
                    Window window = dialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    window.setGravity(Gravity.CENTER);
                    //dialog.getWindow().setGravity(Gravity.CENTER);

                     */
                }


            }
        });
        pridruzi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                Context context = v.getContext();
                SharedPreferences sharedPref = context.getSharedPreferences("zemljopisData",Context.MODE_PRIVATE);
                //sharedPref.edit().putString("sessionToken","test");
                String sToken = sharedPref.getString("sessionToken","");
                Snackbar.make(findViewById(R.id.coordinatorLayout),sToken,Snackbar.LENGTH_LONG).show();
                */

                //pridruzi
                String username = imeBox.getText().toString().trim();
                String room = sobaBox.getText().toString().trim();
                if (Pattern.matches("^[A-Za-zа-шА-ШčČćĆžŽšŠđĐђјљњћџЂЈЉЊЋЏ ]{4,30}$", username) && Pattern.matches("^[A-Za-z0-9]{8}$", room)) {
                    JSONObject body = new JSONObject();
                    try {
                        body.put("username", username);
                        body.put("roomCode", room);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest objectRequest = new JsonObjectRequest(
                            Request.Method.POST,
                            "http://46.40.27.131:3000/joinRoomSQL/",
                            body,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.v("RESPONSE", response.toString());
                                    if (response.has("username")) {
                                        Context context = MainActivity.this;
                                        SharedPreferences sharedPref = context.getSharedPreferences("zemljopisData", Context.MODE_PRIVATE);
                                        Intent i = new Intent(MainActivity.this, gameActivity.class);
                                        try {
                                            sharedPref.edit().putString("sessionToken", response.getString("sessionToken")).apply();
                                            i.putExtra("USERNAME", response.getString("username"));
                                            i.putExtra("ROOMCODE", response.getString("roomCode"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        startActivity(i);
                                    } else {
                                        try {
                                            Snackbar.make(findViewById(R.id.coordinatorLayout), response.getString("ERR_MSG"), Snackbar.LENGTH_LONG).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if(error instanceof com.android.volley.NoConnectionError){
                                        showNoInternetDialog();
                                    }
                                    //Log.v("Error", error.toString());
                                }
                            }
                    );
                    requestQueue.add(objectRequest);

                } else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout), "Korisnicko ime mora da bude barem 4 karaktera dugacko, dozvoljena pisma su sprska latinica,cirilica i engleski alfabet!Soba se sastoji od 8 alfanumerickih karaktetra!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        vrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Context context = v.getContext();
                SharedPreferences sharedPref = context.getSharedPreferences("zemljopisData",Context.MODE_PRIVATE);
                sharedPref.edit().putString("sessionToken","test").apply();
                */
                //Intent i = new Intent(v.getContext(), gameActivity.class);
                //startActivity(i);

                Context context = MainActivity.this;
                SharedPreferences sharedPref = context.getSharedPreferences("zemljopisData", Context.MODE_PRIVATE);
                String username = sharedPref.getString("username", "");
                String room = sharedPref.getString("roomCode", "");
                String sessionToken = sharedPref.getString("sessionToken", "");
                if (Pattern.matches("^[A-Za-zа-шА-ШčČćĆžŽšŠđĐђјљњћџЂЈЉЊЋЏ ]{4,30}$", username) && Pattern.matches("^[A-Za-z0-9]{8}$", room) && Pattern.matches("^[A-Za-z0-9]{48}$", sessionToken)) {

                    Intent i = new Intent(MainActivity.this, gameActivity.class);
                    i.putExtra("USERNAME", username);
                    i.putExtra("ROOMCODE", room);
                    //i.putExtra("sessionToken",sessionToken);
                    //i.putExtra("SOCKET",  (Object) socket);
                    startActivity(i);

                } else if (Pattern.matches("^[A-Za-z0-9]{48}$", sessionToken)) {
                    JsonObjectRequest objectRequest = new JsonObjectRequest(
                            Request.Method.GET,
                            "http://46.40.27.131:3000/joinRoomSQL/" + sessionToken,
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.v("RESPONSE", response.toString());
                                    if (response.has("username")) {
                                        Context context = MainActivity.this;
                                        SharedPreferences sharedPref = context.getSharedPreferences("zemljopisData", Context.MODE_PRIVATE);
                                        Intent i = new Intent(MainActivity.this, gameActivity.class);
                                        try {
                                            sharedPref.edit().putString("sessionToken", response.getString("sessionToken")).apply();
                                            i.putExtra("USERNAME", response.getString("username"));
                                            i.putExtra("ROOMCODE", response.getString("roomCode"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        startActivity(i);
                                    } else {
                                        try {
                                            Snackbar.make(findViewById(R.id.coordinatorLayout), response.getString("ERR_MSG"), Snackbar.LENGTH_LONG).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if(error instanceof com.android.volley.NoConnectionError){
                                        showNoInternetDialog();
                                    }
                                    //Log.v("Error", error.toString());
                                }
                            }
                    );
                    requestQueue.add(objectRequest);
                } else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout), "Korisnicko ime mora da bude barem 4 karaktera dugacko, dozvoljena pisma su sprska latinica,cirilica i engleski alfabet!Soba se sastoji od 8 alfanumerickih karaktetra!", Snackbar.LENGTH_LONG).show();
                }
            }
        });


        btnOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, OfflineActivity.class);
                startActivity(i);

            }
        });
    }
/*
    @Override
    protected void onStart(){
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        super.onStart();
    }

    @Override
    protected void onStop(){
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
*/
}