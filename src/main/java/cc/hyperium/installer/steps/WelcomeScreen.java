package cc.hyperium.installer.steps;

import cc.hyperium.installer.Installer;
import cc.hyperium.installer.components.FlatButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/*
 * Created by Cubxity on 05/07/2018
 */
public class WelcomeScreen extends InstallerStep {
    @Override
    public void addComponents(Container c) {
        super.addComponents(c);

        JLabel icon = new JLabel();
        int w = Math.min(c.getHeight() / 2, 1080);
        try {
            icon.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/icons/hyperium-transparent.png")).getScaledInstance(w, w, Image.SCALE_DEFAULT)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        icon.setBounds(c.getWidth() / 2 - w / 2, w / 4, w, w);

        JLabel text = new JLabel("Hyperium Installer", SwingConstants.CENTER);
        text.setFont(Installer.INSTANCE.getTitle());
        text.setForeground(Color.WHITE);
        text.setBounds(0, w / 4 + w + 10, c.getWidth(), 60);

        JButton next = new FlatButton();
        next.setText("Next");
        next.setBounds(c.getWidth() / 2 - 50, c.getHeight() - 40, 100, 22);
        next.addActionListener(e -> Installer.INSTANCE.next());

        c.add(icon);
        c.add(text);
        c.add(next);
    }
}
