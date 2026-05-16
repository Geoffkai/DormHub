package com.dormhub.view;

import java.awt.Image;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.ImageIcon;

public final class ImageResources {
    private ImageResources() {
    }

    public static ImageIcon loadIcon(String path) {
        String normalizedPath = normalizePath(path);
        URL resource = findResource(normalizedPath);
        if (resource != null) {
            return new ImageIcon(resource);
        }

        Path filePath = resolveFilePath(normalizedPath);
        if (filePath != null && Files.exists(filePath)) {
            return new ImageIcon(filePath.toString());
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

    private static URL findResource(String normalizedPath) {
        URL resource = ImageResources.class.getResource(normalizedPath);
        if (resource != null) {
            return resource;
        }

        if (normalizedPath.startsWith("/")) {
            resource = ImageResources.class.getResource("/assets" + normalizedPath);
            if (resource != null) {
                return resource;
            }
        }

        return null;
    }

    private static Path resolveFilePath(String normalizedPath) {
        if (normalizedPath == null || normalizedPath.isBlank()) {
            return null;
        }

        String relativePath = normalizedPath.startsWith("/") ? normalizedPath.substring(1) : normalizedPath;
        return Paths.get("assets", relativePath);
    }
}