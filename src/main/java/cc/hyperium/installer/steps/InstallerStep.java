package cc.hyperium.installer.steps;

import cc.hyperium.installer.Installer;
import cc.hyperium.installer.components.FlatButton;
import cc.hyperium.installer.components.MotionPanel;
import cc.hyperium.utils.Colors;

import javax.swing.*;
import java.awt.*;

/*
 * Created by Cubxity on 05/07/2018
 */
public abstract class InstallerStep {
    public void addComponents(Container c) {
        MotionPanel mp = new MotionPanel(Installer.INSTANCE.getFrame());
        mp.setBackground(Colors.DARK.brighter());
        mp.setLayout(new BorderLayout());
        FlatButton fb = new FlatButton();
        fb.setText("X");
        fb.setSize(50, 20);
        fb.addActionListener(e -> System.exit(0));

        mp.add(fb, BorderLayout.EAST);
        mp.setBounds(0, 0, c.getWidth(), 20);
        c.add(mp);
    }

    public void modifyFrame(JFrame frame) {
    }
}
