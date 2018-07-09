package cc.hyperium.installer.steps;

import cc.hyperium.installer.InstallerMain;
import cc.hyperium.installer.api.Installer;
import cc.hyperium.installer.api.callbacks.ErrorCallback;
import cc.hyperium.installer.components.MotionPanel;
import cc.hyperium.utils.Colors;
import cc.hyperium.utils.Multithreading;
import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

/*
 * Created by Cubxity on 08/07/2018
 */
public class InstallingScreen extends InstallerStep {
    @Override
    public void addComponents(Container c) {
        JPanel panel = new MotionPanel(InstallerMain.INSTANCE.getFrame());
        panel.setBounds(0, 0, c.getWidth(), c.getHeight());
        panel.setBackground(Colors.DARK);
        panel.setLayout(null);
        c.add(panel);

        JLabel title = new JLabel("Starting installation...", JLabel.CENTER);
        title.setFont(InstallerMain.INSTANCE.getTitle().deriveFont(25f));
        title.setForeground(Color.WHITE);
        title.setBounds(0, panel.getHeight() / 5 * 4, panel.getWidth(), 40);

        int w = Math.min(panel.getHeight() / 3, 366);
        JLabel blob = new JLabel(new ImageIcon(new ImageIcon(getClass().getResource("/icons/loading.gif")).getImage().getScaledInstance(w, w, Image.SCALE_DEFAULT)));
        blob.setBounds(panel.getWidth() / 2 - w / 2, panel.getHeight() / 2 - w / 2, w, w);
        blob.setSize(w, w);

        panel.add(title);
        panel.add(blob);

        Multithreading.runAsync(() -> {
            try {
                Files.write(new File(System.getProperty("user.home"), "hinstaller-state.json").toPath(), new Gson().toJson(InstallerMain.INSTANCE.getConfig()).getBytes(Charset.defaultCharset()));
            } catch (Exception ex) {
                InstallerMain.INSTANCE.getLogger().error("Failed to save current configuration", ex);
            }

            InstallerMain.INSTANCE.getLogger().info("Starting installation...");

            Installer in = new Installer(InstallerMain.INSTANCE.getConfig(), callback -> {
                title.setText(callback.getMessage());
                if (callback instanceof ErrorCallback)
                    InstallerMain.INSTANCE.getLogger().error("Unexpected error occurred during installation", ((ErrorCallback) callback).getError());
            });
            try {
                in.install();
                InstallerMain.INSTANCE.getLogger().info("Installation finished with code {}", in.getCode());
                title.setText("Installation success");
                super.addComponents(panel);
                InstallerMain.INSTANCE.getFrame().repaint();
            } catch (Exception ex) {
                InstallerMain.INSTANCE.getLogger().error("Unexpected error occurred during installation", ex);
                title.setText("Unexpected error: " + ex.getMessage());
                super.addComponents(panel);
                InstallerMain.INSTANCE.getFrame().repaint();
            }
        });
    }
}
