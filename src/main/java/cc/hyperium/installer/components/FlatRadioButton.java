package cc.hyperium.installer.components;

import cc.hyperium.installer.InstallerMain;
import javax.swing.ImageIcon;
import javax.swing.JRadioButton;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class FlatRadioButton extends JRadioButton {

    private static final BufferedImage unselectedButtonImage;
    private static final BufferedImage selectedButtonImage;
    private static final BufferedImage disabledButtonImage;

    static {
        unselectedButtonImage = new BufferedImage(13, 13, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = unselectedButtonImage.createGraphics();
        graphics.setPaint(Color.WHITE);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.draw(new Ellipse2D.Double(0, 0, 12, 12));

        selectedButtonImage = new BufferedImage(13, 13, BufferedImage.TYPE_INT_ARGB);
        graphics = selectedButtonImage.createGraphics();
        graphics.setPaint(Color.WHITE);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.draw(new Ellipse2D.Double(0, 0, 12, 12));
        graphics.fill(new Ellipse2D.Double(2, 2, 9, 9));

        disabledButtonImage = new BufferedImage(13, 13, BufferedImage.TYPE_INT_ARGB);
        graphics = disabledButtonImage.createGraphics();
        graphics.setPaint(Color.GRAY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.draw(new Ellipse2D.Double(0, 0, 12, 12));
    }

    public FlatRadioButton(String label) {
        super(label);
        setForeground(Color.WHITE);
        setBackground(new Color(30, 30, 30));
        setIcon(new ImageIcon(unselectedButtonImage));
        setSelectedIcon(new ImageIcon(selectedButtonImage));
        setDisabledIcon(new ImageIcon(disabledButtonImage));
        setFocusPainted(false);
        setFont(InstallerMain.INSTANCE.getFont());
    }
}
