package cc.hyperium.installer.steps;

import cc.hyperium.installer.Installer;

import javax.swing.*;
import java.awt.*;

/*
 * Created by Cubxity on 05/07/2018
 */
public class SettingsScreen extends InstallerStep {
    @Override
    public void addComponents(Container c) {
        super.addComponents(c);

        JLabel text = new JLabel("Settings  ", SwingConstants.CENTER);
        text.setFont(Installer.INSTANCE.getTitle());
        text.setForeground(Color.WHITE);
        text.setBounds(0, 20, c.getWidth(), 65);
        c.add(text);
    }
}
