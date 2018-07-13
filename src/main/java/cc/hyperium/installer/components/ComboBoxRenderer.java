package cc.hyperium.installer.components;

import cc.hyperium.utils.Colors;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;

/*
 * Created by Cubxity on 06/07/2018
 */
public class ComboBoxRenderer extends JLabel implements ListCellRenderer<String> {
    public ComboBoxRenderer() {
        setOpaque(true);
        setBackground(Colors.DARK.brighter());
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value);
        setFont(list.getFont());

        if (isSelected)
            setBackground(Colors.DARK);
        else
            setBackground(Colors.DARK.brighter());

        setForeground(Color.WHITE);

        return this;
    }
}
