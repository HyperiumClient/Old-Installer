package cc.hyperium.installer.components;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/*
 * Created by Cubxity on 06/07/2018
 */
public class CirclePanel extends JPanel {
    public CirclePanel() {

    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.fillOval(0, 0, g.getClipBounds().width, g.getClipBounds().height);
    }
}
