package com.example.gcex;

public class Cmacro {
    private String name;
    private String cont;
    private int    zanz;
    private int    ID;

    public Cmacro(String name, String cont, int zanz, int ID) {
        this.name = name;
        this.cont = cont;
        this.zanz = zanz;
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }
    public int getLanz() {
        return zanz;
    }
    public String getName() {
        return name;
    }
    public String getCont() {
        return cont;
    }

    public void setZanz(int lanz) { this.zanz = lanz; }
    public void setID(int ID) {
        this.ID = ID;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCont(String cont) {
        this.cont = cont;
    }

    public void addl(String zeile) {
//        System.out.println("add " + zeile.toUpperCase());
        cont = cont.concat(zeile.concat("\n"));
        zanz++;
    }

    public void dump() {
        System.out.println("Macro " + name + " ID:" + ID + " level:" + zanz);
        System.out.println(cont);
    }
}