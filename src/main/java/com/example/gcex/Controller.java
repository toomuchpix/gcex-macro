package com.example.gcex;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import static com.example.gcex.Translate.*;

public class Controller {
    @FXML
    String s;
    String p;
    public TextField   fname = new TextField();
    public TextField   param = new TextField();
    public Button      btn1  = new Button();
    public Label       sText = new Label();
    public Label       m1    = new Label();
    public Label       ma    = new Label();
    public TableView   mt    = new TableView();
    public TableColumn mi    = new TableColumn<>();
    public TableColumn mn    = new TableColumn<>("?");
    public TableColumn ml    = new TableColumn<>();
    @FXML
    public void onChangeFileName() {
        System.out.print("onChangeFileName");
        s = fname.getText();
        System.out.println(" -> " + s);
    }
    public void onChangeParams() {
        System.out.print("onChangeParams");
        s = param.getText();
        System.out.println(" -> " + s);
    }
    public void onStartButtonClick() {
        System.out.println("onStartButtonClick:");
        s = fname.getText();
        System.out.println(" fname " + s +" gelesen");
        p = param.getText();
        System.out.println(" param " + p +" gelesen");
        if (s.isEmpty()) {
            sText.setText("kein Dateiname!");
         } else {
            sText.setText(s + ".txt");
            s = trans(s, p);
        }
        sText.setText(s);
        if (MID > 0) {
            ma.setText("es wurden " + MID + " Makros gefunden.");
            m1.setText("");
            for (int i = 0; i < MID; i++) {
                m1.setText(m1.getText() + " " + mlist[i].getName());
//                mi.setText(toString(mlist[i].getID()));
//                mn.setText(mlist[i].getName());
//                mi.setText(mlist[i].getZanz());
            }
        }
    }
}