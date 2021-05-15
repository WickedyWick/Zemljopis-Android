package com.example.zemljopis;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Utils {


    public static String chooseLetter(List<String> letters){
        Random r = new Random();
        int ind = r.nextInt(letters.size());
        String let = letters.get(ind);
        letters.remove(ind);
        return let ;
    }



}
