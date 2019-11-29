package indexseq;

import indexseq.Zones.OverflowZone;
import indexseq.Zones.PrimaryZone;
import indexseq.Zones.TreePane;
import indexseq.animator.Animation;
import indexseq.animator.Animator;
import indexseq.blockandcomponents.Block;
import indexseq.blockandcomponents.NodeBlock;
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
import java.util.concurrent.Callable;

public class DirectIndexSeq extends IndexSequential {

//    public PrimaryZone getPrimaryZone() {
//        return treePane;
//    }

    //    private JLabel labelGlupa;
//    private PrimaryZone treePane
    public PrimaryZone primaryZone;
    private ZoneTree zoneTree;

    private JSplitPane mainSplitter;
    private JSplitPane centralSplitter;

    private BlockPointer blockAddress;
    private Integer recordAddress;
    private int leafAddress;
    private JScrollPane primaryZoneHolder;


    public DirectIndexSeq(String name, int order, PrimaryZone primaryZone, PropagationType p) {
//        this.treePane = treePane;
//        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//        treePane.setMaximumSize(new Dimension(d.width*2/3,d.height));
        // treePane.addRecord()
        // gbc.weightx = 10;
        //gbc.anchor = 8;
        this.primaryZone = primaryZone;
        this.order = order;
        this.name = name;
        this.propagationType = p;
        UndoSystem.init().setPrimaryZone(primaryZone);
        zoneTree = new ZoneTree(order, primaryZone, p);

    }


    @Override
    public JComponent getViewComponent() {
        mainSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        JScrollPane spTree = new JScrollPane(
                zoneTree,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        zoneTree.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                spTree.setMinimumSize(zoneTree.getMinimumSize());
            }
        });

        zoneTree.attach(mainSplitter);
        JPanel zoneTreeHolder = new JPanel();
        zoneTreeHolder.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.weightx = 1;
        g.weighty = 1;
        g.fill = GridBagConstraints.BOTH;
        zoneTreeHolder.add(spTree, g);
        mainSplitter.setLeftComponent(zoneTreeHolder);

        primaryZoneHolder = new JScrollPane(primaryZone,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //Za fokusiranje blokova pri radu
        primaryZone.addFocuser(new Focuser(primaryZone));

        mainSplitter.setRightComponent(primaryZoneHolder);
        mainSplitter.setContinuousLayout(true);

        return mainSplitter;
    }


    @Override
    protected void formOverflow() {
        zoneTree.formOverFlow();
        //zoneTree.formTree(primaryZone);
        //  treePane.hideAll();


    }

    @Override
    protected void formTree() {
        zoneTree.formTree();
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
                Record record = newRecordDialog.runDialog(primaryZone.getFieldNum());
                /* if(sr.pointer.getType()== BlockPointer.Type.OVERFLOW){
                    zoneTree.insertRecord(sr, record, leafAddress);
                }
                else{
                    System.out.println("OVDE USAAOOO");
                    BlockPointer pointer = sr.pointer;
                    Block b = primaryZone.getBlock(pointer.getAddress());
                    AddResult a = b.insertRecord(record, sr.recordAddress);
                    if(a.record!= null){
                        System.out.println("OVDE OPEEEEEEEEEEEEEEEEEET USAAOOO    " + leafAddress);
                        zoneTree.insertRecord(sr, a.record, leafAddress);
                    }
                   // repaint()
                }*/
                if (record != null) {
                    try {

                        zoneTree.addRecord(sr, record, primaryZone);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(MainFrame.init(), e.getMessage());
                    }
                }
                //repaint();
                return null;
            });

        }
    }

    @Override
    public void update(int key) {
        if (key == -1) {
            int n = zoneTree.getTreePane().getLeafs().get(0).getBlockFactor();
            System.out.println("Faktor blokiranja: " + n);
            for (int i = 0; i < primaryZone.getBlockNum(); i++) {
                Block pom = primaryZone.getBlock(i);

                System.out.println("Trenutni: " + i);
                SearchResult sr = new SearchResult(true, pom.getAddress(), 0, pom);
                Block block = new Block(sr.foundBlock);

                if (block.getRecord(sr.recordAddress).getStatus() == Status.EMPTY) {
                    continue;
                }
                UpdateDialog uw = new UpdateDialog(sr, TipObrade.REDOSLDEDNA);
                Block b = uw.runDialog();
                if (b != null) {
                    primaryZone.updateBlock(i, b);
                    b.getUnloadAnim().run();
                } else {
                    System.out.println("Usao greska!!");
                    return;
                }
                int j = i / n;//pom.getBlockFactor();

                Record rec = zoneTree.getTreePane().getLeafs().get(j).getRecord(i % n);
                if (!rec.getPointer(1).toString().equals(
                        rec.getPointer(0).toString())) {
                    pom = zoneTree.getOverflowZone().getBlock
                            (rec.getPointer(1).getAddress());
                    System.out.println("Usao: " + pom.getAddress());
                    System.out.println("Usao11: " + pom.getAddress().getType());
                    System.out.println(pom);
                    sr = new SearchResult(true, pom.getAddress(), 0, pom);
                    System.out.println(sr);
                    uw = new UpdateDialog(sr, TipObrade.REDOSLDEDNA);
                    b = uw.runDialog();
                    if (b != null) {
                        zoneTree.updateBlock(b, sr);
                        b.getUnloadAnim().run();
                    } else {
                        return;
                    }
                }
                while (pom.getSpecialPointerAddress() != null) {
                    System.out.println("USAOOO : 11");
                    Block pom1 = zoneTree.getOverflowZone().getBlock(pom.getSpecialPointerAddress());
                    sr = new SearchResult(false, pom1.getAddress(), pom.getSpecialPointerAddress(), pom1);
                    System.out.println(sr);
                    uw = new UpdateDialog(sr, TipObrade.REDOSLDEDNA);
                    b = uw.runDialog();
                    if (b != null) {
                        zoneTree.updateBlock(b, sr);
                        b.getUnloadAnim().run();
                    } else {
                        return;
                    }
                    pom = pom1;
                }

            }
        } else {
            SearchResult sr = search(key);
            Animator.init().addCallback(() -> {
                if (sr.found) {

                    UpdateDialog uw = new UpdateDialog(sr, TipObrade.DIREKTNA);
                    Block b = uw.runDialog();
                    if (b != null) {
                        if (b.getAddress().getType() == PointerType.PRIMARY) {
                            primaryZone.updateBlock(sr.pointer.getAddress(), b);
                        } else {
                            zoneTree.updateBlock(b, sr);
                        }
                    }
                    // indexSequential.update(key);
                } else {
                    JOptionPane.showMessageDialog(this, "Record with key " + key + " doesn't exist!");

                }
                return null;
            });
        }
    }

    @Override
    public SearchResult search(int key) {
        System.out.println("search " + key);
        Animator.init().clear();
        SearchResult sr = zoneTree.getTree().search(key);
        blockAddress = sr.pointer;
//        leafAddress = sr.parentAddress;
        System.out.println("BLOK " + blockAddress);
        SearchResult srResults = new SearchResult(false);
        if (blockAddress != null) {
            if (sr.pointer.getType() == PointerType.OVERFLOW) {
//                System.out.println("MA OVDEEEEEEEEEEEEEEEE " +blockAddress );
                Animator.init().addAnimation(zoneTree.getLineAnim(leafAddress * order + sr.leafRecordAddress));
                srResults = new SearchResult(zoneTree.getOverflowZone().search(sr.pointer.getAddress(), null, key), sr.pointer.getAddress());
                recordAddress = sr.recordAddress;
                blockAddress = sr.pointer;
            } else {
//                System.out.println("OVDEEEEEEEEEEEEEEEE " +blockAddress );
                srResults = primaryZone.searchBlock(blockAddress.getAddress(), key);
                recordAddress = sr.recordAddress;
                blockAddress = sr.pointer;
            }
            Animator.init().addLastAnim(srResults.foundBlock.getUnloadAnim());

            //SearchResult sr2 = primaryZone.searchBlock(blockAddress.getPointer(), key);
//           Animator.init().processAnimations();

            //namesti parametre iz klase na one iz pretrage
        }
        // Da bi znao iz kog rekorda je krenuta pretraga
        srResults.leafRecordAddress = sr.leafRecordAddress;
        srResults.leafAddress = sr.leafAddress;
        return srResults;
//
    }

    @Override
    public PropagationType getTreeType() {
        return this.propagationType;
    }

    @Override
    public void reorganisation() {
        UndoSystem.init().backupTree(zoneTree.getTreePane());
        UndoSystem.init().backupOverflow(zoneTree.getOverflowZone());
        UndoSystem.init().backupPrimary(primaryZone);
        OverflowZone overflowZone = zoneTree.getOverflowZone();
        TreePane treePane = zoneTree.getTreePane();
        int n = zoneTree.getTreePane().getOrder();
        ArrayList<Block> blocks = primaryZone.getBlocks();

        List<Record> primaryRecords = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            Block b = blocks.get(i);

            Animator.init().addAnimation(b.getLoadAnim());
            primaryRecords.addAll(b.selectActive());

            int leafPointerAddress = i % n;
            int leafAdress = i / n;

            NodeBlock nb = (NodeBlock) treePane.getLeafs().get(leafAdress);
            if (nb.isChained(leafPointerAddress)) {
                BlockPointer address = nb.getChainPointer(leafPointerAddress);
                overflowZone.selectActiveInChain(address.getAddress());
                primaryRecords.addAll(overflowZone.getBufferRecords());
            }
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
        int fieldNum  = primaryZone.getFieldNum();
        PrimaryZone newPrimary =  new PrimaryZone((int) Math.ceil(((float)primaryRecords.size())/blockFactor),blockFactor,fieldNum);
        newPrimary.setOverFlowType(OverFlowType.DIRECT);
        Animator.init().addCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                setPrimaryZone(newPrimary);
                return null;
            }
        });
        int i = 0;
        Block prev = null;
        for (Record r:primaryRecords){
            if (i%blockFactor==0){
                if (prev!=null){
                    Animator.init().addAnimation(prev.getUnloadAnim());
                }
                prev = newPrimary.getBlock(i/blockFactor);
                Animator.init().addAnimation(prev.getLoadAnim());
            }
            Animator.init().addCallback(new Callable() {
                @Override
                public Object call() throws Exception {
                    r.setStatus(Status.ACTIVE);
                    newPrimary.addRecord(r);
                    return null;
                }
            });
            Animator.init().addAnimation(r.getAddedAnim());
            ++i;
        }
        if (prev!=null){
            Animator.init().addAnimation(prev.getUnloadAnim());
        }
        Animator.init().addCallback(new Callable() {
            @Override
            public Object call() throws Exception {
//                primaryZone.unloadAll();
                overflowZone.rebuild(newPrimary);
//                formOverflow();
                return null;
            }
        });
        Animator.init().addCallback(new Callable() {
            @Override
            public Object call() throws Exception {
//                primaryZone.unloadAll();
//                zoneTree.setTreePane(new TreePane(order,propagationType,OverFlowType.DIRECT,newPrimary));
                zoneTree.rebuildTree(newPrimary);
//                zoneTree.formTree();
                return null;
            }
        });
    }


    @Override
    public int getBlockFactor() {
        return primaryZone.getBlockFactor();
    }

    @Override
    public void setTreeType(PropagationType type) {
        super.setTreeType(type);
        switch(fileStage)
        {
            case TREE: {
                propagationType = type;
                zoneTree.setNewTreePane(primaryZone, order, type);
                zoneTree.attach(mainSplitter);
                deform();

                break;
            }
            case PRIMARY:
            case OVERFLOW: {
                zoneTree.setNewTreePane(primaryZone, order, type);
                propagationType = type;
                break;
            }
        }

    }

    public void setTree(TreePane t) {
        zoneTree.setTreePane(t);
        zoneTree.attach(mainSplitter);
        zoneTree.repaint();
    }

    @Override
    public void setPrimaryZone(PrimaryZone newPrimary) {
        primaryZone = newPrimary;
//        zoneTree = new ZoneTree(order,newPrimary,propagationType);
//        attachZoneTree();
        primaryZone.addFocuser(new Focuser(primaryZone));
        primaryZoneHolder.setViewportView(newPrimary);
        repaint();
        UndoSystem.init().setPrimaryZone(newPrimary);

    }

    @Override
    public void setOver(OverflowZone o) {
        zoneTree.setOver(o);
        zoneTree.attach(mainSplitter);
        zoneTree.repaint();

    }

    @Override
    public OverFlowType getOverflowType() {
        return OverFlowType.DIRECT;
    }

    @Override
    public PrimaryZone getPrimary() {
        return primaryZone;
    }

    @Override
    public OverflowZone getOverflow() {
        return zoneTree.getOverflowZone();
    }

    @Override
    public TreePane getTree() {
        return zoneTree.getTree();
    }

    private void attachZoneTree(){
        JScrollPane spTree = new JScrollPane(
                zoneTree,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        zoneTree.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                spTree.setMinimumSize(zoneTree.getMinimumSize());
            }
        });

        zoneTree.attach(mainSplitter);
        JPanel zoneTreeHolder = new JPanel();
        zoneTreeHolder.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.weightx = 1;
        g.weighty = 1;
        g.fill = GridBagConstraints.BOTH;
        zoneTreeHolder.add(spTree, g);
        mainSplitter.setLeftComponent(zoneTreeHolder);
    }

}

class ZoneTree extends JComponent {
    private TreePane treePane;
    private OverflowZone overflowZone;
    public HashMap<Integer, Line2D.Float> relations = new HashMap<>();

    public ZoneTree(int oreder, PrimaryZone primaryZone, PropagationType p) {
        this.overflowZone = new OverflowZone(primaryZone);
        if (treePane == null) {
            this.treePane = new TreePane(oreder, p, OverFlowType.DIRECT, primaryZone);
        } else {
            this.treePane = new TreePane(oreder, p, OverFlowType.DIRECT, primaryZone, overflowZone, treePane.getMaping());
        }
        UndoSystem.init().setOverflowZone(overflowZone);
        UndoSystem.init().setTreePane(treePane);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        attachTree();
        attachOver();
    }


    public TreePane getTree() {
        return treePane;
    }

    public OverflowZone getOverflowZone() {
        return overflowZone;
    }

    public void drawRelationLeefOver(Graphics2D g, Block parent, Block child, int key) {
//        System.out.println("INDEX LEEEEEEEEEEEEEEEEEEEEEEEEEF " + indexLeef);
        if (parent.isShowing()) {
            int pom = 0;

            pom += parent.getWidth() * (key % treePane.getOrder() + 1) / (treePane.getOrder() + 1);

            int x1 = parent.getX() + pom;
            int y1 = parent.getY() + parent.getHeight();
            int x2 = child.getX() + child.getWidth() / 2;
            int y2 = treePane.getHeight() + child.getY();
            Line2D.Float line = new Line2D.Float(x1, y1, x2, y2);
            relations.put(key, line);
            g.setColor(Color.GRAY);
            g.draw(line);
        }
    }

    public void addRecord(SearchResult sr, Record r, PrimaryZone primaryZone) throws Exception {

//        if (overcrownersDirectHashMap.size() == 0) {
//            initHash();
//        }
        AddResult a;
        BlockPointer pointer = sr.pointer;
        Block primary;
        NodeBlock leaf = (NodeBlock) treePane.getLeafs().get(sr.leafAddress);
        if (pointer.getType() != PointerType.OVERFLOW) {
            BlockPointer pointerS = sr.pointer;
            primary = primaryZone.getBlock(pointerS.getAddress());
            a = primary.insertRecord(r, sr.recordAddress);
            leaf.setUpperPointer(sr.leafRecordAddress, primary.getAddress(), primary.getPrefferedKey(treePane.getType()));
            Animator.init().addAnimation(r.getAddedAnim());
        } else {
            a = new AddResult(r, sr.pointer);
        }
        System.out.println("USAOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO" + sr.pointer);
        Integer address;
        // proveri da li ima sta da se doda
        if (a.record != null) {
            //Proveri da li vec postoji lanac na koji pokazuje list
            if (leaf.isChained(sr.leafRecordAddress)) {
                address = overflowZone.addRecord(leaf.getChainAddress(sr.leafRecordAddress), a.record);
            } else {
                address = overflowZone.addRecord(a.record);
            }
            //ako postoji povratna adresa namestiti donji pointer callbaack je jer se rekord u prekoracenje dodaje isto callbackom
            if (address != null) {
                Animator.init().addCallback(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        int value = overflowZone.getPrefferedChainKey(address, treePane.getType());
                        leaf.setLowerPointer(sr.leafRecordAddress, overflowZone.getBlock(address).getAddress(), value);
                        System.out.println("Adresa rekorda " + sr.leafRecordAddress);

                        int mapKey = sr.leafAddress * treePane.getOrder() + sr.leafRecordAddress;
                        repaint();
                        return null;
                    }
                });


//                Block highest = overflowZone.getChainEnd(address);
//                int mapKey =leafAddress*treePane.getOrder()+sr.leafRecordAddress;
//                if (!chainValues.containsKey(mapKey)){
//
//                }
//                chainValues.

                //System.out.println("USAOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO" + address);
                //  b.setSpecialPointer(new BlockPointer(BlockPointer.Type.OVERFLOW, address));
//                OvercrownersDirect workWhitIt = overcrownersDirectHashMap.get(sr.pointer.getAddress());
//                workWhitIt.addBlock(address, overflowZone, primaryZone);

            }
        }
        repaint();


    }

    public void updateBlock(Block b, SearchResult sr) {
        overflowZone.updateBlock(sr.pointer.getAddress(), b);
        repaint();
    }

    public SearchResult searchBlock(Integer blockAddress, int key) {
        //return sr;
        return null;
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.BLACK);
        for (Block b : treePane.getLeafs()) {
            NodeBlock nb = (NodeBlock) b;
            for (int i = 0; i < b.getFilled(); ++i) {
                BlockPointer blockPointer = nb.getChainPointer(i);
                if (blockPointer.getType() == PointerType.OVERFLOW) {
                    int order = treePane.getOrder();
                    int key = nb.getAddressNum() * order + i;
                    int address = blockPointer.getAddress();
                    Block child = overflowZone.getBlock(address);
                    drawRelationLeefOver((Graphics2D) g, nb, child, key);
                }
            }
        }
//        initHash();
//        relations.clear();
        //Line2D.Float jt = new Line2D.Float();
//        for(Map.Entry<Integer, OvercrownersDirect> over : overcrownersDirectHashMap.entrySet()) {
//            if (over.getValue() != null) {
//                over.getValue().paint(g, this);
//            }
//        }println
    }

    public Animation getLineAnim(int index) {
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
                if (relations.containsKey(index)) {
                    g.draw(relations.get(index));
                }
                a.incStep();
            }
        });
        return a;
    }


    public void formTree() {
        treePane.form();
    }

    void formOverFlow() {
        overflowZone.form();
    }

    public TreePane getTreePane() {
        return treePane;
    }

    public void setTreePane(TreePane t) {
        remove(treePane);
        treePane = t;
        attachTree();
    }

    public void setNewTreePane(PrimaryZone p, int order, PropagationType type) {
        TreePane t = new TreePane(order, type, OverFlowType.DIRECT, p, overflowZone, treePane.getMaping());
        setTreePane(t);
        repaint();
        updateUI();
    }

    public void attach(JSplitPane mainSplitter) {
        ContainerListener cl = new ContainerListener() {
            @Override
            public void componentAdded(ContainerEvent e) {
                mainSplitter.setDividerLocation(ZoneTree.this.getMinimumSize().width);
            }

            @Override
            public void componentRemoved(ContainerEvent e) {
                mainSplitter.setDividerLocation(0);
            }
        };
        treePane.addContainerListener(cl);
        treePane.addFocuser(new Focuser(treePane));
        overflowZone.addContainerListener(cl);
        overflowZone.addFocuser(new Focuser(overflowZone));
        mainSplitter.setDividerLocation(Math.max(treePane.getMinimumSize().width,overflowZone.getMinimumSize().width));
    }
    private void attachOver(){
        overflowZone.addContainerListener(new ContainerListener() {
            @Override
            public void componentAdded(ContainerEvent e) {
                ZoneTree.this.repaint();
                ZoneTree.this.updateUI();
            }

            @Override
            public void componentRemoved(ContainerEvent e) {

            }
        });
        add(overflowZone,2);
        repaint();
        updateUI();
    }
    private void attachTree() {
        add(treePane,0);
        add(Box.createVerticalGlue(),1);
        repaint();
        updateUI();
    }


    public void rebuildTree(PrimaryZone newPrimary) {
        treePane.rebuild(newPrimary);
    }

    public void setOver(OverflowZone o) {
        remove(overflowZone);
        overflowZone = o;
        attachOver();
    }

//    void removeChainValue(Integer value) {
//        chainValues.remove(value);
//    }
//
//    public void putChainValue(int i, Integer address) {
//        chainValues.put(i, address);
//    }
}

