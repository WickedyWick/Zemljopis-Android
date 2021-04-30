package com.example.zemljopis;
import java.net.URI;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import com.example.zemljopis.StaticSocket;
public class MainActivity extends AppCompatActivity {
    Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        URI uri = URI.create("http://46.40.27.131:3000/");
        socket = IO.socket(uri);
        socket.connect();
        //socket.emit("test","test");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button napravi = (Button)findViewById(R.id.btnNapravi);
        Button pridruzi = findViewById(R.id.btnPridruzi);
        Button vrati = findViewById(R.id.btnVrati);
        Spinner brojIgracaSpinner = findViewById(R.id.ddlIgraci);
        Spinner vremeSpinner = findViewById(R.id.ddlVreme);
        EditText sobaBox = (EditText)findViewById(R.id.inputSoba);
        EditText imeBox = (EditText)findViewById(R.id.inputKIme);
        String[] igracArraySpinner = new String[] {
                "1", "2", "3", "4", "5", "6", "7","8"
        };
        ArrayAdapter<String> igracAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, igracArraySpinner);
        igracAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brojIgracaSpinner.setAdapter(igracAdapter);
        String[] vremeArraySpinner = new String[] {
                "60","90","120"
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


               if(Pattern.matches("^[A-Za-zа-шА-ШčČćĆžŽšŠđĐђјљњћџЂЈЉЊЋЏ ]{4,60}$", username)){
                    String playerCount = brojIgracaSpinner.getSelectedItem().toString();
                    String roundTimeLimit = vremeSpinner.getSelectedItem().toString();
                   JSONObject obj = new JSONObject();
                   try {
                       obj.put("username",username);
                       obj.put("playerCount",playerCount);
                       obj.put("roundTimeLimit",roundTimeLimit);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }

                    socket.emit("createRoomM", obj);
               }else{
                   Snackbar.make(findViewById(R.id.coordinatorLayout),"Korisnicko ime mora da bude barem 4 karaktera dugacko, dozvoljena pisma su sprska latinica,cirilica i engleski alfabet!",Snackbar.LENGTH_LONG).show();
               }


            }
        });
        pridruzi.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

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
                if(Pattern.matches("^[A-Za-zа-шА-ШčČćĆžŽšŠđĐђјљњћџЂЈЉЊЋЏ ]{4,30}$", username) && Pattern.matches("^[A-Za-z0-9]{8}$",room)){
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("username",username);
                        obj.put("room",room);
                        socket.emit("joinRoomSQLM",obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else{
                    Snackbar.make(findViewById(R.id.coordinatorLayout),"Korisnicko ime mora da bude barem 4 karaktera dugacko, dozvoljena pisma su sprska latinica,cirilica i engleski alfabet!Soba se sastoji od 8 alfanumerickih karaktetra!",Snackbar.LENGTH_LONG).show();
                }
            }
        });
        vrati.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                /*
                Context context = v.getContext();
                SharedPreferences sharedPref = context.getSharedPreferences("zemljopisData",Context.MODE_PRIVATE);
                sharedPref.edit().putString("sessionToken","test").apply();
                */
                //Intent i = new Intent(v.getContext(), gameActivity.class);
                //startActivity(i);
                String username = imeBox.getText().toString().trim();
                String room = sobaBox.getText().toString().trim();
                if(Pattern.matches("^[A-Za-zа-шА-ШčČćĆžŽšŠđĐђјљњћџЂЈЉЊЋЏ ]{4,30}$", username) && Pattern.matches("^[A-Za-z0-9]{8}$",room)){

                    Intent i = new Intent(MainActivity.this, gameActivity.class);
                    i.putExtra("USERNAME",username);
                    i.putExtra("ROOMCODE",room);
                    //i.putExtra("SOCKET",  (Object) socket);
                    startActivity(i);
                }else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout),"Korisnicko ime mora da bude barem 4 karaktera dugacko, dozvoljena pisma su sprska latinica,cirilica i engleski alfabet!Soba se sastoji od 8 alfanumerickih karaktetra!",Snackbar.LENGTH_LONG).show();
                }
            }
        });

        socket.on("createRoomSQLResponse", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                try {
                    JSONObject result = (JSONObject)args[0];

                    if(result.getBoolean("Success") == true){
                        Context context = MainActivity.this;
                        SharedPreferences sharedPref = context.getSharedPreferences("zemljopisData",Context.MODE_PRIVATE);
                        sharedPref.edit().putString("sessionToken",result.getString("sessionToken")).apply();
                        Intent i = new Intent(MainActivity.this, gameActivity.class);
                        i.putExtra("USERNAME",result.getString("username"));
                        i.putExtra("ROOMCODE",result.getString("roomCode"));
                        StaticSocket.socket = socket;
                        startActivity(i);

                    }else{
                        Snackbar.make(findViewById(R.id.coordinatorLayout),"Doslo je do problema u kreiranju sobe, pokusajte ponovo!",Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /*/
                    Ovako izlegda args[0]:
                    {"Success":true,"roomCode":"KxL7dbKx","username":"Aleksa","sessionToken":"LxA29JuqOyEXXu4HP1Putf2\/jYaRpaRSGYk2f2MxLDEOZ0gj"}
                 */
                //Log.i("CREATEROOMRESPONSE" ,room);
            }
        });
        socket.on("joinRoomSQLResponse",new Emitter.Listener(){
           public void call(Object... args){
               JSONObject result = (JSONObject)args[0];
               try {
                   if(result.getBoolean("Success")){
                       Context context = MainActivity.this;
                       SharedPreferences sharedPref = context.getSharedPreferences("zemljopisData",Context.MODE_PRIVATE);
                       sharedPref.edit().putString("sessionToken",result.getString("sessionToken")).apply();
                       Intent i = new Intent(MainActivity.this,gameActivity.class);
                       i.putExtra("USERNAME",result.getString("username"));
                       i.putExtra("ROOMCODE",result.getString("roomCode"));
                       startActivity(i);
                   }else{
                       Snackbar.make(findViewById(R.id.coordinatorLayout),result.getString("ERR_MSG"),Snackbar.LENGTH_LONG).show();
                   }

               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
        });







    }


}