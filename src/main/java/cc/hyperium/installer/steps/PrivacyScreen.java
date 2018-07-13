package cc.hyperium.installer.steps;

import cc.hyperium.installer.InstallerMain;
import cc.hyperium.installer.components.FlatButton;
import cc.hyperium.installer.components.HScrollBarUI;
import cc.hyperium.installer.components.VScrollBarUI;
import cc.hyperium.utils.Colors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Container;
import java.awt.Rectangle;

/*
 * Created by Cubxity on 09/07/2018
 */
public class PrivacyScreen extends InstallerStep {
    @Override
    public void addComponents(Container c) {
        super.addComponents(c);

        JLabel text = new JLabel("Privacy Policy", SwingConstants.CENTER);
        text.setFont(InstallerMain.INSTANCE.getTitle());
        text.setForeground(Color.WHITE);
        text.setBounds(0, 20, c.getWidth(), 65);
        c.add(text);

        JTextArea essay = new JTextArea("Privacy Policy\n\n\n" +
                "What data does Hyperium Collect?" +
                "\n" +
                "When using Hyperium, the client and integrated mods send a few small pieces of information to a remote server in exchange for a temporary token for accessing data from Hypixel. This information includes your Minecraft UUID and username, Minecraft Version, Client Version and specific mod being used. This information is processed and used to determine if the client is out of date or if the client should abort the startup procedure.\n\n" +
                "What we do with the data?\n" +
                "\n" +
                "The data is stored securely for analytic purposes. We will never sell or release the data collected from specific users. All analytic graphs may be viewed on sk1er.club/graphs/hyperium\n" +
                "Last updated: March 17th, 2018\n");
        essay.setLineWrap(true);
        essay.setWrapStyleWord(true);
        essay.setFont(InstallerMain.INSTANCE.getFont());
        essay.setForeground(new Color(250, 250, 250));
        essay.setBackground(Colors.DARK.brighter());

        JScrollPane sp = new JScrollPane(essay, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        sp.setBackground(Colors.DARK.brighter());
        c.add(sp);
        sp.setBounds(c.getWidth() / 4, c.getHeight() / 4, c.getWidth() / 2, c.getHeight() / 2);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUI(new VScrollBarUI());
        sp.getVerticalScrollBar().setBackground(Colors.DARK.brighter());
        Rectangle b = sp.getVerticalScrollBar().getBounds();
        sp.getVerticalScrollBar().setBounds(b.x + (b.width - 5), b.y, 5, b.height);
        sp.getHorizontalScrollBar().setUI(new HScrollBarUI());
        sp.getHorizontalScrollBar().setBackground(Colors.DARK.brighter());
        b = sp.getHorizontalScrollBar().getBounds();
        sp.getHorizontalScrollBar().setBounds(b.x, b.y + (b.height - 5), b.width, 5);
        UIManager.put("ScrollBar.width", 5);

        JButton next = new FlatButton();
        next.setText("Accept and continue");
        next.setBounds(c.getWidth() / 2 - 100, c.getHeight() - 40, 200, 22);
        next.addActionListener(e -> InstallerMain.INSTANCE.next());
        c.add(next);
    }
}
