package com.dormhub.view;

import java.awt.*;
import javax.swing.*;

public class BackgroundPanel extends JPanel {
    private Image background;
    private boolean keepAspectRatio;

    public BackgroundPanel(String imagePath) {
        this(imagePath, true); // full screen panels keep aspect ratio by default
    }

    public BackgroundPanel(String imagePath, boolean keepAspectRatio) {
        this.keepAspectRatio = keepAspectRatio;
        if (imagePath != null && !imagePath.isEmpty()) {
            background = ImageResources.loadImage(imagePath);
        }
        setOpaque(false);
    }

    public void setBackground(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            background = ImageResources.loadImage(imagePath);
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background == null) {
            return;
        }

        if (!keepAspectRatio) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            return;
        }

        int imgWidth = background.getWidth(this);
        int imgHeight = background.getHeight(this);
        if (imgWidth <= 0 || imgHeight <= 0) {
            return;
        }

        double scaleX = (double) getWidth() / imgWidth;
        double scaleY = (double) getHeight() / imgHeight;
        double scale = Math.min(scaleX, scaleY);

        int newWidth = (int) Math.round(imgWidth * scale);
        int newHeight = (int) Math.round(imgHeight * scale);
        int x = (getWidth() - newWidth) / 2;
        int y = (getHeight() - newHeight) / 2;

        g.drawImage(background, x, y, newWidth, newHeight, this);
    }
}
