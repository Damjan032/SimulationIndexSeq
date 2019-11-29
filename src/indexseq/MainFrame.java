package indexseq;

import indexseq.Zones.PrimaryZone;
import indexseq.animator.Animator;
import indexseq.dialogs.NewFileDialog;
import indexseq.enums.OverFlowType;
import indexseq.enums.PropagationType;
import indexseq.enums.TipObrade;
import indexseq.supportclasses.SearchResult;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import java.awt.*;
import java.awt.event.*;

import java.io.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.Callable;


public class MainFrame extends JFrame {
    public static final int MAX_VAL = 100;
    public static TipObrade obrada = TipObrade.DIREKTNA;
    private boolean saved = false;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JToolBar toolbarFile;
    private JToolBar toolBarSlider;
    private JToolBar toolBarOptions;
    private JSlider slider;
    private JPanel toolbarPanel;

    private JButton btNext;
    private JButton btReplay;
    private JButton btStop;
    private JButton btPause;
    private JButton btPlay;

    private boolean disabled = false;
    private boolean[] actionStates = new boolean[7];
    private FormButton formButton;
    private AddButton addButton;
    private UpdateButton updateButton;
    private SearchButton searchButton;
    private ReorganiseButton reorganiseButton;

    int treeChoice = 0;

    private JCheckBox cbAuto;
    //    private JPanel centralPanel;
//    private JPanel dockPanel;
//    private JSplitPane mainSplitter;
//    private JSplitPane centralSplitter;
    private IndexSequential indexSequential;

    private static MainFrame instance;
    private JComponent centralComponent;
    private JLabel orderLabel;
    private JLabel blockFactorLabel;
    private JTextField fileNameEdit;
    private JLabel overLabel;
    private JButton treeChooser;
    private JButton obradaChooser;
    private JToolBar toolbarType;
    private JToolBar infoToolBar;
    private JDialog odialog;


    private MainFrame(String title, IndexSequential indexSequential) {
        super(title);
        Animator.init().addRuningListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.disableActions();
            }
        });
        Animator.init().addAfterRuningListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.resetActionStates();
            }
        });
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = new Dimension(2 * screen.width / 3, 2 * screen.height / 3);
        setSize(size);
        setLocation((screen.width - size.width) / 2, (screen.height - size.height) / 2);

        setupMenu();
        setupToolBar();
        setupInfoPanel();
        setActionStates(false, false, false, false, false, false, false);

        if (indexSequential != null) {
            setIndexSequential(indexSequential);
        }
        setVisible(true);
    }

    public static MainFrame init() {
        if (instance == null) {
            instance = new MainFrame("MainFrame", null);
        }
        return instance;
    }

    public static MainFrame init(String title, IndexSequential indexSequential) {
        if (instance == null) {

            instance = new MainFrame(title, indexSequential);
        }
        return instance;
    }

    public void setIndexSequential(IndexSequential indexSequential) {
        this.indexSequential = indexSequential;
        UndoSystem.init().setIndexSequential(indexSequential);
        setCentralComponent(indexSequential.getViewComponent());
        getContentPane().repaint();
        fileNameEdit.setText(indexSequential.getName());
        blockFactorLabel.setText(Integer.toString(indexSequential.getBlockFactor()));
        orderLabel.setText(Integer.toString(indexSequential.getOrder()));
        fileNameEdit.setText(indexSequential.getName());
        overLabel.setText(indexSequential.getOverflowType().toString());

        if (indexSequential.propagationType == PropagationType.HIGH) {
            treeChooser.setText("Vrsta stabla: Max ključ");
        } else {
            treeChooser.setText("Vrsta stabla: Min ključ");
        }

        if (MainFrame.obrada == TipObrade.DIREKTNA) {
            obradaChooser.setText("Način obrade: Direktna");
        } else {
            obradaChooser.setText("Način obrade: Redosledna");
        }
        UndoSystem.init().setIndexSequential(indexSequential);
        setActionStatesOnForm(indexSequential.isFormed());
        resetActionStates();
    }


    //    public MainFrame(String title, Dimension d, JPanel jp, JPanel dp) throws HeadlessException {
//        super(title);
//        setSize(d);
//
//        mainSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//        centralSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setupMenu();
//        this.centralPanel = jp;
////        getContentPane().addRecord(BorderLayout.CENTER, centralPanel);
//        this.dockPanel = dp;
//
////        PrimaryZone op = new PrimaryZone((PrimaryZone) jp);
////        getContentPane().addRecord(BorderLayout.WEST,dockPanel);
//        centralSplitter.setTopComponent(centralPanel);
////        centralSplitter.setBottomComponent(op);
//        mainSplitter.setLeftComponent(dockPanel);
//        setupSplitters();
//    }


//    private void setupSplitters() {
//        mainSplitter.setRightComponent(centralSplitter);
//        getContentPane().addRecord(BorderLayout.CENTER, mainSplitter);
//
//    }

    private void setupMenu() {
        menuBar = new JMenuBar();

        MenuListener ml = new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                Animator.init().pause();
            }

            @Override
            public void menuDeselected(MenuEvent e) {
                Animator.init().start();
            }

            @Override
            public void menuCanceled(MenuEvent e) {
                Animator.init().start();

            }
        };
        fileMenu = new JMenu("Fajl");
        fileMenu.addMenuListener(ml);
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new NewAction());
        actions.add(new OpenAction());
        actions.add(new SaveAction());
        actions.add(new CloseAction());
        for (Action a : actions) {
            fileMenu.add(a);
            fileMenu.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Animator.init().end();
                }
            });
        }

        fileMenu.add(new JPopupMenu.Separator());
        ExitAction e = new ExitAction();
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        fileMenu.add(e);
        menuBar.add(fileMenu);

        JMenu actionMenu = new JMenu("Akcije");
        actionMenu.addMenuListener(ml);
        actionMenu.add(new FormAction());
        actionMenu.add(new AddAction());
        actionMenu.add(new UpdateAction());
        actionMenu.add(new SearchAction());
        actionMenu.add(new ReorganiseAction());
        menuBar.add(actionMenu);

        JMenu settingMenu = new JMenu("Podešavanja");
        settingMenu.addMenuListener(ml);
        settingMenu.add(new ChooseTreeAction());
        settingMenu.add(new ChooseEditAction());
        menuBar.add(settingMenu);

        JMenu aboutMenu = new JMenu("Info");
        aboutMenu.addMenuListener(ml);
        //TODO Dodati ovde
        menuBar.add(aboutMenu);

        setJMenuBar(menuBar);
    }


    public void setupFileToolBar() {
        toolbarFile = new JToolBar();
        formButton = new FormButton();
        toolbarFile.add(formButton);
        addButton = new AddButton();
        toolbarFile.add(addButton);
        updateButton = new UpdateButton();
        toolbarFile.add(updateButton);
        searchButton = new SearchButton();
        toolbarFile.add(searchButton);
        reorganiseButton = new ReorganiseButton();
        toolbarFile.add(reorganiseButton);
    }

    public void setupSliderToolBar() {
        toolBarSlider = new JToolBar("Brzina animacije");
//        label = new JLabel("Brzina:");
//        label.setMinimumSize(new Dimension(0, 0));

        slider = new JSlider(JSlider.HORIZONTAL, 0, MAX_VAL, MAX_VAL / 2);
        slider.setMinimumSize(new Dimension(150, 20));
        slider.setMajorTickSpacing(100);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        Hashtable labelTable = new Hashtable();
        labelTable.put(0, new JLabel("Sporo"));
        labelTable.put(MAX_VAL / 2, new JLabel("Srednje"));
        labelTable.put(MAX_VAL, new JLabel("Brzo"));
//        slider.setValue(MAX_VAL / 2);
        slider.setToolTipText("Brzina animacije");
        slider.setLabelTable(labelTable);
        slider.setPaintLabels(true);
        //dodavanje listenera na slider
        Animator.init().setProcessDelay(MAX_VAL * 5);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Animator.init().setProcessDelay((MAX_VAL - slider.getValue()) * 10);
            }
        });

        //'Stilizovanje' drugo toolBara
//        toolBarSlider.addSeparator();
//        toolBarSlider.add(label);
//        toolBarSlider.addSeparator();
        toolBarSlider.add(slider);
//        toolBarSlider.addSeparator();
    }

    private ImageIcon getScaledIcon(String path, int div) {
        ImageIcon icon = new ImageIcon(path);
        Image imgPlay = icon.getImage();
        int w = this.getWidth() / div;
        int h = this.getHeight() / div;
        if (w > h) {
            w = h;
        }
        imgPlay = imgPlay.getScaledInstance(w, w, Image.SCALE_SMOOTH);
        return new ImageIcon(imgPlay);
    }

    public void setupOptionsToolBar() {
        toolBarOptions = new JToolBar("Options");


        btNext = new JButton(getScaledIcon("res/next.png", 30));
        btNext.setBorderPainted(false);
        btNext.setToolTipText("Next");
        //povezivanje
        btNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Animator.init().manualNext();
            }
        });

        btPlay = new JButton(getScaledIcon("res/play3.png", 30));
        btPlay.setBorderPainted(false);
        btPlay.setToolTipText("Play");
        //povezivanje
        btPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Animator.init().start();
            }
        });

        btReplay = new JButton(getScaledIcon("res/replay.png", 30));
        btReplay.setBorderPainted(false);
        btReplay.setToolTipText("Replay");
        //povezivanje na blockAnimator
        btReplay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Animator.init().restart();
            }
        });

        btStop = new JButton(getScaledIcon("res/stop2.png", 30));
        btStop.setBorderPainted(false);
        btStop.setToolTipText("Stop");
        btStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Animator.init().end();
            }
        });

        btPause = new JButton(getScaledIcon("res/pause.png", 30));
        btPause.setBorderPainted(false);
        btPause.setToolTipText("Pause");
        btPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Animator.init().pause();
            }
        });
        cbAuto = new JCheckBox("<html>Automatsko<br>izvršavanje</html>", true);
        cbAuto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Animator.init().setAutomatic(cbAuto.isSelected());
            }
        });

        toolBarOptions.add(btPlay);
        toolBarOptions.add(btPause);
        toolBarOptions.add(btNext);
        toolBarOptions.add(btStop);
        toolBarOptions.add(btReplay);
        toolBarOptions.add(cbAuto);

    }

    private void setupInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JPanel infoPanel = new JPanel();
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
        setupInfoToolbar();
        setupTypeToolbar();
        infoPanel.add(toolbarType);
        infoPanel.add(infoToolBar);
        panel.add(infoPanel);
        panel.add(toolbarPanel);
        this.add(panel, BorderLayout.NORTH);
    }

    private void setupInfoToolbar() {
        infoToolBar = new JToolBar();
        infoToolBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
        ArrayList<JComponent> comps = new ArrayList<>();

        JLabel nf = new JLabel("Naziv fajla: ");
        comps.add(nf);
        fileNameEdit = new JTextField() {

            @Override
            public boolean isValidateRoot() {
                return false;
            }
        };
        fileNameEdit.setMinimumSize(new Dimension(50, 10));
        fileNameEdit.getDocument().addDocumentListener(new DocumentListener() {
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
                if (indexSequential != null) {
                    indexSequential.setName(fileNameEdit.getText().trim());
                }
            }
        });
        comps.add(fileNameEdit);

        comps.add(new JLabel("|"));

        JLabel fb = new JLabel("Faktor Blokiranja: ");
        comps.add(fb);
        blockFactorLabel = new JLabel();
        comps.add(blockFactorLabel);
        comps.add(new JLabel("|"));

        JLabel rs = new JLabel("Red stabla: ");
        comps.add(rs);
        orderLabel = new JLabel();
        comps.add(orderLabel);
        comps.add(new JLabel("|"));

        JLabel labOver = new JLabel("Povezivanje prekoračilaca: ");
        comps.add(labOver);
        overLabel = new JLabel();
        comps.add(overLabel);

        for (JComponent c : comps) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoToolBar.add(c);
        }
    }


    public void setupToolBar() {
        setupFileToolBar();
        setupSliderToolBar();
        setupOptionsToolBar();


        toolbarPanel = new JPanel();
        toolbarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.X_AXIS));
//        toolbarPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        //   toolbarPanel.setLayout(new GridLayout(2,2));

//        toolbarFile.setAlignmentX(Component.RIGHT_ALIGNMENT);
        toolbarPanel.add(toolbarFile);
//        toolBarSlider.setAlignmentX(Component.RIGHT_ALIGNMENT);
        toolbarPanel.add(toolBarSlider);
//        toolBarOptions.setAlignmentX(Component.RIGHT_ALIGNMENT);
        toolbarPanel.add(toolBarOptions);
    }

    private void setupTypeToolbar() {
        toolbarType = new JToolBar();

        treeChooser = new JButton("Vrsta stabla");
        treeChooser.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        treeChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    chooseTree();
            }
        });
        toolbarType.add(treeChooser);

        obradaChooser = new JButton("Način obrade");
        obradaChooser.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        obradaChooser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                chooseEditType();

            }
        });
        toolbarType.add(obradaChooser);


    }

    private void chooseEditType() {
        Object[] options = {"Direktna obrada", "Redosledna obrada", "Poništi"};
        int choice = JOptionPane.showOptionDialog(null, "Način obrade", "Odaberite opciju:", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (choice != 2) {
            if (choice == 0) {
                obradaChooser.setText("Način obrade");
            } else {
                obradaChooser.setText("Način obrade");
            }
            MainFrame.obrada = TipObrade.values()[choice];
        }
    }

    private void chooseTree() {
        Object[] options = {"Propagacija najvecih", "Propagacija najmanjih", "Poništi"};
        int choice = JOptionPane.showOptionDialog(null, "Stablo sa propagacijom najvećih/najmanjih vrednosti ključa", "Odaberite opciju", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (choice != 2) {
            if (choice == 0) {
                treeChooser.setText("Vrsta stabla");
            } else {
                treeChooser.setText("Vrste stabla");
            }
            PropagationType p = PropagationType.values()[choice];
            System.out.println(p);
            indexSequential.setTreeType(p);
            setActionStatesOnForm(indexSequential.isFormed());
//                    formButton.setEnabled(true);
            treeChoice = choice;

        }
    }

    /*
    Zatvara trenutni fajl
     */
    private void saveFile() {
        if (indexSequential == null) {
            return;
        }
        Animator.init().end();
        JFileChooser saveFileChooser = new JFileChooser();
        saveFileChooser.setDialogTitle("Odaberite fajl i putanju za cuvanje primera");
        saveFileChooser.setSelectedFile(new File(indexSequential.getName() + ".bin"));
        int choice = saveFileChooser.showSaveDialog(MainFrame.this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            try {
                File f = saveFileChooser.getSelectedFile();
                FileOutputStream fout = new FileOutputStream(f);
                ObjectOutputStream outputStream = new ObjectOutputStream(fout);
                outputStream.writeObject(indexSequential);
                saved = true;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /*pomocna*/

    private void closeFileDialog() {
        int result = JOptionPane.showConfirmDialog(this,
                "Da li zelite da zatvorite fajl?", "Pitanje", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            closeFile();
        }
    }

    /*
    Zatvara trenutni fajl
    */
    private void closeFile() {
        if (indexSequential != null) {
            if (!saved) {
                int result = JOptionPane.showConfirmDialog(this,
                        "Da li zelite da sacuvate primer pre zatvaranja?", "Pitanje", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    saveFile();
                }
            }
            getContentPane().remove(centralComponent);
            indexSequential = null;
            resetInfoPanel();
            System.gc();
            disableActions();
            refreshUI();
        }

    }

    private void resetInfoPanel() {
        orderLabel.setText("");
        blockFactorLabel.setText("");
        fileNameEdit.setText("");
        overLabel.setText("");
    }

    private void setActionStates(boolean bform, boolean badd, boolean bupd, boolean bsrc, boolean breo, boolean btip, boolean bobrada) {
        actionStates[0] = bform;
        actionStates[1] = badd;
        actionStates[2] = bupd;
        actionStates[3] = bsrc;
        actionStates[4] = breo;
        actionStates[5] = btip;
        actionStates[6] = bobrada;
        if (!disabled) {
            formButton.setEnabled(bform);
            addButton.setEnabled(badd);
            updateButton.setEnabled(bupd);
            searchButton.setEnabled(bsrc);
            reorganiseButton.setEnabled(breo);
            treeChooser.setEnabled(btip);
            obradaChooser.setEnabled(bobrada);
        }
    }

    private void setActionStatesOnForm(boolean formed) {

        if (formed) {
            setActionStates(false, true, true, true, true, true, true);
        } else {
            setActionStates(true, false, false, false, false, true, true);
        }

    }

    private void exit() {
        int result = JOptionPane.showConfirmDialog(MainFrame.this,
                "Da li zelite da izadjete?", "Pitanje", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            closeFile();
//            MainFrame.this.dispatchEvent(new WindowEvent(MainFrame.this, WindowEvent.WINDOW_CLOSING));
            System.exit(0);
        }
    }

    private void disableActions() {
        disabled = true;
        actionStates[0] = formButton.isEnabled();
        actionStates[1] = addButton.isEnabled();
        actionStates[2] = updateButton.isEnabled();
        actionStates[3] = searchButton.isEnabled();
        actionStates[4] = reorganiseButton.isEnabled();
        actionStates[5] = treeChooser.isEnabled();
        actionStates[6] = obradaChooser.isEnabled();

        formButton.setEnabled(false);
        addButton.setEnabled(false);
        updateButton.setEnabled(false);
        searchButton.setEnabled(false);
        reorganiseButton.setEnabled(false);
        treeChooser.setEnabled(false);
        obradaChooser.setEnabled(false);
    }

    private void resetActionStates() {
        disabled = !disabled;
        formButton.setEnabled(actionStates[0]);
        addButton.setEnabled(actionStates[1]);
        updateButton.setEnabled(actionStates[2]);
        searchButton.setEnabled(actionStates[3]);
        reorganiseButton.setEnabled(actionStates[4]);
        treeChooser.setEnabled(actionStates[5]);
        obradaChooser.setEnabled(actionStates[6]);
    }

    /*
    Osvezava izgled MainFrame-a da bi se prikazala promena komponenti
     */
    private void refreshUI() {
        revalidate();
        repaint();
    }


    /*
        Ovim se setuje centralna komponenta kojom se prikazuje fajl
    */
    public void setCentralComponent(JComponent centralComponent) {
        getContentPane().remove(centralComponent);
        this.centralComponent = centralComponent;
        getContentPane().add(BorderLayout.CENTER, centralComponent);
        refreshUI();
    }

    public IndexSequential getIndexSequential() {
        return indexSequential;
    }

    public void closeSearchDialog() {
        if (odialog != null) {
            odialog.setVisible(false);
        }
    }

    public void addSearchDialog(JDialog o) {
        this.odialog = o;
    }

    class NewAction extends AbstractAction {
        public NewAction() {
            super("Novi fajl", MainFrame.this.getScaledIcon("res/new.png", 40));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (indexSequential != null) {
                closeFile();
            }

            NewFileDialog newFileDialog = new NewFileDialog(MainFrame.this);
            if (newFileDialog.runDialog()) {
                OverFlowType overFlowType = newFileDialog.getOverFlowType();
                PrimaryZone p = newFileDialog.getResultPrimary();
                int order = newFileDialog.getOrder();
                PropagationType propagationType = newFileDialog.getPropagationType();
                String name = newFileDialog.getFileName();
                IndexSequential indexSequential;
                if (overFlowType == OverFlowType.DIRECT) {
                    indexSequential = new DirectIndexSeq(name, order, p, propagationType);
                } else {
                    indexSequential = new IndirectIndexSeq(name, order, p, propagationType);
                }
                setIndexSequential(indexSequential);
            }
        }
    }

    class OpenAction extends AbstractAction {
        public OpenAction() {
            super("Otvaranje fajla", MainFrame.this.getScaledIcon("res/open.png", 40));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            closeFile();
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setDialogTitle("Odaberite fajl");
            jfc.setAcceptAllFileFilterUsed(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Binarni fajlovi BIN", "bin");
            jfc.addChoosableFileFilter(filter);

            int returnValue = jfc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                String filePath = jfc.getSelectedFile().getPath();
                System.out.println(filePath);
                try {
                    FileInputStream fin = new FileInputStream(filePath);
                    ObjectInputStream in = new ObjectInputStream(fin);
                    indexSequential = (IndexSequential) in.readObject();
                    setIndexSequential(indexSequential);
                } catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Greška pri učitavanju fajla: " + ex.getMessage());
                }
            }

        }
    }

    class SaveAction extends AbstractAction {
        public SaveAction() {
            super("Čuvanje fajla", MainFrame.this.getScaledIcon("res/save.png", 40));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            saveFile();
        }
    }


    class CloseAction extends AbstractAction {
        public CloseAction() {
            super("Zatvaranje fajla", MainFrame.this.getScaledIcon("res/close.png", 40));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            closeFileDialog();
        }
    }

    class ExitAction extends AbstractAction {
        public ExitAction() {
            super("Izlaz", MainFrame.this.getScaledIcon("res/exit.png", 40));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            exit();
        }
    }


    class FormButton extends JButton {
        FormButton() {
            super("Formiranje", MainFrame.this.getScaledIcon("res/form.png", 30));
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    form();
                }
            });
        }
    }
    class FormAction extends AbstractAction{
        public FormAction() {
            super("Formiranje",MainFrame.this.getScaledIcon("res/form.png", 30) );
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            form();
        }
    }
    private void form() {
        if (indexSequential != null) {
            Animator.init().setInitCallback(() -> {
                UndoSystem.init().setFormUndo(true);
                indexSequential.form();
                setActionStatesOnForm(indexSequential.isFormed());
                return null;
            });
            Animator.init().processAnimations();
            refreshUI();
        }
    }

    class AddButton extends JButton {
        AddButton() {
            super("Dodavanje", MainFrame.this.getScaledIcon("res/add.png", 30));
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addRecord();
                }
            });
        }
    }
    class AddAction extends AbstractAction{
        public AddAction() {
            super("Dodavanje",MainFrame.this.getScaledIcon("res/add.png", 30) );
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            addRecord();
        }
    }
    private void addRecord() {
        //Dodati dijalog za dodavanje
        String input = JOptionPane.showInputDialog("Unesite kljuc novog sloga:");
        if (input == null) {
            return;
        }
        try {
            int key = Integer.parseInt(input);
            if (key >= 99) {
                JOptionPane.showMessageDialog(MainFrame.this, "Uneti kljuc je veći od dozvoljene maksimalne vrednosti ključa 99.");
                return;
            } else if (key < 0) {
                JOptionPane.showMessageDialog(MainFrame.this, "Uneti kljuc je manji od dozvoljene minimalne vrednosti ključa 0.");
                return;
            }
            Animator.init().setInitCallback(() -> {
                UndoSystem.init().setFormUndo(false);
                indexSequential.add(key);
                return null;
            });
            Animator.init().processAnimations();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(MainFrame.this, nfe.getMessage());
        }
    }

    class ReorganiseButton extends JButton {
        ReorganiseButton() {
            super("Reorganizacija", MainFrame.this.getScaledIcon("res/reorg.png", 30));
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    reorganise();
                }
            });
        }

    }
    class ReorganiseAction extends AbstractAction{
        public ReorganiseAction() {
            super("Reorganizacija",MainFrame.this.getScaledIcon("res/reorg.png", 30) );
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            reorganise();
        }
    }

    private void reorganise() {
        PropagationType p = PropagationType.values()[treeChoice];
        System.out.println("Tip1: " + p);
        System.out.println("Tip2: " + indexSequential.propagationType);
//                    UndoSystem.init().backupTree(indexSequential.getTree());

        Animator.init().setInitCallback(() -> {
            UndoSystem.init().setFormUndo(false);
            indexSequential.reorganisation();
            //			indexSequential.setTreeType(p);
            Animator.init().addLastCallback(new Callable() {
                @Override
                public Object call() throws Exception {
                    setActionStatesOnForm(indexSequential.isFormed());
                    return null;
                }
            });
            return null;
        });
        Animator.init().processAnimations();
    }

    class UpdateButton extends JButton {
        UpdateButton() {
            super("Izmena", MainFrame.this.getScaledIcon("res/edit.png", 30));
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateRecord();
                }
            });
        }
    }
    class UpdateAction extends AbstractAction{
        public UpdateAction() {
            super("Izmena",MainFrame.this.getScaledIcon("res/edit.png", 30) );
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            updateRecord();
        }
    }
    private void updateRecord() {
        int key;
        if (MainFrame.obrada == TipObrade.DIREKTNA) {
            String input = JOptionPane.showInputDialog("Unesite kljuc sloga kojeg azurirate:");
            if (input == null) {
                return;
            }
            try {
                key = Integer.parseInt(input);
                if (key >= 99) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Uneti kljuc je veći od dozvoljene maksimalne vrednosti ključa 99.");
                    return;
                } else if (key < 0) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Uneti kljuc je manji od dozvoljene minimalne vrednosti ključa 0.");
                    return;
                }
                Animator.init().setInitCallback(() -> {
                    UndoSystem.init().setFormUndo(false);
                    indexSequential.update(key);
                    return null;
                });
                Animator.init().processAnimations();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(MainFrame.this, nfe.getMessage());
            }
        } else {
            indexSequential.update(-1);

        }
    }

    class SearchButton extends JButton {
        SearchButton() {
            super("Traženje", MainFrame.this.getScaledIcon("res/search.png", 30));
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    search();
                }
            });
        }
    }
    class SearchAction extends AbstractAction{
        public SearchAction() {
            super("Traženje",MainFrame.this.getScaledIcon("res/search.png", 30) );
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            search();
        }
    }

    private void search() {
        //dodati dijalog za unos kljuca
        String input = JOptionPane.showInputDialog("Unesite kljuc sloga kojeg trazite:");
        if (input == null) {
            return;
        }
        int key;
        try {
            key = Integer.parseInt(input);
            if (key >= 99) {
                JOptionPane.showMessageDialog(MainFrame.this, "Uneti kljuc je veći od dozvoljene maksimalne vrednosti ključa 99.");
                return;
            } else if (key < 0) {
                JOptionPane.showMessageDialog(MainFrame.this, "Uneti kljuc je manji od dozvoljene minimalne vrednosti ključa 0.");
                return;
            }
            Animator.init().setInitCallback(() -> {
                SearchResult sr;
                UndoSystem.init().setFormUndo(false);
                sr = indexSequential.search(key);
                Animator.init().addCallback((Callable<Object>) () -> {
                    if (sr.found) {
                        JOptionPane.showMessageDialog(
                                MainFrame.this,
                                "Slog sa vrednoscu kljuca " + key + " je pronadjen!\n na adresi " + sr.pointer + ". bloka.");
                    } else {
                        JOptionPane.showMessageDialog(
                                MainFrame.this,
                                "Slog sa vrednoscu kljuca " + key + " nije pronadjen!");
                    }
                    return null;
                });
                return null;
            });

            Animator.init().processAnimations();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(MainFrame.this, nfe.getMessage());
        }
    }
    class ChooseTreeAction extends AbstractAction{

        public ChooseTreeAction() {
            super("Vrsta stabla");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            chooseTree();
        }
    }
    class ChooseEditAction extends AbstractAction{

        public ChooseEditAction() {
            super("Način obrade");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            chooseEditType();
        }
    }
}

