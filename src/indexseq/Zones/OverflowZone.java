package indexseq.Zones;

import indexseq.Exceptions.FileFullException;
import indexseq.blockandcomponents.Block;
import indexseq.blockandcomponents.BlockPointer;
import indexseq.blockandcomponents.Record;
import indexseq.animator.Animation;
import indexseq.animator.Animator;
import indexseq.enums.PointerType;
import indexseq.enums.PropagationType;
import indexseq.supportclasses.Focuser;
import indexseq.supportclasses.SearchResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerListener;
import java.awt.geom.Arc2D;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;

public class OverflowZone extends JPanel {

    private int size;
    private HashMap<Integer, Arc2D.Float> chains = new HashMap<>();
    private HashMap<Integer, Color> chainColor = new HashMap<>();
    private static Random colorGen = new Random();


    private Block header;
    private ArrayList<Block> blocks;
    private int blockFactor = 1;
    private Overflow overflow;
    private List<Record> bufferRecords;

    public OverflowZone(PrimaryZone p) {
        size = p.getBlockNum() * 2 / 3;
        blocks = new ArrayList<>(size);
        build(p);
        setupLayout();
    }

    public OverflowZone(OverflowZone o) {
        size = o.size;
        header = new Block(o.header);
        blocks = new ArrayList<>();
        for (Block b : o.blocks) {
            blocks.add(new Block(b));
        }
        chainColor.putAll(o.chainColor);
        chains.putAll(o.chains);
        setupLayout();
        formInstant();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(new JLabel("Zona prekoračenja"), BorderLayout.NORTH);
        overflow = new Overflow();
        overflow.setLayout(new GridBagLayout());
        add(overflow, BorderLayout.CENTER);
    }

    public void rebuild(PrimaryZone p) {
        hideAll();
        blocks.clear();
        chainColor.clear();
        build(p);
    }

    private void build(PrimaryZone p) {
        size = p.getBlockNum() * 2 / 3;
        blocks = new ArrayList<>();
        int fieldNum = p.getFieldNum();
        header = new Block(new BlockPointer("Header"), 0, 0);
        addChainHead(header.getAddressNum());
        Block prev = header;
        for (int i = 0; i < size; ++i) {
            BlockPointer bp = new BlockPointer(PointerType.OVERFLOW, i);
            Block b = new Block(bp, blockFactor, fieldNum);
            prev.setSpecialPointer(bp);
            blocks.add(b);
            prev = b;
        }
    }

    public void formInstant() {
        addBlockToLayout(header, 0);
        for (int i = 0; i < size; ++i) {
            Block b = blocks.get(i);
            addBlockToLayout(b, i + 1);
        }
    }

    public void form() {
        addBlockToLayoutAnim(header, 0);
        for (int i = 0; i < size; ++i) {
            Block b = blocks.get(i);
            addBlockToLayoutAnim(b, i + 1);
        }
    }

    /*
    Sakriva sve blokove
     */
    private void hideAll() {
        overflow.remove(header);
        for (Block b : blocks) {
            overflow.remove(b);
        }
    }


    private void addBlockToLayout(Block b, int index) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = index;
        gc.insets = new Insets(80, 5, 80, 5);
        overflow.add(b, gc);
        this.updateUI();
        this.repaint();
    }

    /*
    Dodaje blok na layout i dodaje njegovu animaciju
     */
    private void addBlockToLayoutAnim(Block b, int index) {
        Animator.init().addCallback(() -> {
            if (b.getAddressNum() == -1) {
                hideAll();
            }
            addBlockToLayout(b, index);
            return null;
        });
//        Animator.init().freeze();
        Animator.init().addAnimation(b.getGrowAnim());
        Animator.init().addAnimation(b.getHighlightAnim(true));
    }

    public Integer addRecord(Record r) throws Exception {
        Block b = getFreeBlock();
        OperativeMemoryZone.init().unloadAndSaveBlock(addRecordAnimations(r, b));
        addChainHead(b.getAddressNum());
        return b.getAddressNum();
    }

    //Dodavanje slogova u zonu prekoracenja
    public Integer addRecord(Integer headAddress, Record r) throws Exception {
        Block current = null, previous = null, addedBlock;
        Integer addedAddress = null;

        int counter = 0;
        Integer pointer = headAddress;
        while (pointer != null) {
            current = blocks.get(pointer);
            current = OperativeMemoryZone.init().loadBlock(current);
            if (current.getRecord(0).getStatus() == Record.Status.INACTIVE) {
                current.setRecord(r, 0);
                Animator.init().addAnimation(r.getAddedAnim());
                addedAddress = current.getAddressNum();
                ++counter;
                break;
            } else if (current.getFirstKey() > r.getKey()) {
                addedBlock = getFreeBlock();
                addedAddress = addedBlock.getAddressNum();
                addedBlock = addRecordAnimations(r, addedBlock);


                Block finalAddedBlock = addedBlock;
                Integer finalPointer = pointer;
                Animator.init().addCallback(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        finalAddedBlock.setSpecialPointer(new BlockPointer(PointerType.OVERFLOW, finalPointer));
                        return null;
                    }
                });
                OperativeMemoryZone.init().unloadAndSaveBlock(addedBlock);

                if (previous != null) {
                    previous.setSpecialPointer(new BlockPointer(PointerType.OVERFLOW, addedAddress));
                }
                break;
            }
            BlockPointer temp = current.getSpecialPointer();
            if (temp == null) {
                counter++;
                addedBlock = getFreeBlock();
                addRecordAnimations(r, addedBlock);

                Block finalAddedBlock1 = addedBlock;
                Block finalCurrent = current;
                Animator.init().addCallback(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        finalCurrent.setSpecialPointer(finalAddedBlock1.getAddress());
                        return null;
                    }
                });
                Animator.init().addCallback(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        finalAddedBlock1.setSpecialPointer(temp);
                        return null;
                    }
                });
                OperativeMemoryZone.init().unloadAndSaveBlock(addRecordAnimations(r, addedBlock));
                break;
            }
            ++counter;
            if (previous != null) {
                OperativeMemoryZone.init().unloadAndSaveBlock(previous);
            }
//            Animator.init().addAnimation(current.getUnloadAnim());
            previous = current;
            pointer = current.getSpecialPointerAddress();
        }
        if (previous != null) {
            OperativeMemoryZone.init().unloadAndSaveBlock(previous);
        }
        if (current != null) {
            OperativeMemoryZone.init().unloadAndSaveBlock(current);
        }
        if (counter == 0) {
            setChainHead(headAddress, addedAddress);
            return addedAddress;
        }
        return null;
    }

    /*
    Dodaje rekord u odgovarajuci blok i namesta animacije
     */

    private Block addRecordAnimations(Record r, Block addedBlock) {
        Block loaded = OperativeMemoryZone.init().loadBlock(addedBlock);
        Animator.init().addCallback(() -> {
            loaded.addRecordOnEnd(r);
            return null;
        });
        Animator.init().addAnimation(r.getAddedAnim());
        Animator.init().addCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                setBlock(loaded, loaded.getAddressNum());
                return null;
            }
        });
        return loaded;
    }

    /*
    Namesta glavu lanca
     */
    private void setChainHead(Integer oldKey, Integer newKey) {
        Color c = chainColor.get(oldKey);
        chainColor.remove(oldKey);
        chainColor.put(newKey, c);
    }

    /*
        Dodaje glavu lanca
         */
    private void addChainHead(Integer newKey) {
        Color c = new Color(colorGen.nextInt(256), colorGen.nextInt(256), colorGen.nextInt(256));
        chainColor.put(newKey, c);
    }

    /*
            Brise glavu lanca
             */
    private void removeChainHead(Integer key) {
        chainColor.remove(key);
    }

    /*
    Za uzimanje prvog bloka iz lanca slobodnih i prelancavanje lanca slobodnih
     */
    private Block getFreeBlock() throws Exception {
        Integer specialAddress = header.getSpecialPointerAddress();
        if (specialAddress == null) {
            throw new FileFullException();
        }
        Block b = blocks.get(specialAddress);
        header.setSpecialPointer(b.getSpecialPointer());
        b.setSpecialPointer(null);

        return b;
    }

    /*
        TODO Za vracanje bloka na indeksu u slobodno stanje i povratak rekorda na toj poziciji
         */
    public void setFreeBlock(int i) {
        Block b = blocks.get(i);
        b.clear();
        for (Block block : blocks) {
            if (b.getAddressNum().equals(block.getSpecialPointerAddress())) {
                block.setSpecialPointer(b.getSpecialPointer());
            }
        }
        BlockPointer pointer = header.getSpecialPointer();
        header.setSpecialPointer(b.getAddress());
        b.setSpecialPointer(pointer);
        if (chainColor.containsKey(b.getAddressNum())) {
            removeChainHead(b.getAddressNum());
        }
        repaint();
        updateUI();
    }

    //Za pretrazivanje lanca
    public SearchResult search(int address, Block previous, int key) {

        int blockAddress = address / blockFactor;
        int recAddress = address % blockFactor;
        Block b = blocks.get(blockAddress);
        Block loaded = OperativeMemoryZone.init().loadBlock(b);

        Record r = loaded.getRecord(recAddress);
        Integer pointer = loaded.getSpecialPointerAddress();
        if (r.getStatus() == Record.Status.INACTIVE) {
            boolean notFirst = false;
            do {
                if (notFirst) {
                    OperativeMemoryZone.init().unloadAndSaveBlock(loaded);
                }
                loaded = OperativeMemoryZone.init().loadBlock(blocks.get(pointer));
                r = loaded.getRecord(0);
                pointer = loaded.getSpecialPointerAddress();
                notFirst = true;
            }
            while (r.getStatus() == Record.Status.INACTIVE || pointer != null);
            if (pointer == null || r.getKey() > key) {
                return new SearchResult(false, loaded.getAddress(), loaded);
            }

        }
        if (r.getKey() == key) {
            Animator.init().addAnimation(r.getCorectAnim());
            OperativeMemoryZone.init().unloadLast(loaded);
            return new SearchResult(true, loaded.getAddress(), loaded, 0);
        } else if (pointer == null || r.getKey() > key) {
            Animator.init().addAnimation(r.getWrongAnim());
            OperativeMemoryZone.init().unloadAndSaveBlock(loaded);
            if (previous == null) {
                return new SearchResult(false, loaded.getAddress(), loaded);
            }
            return new SearchResult(false, previous.getAddress(), loaded);
        } else {
            Animator.init().addAnimation(r.getSelectAnim());
            OperativeMemoryZone.init().unloadAndSaveBlock(loaded);
            Animator.init().addAnimation(getArcAnim(blockAddress));
            return search(pointer, loaded, key);
        }
    }

    private Animation getArcAnim(int blockAddress) {
        return overflow.getArcAnim(blockAddress);
    }


//    public Record searchRecord(int address){
//        return searchRecordRec(address);
//    }
//    //Rekurzivna pomocan funkcija za pretragu
//    private Record searchRecordRec(int address){
//        Integer pointer = blocks.get(address).getSpecialPointerAddress();
//        if (pointer==null){
//            return blocks.get(address).getRecord(0);
//        }else {
//            return searchRecordRec(pointer);
//        }
//    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(ArrayList<Block> blocks) {
        this.blocks = blocks;
    }

    public int getBlockNum() {
        return blocks.size();
    }


    public int getHighestKeyFromChain(int address) {
        return getChainEnd(address).getRecord(0).getKey();
    }

    public Block getChainEnd(int address) {
        Block b = blocks.get(address);
        BlockPointer next = b.getSpecialPointer();
        while (next != null) {
            b = blocks.get(next.getAddress());
            next = b.getSpecialPointer();
        }
        return b;
    }

    public Block getBlock(Integer pointer) {
        return blocks.get(pointer);
    }

    public void setBlock(Block b, int blockAddress) {
        if (blockAddress == -1) {
            overflow.remove(header);
            header = b;
            addBlockToLayout(b, 0);
        } else {
            Block oldb = blocks.get(blockAddress);
            overflow.remove(oldb);
            blocks.set(blockAddress, b);
            addBlockToLayout(b, blockAddress + 1);
        }
    }

    public void updateBlock(Integer blockAddress, Block b) {
        setBlock(b, blockAddress);
//        b.getUnloadAnim().run();
//        Animator.init().addLastAnim(b.getUnloadAnim());
    }

    public Component getContentPanel() {
        return overflow;
    }

    public void clear() {
        hideAll();
        blocks.clear();
    }


    class Overflow extends JPanel {

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2d = (Graphics2D) g;
            //Za heder
            //Za ostale blokove
            for (Integer key : chainColor.keySet()) {
                drawChain(g2d, key);
            }
//        Integer pointer;
//        Block next,curr;
//        if (header!=null){
//            pointer = header.getSpecialPointerAddress();
//            if (pointer!=null){
//                next = blocks.get(pointer);
//                drawLink((Graphics2D) g, header, next);
//            }
//        }
//        for (int i = 0; i < blocks.size(); ++i) {
//            curr = blocks.get(i);
//            pointer = curr.getSpecialPointerAddress();
//            if (pointer != null) {
//                next = blocks.get(pointer);
//                drawLink((Graphics2D) g, curr, next);
//            }
//        }

        }

        private void drawChain(Graphics2D g, Integer key) {
            Block head;
            if (key == -1) {
                head = header;
            } else {
                head = blocks.get(key);
            }
            if (head != null) {
                g.setColor(chainColor.get(head.getAddressNum()));
                Integer pointer = head.getSpecialPointerAddress();
                while (pointer != null) {
                    Block next = getBlock(pointer);
                    drawLink(g, head, next);
                    head = next;
                    pointer = next.getSpecialPointerAddress();
                }
            }
//        }
        }

        private void drawLink(Graphics2D g, Block curr, Block next) {
            if (next.getParent() != this) {
                return;
            }
            int x1 = curr.getX();
            int x2 = next.getX();
            int x = Math.min(x1, x2) + curr.getWidth() / 2;
            int y = curr.getY() + curr.getHeight() / 2;
            int w = Math.abs(x1 - x2);
            int h = curr.getHeight();
            int start = 0, end = -180;
            Arc2D.Float arc = new Arc2D.Float(x, y, w, h, start, end, Arc2D.OPEN);
            chains.put(curr.getAddressNum(), arc);
            g.draw(arc);
        }

        private Animation getArcAnim(int index) {
            Animation a = new Animation();
            a.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int i = a.getStep();
                    int n = a.getStepNum();

                    Graphics2D g = (Graphics2D) getGraphics();

                    if (i == n) {
                        g.setColor(chainColor.get(index));
                        g.draw(chains.get(index));
                        a.end();
                        return;
                    }
                    g.setColor(Color.YELLOW);
                    g.draw(chains.get(index));
                    a.incStep();
                }
            });
            return a;
        }
    }

    public void addFocuser(Focuser a) {
        for (Block b : blocks) {
            b.addFocuser(a);
        }
        header.addFocuser(a);
    }

    public boolean isFull() {
        return header.getSpecialPointer() == null;
    }

    public int getPrefferedChainKey(Integer address, PropagationType type) {
        if (type == PropagationType.HIGH) {
            return getChainEnd(address).getPrefferedKey(type);
        } else {
            return getBlock(address).getPrefferedKey(type);
        }
    }

    public List<Record> getBufferRecords() {
        return bufferRecords;
    }

    public List<Block> selectActiveInChain(Integer index) {
        Block curr = blocks.get(index);
        bufferRecords = new ArrayList<>();
        List<Block> loaded = new ArrayList<>();
        BlockPointer next = curr.getAddress();
        while (next != null) {
            curr = blocks.get(next.getAddress());
            curr = OperativeMemoryZone.init().loadBlock(curr);
            loaded.add(curr);
            Record r = curr.getRecord(0);
            next = curr.getSpecialPointer();
            Animator.init().addAnimation(r.getSelectAnim());
            if (r.getStatus() == Record.Status.ACTIVE) {

                bufferRecords.add(r);
                Animator.init().addCallback(() -> {
                    r.setStatus(Record.Status.CORRECT);
                    return null;
                });
            } else {
                Animator.init().addCallback(() -> {
                    r.setStatus(Record.Status.WRONG);
                    return null;
                });
            }
        }
        return loaded;
    }

    @Override
    public synchronized void addContainerListener(ContainerListener l) {
        super.addContainerListener(l);
        overflow.addContainerListener(l);
    }
}
