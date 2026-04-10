package com.dormhub.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AppController {

    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        statusLabel.setText("Ready.");
    }

    @FXML
    private void handleHello() {
        statusLabel.setText("Hello from DormHub!");
    }
}
