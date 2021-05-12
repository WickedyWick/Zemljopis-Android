package com.example.zemljopis;

import io.realm.RealmObject;

public class Data extends RealmObject {
    private int rDataID;
    private String naziv;
    private String slovo;
    private int kategorija;
    private int oData;

    public Data(){

    }

    public Data(int rDataID,String naziv,String slovo, int kategorija, int oData){
        this.rDataID = rDataID;
        this.naziv = naziv;
        this.slovo = slovo;
        this.kategorija = kategorija;
        this.oData = oData;
    }

    public int getrDataID(){return rDataID;}
    public String getNaziv(){return naziv;}
    public String getSlovo(){return slovo;}
    public int getKategorija(){return kategorija;}
    public int getoData(){return oData;}



}
