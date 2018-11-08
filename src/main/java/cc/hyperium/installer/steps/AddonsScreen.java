package cc.hyperium.installer.steps;

import cc.hyperium.installer.InstallerMain;
import cc.hyperium.installer.api.entities.AddonManifest;
import cc.hyperium.installer.api.entities.InstallerConfig;
import cc.hyperium.installer.components.FlatButton;
import cc.hyperium.installer.components.FlatRadioButton;
import cc.hyperium.installer.components.HScrollBarUI;
import cc.hyperium.installer.components.VScrollBarUI;
import cc.hyperium.utils.Colors;
import cc.hyperium.utils.InstallerUtils;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/*
 * Created by Cubxity on 08/07/2018
 */
public class AddonsScreen extends InstallerStep {
    private final HashMap<JRadioButton, String[]> dependencies = new HashMap<>();

    @Override
    public void addComponents(Container c) {
        super.addComponents(c);
        JLabel text = new JLabel("Addons", SwingConstants.CENTER);
        text.setFont(InstallerMain.INSTANCE.getTitle());
        text.setForeground(Color.WHITE);
        text.setBounds(0, 20, c.getWidth(), 65);
        c.add(text);

        int w = c.getWidth() / 3;

        JScrollPane components = new JScrollPane();
        components.setBackground(Colors.DARK.brighter());
        components.setBounds(c.getWidth() / 2 - w, c.getHeight() / 4, w, c.getHeight() / 2);
        components.setBorder(BorderFactory.createEmptyBorder());
        components.getVerticalScrollBar().setUI(new VScrollBarUI());
        components.getVerticalScrollBar().setBackground(Colors.DARK.brighter());
        Rectangle b = components.getVerticalScrollBar().getBounds();
        components.getVerticalScrollBar().setBounds(b.x + (b.width - 5), b.y, 5, b.height);
        components.getHorizontalScrollBar().setUI(new HScrollBarUI());
        components.getHorizontalScrollBar().setBackground(Colors.DARK.brighter());
        b = components.getHorizontalScrollBar().getBounds();
        components.getHorizontalScrollBar().setBounds(b.x, b.y + (b.height - 5), b.width, 5);
        UIManager.put("ScrollBar.width", 5);

        JPanel cView = new JPanel();
        cView.setLayout(new BoxLayout(cView, BoxLayout.Y_AXIS));
        cView.setBackground(Colors.DARK.brighter());

        components.setViewportView(cView);

        JLabel cLabel = new JLabel("Components");
        cLabel.setFont(InstallerMain.INSTANCE.getFont().deriveFont(18f));
        cLabel.setForeground(Color.WHITE);
        cLabel.setBounds(c.getWidth() / 2 + 5, c.getHeight() / 4, w, 20);

        JTextArea cDesc = new JTextArea("Select components to install");
        cDesc.setFont(InstallerMain.INSTANCE.getFont());
        cDesc.setForeground(new Color(250, 250, 250));
        cDesc.setBackground(Colors.DARK);
        cDesc.setBounds(c.getWidth() / 2 + 5, c.getHeight() / 4 + 23, w, c.getHeight() / 2 - 23);
        cDesc.setLineWrap(true);
        cDesc.setWrapStyleWord(true);

        JRadioButton of = new FlatRadioButton("Optifine");
        of.setVerticalAlignment(SwingConstants.TOP);
        of.setHorizontalAlignment(SwingConstants.LEFT);
        of.setBackground(Colors.DARK.brighter());
        of.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                cLabel.setText("Components");
                cDesc.setText("Select the components you'd like to install");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                cLabel.setText("Optifine");
                cDesc.setText("Optifine is a Minecraft optimization mod.\n" +
                        "It allows Minecraft to run faster and look better with full support for HD textures and adds many configuration options.");
            }
        });
        InstallerConfig ic = InstallerMain.INSTANCE.getConfig();
        of.addActionListener(e -> {
            if (of.isSelected() && !ic.getComponents().contains("Optifine"))
                ic.getComponents().add("Optifine");
            else if (!of.isSelected())
                ic.getComponents().remove("Optifine");
        });
        of.setSelected(InstallerMain.INSTANCE.getConfig().getComponents().stream().anyMatch(cm -> cm.equals("Optifine")));
        cView.add(of);

        try {
            for (AddonManifest a : InstallerUtils.getManifest().getAddons()) {
                JRadioButton rb = new FlatRadioButton(a.getName());
                rb.setEnabled(!a.getUrl().isEmpty());
                rb.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseExited(MouseEvent e) {
                        cLabel.setText("Components");
                        cDesc.setText("Select the components you'd like to install");
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        cLabel.setText(a.getName());
                        cDesc.setText(a.getDescription() + (a.getVerified() ? "\n\nThis addon was verified by a Hyperium admin" : "") + "\n\nVersion: " + a.getVersion() + "\nAuthor: " + a.getAuthor());
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        ensureDependencies();
                    }
                });
                rb.setVerticalAlignment(SwingConstants.TOP);
                rb.setHorizontalAlignment(SwingConstants.LEFT);
                rb.setBackground(Colors.DARK.brighter());
                rb.addActionListener(e -> {
                    if (rb.isSelected() && !ic.getComponents().contains(a.getName()))
                        ic.getComponents().add(a.getName());
                    else if (!rb.isSelected())
                        ic.getComponents().remove(a.getName());
                });
                dependencies.put(rb, a.getDepends());
                cView.add(rb);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> comps = InstallerMain.INSTANCE.getConfig().getComponents();
        dependencies.keySet().forEach(rb -> rb.setSelected(comps.stream().anyMatch(cm -> cm.equals(rb.getText()))));

        JButton install = new FlatButton();
        install.setText("Install");
        install.setBounds(c.getWidth() / 2 - 50, c.getHeight() - 40, 100, 22);
        install.addActionListener(e -> InstallerMain.INSTANCE.next());

        c.add(components);
        c.add(cLabel);
        c.add(cDesc);
        c.add(install);
    }

    private void ensureDependencies() {
        dependencies.forEach((k, v) -> {
            k.setEnabled(Arrays.stream(v).allMatch(dep -> dependencies.keySet().stream().filter(c -> dep.equals(c.getText().replace("Addon :: ", ""))).allMatch(JRadioButton::isSelected)));
            if (!k.isEnabled())
                k.setSelected(false);
        });
    }
}
