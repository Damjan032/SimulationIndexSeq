package indexseq.supportclasses;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

public class Focuser implements ActionListener, Serializable {

    private JPanel panel;

    public Focuser(JPanel panel) {
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComponent src = (JComponent) e.getSource();
        src.requestFocus();
        panel.scrollRectToVisible(src.getBounds());
    }
}
