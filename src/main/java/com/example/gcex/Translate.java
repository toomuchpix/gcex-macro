package com.example.gcex;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class Translate {
    public static boolean dm;
    public static double  xmemory;
    public static double  ymemory;
    public static int     ra;
    public static int     rx;
    public static int     ry;
    public static String  scut;
    public static String  sscr;
    public static String  soth;
    public static ArrayList<Cmacro> mlist;
    public ObservableList<Cmacro> tab = FXCollections.observableArrayList(mlist);

    public static String trans(String filename, String params) {
        String  scale = "";
        Scanner scan;
        mlist = new ArrayList<>();

        dm = false;
        ra = 5;
        rx = 3;
        ry = 3;
        boolean phase1 = true;
        double  faktor = 1.000;
        for (int i = 0; i < params.length(); i++) {
            if (params.charAt(i) == ' ') i++;
            if (params.charAt(i) == '-') {
                i++;
                if (params.charAt(i) == 'd') {
                    i++;
                    dm = true;
                    System.out.println("debug ein " + i);
                } else if (params.charAt(i) == 'r') {
                    i++;
                    if ((params.charAt(i) >= '0') && (params.charAt(i) <= '9')) ra = params.charAt(i) - '0';
                    i++;
                    if ((params.charAt(i) >= '0') && (params.charAt(i) <= '9')) rx = params.charAt(i) - '0';
                    i++;
                    if ((params.charAt(i) >= '0') && (params.charAt(i) <= '9')) ry = params.charAt(i) - '0';
                    System.out.println("random mode ra=" + ra + " rx=" + rx + " ry=" + ry);
                } else if (params.charAt(i) == 's') {
                    i++;
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
                        case '9' -> {
                            faktor = 0.9;
                            scale = "100";
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
                    }
                }
            }
        }

        File gce = new File("C:\\Users\\wilfr\\Desktop\\gce\\" + filename + ".txt");
        File gco = new File("C:\\Users\\wilfr\\Desktop\\gco\\" + filename + scale + ".gcode");
        System.out.println(gce.getPath());
        System.out.println(gco.getPath());
        scut = "";
        sscr = "";
        soth = "";
        byte manz = 0;

        try {
            scan = new Scanner(gce);
        } catch (FileNotFoundException e) {
            return ("Datei " + filename + ".txt nicht gefunden.");
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

                    case 'n':
                        System.out.println("Position merken!");
                        xmemory = 0;
                        ymemory = 0;
                        break;

                    case 'r':
                        writer.write("// RETURN:\n");
                        writer.write(("g0x" + Calc.writedimension(-xmemory) + "y" +
                                                  Calc.writedimension(-ymemory)).toUpperCase() + "\n");
                        break;

                    case 'e':
                        StringBuilder mname = new StringBuilder();
                        for (int i = 2; i < aktz.length() - 2; i++) {
                            mname.append(aktz.charAt(i));
                        }
                        System.out.println("neuer Makro: " + mname);
                        mlist.add(new Cmacro(mname.toString().toUpperCase(),
                                "// makro " + mname.toString().toUpperCase() + "\n",
                                0, mlist.size() + 1));
                        break;

                    case '}':
                        if (dm) System.out.println("makro " + mlist.get(manz).getName() + " endet");
                        if (dm) mlist.get(manz).dump();
                        manz++;
                        break;

                    case '<':
                        System.out.print("recall: ");
                        boolean bogen = false;
                        boolean links = false;
                        boolean unten = false;
                        int     grad  = 0;
                        int zeiger = 1;
                        if (aktz.charAt(zeiger) == '°') {
                            bogen = true;
                            zeiger++;
                            if (aktz.charAt(zeiger) == '-') {
                                links = true;
                                zeiger++;
                            }
                            if (aktz.charAt(zeiger) == '|') {
                                unten = true;
                                zeiger++;
                            }
                            grad = aktz.charAt(zeiger) - '0';
                            zeiger++;
                            if ((aktz.charAt(zeiger) >= '0') && (aktz.charAt(zeiger) <= '9')) {
                                grad = grad * 10 + aktz.charAt(zeiger) - '0';
                                zeiger++;
                            }
                        }
                        int repeat = 1;
                        while (aktz.charAt(zeiger) == ' ') {
                            zeiger++;
                        }
                        if ((aktz.charAt(zeiger) >= '0') && (aktz.charAt(zeiger) <= '9')) {
                            repeat = aktz.charAt(zeiger) - '0';
                            zeiger++;
                        }
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
                            System.out.print("addl1 ");
                            mlist.get(manz).addl(aktz);
                        } else {
                            for (int i = 0; i < manz; i++) {
                                int index = mlist.get(i).getName().compareTo(mname.toString().toUpperCase());
                                System.out.println("i: " + i + " index: " + index + " n " + mlist.get(i).getName() + "/" + mname.toString().toUpperCase());
                                if (index == 0) {
                                    for (int j = 0; j < repeat; j++) {
                                        if (bogen) {
                                            mlist.get(i).drehen(grad, links, unten);
                                            xmemory += mlist.get(i).getDeltax();
                                            ymemory += mlist.get(i).getDeltay();
                                            writer.write(insertmacro(mlist.get(i).getOutput()).toUpperCase());
                                        } else writer.write(insertmacro(mlist.get(i).getCont()).toUpperCase());
                                        if (dm) writer.write("// END " + mlist.get(i).getName() + "\n");
                                    }
                                    bogen = false;
                                }
                            }
                        }
                        break;

                    case '~':
                        aktz = Calc.skaliere(aktz, faktor);
                        System.out.println("addl2 " + aktz);
                        if (phase1) mlist.get(manz).addl(aktz);
                        else {
                            Scanner rscan = new Scanner(Calc.randomize(aktz, ra, rx, ry));
                            while (rscan.hasNext()) {
                                String raktz = rscan.nextLine();
                                writer.write(raktz.toUpperCase() + "\n");
                            }
                        }
                        break;

                    case 'g':
                        aktz = Calc.skaliere(aktz, faktor);
                        // break;

                    default:
                        aktz = insertspeed(aktz);
                        if (phase1) mlist.get(manz).addl(aktz);
                        else writer.write(aktz.toUpperCase() + "\n");
                }
            }
            writer.flush();
            writer.close();

        } catch (IOException e) {
            return ("Datei " + filename + ".gcode kann nicht bearbeitet werden.");
        }
        return ("Fertig!");
    }

    public static String insertmacro(String macro) {
        Scanner scan = new Scanner(macro);
        StringBuilder insert = new StringBuilder();

        System.out.println("insertmacro: ");
        while (scan.hasNext()) {
            String aktz = scan.nextLine();
            System.out.println("aktz: " + aktz);
            while (aktz.length() == 0) aktz = scan.nextLine();
            switch (aktz.charAt(0)) {
                case '/' -> insert.append(aktz).append("\n");
                case '<' -> {
                    boolean bogen = false;
                    boolean links = false;
                    boolean unten = false;
                    int     grad  = 0;
                    int zeiger = 1;
                    if (aktz.charAt(zeiger) == '°') {
                        bogen = true;
                        zeiger++;
                        if (aktz.charAt(zeiger) == '-') {
                            links = true;
                            zeiger++;
                        }
                        if (aktz.charAt(zeiger) == '|') {
                            unten = true;
                            zeiger++;
                        }
                        grad = aktz.charAt(zeiger) - '0';
                        zeiger++;
                        if ((aktz.charAt(zeiger) >= '0') && (aktz.charAt(zeiger) <= '9')) {
                            grad = grad * 10 + aktz.charAt(zeiger) - '0';
                            zeiger++;
                        }
                    }
                    int repeat = 1;
                    while (aktz.charAt(zeiger) == ' ') {
                        zeiger++;
                    }
                    if ((aktz.charAt(zeiger) >= '0') && (aktz.charAt(zeiger) <= '9')) {
                        repeat = aktz.charAt(zeiger) - '0';
                        zeiger++;
                    }
                    while ((aktz.charAt(zeiger) >= '0') && (aktz.charAt(zeiger) <= '9')) {
                        repeat = repeat * 10 + (aktz.charAt(zeiger) - '0');
                        zeiger++;
                    }
                    while (aktz.charAt(zeiger) == ' ') {
                        zeiger++;
                    }
                    System.out.print(repeat + "x aufrufen: ");
                    StringBuilder mname = new StringBuilder();
                    for (int i = zeiger; i < aktz.length(); i++) {
                        mname.append(aktz.charAt(i));
                    }
                    for (Cmacro cmacro : mlist) {
                        int index = cmacro.getName().compareTo(mname.toString().toUpperCase());
                        System.out.println("index: " + index + " n " + cmacro.getName() + "/" + mname.toString().toUpperCase());
                        if (index == 0) {
                            for (int j = 0; j < repeat; j++) {
                                if (bogen) {
                                    cmacro.drehen(grad, links, unten);
                                    xmemory += cmacro.getDeltax();
                                    ymemory += cmacro.getDeltay();
                                    insert.append(insertmacro(cmacro.getOutput()).toUpperCase());
                                } else insert.append(insertmacro(cmacro.getCont()).toUpperCase());
                            }
                            bogen = false;
                        }
                    }
                }
                case '~' -> insert.append(Calc.randomize(aktz, ra, rx, ry).toUpperCase());
                default -> insert.append(aktz.toUpperCase()).append("\n");
            }
        }
        System.out.println("inserted: " + insert);
        return insert.toString();
    }
    public static String insertspeed(String aktz) {
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
        return aktz;
    }
}