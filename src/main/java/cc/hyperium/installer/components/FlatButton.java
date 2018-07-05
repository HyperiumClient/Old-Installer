package cc.hyperium.installer.components;

import cc.hyperium.installer.Installer;
import cc.hyperium.utils.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
 * Created by Cubxity on 05/07/2018
 */
public class FlatButton extends JButton {
    public FlatButton() {
        super();
        setBorderPainted(false);
        setFocusPainted(false);
        setBackground(Color.WHITE);
        setForeground(Colors.DARK);
        setFont(Installer.INSTANCE.getFont());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(getBackground().darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(getBackground().brighter());
            }
        });
    }
}
