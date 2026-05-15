package com.dormhub.view;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

public final class ImageResources {
    private ImageResources() {
    }

    public static ImageIcon loadIcon(String path) {
        URL resource = ImageResources.class.getResource(normalizePath(path));
        if (resource != null) {
            return new ImageIcon(resource);
        }
        return new ImageIcon(path);
    }

    public static Image loadImage(String path) {
        return loadIcon(path).getImage();
    }

    private static String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }

        String normalized = path.replace('\\', '/');
        if (normalized.startsWith("src/")) {
            normalized = normalized.substring(3);
        }
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        return normalized;
    }
}