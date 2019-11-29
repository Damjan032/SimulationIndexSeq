package indexseq.dialogs;

import indexseq.blockandcomponents.Record;
import indexseq.enums.OverFlowType;
import indexseq.Zones.PrimaryZone;
import indexseq.enums.PropagationType;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import static indexseq.dialogs.NewRecordDialog.dialogBcg;

public class NewFileDialog extends JDialog {
    private String fileName = "Fajl";
    private PrimaryZone resultPrimary = new PrimaryZone(MIN_BLOCKS, MIN_FACT, MIN_FIELD);
    private OverFlowType overFlowType = OverFlowType.INDIRECT;
    private PropagationType propagationType = PropagationType.HIGH;
    private int order = MIN_ORDER;
    private static final int MIN_BLOCKS = 5;
    private static final int MAX_BLOCKS = 15;
    private static final int MIN_FACT = 1;
    private static final int MAX_FACT = 5;
    private static final int MIN_FIELD = 1;
    private static final int MAX_FIELD = 5;
    private static final int MIN_ORDER = 2;
    private static final int MAX_ORDER = 3;
    private boolean result;
    private JPanel fillPanel;
    private JPanel optionsPanel;
    private final JSplitPane splitPane;
    private JButton ok;
    private JTextField nameInput;

    public NewFileDialog(Frame owner) {
        super(owner, "Dijalog za novi fajl", true);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        JScrollPane jScrollPane = new JScrollPane(resultPrimary, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, optionsPanel, jScrollPane);
//        splitPane.setDividerLocation(splitPane.getWidth()/2);
        add(splitPane, BorderLayout.CENTER);
        setupOptionsPanel();
        setupButtonPanel();
        pack();
    }

    private void setupFillPanel() {
        fillPanel = new JPanel();
        fillPanel.setOpaque(true);
        fillPanel.setBackground(dialogBcg);
        fillPanel.setLayout(new GridBagLayout());


        JButton addButton = new JButton("Dodaj slog");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRecord();
                if (resultPrimary.isNearlyFull()) {
                    ok.setEnabled(true);
                }
                if (resultPrimary.isFull()) {
                    addButton.setEnabled(false);
                }
            }
        });
        GridBagConstraints g1 = new GridBagConstraints();
        g1.insets = new Insets(5, 5, 5, 5);
        g1.gridx = 0;
        fillPanel.add(addButton, g1);

        JButton randButton = new JButton("Nasumično popunjavanje");
        randButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                randomFill();
                if (resultPrimary.isFull()) {
                    addButton.setEnabled(false);
                }
                ok.setEnabled(true);
            }
        });
        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(5, 5, 5, 5);
        g2.gridx = 1;
        fillPanel.add(randButton, g2);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        splitPane.setLeftComponent(fillPanel);
    }

    private void setupOptionsPanel() {
        optionsPanel = new JPanel();
        optionsPanel.setOpaque(true);
        optionsPanel.setBackground(dialogBcg);
        optionsPanel.setLayout(new GridBagLayout());

        JLabel nameLabel = new JLabel("Naziv fajla");
        GridBagConstraints g1 = new GridBagConstraints();
        g1.gridx = 0;
        g1.gridy = 0;
        g1.insets = new Insets(5, 5, 5, 5);
        optionsPanel.add(nameLabel, g1);

        nameInput = new JTextField();
        nameInput.requestFocus();
        nameInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setText();

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setText();

            }

            private void setText() {
                fileName = nameInput.getText().trim();
            }
        });
        nameInput.setPreferredSize(new Dimension(200, 20));
        GridBagConstraints g2 = new GridBagConstraints();
        g2.gridx = 1;
        g2.gridy = 0;
        g2.insets = new Insets(5, 5, 5, 5);
        optionsPanel.add(nameInput, g2);

        JLabel buttonLabel = new JLabel("Tip zone prekoračenja");
        buttonLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        GridBagConstraints gg = new GridBagConstraints();
        gg.gridx = 0;
        gg.gridy = 2;
        gg.gridwidth = 2;
        gg.insets = new Insets(10, 5, 5, 5);
        optionsPanel.add(buttonLabel, gg);

        JRadioButton indirect = new JRadioButton("Indirektna");
        indirect.setSelected(true);
        indirect.setOpaque(true);
        indirect.setBackground(dialogBcg);
        indirect.addActionListener(e -> {
            overFlowType = OverFlowType.INDIRECT;
            resultPrimary.setOverFlowType(OverFlowType.INDIRECT);
        });
        GridBagConstraints g3 = new GridBagConstraints();
        g3.gridx = 0;
        g3.gridy = 3;
        g3.insets = new Insets(5, 5, 10, 5);
        optionsPanel.add(indirect, g3);

        JRadioButton direct = new JRadioButton("Direktna");
        direct.setOpaque(true);
        direct.setBackground(dialogBcg);
        direct.addActionListener(e -> {
            overFlowType = OverFlowType.DIRECT;
            resultPrimary.setOverFlowType(OverFlowType.DIRECT);
        });
        GridBagConstraints g4 = new GridBagConstraints();
        g4.gridx = 1;
        g4.gridy = 3;
        g4.insets = new Insets(5, 5, 10, 5);
        optionsPanel.add(direct, g4);

        ButtonGroup buttons = new ButtonGroup();
        buttons.add(indirect);
        buttons.add(direct);

        JLabel label = new JLabel("Tip propagacije ključa");
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        GridBagConstraints gl = new GridBagConstraints();
        gl.gridx = 0;
        gl.gridy = 4;
        gl.gridwidth = 2;
        gl.insets = new Insets(10, 5, 5, 5);
        optionsPanel.add(label, gl);

        JRadioButton high = new JRadioButton("Propagacija najveceg");
        high.setSelected(true);
        high.setOpaque(true);
        high.setBackground(dialogBcg);
        high.addActionListener(e -> propagationType = PropagationType.HIGH);
        GridBagConstraints gggf = new GridBagConstraints();
        gggf.gridx = 0;
        gggf.gridy = 5;
        gggf.insets = new Insets(5, 5, 10, 5);
        optionsPanel.add(high, gggf);

        JRadioButton low = new JRadioButton("Propagacija najmanjeg");
        low.setOpaque(true);
        low.setBackground(dialogBcg);
        low.addActionListener(e -> propagationType = PropagationType.LOW);
        GridBagConstraints ggh = new GridBagConstraints();
        ggh.gridx = 1;
        ggh.gridy = 5;
        ggh.insets = new Insets(5, 5, 10, 5);
        optionsPanel.add(low, ggh);

        ButtonGroup buttons2 = new ButtonGroup();
        buttons2.add(high);
        buttons2.add(low);


        JLabel numLabel = new JLabel("Broj blokova");
        GridBagConstraints gcl = new GridBagConstraints();
        gcl.gridx = 0;
        gcl.gridy = 6;
//        g4.insets = new Insets(5,5,5,5);
        optionsPanel.add(numLabel, gcl);

        JLabel factLabel = new JLabel("Faktor blokiranja");
        GridBagConstraints gcl1 = new GridBagConstraints();
        gcl1.gridx = 0;
        gcl1.gridy = 7;
        optionsPanel.add(factLabel, gcl1);

        JLabel FieldLabel = new JLabel("Broj polja slogova");
        GridBagConstraints gcc = new GridBagConstraints();
        gcc.gridx = 0;
        gcc.gridy = 8;
        optionsPanel.add(FieldLabel, gcc);

        JLabel treeLabel = new JLabel("Red stabla");
        GridBagConstraints ggg = new GridBagConstraints();
        ggg.gridx = 0;
        ggg.gridy = 9;
//        g4.insets = new Insets(5,5,5,5);
        optionsPanel.add(treeLabel, ggg);

        JSlider blockNumSlider = new JSlider(JSlider.HORIZONTAL, MIN_BLOCKS, MAX_BLOCKS, MIN_BLOCKS);
        blockNumSlider.setMinimumSize(new Dimension(200, 20));
        blockNumSlider.setSnapToTicks(true);
        blockNumSlider.setMajorTickSpacing(5);
        blockNumSlider.setMinorTickSpacing(1);
        blockNumSlider.setPaintTicks(true);
        blockNumSlider.setPaintLabels(true);

        blockNumSlider.setToolTipText("Broj blokova primarne zone");
        //dodavanje listenera na slider
        blockNumSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                resultPrimary.setBlockNum(blockNumSlider.getValue());
            }
        });
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = 6;
        gc.insets = new Insets(5, 5, 10, 5);
        optionsPanel.add(blockNumSlider, gc);

        JSlider blockFactSlider = new JSlider(JSlider.HORIZONTAL, MIN_FACT, MAX_FACT, MIN_FACT);
        blockFactSlider.setMinimumSize(new Dimension(200, 20));
        blockFactSlider.setSnapToTicks(true);
        blockFactSlider.setMajorTickSpacing(1);
        blockFactSlider.setPaintTicks(true);
        blockFactSlider.setPaintLabels(true);

//        slider.setValue(MAX_VAL / 2);
        blockFactSlider.setToolTipText("Faktor blokiranja primarne zone");
        //dodavanje listenera na slider
        blockFactSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                resultPrimary.setBlockFactor(blockFactSlider.getValue());
            }
        });
        GridBagConstraints gc1 = new GridBagConstraints();
        gc1.gridx = 1;
        gc1.gridy = 7;
        gc1.insets = new Insets(10, 5, 10, 5);
        optionsPanel.add(blockFactSlider, gc1);

        JSlider fieldNumSlider = new JSlider(JSlider.HORIZONTAL, MIN_FIELD, MAX_FIELD, MIN_FIELD);
        fieldNumSlider.setMinimumSize(new Dimension(200, 20));
        fieldNumSlider.setSnapToTicks(true);
        fieldNumSlider.setMajorTickSpacing(1);
        fieldNumSlider.setPaintTicks(true);
        fieldNumSlider.setPaintLabels(true);

//        slider.setValue(MAX_VAL / 2);
        fieldNumSlider.setToolTipText("Broj polja slogova u primarnoj zoni");
        //dodavanje listenera na slider
        fieldNumSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                resultPrimary.setFieldNum(fieldNumSlider.getValue());
            }
        });
        GridBagConstraints ggg1 = new GridBagConstraints();
        ggg1.gridx = 1;
        ggg1.gridy = 8;
        ggg1.insets = new Insets(10, 5, 10, 5);
        optionsPanel.add(fieldNumSlider, ggg1);


        JSlider treeOrderSlider = new JSlider(JSlider.HORIZONTAL, MIN_ORDER, MAX_ORDER, MIN_ORDER);
        treeOrderSlider.setMinimumSize(new Dimension(200, 20));
        treeOrderSlider.setSnapToTicks(true);
        treeOrderSlider.setMajorTickSpacing(1);
        treeOrderSlider.setPaintTicks(true);
        treeOrderSlider.setPaintLabels(true);

        treeOrderSlider.setToolTipText("Red stabla");
        treeOrderSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                order = treeOrderSlider.getValue();
            }
        });
        GridBagConstraints gege = new GridBagConstraints();
        gege.gridx = 1;
        gege.gridy = 9;
        gege.insets = new Insets(10, 5, 10, 5);
        optionsPanel.add(treeOrderSlider, gege);


        splitPane.setLeftComponent(optionsPanel);
    }

    private void setupButtonPanel() {
        JPanel buttonPanel = new JPanel();
        ok = new JButton("Ok");
        ok.setEnabled(false);
        ok.addActionListener(e -> {
            result = true;
            setVisible(false);
        });

        JButton nextButton = new JButton("Sledeće");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameInput.getText().isEmpty()){
                    JOptionPane.showMessageDialog(NewFileDialog.this,"Niste uneli naziv fajla.");
                }else {
                    nextButton.setEnabled(false);
                    setupFillPanel();
                }
            }
        });
        JButton cancel = new JButton("Poništavanje");
        cancel.addActionListener(e -> {
            close();
        });
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(nextButton);
        buttonPanel.add(ok);
        buttonPanel.add(cancel);


        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void close() {
        resultPrimary = null;
        result = false;
        setVisible(false);
    }

    public boolean runDialog() {


        setLocationRelativeTo(null);
        setVisible(true);

        return result;
    }


    private void addRecord() {
        NewRecordDialog newRecordDialog = new NewRecordDialog(this);
        Record r = newRecordDialog.runDialog(resultPrimary.getFieldNum());
        if (r != null) {
            resultPrimary.addRecord(r);
        }
    }

    private void randomFill() {
        resultPrimary.clear();
        Random r = new Random();
        int blockFactor = resultPrimary.getBlockFactor();
        int numoOfRecords = blockFactor * resultPrimary.getBlockNum() - r.nextInt(blockFactor);
        Set<Integer> keys = new TreeSet<>();
        do {
            keys.add(r.nextInt(99));
        }
        while (keys.size() != numoOfRecords);

        for (int key : keys) {
            ArrayList<String> fields = new ArrayList<>();
            for (int i = 0; i < resultPrimary.getFieldNum(); ++i) {
                fields.add("Polje");
            }
            resultPrimary.addRecord(new Record(key, fields));
        }
    }

    public PrimaryZone getResultPrimary() {
        return resultPrimary;
    }

    public OverFlowType getOverFlowType() {
        return overFlowType;
    }

    public PropagationType getPropagationType() {
        return propagationType;
    }

    public int getOrder() {
        return order;
    }

    public String getFileName() {
        return nameInput.getText();
    }
}
