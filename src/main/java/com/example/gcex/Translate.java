package com.example.gcex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class Translate {
    public static ArrayList<Cmacro> mlist;
    public static String trans(String filename, String params) {
        boolean phase1 = true;
        boolean debugm = false;
        boolean zoom   = false;
        double  faktor = 1.00;
        String  scale = "";
        Scanner scan;
        mlist = new ArrayList<>();
        for (int i = 0; i < params.length(); i++) {
            if (params.charAt(i) == ' ') i++;
            if (params.charAt(i) == '-') {
                i++;
                if (params.charAt(i) == 'd') {
                   i++;
                   debugm = true;
                   System.out.println("debug ein " + i);
                } else if (params.charAt(i) == 's') {
                    i++;
                    zoom = true;
                    switch (params.toUpperCase().charAt(i)) {
                        case 'Z' -> {
                            faktor = 0.40;
                            scale = "Z";
                        }
                        case 'N' -> {
                            faktor = 0.55;
                            scale = "N";
                        }
                        case 'T' -> {
                            faktor = 0.73;
                            scale = "TT";
                        }
                        case 'S' -> {
                            faktor = 1.35;
                            scale = "S";
                        }
                        case '0' -> {
                            faktor = 2.00;
                            scale = "0";
                        }
                        case '1' -> {
                            faktor = 2.72;
                            scale = "1";
                        }
                        case 'G' -> {
                            faktor = 3.90;
                            scale = "G";
                        }
                        default -> zoom = false;
                    }
                }
                i++;
                System.out.println("Faktor: " + faktor + " zoom: " + zoom + " i: " + i);
            }
        }

        File gce = new File("C:\\Users\\wilfr\\Desktop\\gce\\" + filename + ".txt");
        File gco = new File("C:\\Users\\wilfr\\Desktop\\gco\\" + filename + scale + ".gcode");
        System.out.println(gce.getPath());
        System.out.println(gco.getPath());
        String scut = "";
        String sscr = "";
        String soth = "";
        byte   manz = 0;

        try {
            scan = new Scanner(gce);
        } catch (FileNotFoundException e) {
            return("Datei " + filename + ".txt nicht gefunden.");
        }
        try {
            FileWriter writer = new FileWriter(gco);
            while (scan.hasNext()) {
                String aktz = scan.nextLine();
                while (aktz.length() == 0) aktz = scan.nextLine();
                switch (aktz.charAt(0)) {
                    case '@':
                        phase1 = false;
                        System.out.println("Ende der Phase 1 manz: " + mlist.size());
                        break;

                    case '%':
                        System.out.print("Cut-Speed: ");
                        for (int i = 1; i < aktz.length(); i++) {
                            System.out.print(aktz.charAt(i));
                            scut += aktz.charAt(i);
                        }
                        System.out.print("\n");
                        break;

                    case '$':
                        System.out.print("Other-Speed: ");
                        for (int i = 1; i < aktz.length(); i++) {
                            System.out.print(aktz.charAt(i));
                            soth += aktz.charAt(i);
                        }
                        System.out.print("\n");
                        break;

                    case '§':
                        System.out.print("Scribe-Speed: ");
                        for (int i = 1; i < aktz.length(); i++) {
                            System.out.print(aktz.charAt(i));
                            sscr += aktz.charAt(i);
                        }
                        System.out.print("\n");
                        break;

                    case '/':
                        System.out.println(aktz);
                        writer.write(aktz + "\n");
                        break;

                    case 'e':
                        StringBuilder mname = new StringBuilder();
                        for (int i = 2; i < aktz.length()-2; i++) {
                            mname.append(aktz.charAt(i));
                        }
                        System.out.println("neuer Makro: " + mname);
                        mlist.add(new Cmacro(mname.toString().toUpperCase(),
                                        "// makro " + mname.toString().toUpperCase() + "\n",
                                        0, mlist.size() + 1));
                        break;

                    case '}':
                        if (debugm) System.out.println("makro "+ mlist.get(manz).getName() + " endet");
                        mlist.get(manz).dump();
                        manz++;
                        break;

                    case '<':
                        System.out.print("recall: ");
                        int repeat = (aktz.charAt(1) - '0');
                        int zeiger = 2;
                        while ((aktz.charAt(zeiger) >= '0') && (aktz.charAt(zeiger) <= '9')) {
                            repeat = repeat * 10 + (aktz.charAt(zeiger) - '0');
                            zeiger++;
                        }
                        while (aktz.charAt(zeiger) == ' ') {
                            zeiger++;
                        }
                        System.out.print(repeat + "x aufrufen: ");
                        StringBuilder mnameBuilder = new StringBuilder();
                        for (int i = zeiger; i < aktz.length(); i++) {
                            mnameBuilder.append(aktz.charAt(i));
                        }
                        mname = new StringBuilder(mnameBuilder.toString());
                        System.out.println(mname);
                        if (phase1) {
                            mlist.get(manz).addl(aktz);
                        } else {
                            for (int i = 0; i < manz; i++) {    //  ??? for (i: mlist) {
                                int index = mlist.get(i).getName().compareTo(mname.toString().toUpperCase());
                                for (int j = 0; j < repeat; j++) {
                                    if (index == 0) {
                                        writer.write(insertmacro(mlist.get(i).getCont()).toUpperCase());
                                        if (debugm) writer.write("// END " + mlist.get(i).getName() + "\n");
                                    }
                                }
                            }
                        }
                        break;

                    case '°':
                        aktz = kreisbogen(aktz);
                        // break;

                    case '~':
                        aktz = randomize(aktz);
                        // break;

                    case 'g':
                        if (zoom) {
                             aktz = skaliere(aktz, faktor);
                        }
                        // break;

                    default:
                        if (aktz.contains("%")) {
                            aktz = aktz.replace('%', ' ');
                            aktz = aktz.concat(scut);
                        }
                        if (aktz.contains("$")) {
                            aktz = aktz.replace('$', ' ');
                            aktz = aktz.concat(soth);
                        }
                        if (aktz.contains("§")) {
                            aktz = aktz.replace('§', ' ');
                            aktz = aktz.concat(sscr);
                        }
                        if (phase1) {
                            mlist.get(manz).addl(aktz);
                        } else {
                            writer.write(aktz.toUpperCase() + "\n");
                        }
                }
            }
            writer.flush();
            writer.close();

        } catch(IOException e) {
            return("Datei " + filename + ".gcode kann nicht bearbeitet werden.");
        }
        return("Fertig!");
    }
    public static String insertmacro (String macro) {
        String  erg    = "";
        Scanner scan   = new Scanner(macro);

        while (scan.hasNext()) {
            String aktz = scan.nextLine();
            while (aktz.length() == 0) aktz = scan.nextLine();
            switch (aktz.charAt(0)) {
                case '/' -> erg = erg.concat(aktz + "\n");
                case '<' -> {
                    System.out.print("insertm: ");
                    StringBuilder mname = new StringBuilder();
                    int repeat = (aktz.charAt(1) - '0');
                    int zeiger = 2;
                    while ((aktz.charAt(zeiger) >= '0') && (aktz.charAt(zeiger) <= '9')) {
                        repeat = repeat * 10 + (aktz.charAt(zeiger) - '0');
                        zeiger++;
                    }
                    while (aktz.charAt(zeiger) == ' ') {
                        zeiger++;
                    }
                    System.out.print(repeat + "x aufrufen: ");
                    for (int i = zeiger; i < aktz.length(); i++) {
                        mname.append(aktz.charAt(i));
                    }
                    System.out.println(mname);
                    for (int j = 0; j < repeat; j++) {
                        for (Cmacro cmacro : mlist) {
                            if (cmacro.getName().compareTo(mname.toString().toUpperCase()) == 0) {
                                erg = erg.concat(insertmacro(cmacro.getCont()).toUpperCase());
                            }
                        }
                    }
                }
                default -> erg = erg.concat(aktz.toUpperCase() + "\n");
            }
        }
        return erg;
    }

    public static String skaliere (String zeile, double faktor) {
        boolean getx   = false;
        boolean gety   = false;
        boolean geti   = false;
        boolean getj   = false;
        StringBuilder neuez  = new StringBuilder("g" + zeile.charAt(1));
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
        System.out.println("zende: " + zeile.length() + " -> " + ze);
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
        System.out.print("%pos: " + zeile.indexOf("%") + "  ");
        System.out.print("$pos: " + zeile.indexOf("$") + "  ");
        System.out.print("§pos: " + zeile.indexOf("§") + "  ");
        System.out.print("fpos: " + zeile.indexOf("f") + "  ");
        System.out.println("kpos: " + zeile.indexOf("/"));
        System.out.println(neuez);

        if ((xp < 0) && (yp < 0)) return zeile;

        if ((ze >= 2) && (getx)) {
            neuez = new StringBuilder(neuez.toString().concat(convert(zeile, xp, xe, "x", faktor)));
        }
        if ((ze >= 2) && (gety)) {
            neuez = new StringBuilder(neuez.toString().concat(convert(zeile, yp, ye, "y", faktor)));
        }
        if ((ze > 2) && (geti)) {
            neuez = new StringBuilder(neuez.toString().concat(convert(zeile, ip, ie, " i", faktor)));
        }
        if ((ze > 2) && (getj)) {
            neuez = new StringBuilder(neuez.toString().concat(convert(zeile, jp, je, "j", faktor)));
        }
        if (zeile.indexOf("%") > 0) neuez = new StringBuilder(neuez.toString().concat("%"));
        if (zeile.indexOf("$") > 0) neuez = new StringBuilder(neuez.toString().concat("$"));
        if (zeile.indexOf("§") > 0) neuez = new StringBuilder(neuez.toString().concat("§"));
        System.out.println("neue skalerte Zeile: " + neuez);
        if ((zeile.indexOf("f") > ze) || (zeile.indexOf("/") > ze)) {
            System.out.print("rest: ");
            neuez.append(" ");
            for (int i = ze + 1; i < zeile.length(); i++) {
                neuez.append(zeile.charAt(i));
            }
        }
        System.out.println("neue skalierte Zeile: " + neuez);
        return neuez.toString();
    }
    public static String convert ( String z, int p, int e, String vn, double f) {
        boolean minus = false;
        if (p >= e) return vn + "0";
        if (z.charAt(p+1) == '-') {
             p++;
             minus = true;
        }
        double wert = readdimension(z, p, e) * f;
        String neuw = vn;
        if (minus) neuw = neuw.concat("-");
        neuw = neuw.concat(writedimension(wert));
        System.out.println(neuw);
        return neuw;
    }
    public static String writedimension (double v) {
        return String.format("%.2f", v).replaceFirst(",", ".");
    }
    public static double readdimension (String z, int von, int bis) {
        boolean punkt = false;
        double  dzs   = 0.1;
        double  wert  = 0.0;

        System.out.println("readdimension von " + von + " bis " + bis);
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
    public static String kreisbogen (String zeile) {
        return zeile;
    }
    public static String randomize (String zeile) {
        return zeile;
    }
}