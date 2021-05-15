package com.example.zemljopis;

import io.realm.RealmObject;
// To add an object to your Realm Schema, extend RealmObject
public class referencedata extends RealmObject {
    private Integer rDataID;
    private String naziv;
    private String slovo;
    private Integer kategorija;
    private Integer oDataID;
    public referencedata(int rDataID,String naziv,String slovo,int kategorija,int oDataID) {
        this.rDataID =rDataID;
        this.naziv = naziv;
        this.slovo = slovo;
        this.kategorija = kategorija;
        this.oDataID = oDataID;
    }
    public referencedata(){} // RealmObject subclasses must provide an empty constructor
    public Integer getrDataID(){return rDataID;}
    public void setrDataID(int rDataID){this.rDataID = rDataID;}
    public String getNaziv(){return naziv;}
    public void setNaziv(String naziv){this.naziv = naziv;}
    public String getSlovo(){return slovo;}
    public void setSlovo(String slovo){this.slovo = slovo;}
    public Integer getKategorija(){return kategorija;}
    public void setKategorija(int kategorija){this.kategorija = kategorija;}
    public Integer getoDataID(){return oDataID;}
    public void setoDataID(int oDataID){this.oDataID = oDataID;}
}