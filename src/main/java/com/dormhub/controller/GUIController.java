package com.dormhub.controller;

import java.util.List;

import com.dormhub.model.Resident;
import com.dormhub.service.ResidentService;
import com.dormhub.view.ContentPanel;
import com.dormhub.view.PanelsHandler;

public class GUIController {
    private final ResidentService residentService;
    // private final RoomService roomService;
    // private final DormPassService dormPassService;
    // private final PaymentService paymentService;
    // private final RoomAssignmentService roomAssignmentService;

    private final PanelsHandler panelsHandler;
    private final ContentPanel contentPanel;

    public GUIController(ResidentService residentService, PanelsHandler panelsHandler, ContentPanel contentPanel) {
        this.residentService = residentService;
        this.panelsHandler = panelsHandler;
        this.contentPanel = contentPanel;

        bindResidentActions();
    }

    private void bindResidentActions() {
        contentPanel.setViewAction(e -> {
            try {
                List<Resident> residents = residentService.findAllResidents();
                contentPanel.showResidentsTable(residents);
            } catch (Exception ex) {
                contentPanel.showMessage("Failed to load residents: " + ex.getMessage());
            }
        });

        contentPanel.setAddAction(e -> {
            try {

            } catch (Exception e) {

            }
        });
    }
}
