package cc.hyperium.installer.steps;

import cc.hyperium.installer.InstallerMain;
import cc.hyperium.installer.api.Installer;
import cc.hyperium.installer.components.ComboBoxEditor;
import cc.hyperium.installer.components.ComboBoxRenderer;
import cc.hyperium.installer.components.FlatButton;
import cc.hyperium.installer.components.FlatRadioButton;
import cc.hyperium.utils.Colors;
import cc.hyperium.utils.InstallerUtils;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.Color;
import java.awt.Container;
import java.awt.Image;
import java.io.IOException;
import java.util.Arrays;

/*
 * Created by Cubxity on 06/07/2018
 */
public class VersionScreen extends InstallerStep {
    @Override
    public void addComponents(Container c) {
        super.addComponents(c);

        InstallerMain.INSTANCE.getConfig().setVersion(InstallerUtils.getManifest().getLatest());

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

        Arrays.stream(InstallerUtils.getManifest().getVersions()).filter(vm -> vm.getTargetInstaller() <= Installer.API_VERSION)
                .forEach(vm -> versions.addItem(vm.getName()));

        versions.addItem("BETA");
        versions.setSelectedItem(InstallerMain.INSTANCE.getConfig().getVersion().getName());
        versions.addActionListener(e -> {
            InstallerMain.INSTANCE.getConfig().setVersion(Arrays.stream(InstallerUtils.getManifest().getVersions()).filter(v -> v.getName().equals(versions.getSelectedItem())).findFirst().orElseGet(() -> InstallerUtils.getManifest().getLatest_beta()));
        });
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
        // c.add(beta);
        c.add(versions);
        c.add(next);
    }
}
