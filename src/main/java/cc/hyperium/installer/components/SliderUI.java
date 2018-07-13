package cc.hyperium.installer.components;

import cc.hyperium.utils.Colors;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

/*
 * Created by Cubxity on 06/07/2018
 */
public class SliderUI extends BasicSliderUI {
    private final Stroke s = new BasicStroke(1f);

    public SliderUI(JSlider b) {
        super(b);
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Stroke old = g2d.getStroke();
        g2d.setPaint(Colors.DARK);
        g2d.setStroke(s);
        g2d.drawLine(trackRect.x, trackRect.y + trackRect.height / 2, trackRect.x + trackRect.width, trackRect.y + trackRect.height / 2);
        g2d.setStroke(old);
    }

    @Override
    public void paintFocus(Graphics g) {
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(Colors.DARK.brighter());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fill(new Ellipse2D.Double(thumbRect.x, thumbRect.y + 4.5, thumbRect.width, thumbRect.height - 9));
    }
}
