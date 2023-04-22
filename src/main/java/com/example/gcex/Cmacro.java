package com.example.gcex;

import java.util.ArrayList;
import static com.example.gcex.Calc.minus;
import static com.example.gcex.Translate.*;

public class Cmacro {
    private int    ID;
    private int    zanz;
    private String name;
    private String cont;
    private String output;
    private double posx;
    private double posy;
    private double deltax;
    private double deltay;
    private ArrayList<Vector> punkt;
    private ArrayList<Vector> punktmx;
    private ArrayList<Vector> punktmy;
    private ArrayList<Vector> punktxy;

    public Cmacro(String name, String cont, int zanz, int ID) {
        this.ID      = ID;
        this.zanz    = zanz;
        this.name    = name;
        this.cont    = cont;
        this.punkt   = new ArrayList<>();
        this.punktmx = new ArrayList<>();
        this.punktmy = new ArrayList<>();
        this.punktxy = new ArrayList<>();
        this.output  = "";
        this.posx    = 0;
        this.posy    = 0;
        this.deltax  = 0;
        this.deltay  = 0;
    }

    public int getID() { return ID; }
    public int getZanz() { return zanz; }
    public String getName() {
        return name;
    }
    public String getCont() { return cont; }
    public String getOutput() { return output; }
    public double getDeltax() { return deltax; }
    public double getDeltay() { return deltay; }

    public void addl(String zeile) {
        System.out.println("addl " + zeile);
        String sp = "";
        if (zeile.contains("%")) sp = " %";
        if (zeile.contains("$")) sp = " $";
        if (zeile.contains("§")) sp = " §";
        String s = "x0";
        if (zeile.indexOf("x") > 1) {
            s = Calc.convert(zeile, zeile.indexOf("x"),
                                    zeile.indexOf("y") > 0 ? zeile.indexOf('y')-1 : zeile.length()-1, "dx", 1);
        }
        posx = Calc.readdimension(s, 1,s.length()-1) * (minus ? -1.0 : 1.0);
        s = "y0";
        if (zeile.indexOf("y") > 1) {
            s = Calc.convert(zeile, zeile.indexOf("y"), zeile.length() - 1, "dy", 1);
        }
        posy = Calc.readdimension(s, 1,s.length()-1) * (minus ? -1.0 : 1.0);
        int z = 0;
        int r = 0;
        if (zeile.charAt(z) == '~') {
            z++;
            r = zeile.charAt(z) != 'g' ? zeile.charAt(z) -'0' : ra;
            z = zeile.indexOf('g');
        }
        punkt.add (new Vector(posx, posy, (zeile.charAt(z+1) != '0'), sp, r));
        punktmx.add (new Vector(-posx, posy, (zeile.charAt(z+1) != '0'), sp, r));
        punktmy.add (new Vector(posx, -posy, (zeile.charAt(z+1) != '0'), sp, r));
        punktxy.add (new Vector(-posx, -posy, (zeile.charAt(z+1) != '0'), sp, r));

        cont = cont.concat(zeile.concat("\n"));
        zanz++;
        System.out.println("posx " + posx + " / posy " + posy + (zeile.charAt(z+1) != '0' ? " burn r" : "r" + r));
    }

    public void dump() {
        System.out.println("Macro " + name + " ID:" + ID + " Zeilen:" + zanz);
        System.out.println(cont);
        System.out.println(punkt.size() + " Vektoren:");
        for (Vector vector:  punkt ) {
            System.out.println(vector.getCS());
        }
    }

    public void drehen (int grad, boolean links, boolean unten) {
        StringBuilder outputBuilder = new StringBuilder();
        deltax = 0;
        deltay = 0;
        for (Vector vector: links ? (unten ? punktxy : punktmx) : (unten ? punktmy : punkt)) {
            vector.turn((links || unten) ? -grad : grad);
            String s = vector.getCS();
            System.out.println("drehen -> getCS s= " + s);
            if (s.contains("~")) {
                s = Calc.randomize(s, ra, rx, ry);
                System.out.println("nach randomize: " + s);
            }
            outputBuilder.append(s).append("\n");
            output = outputBuilder.toString();
            deltax += vector.getCoordx();
            deltay += vector.getCoordy();
        }
        System.out.println("drehen:" + punkt.size() + " Elemente gedreht um " + grad + " grad");
        System.out.println("  -> deltax " + deltax + " / deltay " + deltay);
    }
}