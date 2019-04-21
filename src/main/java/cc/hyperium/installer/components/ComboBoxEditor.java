package cc.hyperium.installer.components;

import cc.hyperium.installer.InstallerMain;
import cc.hyperium.utils.Colors;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

public class ComboBoxEditor extends BasicComboBoxEditor {
    private final JLabel label = new JLabel();
    private final JPanel panel = new JPanel();
    private Object selectedItem;

    public ComboBoxEditor() {
        label.setOpaque(true);
        label.setFont(InstallerMain.INSTANCE.getFont());
        label.setForeground(Color.WHITE);

        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
        panel.add(label);
        panel.setBackground(Colors.DARK.brighter());
    }

    public Component getEditorComponent() {
        return this.panel;
    }

    public Object getItem() {
        return this.selectedItem;
    }

    public void setItem(Object item) {
        this.selectedItem = item;
        label.setText(item.toString());
    }

}
