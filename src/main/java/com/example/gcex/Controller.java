package com.example.gcex;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import static com.example.gcex.Translate.*;

public class Controller {
    @FXML
    TextField fname = new TextField();
    @FXML
    TextField param = new TextField();
    @FXML
    Button    btn1  = new Button();
    @FXML
    Label     mText = new Label();
    @FXML
    Label      m1    = new Label();
    @FXML
    TableView<Cmacro> mt = new TableView<>();
    @FXML
    TableColumn<Cmacro, Integer> ti = new TableColumn<>("ID");
    @FXML
    TableColumn<Cmacro, String>  tn = new TableColumn<>("name");
    @FXML
    TableColumn<Cmacro, Integer> tz = new TableColumn<>("zanz");

    @FXML
    public void onChangeFileName() {
        System.out.println("onChangeFileName: -> " + fname.getText());
    }
    public void onChangeParams() {
        System.out.print("onChangeParams: -> " + param.getText());
    }
    public void onStartButtonClick() {
        System.out.println("onStartButtonClick:");
        String f = fname.getText();
        System.out.println(" fname " + f +" gelesen");
        String p = param.getText();
        System.out.println(" param " + p +" gelesen");
        if (f.isEmpty()) f = "x";
        mText.setText(f + ".txt");
        mText.setText(trans(f, p));
        m1.setText("");
        for (Cmacro cmacro : mlist) {
            m1.setText(m1.getText() + " " + cmacro.getName());
        }
    }
}