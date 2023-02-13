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
    public Label       mText = new Label();
    public Label       m1    = new Label();
    public Label       ma    = new Label();
    public TableView   mt    = new TableView();
//    public TableColumn ti    = new TableColumn<>();
//    public TableColumn tn    = new TableColumn<>("?");
//    public TableColumn tz    = new TableColumn<>();
    public ListView     li = new ListView ();
    public ListView     ln = new ListView ();
    public ListView     lz = new ListView ();
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
            mText.setText("kein Dateiname!");
         } else {
            mText.setText(s + ".txt");
            m1.setText("");
            s = trans(s, p);
        }
        mText.setText(s);
            ma.setText("es wurden " + mlist.size() + " Makros gefunden.");
            m1.setText("");
            for (Cmacro cmacro : mlist) {
                m1.setText(m1.getText() + " " + cmacro.getName());
//                li.ssetItems(mlist.get(i).getID());
//                ln.setItems(mlist.get(i).getName());
//                lz.setItems(mlist.get(i).getZanz());
        }
    }
}