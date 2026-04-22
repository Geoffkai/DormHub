package com.dormhub.controller;

import com.dormhub.service.ResidentService;
import com.dormhub.view.ContentPanel;
import com.dormhub.view.PanelsHandler;

public class GUIController {
    private final ResidentService residentService;
    private final PanelsHandler panelsHandler;
    private final ContentPanel contentPanel;

    public GUIController(ResidentService residentService, PanelsHandler panelsHandler, ContentPanel contentPanel) {
        this.residentService = residentService;
        this.panelsHandler = panelsHandler;
        this.contentPanel = contentPanel;
    }
}
