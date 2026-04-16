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
            background = new ImageIcon(imagePath).getImage();
        }
        setOpaque(false);
    }

    public void setBackground(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            background = new ImageIcon(imagePath).getImage();
            repaint();
        }

        background = new ImageIcon(imagePath).getImage();
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            if (keepAspectRatio) {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

                int imgWidth = background.getWidth(this);
                int imgHeight = background.getHeight(this);

                double scaleX = screenSize.getWidth() / imgWidth;
                double scaleY = screenSize.getHeight() / imgHeight;
                double scale = Math.min(scaleX, scaleY); // keep aspect ratio

                int newWidth = (int) (imgWidth * scale);
                int newHeight = (int) (imgHeight * scale);

                // Center the image
                int x = (screenSize.width - newWidth) / 2;
                int y = (screenSize.height - newHeight) / 2;

                g.drawImage(background, x, y, newWidth, newHeight, this);
            }
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
