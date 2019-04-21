package cc.hyperium.installer.components;

import cc.hyperium.installer.InstallerMain;
import cc.hyperium.utils.Colors;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FlatButton extends JButton {
    public FlatButton() {
        this(true);
    }

    public FlatButton(boolean hover) {
        super();
        setBorderPainted(false);
        setFocusPainted(false);
        setBackground(Color.WHITE);
        setForeground(Colors.DARK);
        setFont(InstallerMain.INSTANCE.getFont());
        if (hover)
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
