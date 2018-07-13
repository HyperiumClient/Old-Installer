package cc.hyperium.installer.steps;

import cc.hyperium.installer.InstallerMain;
import cc.hyperium.installer.components.CirclePanel;
import cc.hyperium.installer.components.FlatButton;
import cc.hyperium.installer.components.SliderUI;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.Color;
import java.awt.Container;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

/*
 * Created by Cubxity on 05/07/2018
 */
public class SettingsScreen extends InstallerStep {
    @Override
    public void addComponents(Container c) {
        super.addComponents(c);

        JLabel text = new JLabel("Settings", SwingConstants.CENTER);
        text.setFont(InstallerMain.INSTANCE.getTitle());
        text.setForeground(Color.WHITE);
        text.setBounds(0, 20, c.getWidth(), 65);
        c.add(text);

        int w = c.getHeight() / 3;
        int y = c.getHeight() / 2 - w / 2;
        int x = c.getWidth() / 2;

        JPanel wam = new CirclePanel();
        wam.setBounds(x - w * 2, y, w, w);
        wam.setOpaque(false);
        wam.setLayout(null);

        try {
            JLabel wamIcon = new JLabel(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/icons/wam.png")).getScaledInstance(w / 3, w / 3, Image.SCALE_DEFAULT)));
            wamIcon.setBounds(w / 3, w / 8, w / 3, w / 3);
            wamIcon.setBackground(Color.WHITE);
            wam.add(wamIcon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JLabel wamText = new JLabel(InstallerMain.INSTANCE.getConfig().getWam() + "GB", JLabel.CENTER);
        wamText.setFont(InstallerMain.INSTANCE.getFont());
        wamText.setBounds(w / 3, w / 2 + 20, w / 3, 14);
        wam.add(wamText);

        JSlider wamSlider = new JSlider();
        wamSlider.setUI(new SliderUI(wamSlider));
        wamSlider.setMinimum(1);
        wamSlider.setMaximum(8);
        wamSlider.setValue(InstallerMain.INSTANCE.getConfig().getWam());
        wamSlider.setBounds(20, w / 2 - 10, w - 40, 20);
        wamSlider.setBackground(Color.WHITE);
        wamSlider.addChangeListener(e -> {
            InstallerMain.INSTANCE.getConfig().setWam(wamSlider.getValue());
            wamText.setText(wamSlider.getValue() + "GB");
            wam.repaint();
        });
        wam.add(wamSlider);

        JPanel dir = new CirclePanel();
        dir.setBounds(x - w / 2, y, w, w);
        dir.setOpaque(false);
        dir.setLayout(null);

        JButton dirBtn = new FlatButton(false);
        try {
            dirBtn.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/icons/folder.png")).getScaledInstance(w / 2, w / 2, Image.SCALE_DEFAULT)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        dirBtn.setBounds(w / 4, w / 4, w / 2, w / 2);
        dirBtn.addActionListener(e -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(InstallerMain.INSTANCE.getConfig().getDir()));
            chooser.setDialogTitle("Choose minecraft folder");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            if (chooser.showOpenDialog(InstallerMain.INSTANCE.getFrame()) == JFileChooser.APPROVE_OPTION)
                InstallerMain.INSTANCE.getConfig().setDir(chooser.getSelectedFile().getAbsolutePath());
            try {
                UIManager.setLookAndFeel(new MetalLookAndFeel());
            } catch (Exception ignored) {
            }
        });
        dir.add(dirBtn);

        JPanel jre = new CirclePanel();
        jre.setBounds(x + w, y, w, w);
        jre.setOpaque(false);
        jre.setLayout(null);

        JLabel local = new JLabel(InstallerMain.INSTANCE.getConfig().getLocalJre() ? "LOCAL" : "INTEGRATED", JLabel.CENTER);
        local.setFont(InstallerMain.INSTANCE.getFont());
        local.setBounds(0, w / 7 * 4, w, 30);

        JButton jreBtn = new FlatButton(false);
        jreBtn.setBounds(w / 6, w / 4, w / 3 * 2, w / 3);
        jreBtn.setFont(jreBtn.getFont().deriveFont(25f));
        jreBtn.setText("JRE");
        jreBtn.addActionListener(e -> {
            boolean b = !InstallerMain.INSTANCE.getConfig().getLocalJre();
            InstallerMain.INSTANCE.getConfig().setLocalJre(b);
            local.setText(b ? "LOCAL" : "INTEGRATED");
            jre.repaint();
        });
        jre.add(jreBtn);
        jre.add(local);

        JButton next = new FlatButton();
        next.setText("Next");
        next.setBounds(c.getWidth() / 2 - 50, c.getHeight() - 40, 100, 22);
        next.addActionListener(e -> InstallerMain.INSTANCE.next());

        c.add(wam);
        c.add(dir);
        c.add(jre);
        c.add(next);
    }
}
