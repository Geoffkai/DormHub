package com.dormhub.controller;

import com.dormhub.service.ResidentService;
import com.dormhub.service.ResidentServiceImpl;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AppController {

    private final ResidentService residentService = new ResidentServiceImpl();

    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        statusLabel.setText("Ready.");
    }

    @FXML
    private void handleHello() {
        try {
            int residentCount = residentService.findAllResidents().size();
            statusLabel.setText("Resident records: " + residentCount);
        } catch (RuntimeException e) {
            statusLabel.setText("Unable to load resident data.");
        }
    }
}
