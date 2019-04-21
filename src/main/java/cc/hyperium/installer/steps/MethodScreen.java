package cc.hyperium.installer.steps;

import cc.hyperium.installer.InstallerMain;
import cc.hyperium.installer.components.FlatButton;
import javax.swing.*;
import java.awt.*;

public class MethodScreen extends InstallerStep {
    @Override
    public void addComponents(Container c) {
        super.addComponents(c);

        JLabel text = new JLabel("Method", SwingConstants.CENTER);
        text.setFont(InstallerMain.INSTANCE.getTitle());
        text.setForeground(Color.WHITE);
        text.setBounds(0, 20, c.getWidth(), 65);
        c.add(text);

        JButton express = new FlatButton();
        express.setText("Express (Recommended)");
        express.setBounds(80, c.getHeight() / 2 - 60, c.getWidth() - 160, 50);
        express.addActionListener(e -> {
            InstallerMain.INSTANCE.next();
            InstallerMain.INSTANCE.next();
        });

        JButton custom = new FlatButton();
        custom.setText("Custom");
        custom.setBounds(80, c.getHeight() / 2 + 10, c.getWidth() - 160, 50);
        custom.addActionListener(e -> InstallerMain.INSTANCE.next());

        c.add(express);
        c.add(custom);
    }
}
