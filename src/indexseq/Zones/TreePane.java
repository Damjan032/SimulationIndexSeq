package indexseq.Zones;

import indexseq.animator.Animation;
import indexseq.animator.Animator;
import indexseq.blockandcomponents.Block;
import indexseq.blockandcomponents.BlockPointer;
import indexseq.blockandcomponents.NodeBlock;
import indexseq.blockandcomponents.Record;
import indexseq.enums.OverFlowType;
import indexseq.enums.PointerType;
import indexseq.enums.PropagationType;
import indexseq.supportclasses.Focuser;
import indexseq.supportclasses.SearchResult;
import javafx.scene.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerListener;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TreePane extends JPanel implements Serializable {
    private NodeBlock root;
    private int height = 0;
    private int order;
    private int numOfBlocks = 0;
    private int leafsSize = 0;
    private int maxNumLeafs;
    //    private JLabel labelGlupa;
    private List<Block> leafs;

    private PropagationType propagationType;
    private OverFlowType overFlowType;


    private List<Block> blocks = new ArrayList<>();

    private HashMap<String, Line2D.Float> relations = new HashMap<>();


    protected Color lineColor = new Color(77, 131, 222);
    private Focuser focuser;
    private Tree tree;

    public TreePane(int order, PropagationType propagationType, OverFlowType overFlowType, PrimaryZone p) {
        this.order = order;
        this.propagationType = propagationType;
        this.overFlowType = overFlowType;
        leafs = new ArrayList<>();
        setupLayout();
        build(p);
    }


    public TreePane(int order, PropagationType propagationType, OverFlowType overFlowType, PrimaryZone p, OverflowZone o, Map<Integer, Integer> maping) {
        this.order = order;
        this.propagationType = propagationType;
        this.overFlowType = overFlowType;
        leafs = new ArrayList<>();
        setupLayout();
//        form(p);
        build(p, o, maping);
    }

    public TreePane(TreePane other) {
        this.height = other.height;
        this.order = other.order;
        this.numOfBlocks = other.numOfBlocks;
        this.leafsSize = other.leafsSize;
        this.maxNumLeafs = other.maxNumLeafs;
        this.propagationType = other.propagationType;
        this.overFlowType = other.overFlowType;
        this.lineColor = other.lineColor;
        this.focuser = other.focuser;
        leafs = new ArrayList<>();
        setupLayout();
        for (Block b : other.leafs) {
            NodeBlock nb = (NodeBlock) b;
            NodeBlock newBlock = new NodeBlock(nb);
            leafs.add(newBlock);
            addNode(newBlock, height);
        }
        buildTree();
        formInstant();
    }

    private void setupLayout(){
        setLayout(new BorderLayout());
        add(new JLabel("Indeksno stablo"),BorderLayout.NORTH);
        tree = new Tree();
        tree.setLayout(new GridBagLayout());
        add(tree,BorderLayout.CENTER);
    }


    public List<Block> getLeafs() {
        return leafs;
    }

    public int getOrder() {
        return order;
    }

    public void instantBuild() {

    }

    public void build(PrimaryZone p) {
        ArrayList<Block> blocks = p.getBlocks();
        if (root == null) {
            numOfBlocks = blocks.size();
            height = (int) Math.ceil(Math.log(numOfBlocks) / Math.log(order));

            if (numOfBlocks > 0) {
                addLeafs(blocks);
            }
        }
    }
    public void rebuild(PrimaryZone p){
        leafsSize = numOfBlocks / order + numOfBlocks % order;
        maxNumLeafs = order * (int) Math.ceil((float) leafsSize / order);
        height = (int) Math.ceil(Math.log(numOfBlocks) / Math.log(order));
        hideAll();
        root = null;
        blocks.clear();
        leafs.clear();
        build(p);
    }

    public Map<Integer, Integer> getMaping() {
        Map<Integer, Integer> map = new HashMap<>();
        for (Block b : leafs) {
            for (Record r : b.getRecords()) {
                if (r.getStatus() == Record.Status.ACTIVE) {
                    BlockPointer addressP = r.getPointer(0);
                    BlockPointer addressO = r.getPointer(1);
                    if (addressO.getType() != PointerType.OVERFLOW) {
                        map.put(addressP.getAddress(), null);
                    } else {
                        map.put(addressP.getAddress(), addressO.getAddress());
                    }
                }
            }
        }
        return map;
    }
    public void build(PrimaryZone p, OverflowZone o) {
        ArrayList<Block> pBlocks = p.getBlocks();
        numOfBlocks = pBlocks.size();
        leafsSize = numOfBlocks / order + numOfBlocks % order;
        maxNumLeafs = order * (int) Math.ceil((float) leafsSize / order);
        height = (int) Math.ceil(Math.log(numOfBlocks) / Math.log(order));
        hideAll();
        blocks.clear();
        leafs.clear();
        NodeBlock node = null;
        int blocksize = p.getBlocks().size();
        ArrayList<Block> children = null;
        for (int i = 0; i < blocksize; ++i) {
            if (i % order == 0) {
                node = new NodeBlock(i / order, order, true, height, propagationType);
                children = new ArrayList<>();
            }
            Block b = pBlocks.get(i);
            children.add(b);


            ArrayList<Integer> keys = new ArrayList<>();
            ArrayList<BlockPointer> pointers = new ArrayList<>();

            Integer paddress = b.getAddressNum();

            if (propagationType == PropagationType.LOW && i == 0) {
                keys.add(0);
                node.setNodeStatus(NodeBlock.NodeStatus.FIRST);
            } else if (propagationType == PropagationType.HIGH && i == blocksize - 1) {
                keys.add(99);
                node.setNodeStatus(NodeBlock.NodeStatus.LAST);
            } else {
                keys.add(b.getPrefferedKey(propagationType));
            }
            pointers.add(b.getAddress());

            node.addSpecialRecord(keys, pointers);

//            if (poMaping.get(b.getAddressNum())!=null){
//                b = o.getBlock(poMaping.get(b.getAddressNum()));
//                int key;
//                if (propagationType==PropagationType.LOW&&i==0) {
//                    key = 0;
//                }else if (propagationType==PropagationType.HIGH&&i==blocksize-1){
//                    key = 99;
//                }else {
//                    key=b.getPrefferedKey(propagationType);
//                }
//                if (overFlowType==OverFlowType.DIRECT) {
//                    keys.add(key);
//                    pointers.add(b.getAddress());
//                }else{
//                    keys.set(0,key);
//                }
//            }else if(overFlowType==OverFlowType.DIRECT){
//                int key;
//                if (propagationType==PropagationType.LOW&&i==0) {
//                    key = 0;
//                }else if (propagationType==PropagationType.HIGH&&i==blocksize-1) {
//                    key = 99;
//                }else{
//                    key=b.getPrefferedKey(propagationType);
//                }
//                keys.add(key);
//                pointers.add(b.getAddress());
//            }

//            node.addSpecialRecord(keys,pointers);

            if (i % order == order - 1 || i == blocksize - 1) {
                leafs.add(node);
                addNode(node, height);
                node.setChildren(children);
            }

        }
        buildTree();
    }

    public void build(PrimaryZone p, OverflowZone o, Map<Integer, Integer> poMaping) {
        ArrayList<Block> pBlocks = p.getBlocks();
        numOfBlocks = pBlocks.size();
        leafsSize = numOfBlocks / order + numOfBlocks % order;
        maxNumLeafs = order * (int) Math.ceil((float) leafsSize / order);
        height = (int) Math.ceil(Math.log(numOfBlocks) / Math.log(order));
        hideAll();
        blocks.clear();
        leafs.clear();
        NodeBlock node = null;
        int blocksize = p.getBlocks().size();
        ArrayList<Block> children = null;
        for (int i = 0; i < blocksize; ++i) {
            if (i % order == 0) {
                node = new NodeBlock(i / order, order, true, height, propagationType);
                children = new ArrayList<>();
            }
            Block b = pBlocks.get(i);
            children.add(b);


            ArrayList<Integer> keys = new ArrayList<>();
            ArrayList<BlockPointer> pointers = new ArrayList<>();

            Integer paddress = b.getAddressNum();

            if (propagationType == PropagationType.LOW && i == 0) {
                keys.add(0);
                node.setNodeStatus(NodeBlock.NodeStatus.FIRST);
            } else if (propagationType == PropagationType.HIGH && i == blocksize - 1) {
                keys.add(99);
                node.setNodeStatus(NodeBlock.NodeStatus.LAST);
            } else {
                keys.add(b.getPrefferedKey(propagationType));
            }
            pointers.add(b.getAddress());

            if (poMaping.get(b.getAddressNum()) != null) {
                int key;
                b = o.getBlock(poMaping.get(paddress));
                if (overFlowType == OverFlowType.DIRECT) {
                    pointers.add(b.getAddress());
                    if (i == blocksize - 1 && propagationType == PropagationType.HIGH) {
                        key = 99;
                        node.setNodeStatus(NodeBlock.NodeStatus.LAST);
                    } else {
                        key = o.getPrefferedChainKey(b.getAddressNum(),propagationType);
                    }
                    keys.add(key);
                    node.setChained(i%order,true);
                } else {
                    if (propagationType == PropagationType.HIGH) {
                        keys.set(0, o.getChainEnd(b.getAddressNum()).getPrefferedKey(propagationType));
                    }
                }
            } else if (overFlowType == OverFlowType.DIRECT) {
                int key;
                if (propagationType == PropagationType.LOW && i == 0) {
                    key = 0;
                    node.setNodeStatus(NodeBlock.NodeStatus.FIRST);
                } else if (propagationType == PropagationType.HIGH && i == blocksize - 1) {
                    key = 99;
                    node.setNodeStatus(NodeBlock.NodeStatus.LAST);
                } else {
                    key = b.getPrefferedKey(propagationType);
                }
                keys.add(key);
                pointers.add(b.getAddress());
            }

            node.addSpecialRecord(keys, pointers);

//            if (poMaping.get(b.getAddressNum())!=null){
//                b = o.getBlock(poMaping.get(b.getAddressNum()));
//                int key;
//                if (propagationType==PropagationType.LOW&&i==0) {
//                    key = 0;
//                }else if (propagationType==PropagationType.HIGH&&i==blocksize-1){
//                    key = 99;
//                }else {
//                    key=b.getPrefferedKey(propagationType);
//                }
//                if (overFlowType==OverFlowType.DIRECT) {
//                    keys.add(key);
//                    pointers.add(b.getAddress());
//                }else{
//                    keys.set(0,key);
//                }
//            }else if(overFlowType==OverFlowType.DIRECT){
//                int key;
//                if (propagationType==PropagationType.LOW&&i==0) {
//                    key = 0;
//                }else if (propagationType==PropagationType.HIGH&&i==blocksize-1) {
//                    key = 99;
//                }else{
//                    key=b.getPrefferedKey(propagationType);
//                }
//                keys.add(key);
//                pointers.add(b.getAddress());
//            }

//            node.addSpecialRecord(keys,pointers);

            if (i % order == order - 1 || i == blocksize - 1) {
                leafs.add(node);
                addNode(node, height);
                node.setChildren(children);
            }

        }
        buildTree();
    }

    public void form() {
        int nodesOnLevel = 0;
        int level = height;
        int old_i = 0;
        while (nodesOnLevel != 1) {
            int i;
            nodesOnLevel = (int) Math.ceil((float) numOfBlocks / Math.pow(order, height - level + 1));
            for (i = 0; i < nodesOnLevel; ++i) {
                int lev = level;
                int index = i;
                int nol = nodesOnLevel;
                NodeBlock nb = (NodeBlock) blocks.get(old_i + i);
                nb.addFormingBlocksAnim();
                Animator.init().addCallback(() -> {
                    if (nb.getAddressNum() == 0) {
                        hideAll();
                    }
                    addNodeToLayout(nb, lev, index, nol);
                    return null;
                });
                Animator.init().addAnimation(nb.getGrowAnim());
                Animator.init().addAnimation(nb.getHighlightAnim(true));
            }
            old_i += i;
            level--;
        }

    }

    public void formInstant() {
        int nodesOnLevel = 0;
        int level = height;
        int old_i = 0;
        while (nodesOnLevel != 1) {
            int i;
            nodesOnLevel = (int) Math.ceil((float) numOfBlocks / Math.pow(order, height - level + 1));
            for (i = 0; i < nodesOnLevel; ++i) {
                NodeBlock nb = (NodeBlock) blocks.get(old_i + i);
                addNodeToLayout(nb, level, i, nodesOnLevel);
            }
            old_i += i;
            level--;
        }
    }

    public void setType(PropagationType type) {
        propagationType = type;
    }

    private void addLeafs(ArrayList<Block> blocks) {
        leafsSize = numOfBlocks / order + numOfBlocks % order;
//        Dimension d = blocks.get(0).getMinimumSize();
//        setSize(d.width*leafsSize,d.height);

        maxNumLeafs = order * (int) Math.ceil((float) leafsSize / order);
        for (int i = 0; i < numOfBlocks; i += order) {


            NodeBlock nb = new NodeBlock(i / order, order, true, height, propagationType);
            ArrayList<Block> children = new ArrayList<>();
            for (int j = 0; j < order && (i + j < numOfBlocks); j++) {
                Block b = blocks.get(i + j);
//                Animator.init().addAnimation(b.getHighlightAnim());
                children.add(b);
            }
            nb.setChildrenAndGenerateRecords(children, propagationType, overFlowType);

            if (i + order >= numOfBlocks && propagationType == PropagationType.HIGH) {
                nb.setLast();
            } else if (i == 0 && propagationType == PropagationType.LOW) {
                nb.setFirst();
            }

            leafs.add(nb);
            addNode(nb, height);
        }
//        maxNumLeafs = (int) Math.pow(order, Math.log(leafsSize) / Math.log(order));
        buildTree();

    }

    public void buildTree() {
        buildUpper(leafs, leafs.size() - 1, height - 1);
    }

    private void buildUpper(List<Block> lower, int lastAddress, int level) {
        int n = lower.size();
        ArrayList<Block> newLower = new ArrayList<>();
        if (n > 1) {
            for (int i = 0; i < Math.ceil(((float) n) / order); i++) {
                ++lastAddress;
                ArrayList<Block> children = new ArrayList<>();
                for (int j = 0; j < order; j++) {
                    int index = i * order + j;
                    if (index >= n) {
                        break;
                    }
                    Block b = lower.get(i * order + j);
//                    Animator.init().addAnimation(b.getHighlightAnim());
                    children.add(b);
                }

                NodeBlock nodeBlock = new NodeBlock(lastAddress, order, false, level, propagationType);
                nodeBlock.setChildrenAndGenerateRecords(children, propagationType, overFlowType);
                //NodeBlock nodeBlock = new NodeBlock(lastAddress, order, false, this, children);


                //  nodeBlock.setVisible(false);

                addNode(nodeBlock, level);
                newLower.add(nodeBlock);
            }
            buildUpper(newLower, lastAddress, level - 1);
        }
    }

    private void hideAll() {
        if (root != null) {
            tree.remove(root);
//        root = null;
//        root.setVisible(false);
            hideChildren(root.getChildren());
        }
        repaint();
    }

    private void hideChildren(ArrayList<Block> child) {
        if (child == null) return;
        for (Block b : child) {
//            b.setVisible(false);
            tree.remove(b);
            hideChildren(((NodeBlock) b).getChildren());
        }

    }

    private void addNode(NodeBlock nb, int level) {
        if (level == 1) {
            root = nb;
        }
        blocks.add(nb);
    }

    private void addNodeToLayout(NodeBlock nb, int level, int index, int nodesOnLevel) {
        GridBagConstraints gc = new GridBagConstraints();
        if (level!=1){
            gc.insets = new Insets(50, 5, 5, 5);

        }else{
            gc.insets = new Insets(5, 5, 5, 5);
        }
//        gc.gridx = index;
//        int nodesOnLevel = (int) Math.ceil((float) numOfBlocks / Math.pow(order, height - level + 1));
//        gc.gridx = (index+1)*order/2;

        //stara pouzdana
        gc.gridx = (index + 1) * maxNumLeafs / (nodesOnLevel + 1);
//        gc.gridx = level*order/2;
//        gc.gridx = (int) Math.pow(order, height - level) - 1 + index * (order * (height - level + 1));
        gc.gridy = level;

//        Animator.init().freeze();
        System.out.println("Added node" + nb.getAddress() + " on " + level + " " + index + " " + nodesOnLevel);

        tree.add(nb, gc);
    }


/*
    public int find(int key){
        try {
        		return root.find(key);
        	
        } catch (Exception e) {
*/

//    public void delLab(){
//        labelGlupa.setVisible(false);
//    }

//    public void inittlabGLupa(){
//        labelGlupa = new JLabel("");
//        labelGlupa.setMinimumSize(new Dimension(500,0));
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.gridy=10000;
//        gbc.gridx=1;
//        gbc.weightx=10;
//        addRecord(labelGlupa, gbc);
//
//    }

    //    public Integer find(int key) {
//        Integer found = root.searchNode(key);
//        return found;
//    }
    public SearchResult search(int key) {
        NodeBlock nb = root;
        Integer next;
        do {
            SearchResult sr = nb.findNext(key);
//            if (sr.pointer==null){
//                return new SearchResult(false);
//            }
            next = sr.pointer.getAddress();
            NodeBlock b = (NodeBlock) blocks.get(next);
            Animator.init().addAnimation(getRelationAnim(nb.getAddressNum(), b.getAddressNum()));
            nb = b;
        } while (!nb.isLeaf());
        return nb.findNext(key);
    }

    private Animation getRelationAnim(Integer addressNum, Integer addressNum1) {
        return tree.getRelationAnim(addressNum,addressNum1);
    }

    public void addFocuser(Focuser f) {
        focuser = f;
        for (Block b : blocks) {
            b.addFocuser(f);
        }
    }

    public void clear() {
        hideAll();
        blocks.clear();
    }


    class Tree extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
//        g.drawLine(0,0,100,100);
            paintChildren(g, root);
        }

        private void paintChildren(Graphics g, NodeBlock parent) {
            if (getComponentCount() == 0 || parent == null) {
            } else {
                ArrayList<Block> children = parent.getChildren();
                if (children != null) {
                    for (int i = 0; i < children.size(); i++) {
                        NodeBlock child = (NodeBlock) children.get(i);
                        drawRelation((Graphics2D) g, parent, child, i);
                        paintChildren(g, child);
                    }
                }
            }
        }

        private void drawRelation(Graphics2D g, Block parent, Block child, int index) {
            if (parent.isShowing()) {

                int x1 = parent.getX() + parent.getWidth() * (index + 1) / (order + 1);
                int y1 = parent.getY() + parent.getHeight();
                int x2 = child.getX() + child.getWidth() / 2;
                int y2 = child.getY();
                g.setColor(Color.GRAY);
                Line2D.Float line = new Line2D.Float(x1, y1, x2, y2);
                relations.put(parent.getAddressNum() + "-" + child.getAddressNum(), line);
                g.draw(line);
//                g.drawLine(x1, y1, x2, y2)
            }
        }

        public Animation getRelationAnim(Integer parent, Integer child) {
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
                    Line2D.Float line = relations.get(parent.toString() + "-" + child);
                    Graphics2D g = (Graphics2D) getGraphics();
                    g.setColor(Color.YELLOW);
                    g.draw(line);
                    a.incStep();
                }
            });
            return a;
        }
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public PropagationType getType() {
        return propagationType;
    }

    public void setBlock(NodeBlock b, Integer address) {
        NodeBlock old = (NodeBlock) blocks.get(address);
        int index = old.getParentNode().getChildren().indexOf(old);
        old.getParentNode().setChildNode(index,b);
        remove(old);
        if (old.isLeaf()){
            leafs.set(address,b);
        }
        blocks.set(address, b);
        int level = findLevel(address);
        index = findIndex(address, level);
        int indexParent = index % order;
        b.getParentNode().setChildNode(indexParent, b);
        int nodesOnLevel = (int) Math.ceil((float) numOfBlocks / Math.pow(order, height - level + 1));
        System.out.println(level + " " + index + " " + nodesOnLevel);
        if (b.isLeaf()) {
            leafs.set(address, b);
        }
        addNodeToLayout(b, level, index, nodesOnLevel);
    }

    private int findLevel(Integer address) {
        int currlevel = height;
        int index = 0;
        int nodesOnLevel;
        while (index < numOfBlocks) {
            nodesOnLevel = (int) Math.ceil((float) numOfBlocks / Math.pow(order, height - currlevel + 1));
            for (int i = 0; i < nodesOnLevel; i++) {
                if (index == address) {
                    return currlevel;
                } else if (index > address) {
                    break;
                }
                ++index;
            }
            currlevel--;
        }
        return -1;
    }

    private int findIndex(int address, int level) {
        int currlevel = 0;
        int index = 0;
        int nodesOnLevel;
        while (currlevel <= level) {
            nodesOnLevel = (int) Math.ceil((float) numOfBlocks / Math.pow(order, height - currlevel + 1));
            for (int i = 0; i < nodesOnLevel; i++) {
                if (index == address) {
                    return i;
                } else if (index > address) {
                    break;
                }
                index++;
            }
            currlevel++;
        }
        return -1;
    }

    public void updateLeaf(Block b) {
        NodeBlock nb = (NodeBlock) leafs.get(b.getAddressNum() / order);
        nb.setPointer(b, b.getPrefferedKey(propagationType));
    }

    @Override
    public synchronized void addContainerListener(ContainerListener l) {
        super.addContainerListener(l);
        tree.addContainerListener(l);
    }
}