package com.example.gcex;

import static java.lang.Math.*;

public class Vector {
    private boolean burn;
    private String  speed;
    private int     rand;
    private Double  coordx;
    private Double  coordy;

    public Vector(Double x, Double y, boolean b, String c, int r) {
        this.burn   = b;
        this.speed  = c;
        this.rand   = r;
        this.coordx = x;
        this.coordy = y;
    }
    public boolean getBurn()   { return burn; }
    public int     getRand()   { return rand; }
    public String  getSpeed()  { return speed; }
    public Double  getCoordx() { return coordx; }
    public Double  getCoordy() { return coordy; }
    public String getCS() {
        String pr = "";
        if (rand > 1) pr = "~" + rand;
        return (pr + (burn ? "g1" : "g0") + "x" + coordx.floatValue() + "y" + coordy.floatValue() + speed);
    }

    public void turn (int grad) {
        double bm = 3.14159 / 180.0 * grad;
        double tx = coordx * cos(bm) + coordy * sin(bm);
        double ty = coordy * cos(bm) - coordx * sin(bm);
        coordx = tx;
        coordy = ty;
    }
}