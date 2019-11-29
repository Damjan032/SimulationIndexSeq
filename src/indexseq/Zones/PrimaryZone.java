package indexseq.Zones;

import indexseq.MainFrame;
import indexseq.animator.Animation;
import indexseq.blockandcomponents.Block;
import indexseq.blockandcomponents.BlockPointer;
import indexseq.blockandcomponents.Record;
import indexseq.animator.Animator;
import indexseq.dialogs.OperativeMemoryDialog;
import indexseq.enums.FileStatus;
import indexseq.enums.OverFlowType;
import indexseq.enums.PointerType;
import indexseq.supportclasses.AddResult;
import indexseq.supportclasses.Focuser;
import indexseq.supportclasses.ModifiedFlowLayout;
import indexseq.supportclasses.SearchResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class PrimaryZone extends JPanel {
    private ArrayList<Block> blocks;
    private int filledNum;
    private int blockNum;
    private int blockFactor;
    private int fieldNum;
    private OverFlowType overFlowType = OverFlowType.INDIRECT;
    private JPanel panel;
    private JPanel operativeMemory;

    public PrimaryZone(ArrayList<Block> blocks, int blockFactor) {
        this.blocks = blocks;
        this.blockFactor = blockFactor;
        this.blockNum = blocks.size();
        setupLayout();
    }

    public PrimaryZone(PrimaryZone p) {
        blocks = new ArrayList<>();
        overFlowType = p.overFlowType;
        filledNum = p.filledNum;
        blockFactor = p.blockFactor;
        fieldNum = p.fieldNum;
        blockNum = p.blockNum;

        for (Block b : p.blocks) {
            blocks.add(new Block(b));
        }
        panel = new JPanel();
        setupLayout();
    }

    public PrimaryZone(int blockNum, int blockFactor, int fieldNum) {
        blocks = new ArrayList<>(blockNum);
        this.blockFactor = blockFactor;
        this.blockNum = blockNum;
        this.fieldNum = fieldNum;
        for (int i = 0; i < blockNum; ++i) {
            blocks.add(new Block(new BlockPointer(PointerType.PRIMARY, i), blockFactor, fieldNum));
        }
        panel = new JPanel();
        setupLayout();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(new JLabel("Primarna zona"), BorderLayout.NORTH);
        if (overFlowType==OverFlowType.DIRECT){
            panel.setLayout(new ModifiedFlowLayout());
        }else {
            panel.setLayout(new GridBagLayout());
        }
        add(panel, BorderLayout.CENTER);
//        if (overFlowType==OverFlowType.INDIRECT) {
//            setLayout(new GridBagLayout());
//        }else{
//            setLayout(new ModifiedFlowLayout(FlowLayout.CENTER,10,10));
//        }
        for (int i = 0; i < blocks.size(); ++i) {
            addBlockToLayout(blocks.get(i), i);
        }

//        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
//        for (Block b : blocks) {
//            b.setAlignmentY(Component.CENTER_ALIGNMENT);
//            this.addRecord(b);
//        }
//        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));


//        setLayout(new ModifiedFlowLayout());
//        for (int i=0;i<blockNum;++i) {
//            this.addRecord(blocks.get(i));
//        }
    }

    public SearchResult searchBlock(int address, int key) {
        Block block = blocks.get(address);
        OperativeMemoryZone op = OperativeMemoryZone.init();
        Block loadedBlock = op.loadBlock(block);
//        Animator.init().addCallback(new Callable() {
//            @Override
//            public Object call() throws Exception {
//                OperativeMemoryDialog o=new OperativeMemoryDialog(MainFrame.init(),loadedBlock);
//                MainFrame.init().addSearchDialog(o);
//                o.runDialog();
//                return null;
//            }
//        });
        SearchResult sr = loadedBlock.find(key);
        op.unloadLast(loadedBlock);
        return sr;
    }




    public void updateBlock(int blockAddress, Block b) {
        setBlock(b, blockAddress);
    }

    public void setBlock(Block b, int index) {
        Block oldb = blocks.get(index);
        oldb.hasChanged();
        panel.remove(oldb);
        blocks.set(index, b);
        addBlockToLayout(b, index);
        panel.repaint();
        panel.updateUI();
        repaint();
        updateUI();
    }

    public void addBlock(Block b) {
        blocks.add(b);
        addBlockToLayout(b, blockNum++);
    }

    public void addBlockToLayout(Block b, int index) {
        GridBagConstraints gc = new GridBagConstraints();
        if (overFlowType == OverFlowType.DIRECT) {
            gc.gridx = index % 3;
            gc.gridy = 2*(index / 3);

        } else {
            gc.gridx = index;
            gc.gridy = 0;
        }
        gc.insets = new Insets(5, 5, 5, 5);
        panel.add(b, gc);

        updateUI();
    }

//    private void addBlockToMemory(Block loaded) {
//        GridBagConstraints gc = new GridBagConstraints();
//        if (overFlowType == OverFlowType.DIRECT) {
//            gc.gridy = 1+ loaded.getAddressNum() / 3;
//        } else {
//            gc.gridy = 1;
//        }
//        gc.insets = new Insets(5, 5, 5, 5);
//        gc.gridx = loaded.getAddressNum();
//        panel.add(loaded, gc);
//
//    }

    public void removeBlock(int index) {
        blockNum--;
        panel.remove(blocks.get(index));
        blocks.remove(index);
        updateUI();
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public void clear() {
        filledNum = 0;
        for (Block b : blocks) {
            b.clear();
        }
    }

    public int getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(int value) {
        if (value < blockNum) {
            for (int i = blockNum - 1; i >= value; i--) {
                removeBlock(i);
            }
        } else if (value > blockNum) {
            for (int i = blockNum; i < value; i++) {
                addBlock(new Block(new BlockPointer(PointerType.PRIMARY, i), blockFactor, fieldNum));
            }
        }
    }

    public void setOverFlowType(OverFlowType o) {
        overFlowType = o;
        setupLayout();
    }

    public int getFilledNum() {
        return filledNum;
    }

    public Block getBlock(Integer blockAddress) {
        return blocks.get(blockAddress);
    }

    public int getRecordNum() {
        return blockNum * blockFactor;
    }

    public int getBlockFactor() {
        return blockFactor;
    }

    public void setBlockFactor(int value) {
        blockFactor = value;
        for (Block b :
                blocks) {
            b.setBlockFactor(blockFactor);
        }
    }

    public void addFocuser(Focuser a) {
        for (Block b : blocks) {
            b.addFocuser(a);
        }
    }

    public void setFieldNum(int value) {
        for (Block b : blocks) {
            b.setFieldNum(value);
        }
        fieldNum = value;
    }

    public int getFieldNum() {
        return fieldNum;
    }

    public void addRecord(Record record) {
        if (filledNum == blockNum && blocks.get(filledNum - 1).isFull()) {
            return;
        }
        int index = 0;
        AddResult a;
        do {
            a = blocks.get(index++).addRecord(record);
            record = a.record;
        } while (record != null && record.getStatus() != Record.Status.EMPTY);
        if (index > filledNum) {
            filledNum = index;
        }
    }

    public Map<Integer, Integer> getMaping() {
        Map<Integer, Integer> map = new HashMap<>();
        for (Block b : blocks) {
            map.put(b.getAddressNum(), b.getSpecialPointerAddress());
        }
        return map;
    }

    public boolean isFull() {
        for (Block b : blocks) {
            if (!b.isFull()) {
                return false;
            }
        }
        return true;
    }

    public boolean isNearlyFull() {
        return filledNum == blockNum;
    }

    public void unloadAll() {
        for (Block b : blocks) {
            b.setFileStatus(FileStatus.UNLOADED);
        }
//        repaint();
    }

    public void clearEmptyBlocks() {
        int i = 0;
        for (Block b :
                blocks) {
            int finalI = i;
            Animator.init().addCallback(new Callable() {
                @Override
                public Object call() throws Exception {
                    if (b.isEmpty()) {
                        removeBlock(finalI);
                    }
                    return null;
                }
            });
            ++i;
        }
    }

    public Animation getAddedAnim(){
        Animation a = new Animation(this);
        a.addActionListener(e -> {
            int i,n;
            i = a.getStep();
            n = a.getStepNum();
            if (i==n){
                a.end();
                return;
            }
            Graphics2D g = (Graphics2D)getGraphics();
            g.setColor(new Color(0,255,0,255*i/n));
            g.drawRect(0,0,getWidth(),getHeight());
            a.incStep();
        });
        return a;
    }

    @Override
    public synchronized void addContainerListener(ContainerListener l) {
        super.addContainerListener(l);
        panel.addContainerListener(l);
    }

    public Component getContentPanel() {

        return panel;
    }
}
