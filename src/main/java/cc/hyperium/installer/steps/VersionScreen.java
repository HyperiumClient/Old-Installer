package cc.hyperium.installer.steps;

import cc.hyperium.installer.InstallerMain;
import cc.hyperium.installer.components.ComboBoxEditor;
import cc.hyperium.installer.components.ComboBoxRenderer;
import cc.hyperium.installer.components.FlatButton;
import cc.hyperium.installer.components.FlatRadioButton;
import cc.hyperium.utils.Colors;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.io.IOException;

/*
 * Created by Cubxity on 06/07/2018
 */
public class VersionScreen extends InstallerStep {
    @Override
    public void addComponents(Container c) {
        super.addComponents(c);

        int tw = c.getFontMetrics(InstallerMain.INSTANCE.getFont()).stringWidth("Select version");
        int w = Math.min(c.getHeight() / 3, 1080);

        JLabel icon = new JLabel();
        try {
            icon.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/icons/hyperium-transparent.png")).getScaledInstance(w, w, Image.SCALE_DEFAULT)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        icon.setBounds(c.getWidth() / 2 - tw / 2 - w / 2 - w, c.getHeight() / 2 - w / 2, w, w);

        JLabel text = new JLabel("Select version");
        text.setFont(InstallerMain.INSTANCE.getTitle());
        text.setForeground(Color.WHITE);
        text.setBounds(c.getWidth() / 2 - tw / 2, c.getHeight() / 2 - w / 2, 600, 65);

        JRadioButton beta = new FlatRadioButton("Show beta builds (Unstable)");
        beta.setBounds(c.getWidth() / 2 - tw / 2, c.getHeight() / 2 + 25, 250, 30);

        JComboBox<String> versions = new JComboBox<>();
        versions.setFont(InstallerMain.INSTANCE.getFont());
        versions.setUI(new BasicComboBoxUI());
        versions.addItem("Release B16");
        versions.addItem("Release B15");
        versions.setBounds(c.getWidth() / 2 - tw / 2, c.getHeight() / 2 + 5, 250, 20);
        versions.setBackground(Colors.DARK.brighter());
        versions.setForeground(Color.WHITE);
        versions.setRenderer(new ComboBoxRenderer());
        versions.setEditor(new ComboBoxEditor());
        versions.getEditor().getEditorComponent().setBackground(Colors.DARK.brighter());

        JButton next = new FlatButton();
        next.setText("Next");
        next.setBounds(c.getWidth() / 2 - 50, c.getHeight() - 40, 100, 22);
        next.addActionListener(e -> InstallerMain.INSTANCE.next());

        c.add(text);
        c.add(icon);
        c.add(beta);
        c.add(versions);
        c.add(next);
    }
}
