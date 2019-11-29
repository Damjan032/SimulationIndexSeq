package indexseq;

import indexseq.Exceptions.FileFullException;
import indexseq.Zones.OperativeMemoryZone;
import indexseq.Zones.OverflowZone;
import indexseq.Zones.PrimaryZone;
import indexseq.Zones.TreePane;
import indexseq.animator.Animation;
import indexseq.animator.Animator;
import indexseq.blockandcomponents.Block;
import indexseq.blockandcomponents.Record;
import indexseq.blockandcomponents.Record.Status;
import indexseq.dialogs.NewRecordDialog;
import indexseq.dialogs.UpdateDialog;
import indexseq.enums.OverFlowType;
import indexseq.enums.PointerType;
import indexseq.enums.PropagationType;
import indexseq.enums.TipObrade;
import indexseq.supportclasses.AddResult;
import indexseq.blockandcomponents.BlockPointer;
import indexseq.supportclasses.Focuser;
import indexseq.supportclasses.SearchResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class IndirectIndexSeq extends IndexSequential {

    private TreePane treePane;
    private ZonePane zonePane;

    private transient JSplitPane mainSplitter;
    private JScrollPane zoneScrollPane;
    private JSplitPane leftSplitter;


    public IndirectIndexSeq(String name, int order, PrimaryZone primaryZone, PropagationType p) {
//        this.primaryZone = primaryZone;
//        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//        primaryZone.setMaximumSize(new Dimension(d.width*2/3,d.height));
        propagationType = p;
        this.order = order;
        this.name = name;
        zonePane = new ZonePane(primaryZone);
        treePane = new TreePane(order, p, OverFlowType.INDIRECT, zonePane.getPrimaryZone(), zonePane.getOverflowZone(), zonePane.getPrimaryZone().getMaping());
    }

    @Override
    public JComponent getViewComponent() {
        mainSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        leftSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftSplitter.setContinuousLayout(true);
        attachOperativeMemoryZone();
        attachTreePane();

        zoneScrollPane = new JScrollPane(
                zonePane,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        mainSplitter.setLeftComponent(leftSplitter);
        mainSplitter.setRightComponent(zoneScrollPane);
        mainSplitter.setContinuousLayout(true);


//        JPanel kojkurac = new JPanel();
//        kojkurac.setLayout(new BoxLayout(kojkurac,BoxLayout.X_AXIS));
//        kojkurac.addRecord(treePane);
//        kojkurac.addRecord(new ZonePane(primaryZone,overflowZone));
//        frame.getContentPane().addRecord(BorderLayout.CENTER, kojkurac);

        return mainSplitter;

    }


    @Override
    protected void formOverflow() {
        zonePane.formOverFlow();
    }

    @Override
    protected void formTree() {
        treePane.form();
    }

    @Override
    public void add(int key) {
        SearchResult sr = search(key);
        if (sr.found) {
            Animator.init().addCallback(() -> {
                JOptionPane.showMessageDialog(
                        this,
                        "Record with key " + key + " already exists!");
                return null;
            });
        } else {
            Animator.init().addCallback(() -> {
                NewRecordDialog newRecordDialog = new NewRecordDialog(MainFrame.init(), key);
                Record record = newRecordDialog.runDialog(zonePane.getPrimaryZone().getFieldNum());
//                String value = JOptionPane.showInputDialog("Insert value of record with key: " + key + " :");
//                Record record = new Record(key, new ArrayList<>(Collections.singletonList(value)));
//                Kod za dodavanje
                if (record != null) {
                    zonePane.addRecord(sr, record);
                }
//                primaryZone.insertRecord(record, sr);
                return null;
            });
        }
    }

    @Override
    public void update(int key) {

        if (key != -1) {
            SearchResult sr = search(key);
            Animator.init().addCallback(() -> {
                if (sr.found) {

                    UpdateDialog uw = new UpdateDialog(sr, TipObrade.DIREKTNA);
                    Block b = uw.runDialog();

                    if (b != null) {
                        zonePane.updateBlock(b, sr);
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Record with key " + key + " doesn't exist!");

                }
                return null;
            });
        } else {
            for (int i = 0; i < zonePane.getPrimaryZone().getBlockNum(); i++) {
                Block pom = zonePane.getPrimaryZone().getBlock(i);

                SearchResult sr = new SearchResult(true, pom.getAddress(), 0, pom);
                Block block = new Block(sr.foundBlock);

                if (block.getRecord(sr.recordAddress).getStatus() == Status.EMPTY) {
                    continue;
                }
                UpdateDialog uw = new UpdateDialog(sr, TipObrade.REDOSLDEDNA);
                Block b = uw.runDialog();
                if (b != null) {
                    zonePane.updateBlock(b, sr);
                    b.getUnloadAnim().run();
                } else {
                    return;
                }
//
//                for (int j = 0; j < pom.getRecords().size(); j++) {
//                    Record r = pom.getRecord(j);
//                    Animator.init().addAnimation(r.getSelectAnim());
//                    SearchResult sr = new SearchResult(true, pom.getAddress(), j, pom);
//                    System.out.println(sr);
//                    UpdateDialog uw = new UpdateDialog(sr);
//                    Block b = uw.runDialog();
//                    if (b != null) {
//                        zonePane.updateBlock(b, sr);
//                    }
//                }

                while (pom.getSpecialPointerAddress() != null) {
                    Block pom1 = zonePane.getOverflowZone().getBlock(pom.getSpecialPointerAddress());
                    sr = new SearchResult(false, pom1.getAddress(), pom.getSpecialPointerAddress(), pom1);
                    System.out.println(sr);
                    uw = new UpdateDialog(sr, TipObrade.REDOSLDEDNA);
                    b = uw.runDialog();
                    if (b != null) {
                        zonePane.updateBlock(b, sr);
                        b.getUnloadAnim().run();
                    } else {
                        return;
                    }
                    pom = pom1;
                }
            }

            //return null;
        }
        ;
    }


    @Override
    public SearchResult search(int key) {

        Animator.init().clear();
        BlockPointer blockAddress = treePane.search(key).pointer;
/*
        treePane.animateSelection();
        System.out.println("Adresse: " + blockAddress);
        recordAddress = primaryZone.searchBlock(blockAddress, key);
*/
        if (blockAddress != null) {

            SearchResult sr = zonePane.searchBlock(blockAddress.getAddress(), key);
//            Animator.init().addLastAnim(sr.foundBlock.getUnloadAnim());

            //namesti parametre iz klase na one iz pretrage
            return sr;
        }
        return new SearchResult(false);
    }


    @Override
    public void reorganisation() {
        OverflowZone overflowZone = zonePane.getOverflowZone();
        PrimaryZone primaryZone = zonePane.getPrimaryZone();
        UndoSystem.init().backupTree(treePane);
        UndoSystem.init().backupOverflow(overflowZone);
        UndoSystem.init().backupPrimary(primaryZone);
        OperativeMemoryZone.init().clear();

        ArrayList<Block> blocks = primaryZone.getBlocks();

        List<Record> allRecords = new ArrayList<>();
//        Map<Integer,List<Record>> primaryRecords = new HashMap<>();
        List<Block> loadedBlocks = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            Block b = blocks.get(i);
            Block loaded = OperativeMemoryZone.init().loadBlock(b);
            loadedBlocks.add(loaded);
            List<Record> records = new ArrayList<>(loaded.selectActive());
            BlockPointer address = b.getSpecialPointer();
            if (address != null) {
                loadedBlocks.addAll(overflowZone.selectActiveInChain(address.getAddress()));
                records.addAll(overflowZone.getBufferRecords());
            }
//            primaryRecords.put(i,records);
            allRecords.addAll(records);
        }
        Animator.init().addCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                deform();
                deform();

                return null;
            }
        });
        int blockFactor = primaryZone.getBlockFactor();
        int fieldNum = primaryZone.getFieldNum();
        PrimaryZone newPrimary = new PrimaryZone((int) Math.ceil(((float) allRecords.size()) / blockFactor), blockFactor, fieldNum);
        newPrimary.setOverFlowType(OverFlowType.INDIRECT);
        Animator.init().addAnimation(primaryZone.getAddedAnim());
        Animator.init().addCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                setPrimaryZone(newPrimary);
                return null;
            }
        });
        Animator.init().addAnimation(newPrimary.getAddedAnim());

//        Animator.init().addCallback(new Callable() {
//            @Override
//            public Object call() throws Exception {
//                treePane.clear();
//                overflowZone.clear();
//                return null;
//            }
//        });
        int counter = 0;
        for (Block block : newPrimary.getBlocks()) {
            Block b = OperativeMemoryZone.init().loadBlock(block);

            for (int j = 0; j < blockFactor && counter < allRecords.size(); j++) {
                Record r = allRecords.get(counter);
                Animator.init().addAnimation(r.getSelectAnim());
                Record newRecord = new Record(r);
                Animator.init().addCallback(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        newRecord.setStatus(Status.ACTIVE);
                        b.addRecord(newRecord);
                        return null;
                    }
                });
                Animator.init().addAnimation(newRecord.getAddedAnim());
                counter++;
            }
            OperativeMemoryZone.init().unloadAndSaveBlock(b);
//            Animator.init().addCallback(new Callable() {
//                @Override
//                public Object call() throws Exception {
//                    newPrimary.addBlock(b);
//                    return null;
//                }
//            });
//            Animator.init().addAnimation(b.getLoadAnim());
        }
        for (Block b : loadedBlocks) {
            OperativeMemoryZone.init().unloadBlock(b);
        }
//        for (Map.Entry<Integer, List<Record>> e:primaryRecords.entrySet()){
//            Block b = OperativeMemoryZone.init().loadBlock(new Block(new BlockPointer(PointerType.PRIMARY,e.getKey()),blockFactor,fieldNum));
//
//            for (Record r:e.getValue()){
//                Animator.init().addCallback(new Callable() {
//                    @Override
//                    public Object call() throws Exception {
//                        Record newRecord = new Record(r);
//                        newRecord.setStatus(Status.ACTIVE);
//                        b.addRecord(newRecord);
//                        return null;
//                    }
//                });
//                Animator.init().addAnimation(r.getAddedAnim());
//            }
//            OperativeMemoryZone.init().unloadBlock(loadedBlocks.get(e.getKey()));
//            OperativeMemoryZone.init().unloadBlock(b);
//            Animator.init().addCallback(new Callable() {
//                @Override
//                public Object call() throws Exception {
//                    newPrimary.addBlock(b);
//                    return null;
//                }
//            });
//            Animator.init().addAnimation(b.getLoadAnim());
//        }

        Animator.init().addCallback(new Callable() {
            @Override
            public Object call() throws Exception {
//                primaryZone.unloadAll();
                overflowZone.rebuild(newPrimary);
                zonePane.repaint();
//                formOverflow();
                return null;
            }
        });
        Animator.init().addCallback(new Callable() {
            @Override
            public Object call() throws Exception {
//                primaryZone.unloadAll();
//                zoneTree.setTreePane(new TreePane(order,propagationType,OverFlowType.DIRECT,newPrimary));
                treePane.rebuild(newPrimary);
//                zoneTree.formTree();
                return null;
            }
        });
    }

    @Override
    public PropagationType getTreeType() {
        return this.propagationType;
    }

    @Override
    public int getBlockFactor() {
        return zonePane.getPrimaryZone().getBlockFactor();
    }

    @Override
    public void setTree(TreePane t) {
        treePane = t;
        attachTreePane();
    }

    @Override
    public void setPrimaryZone(PrimaryZone newPrimary) {
        zonePane.setPrim(newPrimary);
        zonePane.repaint();
        UndoSystem.init().setPrimaryZone(newPrimary);
        OperativeMemoryZone.init().setPrimaryZone(newPrimary);
    }

    @Override
    public void setOver(OverflowZone overflowBackup) {
        zonePane.setOver(overflowBackup);
    }

    @Override
    public OverFlowType getOverflowType() {
        return OverFlowType.INDIRECT;
    }

    @Override
    public PrimaryZone getPrimary() {
        return zonePane.getPrimaryZone();
    }

    @Override
    public OverflowZone getOverflow() {
        return zonePane.getOverflowZone();
    }

    @Override
    public TreePane getTree() {
        return treePane;
    }

    @Override
    public void setTreeType(PropagationType type) {
        super.setTreeType(type);
        switch (fileStage) {
            case TREE: {
                propagationType = type;
                mainSplitter.remove(treePane);
                treePane = new TreePane(order, type, OverFlowType.INDIRECT, zonePane.getPrimaryZone(), zonePane.getOverflowZone(), zonePane.getPrimaryZone().getMaping());
                mainSplitter.setLeftComponent(treePane);
                deform();
                break;
            }
            case PRIMARY:
            case OVERFLOW: {
                propagationType = type;
                treePane = new TreePane(order, type, OverFlowType.INDIRECT, zonePane.getPrimaryZone(), zonePane.getOverflowZone(), zonePane.getPrimaryZone().getMaping());
                break;
            }
        }
    }

    private void attachTreePane() {
        OperativeMemoryZone memoryZone = OperativeMemoryZone.init();
        treePane.addContainerListener(new ContainerListener() {
            @Override
            public void componentAdded(ContainerEvent e) {
                Dimension size = treePane.getPreferredSize();
                mainSplitter.setDividerLocation(Math.max(memoryZone.getPreferredSize().width, treePane.getPreferredSize().width));
                leftSplitter.setDividerLocation(size.height);
            }

            @Override
            public void componentRemoved(ContainerEvent e) {
                mainSplitter.setDividerLocation(0);

            }
        });
        leftSplitter.setTopComponent(treePane);
        UndoSystem.init().setTreePane(treePane);
    }

    private void attachOperativeMemoryZone() {
        OperativeMemoryZone memoryZone = OperativeMemoryZone.init();
        memoryZone.addContainerListener(new ContainerAdapter() {
            @Override
            public void componentAdded(ContainerEvent e) {
                mainSplitter.setDividerLocation(Math.max(memoryZone.getPreferredSize().width, treePane.getPreferredSize().width));
            }
            @Override
            public void componentRemoved(ContainerEvent e) {
                mainSplitter.setDividerLocation(Math.max(memoryZone.getPreferredSize().width, treePane.getPreferredSize().width));
            }
        });
        leftSplitter.setBottomComponent(memoryZone);
    }
}

class ZonePane extends JComponent {
    private PrimaryZone primaryZone;
    private OverflowZone overflowZone;
    private HashMap<Integer, Line2D.Float> relations = new HashMap<>();
    private Focuser focuser;


    ZonePane(PrimaryZone p) {
        primaryZone = p;
        overflowZone = new OverflowZone(p);
        setup();
    }

    private void setup() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        UndoSystem.init().setPrimaryZone(primaryZone);
        UndoSystem.init().setOverflowZone(overflowZone);
        OperativeMemoryZone.init().setPrimaryZone(primaryZone);
        OperativeMemoryZone.init().setOverflowZone(overflowZone);
        setupPrimary();
        setupOver();
    }

    void setupPrimary() {
        primaryZone.addFocuser(new Focuser(primaryZone));
        primaryZone.addContainerListener(new ContainerListener() {
            @Override
            public void componentAdded(ContainerEvent e) {
                ZonePane.this.repaint();
                ZonePane.this.updateUI();
            }

            @Override
            public void componentRemoved(ContainerEvent e) {
                ZonePane.this.repaint();
                ZonePane.this.updateUI();
            }
        });
        add(primaryZone, 0);
        add(Box.createVerticalGlue(), 1);
    }

    void setupOver() {
        add(overflowZone, 2);

        overflowZone.addContainerListener(new ContainerListener() {
            @Override
            public void componentAdded(ContainerEvent e) {
                ZonePane.this.repaint();
                ZonePane.this.updateUI();
            }

            @Override
            public void componentRemoved(ContainerEvent e) {

            }
        });

    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
//        Block next;
        for (Block block : primaryZone.getBlocks()) {
            Integer pointer = block.getSpecialPointerAddress();
            if (pointer != null) {
//                next = overflowZone.getBlock(pointer);
                drawRelation((Graphics2D) g, block, overflowZone.getBlock(pointer));
//                drawChain((Graphics2D)g, next);
            }
        }
    }

    private void drawRelation(Graphics2D g, Block parent, Block child) {
        Point parentPoint = SwingUtilities.convertPoint(primaryZone.getContentPanel(), parent.getLocation(), this);
        Point childPoint = SwingUtilities.convertPoint(overflowZone.getContentPanel(), child.getLocation(), this);
        int x1 = parentPoint.x + parent.getWidth() / 2;
        int y1 = parentPoint.y + parent.getHeight();
        int x2 = childPoint.x + child.getWidth() / 2;
        int y2 = childPoint.y;
        Line2D.Float line = new Line2D.Float(x1, y1, x2, y2);
        relations.put(parent.getAddressNum(), line);
        g.setColor(Color.GRAY);
        g.draw(line);
    }

    void addRecord(SearchResult sr, Record r) throws Exception {

        if (overflowZone.isFull() && primaryZone.isFull()) {

            throw new FileFullException();
        }
        AddResult a;
        Block b;
        if (sr.parentAddress == null) {
            b = sr.foundBlock;
        } else {
            b = primaryZone.getBlock(sr.parentAddress);
        }
        if (sr.pointer.getType() != PointerType.OVERFLOW) {
            a = b.insertRecord(r, sr.recordAddress);
        } else {
            a = new AddResult(r, sr.pointer);
        }
        Integer address;
        if (a.record != null) {
            if (a.pointer != null) {
                address = overflowZone.addRecord(a.pointer.getAddress(), a.record);
            } else {
                address = overflowZone.addRecord(a.record);

            }
            if (address != null) {
                b.setSpecialPointer(new BlockPointer(PointerType.OVERFLOW, address));
            }
        }
        repaint();
    }

    void updateBlock(Block b, SearchResult sr) {
        BlockPointer pointer = sr.pointer;
        if (pointer.getType() == PointerType.OVERFLOW) {
            overflowZone.updateBlock(pointer.getAddress(), b);
        } else {
            primaryZone.updateBlock(pointer.getAddress(), b);
        }
        repaint();
    }

    SearchResult searchBlock(Integer blockAddress, int key) {


        //pretrazi blok u primarnoj zoni
        SearchResult sr = primaryZone.searchBlock(blockAddress, key);
        //pa zonu prekoracenja ako je potrebno tj. ako blok ima pokazivac na blok iz zone prekoracenja
        Animator.init().addCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                MainFrame.init().closeSearchDialog();
                return null;
            }
        });
        BlockPointer pointer = sr.pointer;
        if (pointer.getType() == PointerType.OVERFLOW) {
            OperativeMemoryZone.init().unloadAndSaveBlock(sr.foundBlock);
//            Animator.init().addAnimation(sr.foundBlock.getUnloadAnim());
            Animator.init().addAnimation(getLineAnim(blockAddress));
            sr = new SearchResult(overflowZone.search(sr.pointer.getAddress(), null, key), blockAddress);
        }
        return sr;
    }

    PrimaryZone getPrimaryZone() {
        return primaryZone;
    }

    OverflowZone getOverflowZone() {
        return overflowZone;
    }

    private Animation getLineAnim(int index) {
        Animation a = new Animation(this);
        a.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = a.getStep();
                int n = a.getStepNum();
                if (i == n) {
                    a.end();
                    return;
                }
                Graphics2D g = (Graphics2D) getGraphics();
                g.setColor(Color.YELLOW);
                g.draw(relations.get(index));
                a.incStep();
            }
        });
        return a;
    }


    void formOverFlow() {
        overflowZone.form();
        overflowZone.addFocuser(new Focuser(overflowZone));
    }

    public void reorganise(TreePane treePane) {

//        Block prev = null;
//        int freeCount=0;
//        int count;
//        ArrayList<Block> blocks = primaryZone.getBlocks();
//        Block b=null;
//        for (int i = 0; i < blocks.size(); i++) {
//            b = blocks.get(i);
//            count = freeCount;
//
//            if (prev != null) {
//                Block finalPrev = prev;
////                int freeCount = finalPrev.getFreeCount();
//                for (int j = 0; j < freeCount; ++j) {
//                    Block finalB1 = b;
//                    Animator.init().addCallback(new Callable() {
//                        @Override
//                        public Object call() throws Exception {
//
//                            finalPrev.addRecordOnEnd(finalB1.popRecord());
//
//                            return null;
//                        }
//                    });
//                    Animator.init().addAnimation(b.getRecord(j).getAddedAnim());
//
//                }
//            }
//            Animator.init().addAnimation(b.getLoadAnim());
//            count += b.free(count);
//
//
//            if (b.getSpecialPointer() != null) {
//                Animator.init().addAnimation(getLineAnim(b.getAddressNum()));
//                BlockPointer address = overflowZone.freeChain(b.getSpecialPointerAddress());
//                List<Record> overflowBuffer = overflowZone.getFreedRecords();
//                for (Record r : overflowBuffer) {
//                    Block finalB = b;
//                    Animator.init().addCallback(() -> {
//                        finalB.addRecord(r);
//                        return null;
//                    });
//                    Animator.init().addAnimation(r.getAddedAnim());
//                }
//
//
////                if (address == null) {
////                    treePane.updateLeaf(b);
////                }else{
////                    treePane.updateLeaf(o.getChainEnd(address.getAddress()));
////                }
//            }
//            if (prev != null) {
//                Animator.init().addAnimation(prev.getUnloadAnim());
//            }
//            freeCount = count;
//            prev = b;
//        }
//        if (b!=null) {
//            Animator.init().addAnimation(b.getUnloadAnim());
//        }
//        primaryZone.clearEmptyBlocks();
//        Animator.init().addCallback(new Callable() {
//            @Override
//            public Object call() throws Exception {
//                primaryZone.unloadAll();
//                treePane.build(primaryZone, overflowZone, primaryZone.getMaping());
//                treePane.form();
//                return null;
//            }
//        });
    }

    public void setPrim(PrimaryZone newPrimary) {
        remove(primaryZone);
        primaryZone = newPrimary;
        setupPrimary();
    }

    public void setOver(OverflowZone o) {
        remove(overflowZone);
        overflowZone = o;
        setupOver();
    }
}

