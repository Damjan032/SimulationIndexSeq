package indexseq.dialogs;

import indexseq.*;
import indexseq.animator.Animator;
import indexseq.blockandcomponents.Block;
import indexseq.blockandcomponents.Record;
import indexseq.enums.FileStatus;
import indexseq.enums.PointerType;
import indexseq.enums.TipObrade;
import indexseq.supportclasses.SearchResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;


public class UpdateDialog extends JDialog {

    int recordIndex;
    private Block block;
    private Record record;

    private JCheckBox cbStatus, cbField;

    private JPanel optionPanel;

    private ArrayList<JTextField> tfFields = new ArrayList<>();
    private JRadioButton tbActive, tbInactive;


    public UpdateDialog(SearchResult sr, TipObrade tip) {
        super(MainFrame.init(), true);

        if (sr.pointer.getType()== PointerType.OVERFLOW){
            recordIndex = 0;
        }else{
            recordIndex = sr.recordAddress;
        }

        block = new Block(sr.foundBlock);
        block.setFileStatus(FileStatus.LOADED);
        block.changeRecordSpecialStatus(Record.Status.SELECTED, recordIndex);
        setTitle("Izmeni slog");

        record = block.getRecord(recordIndex);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeDialog();
            }
        });
        setUpGrid();
        setButton(tip);
        pack();
    }

    private void setButton(TipObrade tip) {
        JPanel boxPanel = new JPanel();
//        boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.X_AXIS));
        boxPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton btCancel = new JButton("Poništavanje");
        btCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        });

        JButton btUpdate = new JButton("Izmeni");
        btUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isCorrect()) {
                    JOptionPane.showMessageDialog(MainFrame.init(), "Netačan unos!", "Upozorenje",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                updateRecord();
                setVisible(false);
            }
        });
        JButton btNext = new JButton("Next");

        btNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ++recordIndex;

                updateRecord();

                if(recordIndex>=block.getBlockFactor()){
                    btNext.setEnabled(false);
                    btUpdate.setEnabled(true);
                    return;
                }
                record = block.getRecord(recordIndex);
                if (record.getStatus()== Record.Status.EMPTY){
                    btNext.setEnabled(false);
                    btUpdate.setEnabled(true);
                    return;
                }
                setupNewRecord();
            }
        });

        if (tip==TipObrade.DIREKTNA){
            btNext.setEnabled(false);
        }else{
            btUpdate.setEnabled(false);
        }
        boxPanel.add(Box.createRigidArea(new Dimension(150, 0)));
        boxPanel.add(btCancel);
        boxPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        boxPanel.add(btNext);
        boxPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        boxPanel.add(btUpdate);
        boxPanel.add(Box.createVerticalStrut(50));
        this.add(boxPanel, BorderLayout.AFTER_LAST_LINE);
        //  addRecord(boxPanel);

    }

    private void closeDialog() {
        block = null;
        setVisible(false);
    }

    private void setUpGrid() {
//        JPanel glavniGread = new JPanel();
//        glavniGread.setLayout(new GridLayout(1, 2));

        JSplitPane split = new JSplitPane();

        optionPanel = new JPanel();
//        optionPanel.setLayout(new GridLayout(4, 1));
        optionPanel.setLayout(new GridBagLayout());

        tbActive = new JRadioButton("Aktivan", true);
        tbInactive = new JRadioButton("Neaktivan");
        ButtonGroup buttons = new ButtonGroup();
        buttons.add(tbActive);
        buttons.add(tbInactive);


//        JPanel boxPanel = new JPanel();
//        boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.X_AXIS));
        JLabel lab = new JLabel("Da li želite da menjate status? ");
        lab.setFont(new Font("Serif", Font.PLAIN, 14));
        GridBagConstraints g1 = new GridBagConstraints();
        g1.gridx = 0;
        g1.gridy = 0;
        optionPanel.add(lab, g1);

        cbStatus = new JCheckBox("");
        cbStatus.addChangeListener(e -> {
            if (cbStatus.isSelected()) {
                tbActive.setEnabled(true);
                tbInactive.setEnabled(true);
            } else {
                tbActive.setEnabled(false);
                tbInactive.setEnabled(false);
            }
        });
        GridBagConstraints g2 = new GridBagConstraints();
        g2.gridx = 1;
        g2.gridy = 0;
        optionPanel.add(cbStatus, g2);
//        boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.X_AXIS));
//        boxPanel.addRecord(Box.createRigidArea(new Dimension(20, 0)));
//        boxPanel.addRecord(lab);
//
//        boxPanel.addRecord(cbStatus);
//        optionPanel.addRecord(boxPanel);


//        JPanel boxPanel2 = new JPanel();
//        boxPanel2.setLayout(new BoxLayout(boxPanel2, BoxLayout.X_AXIS));

        tbActive.setEnabled(false);
        GridBagConstraints g3 = new GridBagConstraints();
        g3.gridx = 0;
        g3.gridy = 1;
        optionPanel.add(tbActive, g3);

        tbInactive.setEnabled(false);
        GridBagConstraints g4 = new GridBagConstraints();
        g4.gridx = 1;
        g4.gridy = 1;
        optionPanel.add(tbInactive, g4);//        boxPanel2.addRecord(Box.createRigidArea(new Dimension(40, 0)));
//        boxPanel2.addRecord(tbActive);
//        boxPanel2.addRecord(Box.createRigidArea(new Dimension(40, 0)));
//        boxPanel2.addRecord(tbInactive);
//        optionPanel.addRecord(boxPanel2);

//        JPanel boxPanel3 = new JPanel();
//        boxPanel3.setLayout(new BoxLayout(boxPanel3, BoxLayout.X_AXIS));
        JSeparator separator = new JSeparator();
        separator.setOrientation(JSeparator.HORIZONTAL);
        GridBagConstraints gg = new GridBagConstraints();
        gg.gridx = 0;
        gg.gridy = 2;
        optionPanel.add(separator, gg);

        JLabel lab2 = new JLabel("Da li žečite da menjate polja? ");
        lab2.setFont(new Font("Serif", Font.PLAIN, 14));
        GridBagConstraints g5 = new GridBagConstraints();
        g5.gridx = 0;
        g5.gridy = 3;
        optionPanel.add(lab2, g5);

        cbField = new JCheckBox("");
//        boxPanel3.setLayout(new BoxLayout(boxPanel3, BoxLayout.X_AXIS));
//        boxPanel3.addRecord(Box.createRigidArea(new Dimension(20, 0)));
//        boxPanel3.addRecord(lab2);

//        boxPanel3.addRecord(cbField);
//        optionPanel.addRecord(boxPanel3);


        cbField.addChangeListener(e -> {
            if (cbField.isSelected()) {
                for (JTextField textField : tfFields) {
                    textField.setEnabled(true);
                }
            } else {
                for (JTextField textField : tfFields) {
                    textField.setEnabled(false);
                }
            }
        });
        GridBagConstraints g6 = new GridBagConstraints();
        g6.gridx = 1;
        g6.gridy = 3;
        optionPanel.add(cbField, g6);

        setupTextFields();

//        glavniGread.addRecord(optionPanel);
        split.setLeftComponent(optionPanel);

//        borderPanel.setLayout(new BorderLayout());
        //   JLabel lab4 = new JLabel("Selected block:");
        //   lab4.setFont(new Font("Serif", Font.PLAIN, 14));
        //   boxPanel22.addRecord(lab4);
//        borderPanel.addRecord(indexSequential.getPrimaryZone().getBlocks().get(searchResult.getBlockAddress()), BorderLayout.CENTER);
        // TODO izmenjeno dodavanje u panel
//        borderPanel.addRecord(block, BorderLayout.CENTER);
//        glavniGread.addRecord(borderPanel);
        JPanel blockPanel = new JPanel();
        blockPanel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        blockPanel.add(block, gc);
        split.setRightComponent(blockPanel);
        split.setContinuousLayout(true);
//        addRecord(glavniGread);
        add(split);
    }

    private void setupTextFields() {
//        JPanel box = new JPanel();
//        box.setLayout(new BoxLayout(box,BoxLayout.Y_AXIS));
        for (int i = 0; i < record.getFieldSize(); i++) {
            JPanel editPanel = new JPanel();
            editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.X_AXIS));
            JLabel editLabel = new JLabel("Izmeni polje " + (i + 1) + " :");
            editLabel.setFont(new Font("Serif", Font.PLAIN, 14));

            JTextField editTextField = new JTextField(record.getField(i));
            editTextField.setEnabled(false);

            editPanel.add(Box.createRigidArea(new Dimension(40, 0)));
            editPanel.add(editLabel);
            editPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            editPanel.add(editTextField);
            editPanel.add(Box.createRigidArea(new Dimension(30, 0)));


            GridBagConstraints g = new GridBagConstraints();
            g.gridx = 0;
            g.gridy = i + 4;
            g.gridwidth = 2;
            g.fill = GridBagConstraints.BOTH;
            optionPanel.add(editPanel, g);

            tfFields.add(editTextField);
        }
    }

    private boolean isCorrect() {
        for (JTextField textField : tfFields
        ) {
            String text = textField.getText().trim();
            if (text.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public Block runDialog() {
        setVisible(true);
        return block;
    }

    private void updateRecord(){
        if (cbField.isSelected()) {
            for (int i = 0; i < record.getFieldSize(); i++) {
                record.setField(i, tfFields.get(i).getText().trim());
            }
        }
        record.resetStatus();

        if (cbStatus.isSelected()) {
            if (tbActive.isSelected()) {
                record.setStatus(Record.Status.ACTIVE);
            } else {
                record.setStatus(Record.Status.INACTIVE);
            }
        }
//                Animator.init().addAnimation(block.getHighlightAnim());
//                Animator.init().addAnimation(block.getUnloadAnim());
        //   JOptionPane.showMessageDialog(new JPanel(), "Incorrect outputs!", "Warning");

        Animator.init().addAnimation(record.getUpdateAnim());
        Animator.init().addLastAnim(block.getUnloadAnim());
    }

    private void setupNewRecord(){
        block.changeRecordSpecialStatus(Record.Status.SELECTED, recordIndex);
        for(JTextField textField:tfFields){
            optionPanel.remove(textField);
        }
        cbStatus.setSelected(false);
        cbField.setSelected(false);
        setupTextFields();
    }

//    private void saveBlock(){
//        block.getRecords().setRecord(searchResult.getRecordAddress(),record);
//        System.out.println(block);
//        indexSequential.getPrimaryZone().getBlocks().setRecord(searchResult.getBlockAddress(),block);
//        Animator.init().addAnimation(block.getHighlightAnim());
//    }
}

