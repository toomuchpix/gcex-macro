package com.example.gcex;

import static com.example.gcex.Translate.*;
import static java.lang.Math.random;

public class Calc {
    public static boolean minus = false;

    public static String skaliere (String zeile, double faktor) {
        boolean getx   = false;
        boolean gety   = false;
        boolean geti   = false;
        boolean getj   = false;
        int xp = zeile.indexOf("x");
        int yp = zeile.indexOf("y");
        int ip = zeile.indexOf("i");
        int jp = zeile.indexOf("j");
        int ze = zeile.length() - 1;
        if (zeile.indexOf("/") > 0) ze = zeile.indexOf("/") - 1;
        if (zeile.indexOf("f") > 0) ze = zeile.indexOf("f") - 1;
        if (zeile.indexOf("§") > 0) ze = zeile.indexOf("§") - 1;
        if (zeile.indexOf("$") > 0) ze = zeile.indexOf("$") - 1;
        if (zeile.indexOf("%") > 0) ze = zeile.indexOf("%") - 1;
        int ye = (ip >= 2) ? ip-1 : ze;
        int xe = (yp >= 2) ? yp-1 : ye;
        int ie = jp-1;
        int je = ze;
        if ((xp >= 2) && (yp < 0)) xe = (ip >= 2 ? ip-1 : ze);
        if ((xp < 0) && (yp >= 2)) ye = (ip >= 2 ? yp-1 : ze);
        if (xp >= 2) getx = true;
        if (yp >= 2) gety = true;
        if (ip >= 2) geti = true;
        if (jp >= 2) getj = true;
        if ((xp < 0) && (yp < 0)) return zeile;
        StringBuilder neuez  = new StringBuilder();
        if (zeile.charAt(0) == '~') {
            neuez.append("~");
            if (zeile.charAt(1) != 'g') neuez.append(zeile.charAt(1));
        }
        neuez.append("g");
        neuez.append(zeile.charAt(zeile.indexOf('g') + 1));
        if (ze >= 2) {
            if (getx) neuez.append(convert(zeile, xp, xe, "x", faktor));
            if (gety) neuez.append(convert(zeile, yp, ye, "y", faktor));
            if (geti) neuez.append(convert(zeile, ip, ie, " i", faktor));
            if (getj) neuez.append(convert(zeile, jp, je, "j", faktor));
        }
        if (zeile.indexOf("%") > 0) neuez.append("%");
        if (zeile.indexOf("$") > 0) neuez.append("$");
        if (zeile.indexOf("§") > 0) neuez.append("§");
        if ((zeile.indexOf("f") > ze) || (zeile.indexOf("/") > ze)) {
            neuez.append(" ");
            for (int i = ze + 1; i < zeile.length(); i++) {
                neuez.append(zeile.charAt(i));
            }
        }
        System.out.println("neue skalierte Zeile: " + neuez);
        return neuez.toString();
    }
    public static String convert ( String z, int p, int e, String vn, double f) {
        minus = false;
        if (p >= e) return vn + "0";
        if (z.charAt(p+1) == '-') {
            p++;
            minus = true;
        }
        double wert = readdimension(z, p, e) * f;
        String neuw = vn;
        if (minus) neuw = neuw.concat("-");
        neuw = neuw.concat(writedimension(wert));
        if (dm) System.out.println("convert -> " + neuw);
        return neuw;
    }
    public static double convertr ( String z, int p, int e) {
        minus = false;
        if (p >= e) return 0;
        if (z.charAt(p+1) == '-') {
            p++;
            minus = true;
        }
        return (minus ? -1 : 1) * readdimension(z, p, e);
    }
    public static String writedimension (double v) {
        return String.format("%.3f", v).replaceFirst(",", ".");
    }
    public static double readdimension (String z, int von, int bis) {
        boolean punkt = false;
        double  dzs   = 0.1;
        double  wert  = 0.0;

        if (dm) System.out.println("readdimension von " + von + " bis " + bis + " -> ");
        int p = von + 1;
        if (p > bis) return 0.0;
        while (p <= bis) {
            int ziffer = z.charAt(p) - '0';
            if ((ziffer < 0) || (ziffer > 9)) {
                if (z.charAt(p) == '.') {
                    punkt = true;
                }
            } else {
                if (!punkt) wert = 10.0 * wert + ziffer;
                else wert = wert + (dzs * ziffer);
                if (punkt) dzs = dzs * 0.1;
            }
            p++;
        }
        return wert;
    }
    public static String randomize (String zeile, int ra, int rx, int ry) {
        if (zeile.charAt(1) != 'g') ra = zeile.charAt(1) - '0';
        if (ra < 2) return (insertspeed(zeile.replaceFirst("~", "")));
        System.out.println("randomize " + zeile + " raxy=" + ra + rx + ry);
        double ewertx = 0;
        double ewerty = 0;
        boolean getx   = false;
        boolean gety   = false;
        int xp = zeile.indexOf("x");
        int yp = zeile.indexOf("y");
        int ye = zeile.length() - 1;
        int xe = (yp >= 2) ? yp-1 : ye;
        if (xp >= 2) getx = true;
        if (yp >= 2) gety = true;
        if (getx) ewertx = convertr(zeile, xp, xe);
        if (gety) ewerty = convertr(zeile, yp, ye);
        double iwertx = ewertx;
        double iwerty = ewerty;
        StringBuilder neuez  = new StringBuilder();
        boolean expanded = false;
        for (int i = 0; i < (ra-1); i++) {
            double zwertx = ewertx / ra + (random() - 0.5) * rx / 20;
            double zwerty = ewerty / ra + (random() - 0.5) * ry / 20;
            iwertx -= zwertx;
            iwerty -= zwerty;
            neuez.append("g1");
            neuez.append("x");
            neuez.append(writedimension(zwertx));
            neuez.append("y");
            neuez.append(writedimension(zwerty));
            if (!expanded) {
                if (zeile.endsWith("%")) neuez.append(" ").append(scut);
                if (zeile.endsWith("$")) neuez.append(" ").append(soth);
                if (zeile.endsWith("§")) neuez.append(" ").append(sscr);
                expanded = true;
            }
            neuez.append("\n");
            System.out.println("randomize " + neuez + " i=" + i);
        }
        neuez.append("g1");
        neuez.append("x");
        neuez.append(writedimension(iwertx));
        neuez.append("y");
        neuez.append(writedimension(iwerty));
        neuez.append("\n// last random\n");
        return neuez.toString();
    }
}