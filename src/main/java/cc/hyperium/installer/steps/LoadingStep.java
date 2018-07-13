package cc.hyperium.installer.steps;

import cc.hyperium.installer.InstallerMain;

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Container;

/*
 * Created by Cubxity on 06/07/2018
 */
public class LoadingStep extends InstallerStep {
    @Override
    public void addComponents(Container c) {
        super.addComponents(c);

        JLabel label = new JLabel("Loading manifest...", JLabel.CENTER);
        label.setFont(InstallerMain.INSTANCE.getTitle());
        label.setForeground(Color.WHITE);
        label.setBounds(0, c.getHeight() / 2 - 25, c.getWidth(), 60);
        c.add(label);
    }
}
