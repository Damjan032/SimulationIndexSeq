package indexseq.dialogs;

import indexseq.blockandcomponents.Block;
import indexseq.enums.FileStatus;

import javax.swing.*;
import java.awt.*;

public class OperativeMemoryDialog extends JDialog {

    Block block;

    public OperativeMemoryDialog(Frame owner, Block b) {
        super(owner);
        block = b;
        block.setFileStatus(FileStatus.LOADED);
        setup();
    }

    private void setup() {
        setLayout(new BorderLayout());
        add(new JLabel("Operativna memorija"),BorderLayout.NORTH);
        JPanel center = new JPanel();
        center.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5,5,5,5);
        center.add(block,gc);
        add(center,BorderLayout.CENTER);
        setLocationRelativeTo(null);
        pack();
    }

    public void runDialog() {
        setVisible(true);
    }

    public Block getBlock() {
        return block;
    }
}
