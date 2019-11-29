package indexseq.dialogs;

import indexseq.blockandcomponents.Record;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class NewRecordDialog extends JDialog {
    private static final String title = "Dijalog za dodavanje sloga";
    private JPanel centralPanel;
    private ArrayList<JTextField> textFields = new ArrayList<>();
    private ArrayList<String> fields = new ArrayList<>();
    private Record resultRecord;
    static Color dialogBcg = new Color(92, 160, 255);
    private JSpinner keyEdit;
    private JButton okbutton;
    private boolean okPressed = false;
    //    public static void main(String[] args){
//        JFrame mainframe = new JFrame();
//        JButton block = new JButton("Dugme");
//        block.addActionListener(e -> {
//
//            NewRecordDialog dialog = new NewRecordDialog(mainframe);
//            Record r = dialog.runDialog(5);
//            System.out.println(r);
//        });
//        mainframe.addRecord(block);
//        mainframe.pack();
//        mainframe.setVisible(true);
//    }

    public NewRecordDialog(JFrame owner, int key) {
        super(owner, title, true);
        setup();
        keyEdit.setValue(key);
        keyEdit.setEnabled(false);
    }

    public NewRecordDialog(JDialog owner) {
        super(owner, title, true);
        setup();
    }

    private void setup() {
        setLayout(new BorderLayout());
//        mainPane.setBackground(new Color(0,0,0));
//        mainPane.setOpaque(true);
        centralPanel = new JPanel();
        centralPanel.setBackground(dialogBcg);
        centralPanel.setOpaque(true);
        centralPanel.setLayout(new GridBagLayout());
        add(centralPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        okbutton = new JButton("Ok");
        okbutton.addActionListener(e -> {
            ok();
        });
        JButton cancel = new JButton("Poništavanje");
        cancel.addActionListener(e -> {
            resultRecord = null;
            setVisible(false);
        });
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okbutton);
        buttonPanel.add(cancel);
        add(buttonPanel, BorderLayout.SOUTH);

        keyEdit = new JSpinner();


        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyEventDispatcher() {
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        // This example converts all typed keys to upper
                        // case
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            if (!okPressed) {

                                ok();
                                okPressed = false;
                            }
                            return true;
                        }

                        // If the key should not be dispatched to the
                        // focused component, set discardEvent to true
                        return false;
                    }
                });
    }

    private void ok() {
        okPressed = true;
        for (int i = 0; i < textFields.size(); ++i) {
            JTextField textField = textFields.get(i);
            String text = textField.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(NewRecordDialog.this, "Polje " + (i + 1) + " nije validno!");
                return;
            }
            fields.add(text);
        }
        resultRecord = new Record((Integer) keyEdit.getValue(), fields);
        setVisible(false);
    }

    public Record runDialog(int rowNumber) {
//        JLabel keyLabel = new JLabel(Integer.toString(key));
//        keyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < rowNumber + 1; i++) {
            GridBagConstraints gc = new GridBagConstraints();
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.gridx = i;
            gc.gridy = 1;
            gc.weightx = 0.5;
            gc.insets = new Insets(5, 5, 5, 5);
            if (i == 0) {
                centralPanel.add(keyEdit, gc);
                gc.gridy = 0;
                JLabel label = new JLabel("Ključ", SwingConstants.CENTER);
//                label.setBackground(new Color(68, 75, 255));
//                label.setOpaque(true);
                centralPanel.add(label, gc);

            } else {
                JTextField input = new JTextField();
                centralPanel.add(input, gc);
                textFields.add(input);
                gc.gridy = 0;
                JLabel label = new JLabel("Polje " + i, SwingConstants.CENTER);
//                label.setBackground(new Color(91, 198, 255));
//                label.setOpaque(true);
                centralPanel.add(label, gc);
            }

        }
        setMinimumSize(new Dimension(200 + 30 * rowNumber, 200));
        setLocationRelativeTo(null);
//        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
//        setSize(new Dimension(screen.width/4,screen.height/3));
        setVisible(true);
        return resultRecord;
    }
}
