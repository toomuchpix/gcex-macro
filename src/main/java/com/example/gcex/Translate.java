package com.example.gcex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
//private Label m1 = new Label;

 public class Translate {
     public static boolean debug  = false;
     public static int     MID    = 0;
     public static String  scut   = "";
     public static String  sscr   = "";
     public static Cmacro[] mlist = new Cmacro[15];
     public static String trans(String filename, String params) {
         boolean phase1 = true;
         char    actc   = '.';
         int     level  = 1;
         int     repeat = 0;
         int     zeiger = 0;
         int     index  = 0;
         String  aktz   = "";
         String  mname  = null;
         Scanner scan   = null;
         Scanner scanz  = new Scanner(aktz);
         File gce = new File("C:\\Users\\wilfr\\Desktop\\gce\\" + filename + ".txt");
         File gco = new File("C:\\Users\\wilfr\\Desktop\\gco\\" + filename + ".gcode");

         System.out.println(gce.getPath());
         System.out.println(gco.getPath());
         MID  = 0;
         scut = "";
         sscr = "";

        try {
            scan = new Scanner(gce);
        } catch (FileNotFoundException e) {
            return("Datei " + filename + ".txt nicht gefunden.");
        }
        try {
            FileWriter writer = new FileWriter(gco);
            while (scan.hasNext()) {
                aktz = scan.nextLine();
                while (aktz.length() == 0) aktz = scan.nextLine();
                actc = aktz.charAt(0);
                switch (actc) {
                    case '@':
                        phase1 = false;
                        System.out.println("Ende der Phase 1 MID: " + MID);
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

                    case '-':

                        break;

                    case 'e':
                        mname = "";
                        for (int i = 2; i < aktz.length()-2; i++) {
                            mname += aktz.charAt(i);
                        }
                        System.out.println("neuer Makro: " + mname);
                        mlist[MID] = new Cmacro(mname.toUpperCase(), "", level,MID+1);
                        mlist[MID].addl("// makro " + mname);
                        break;

                    case '}':
                        if (debug) System.out.println("makro "+ mlist[MID].getName() + " endet");
                        mlist[MID].dump();
                        MID++;
                        break;

                    case '<':
                        System.out.print("recall: ");
                        mname = "";
                        repeat = (aktz.charAt(1) - '0');
                        zeiger = 2;
                        while ((aktz.charAt(zeiger) >= '0') && (aktz.charAt(zeiger) <= '9')) {
                            repeat = repeat * 10 + (aktz.charAt(zeiger) - '0');
                            zeiger++;
                        }
                        while (aktz.charAt(zeiger) == ' ') {
                            zeiger++;
                        }
                        System.out.print(repeat + "x aufrufen: ");
                        for (int i = zeiger; i < aktz.length(); i++) {
                            mname += aktz.charAt(i);
                        }
                        System.out.println(mname);
                        if (phase1) {
                            mlist[MID].addl(aktz);
                        } else {
                            for (int i = 0; i < MID; i++) {
                                index = mlist[i].getName().compareTo(mname.toUpperCase());
                                for (int j = 0; j < repeat; j++) {
                                    if ((index == 0) && (!phase1)) {
                                        writer.write(insertmacro(mlist[i].getCont()).toUpperCase());
                                        if (debug) writer.write("// END " + mlist[i].getName() + "\n");
                                    }
                                }
                            }
                        }
                        break;

                    default:
                        if (aktz.contains("%")) {
                            aktz = aktz.replace('%', ' ');
                            aktz = aktz.concat(scut);
                        }
                        if (aktz.contains("$")) {
                            aktz = aktz.replace('$', ' ');
                            aktz = aktz.concat(sscr);
                        }
                        if (phase1) {
                            mlist[MID].addl(aktz);
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
        int     repeat = 0;
        int     zeiger = 0;
        String  aktz   = "";
        String  erg    = "";
        String  mname  = null;
        Scanner scan   = new Scanner(macro);

        while (scan.hasNext()) {
            aktz = scan.nextLine();
            while (aktz.length() == 0) aktz = scan.nextLine();
            switch (aktz.charAt(0)) {
                case '/':
                    erg = erg.concat(aktz + "\n");
                    break;

                case '~':

                    break;

                case '<':
                    System.out.print("insertm: ");
                    mname = "";
                    repeat = (aktz.charAt(1) - '0');
                    zeiger = 2;
                    while ((aktz.charAt(zeiger) >= '0') && (aktz.charAt(zeiger) <= '9')) {
                        repeat = repeat * 10 + (aktz.charAt(zeiger) - '0');
                        zeiger++;
                    }
                    while (aktz.charAt(zeiger) == ' ') {
                        zeiger++;
                    }
                    System.out.print(repeat + "x aufrufen: ");
                    for (int i = zeiger; i < aktz.length(); i++) {
                        mname += aktz.charAt(i);
                    }
                    System.out.println(mname);
                    for (int i = 0; i < MID; i++) {
                        int index = mlist[i].getName().compareTo(mname.toUpperCase());
                        for (int j = 0; j < repeat; j++) {
                            if (index == 0) {
                                if (debug) System.out.println(mname + " gefunden");
                                erg = erg.concat(insertmacro(mlist[i].getCont()).toUpperCase());
                                if (debug) erg = erg.concat("// END " + mlist[i].getName() + "\n");
                            }
                        }
                    }
                    break;

                default:
                    if (aktz.contains("%")) {
                        aktz = aktz.replace('%', ' ');
                        aktz = aktz.concat(scut);
                    }
                    if (aktz.contains("$")) {
                        aktz = aktz.replace('$', ' ');
                        aktz = aktz.concat(sscr);
                    }
                    erg = erg.concat(aktz.toUpperCase() + "\n");
            }
        }
        return erg;
    }
}