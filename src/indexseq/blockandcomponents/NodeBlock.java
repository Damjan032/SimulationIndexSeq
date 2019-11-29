package indexseq.blockandcomponents;

import indexseq.animator.Animator;
import indexseq.enums.FileStatus;
import indexseq.enums.OverFlowType;
import indexseq.enums.PointerType;
import indexseq.enums.PropagationType;
import indexseq.supportclasses.SearchResult;
import indexseq.supportclasses.SearchStrategy;
import indexseq.supportclasses.SearchStrategyFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NodeBlock extends Block {
    private ArrayList<Block> children = new ArrayList<>();

    private boolean leaf = false;
    private int level;
    private SearchStrategy searchStrategy;
    private NodeBlock parent;
    private NodeStatus nodeStatus = NodeStatus.NONE;
    private Map<Integer, Boolean> chained = new HashMap<>();

    public enum NodeStatus {
        FIRST, NONE, LAST
    }


    public NodeBlock(int address, int order, boolean leaf, int level, PropagationType propagationType) {
        super(new BlockPointer(PointerType.TREE, address), order, 1, RecordOrientation.HORIZONTAL);
        setFileStatus(FileStatus.LOADED);
        for (int i = 0; i < blockFactor; ++i) {
            chained.put(i, false);
        }
        this.leaf = leaf;
        this.level = level;
        this.searchStrategy = SearchStrategyFactory.makeSearchStrategy(propagationType);
    }

    public NodeBlock(NodeBlock b) {
        super(b);
        for (int i = 0; i < blockFactor; ++i) {
            chained.put(i, b.chained.get(i));
        }
        leaf = b.leaf;
        children.addAll(b.children);
        level = b.level;
        parent = b.parent;
        searchStrategy = b.searchStrategy;
        setFileStatus(FileStatus.LOADED);
    }

    private void setChild(int index, NodeBlock nodeBlock) {
        children.set(index, nodeBlock);
    }

    public SearchResult findNext(int key) {
        return searchStrategy.findNext(this, key);
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setFirst() {
        Record r = records.get(0);
        for (int i = 0; i < r.getKeys().size(); ++i) {
            r.setKey(i, 0);
        }
        nodeStatus = NodeStatus.FIRST;
    }

    public void setLast() {
        Record r = records.get(filled-1);
        for (int i = 0; i < r.getKeys().size(); ++i) {
            r.setKey(i, 99);
        }
        nodeStatus = NodeStatus.LAST;
    }

    public void setNodeStatus(NodeStatus nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public int getOrder() {
        return blockFactor;
    }

    public void setOrder(int order) {
        this.blockFactor = order;
    }

    private void setParentNode(NodeBlock p) {
        parent = p;
    }

    public NodeBlock getParentNode() {
        return parent;
    }

    public void setChildNode(int i, NodeBlock b) {
        children.set(i, b);
    }

    public void setChained(int index, boolean val) {
        this.chained.put(index, val);
    }

    public ArrayList<Block> getChildren() {
        if (leaf) {
            return null;
        }
        return children;
    }

    public void addSpecialRecord(ArrayList<Integer> keys, ArrayList<BlockPointer> pointers) {
        addRecordOnEnd(new Record(keys, pointers));
    }

    public void setChildren(ArrayList<Block> children) {
        this.children = children;
    }

    public void setChildrenAndGenerateRecords(ArrayList<Block> children, PropagationType propagationType, OverFlowType overFlowType) {
        this.children = children;
        for (Block nb : children) {
            int k = nb.getPrefferedKey(propagationType);
            if (overFlowType == OverFlowType.DIRECT && leaf) {
                addRecordOnEnd(new Record(k, nb.getAddressNum(), k, nb.getAddressNum()));
            } else {
                addRecordOnEnd(new Record(k, nb.getAddress()));
            }
            if (nb instanceof NodeBlock) {
                ((NodeBlock) nb).setParentNode(this);
            }
        }
    }

    public Integer getChainPointerNum(int i) {
        return records.get(i).getPointer(1).getAddress();
    }

    public BlockPointer getChainPointer(int i) {
        return records.get(i).getPointer(1);
    }

    public int getLevel() {
        return level;
    }

    public void setUpperPointer(int i, BlockPointer b, int key) {
        Animator.init().addAnimation(getHighlightAnim(false));
        hasChanged();
        Record rec = records.get(i);
        Animator.init().addAnimation(rec.getUpdateAnim());
        rec.setPointer(b, 0);
        if (i != 0 || nodeStatus != NodeStatus.FIRST) {
            rec.setKey(0, key);
        }
        repaint();
    }

    public void setLowerPointer(int i, BlockPointer b, int key) {
        Animator.init().addAnimation(getHighlightAnim(false));
        hasChanged();
        Record rec = records.get(i);
        Animator.init().addAnimation(rec.getUpdateAnim());
        if (i != filled - 1 || nodeStatus != NodeStatus.LAST) {
            rec.setKey(1, key);
        }
        rec.setPointer(b, 1);
        chained.put(i, true);
        repaint();
    }

    public void setPointer(Block b, int key) {
        Animator.init().addAnimation(getHighlightAnim(false));
        Record r = getRecord(b.getAddressNum() % blockFactor);
        r.updateSpecialField(key, b.getAddress());
        Animator.init().addAnimation(r.getUpdateAnim());
    }


//    private void propagateKeyChange(NodeBlock nodeBlock, int key) {
//
//        Animator.init().addAnimation(getLoadAnim());;
//        int i = children.indexOf(nodeBlock);
//        Record r = records.get(i);
//        if (i != 0 || nodeStatus != NodeStatus.FIRST) {
//            if (i != blockFactor || nodeStatus != NodeStatus.LAST) {
//                r.setKey(0, key);
//            }
//        }
//        if (parent!=null){
//            parent.propagateKeyChange(this,key);
//        }
//    }

    public void addFormingBlocksAnim() {
        for (Block b : children) {
            Animator.init().addAnimation(b.getHighlightAnim(false));
        }
    }

    public void updateLeafKey(int key, int value) {

    }

    @Override
    protected void updateBlockSize() {
        int mul = 0;
        if (records.get(0).hasMoreKeys()) {
            mul = 1;
        }
        setMinimumSize(new Dimension(70 + blockFactor * 10, 50 + mul * 10));
        setPreferredSize(new Dimension(80 + blockFactor * 10, 80));
        setMaximumSize(new Dimension(190, 190));
    }

    public boolean isChained(int i) {
        return chained.get(i);
    }

    public void unchain(int i) {
        Record first = records.get(i);
        ArrayList<Integer> keys = first.getKeys();
        ArrayList<BlockPointer> pointers = first.getPointers();
        Integer firstKey = keys.get(0);
        BlockPointer firstAddress = pointers.get(0);
        for (i = 1; i < keys.size(); ++i) {
            keys.set(i, firstKey);
            pointers.set(i, new BlockPointer(firstAddress));
        }
        chained.put(i, false);
    }

    public Integer getChainAddress(Integer leafRecordAddress) {
        return records.get(leafRecordAddress).getPointerAddress(1);
    }


}
