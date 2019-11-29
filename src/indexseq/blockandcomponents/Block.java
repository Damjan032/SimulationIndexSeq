package indexseq.blockandcomponents;

import indexseq.UndoSystem;
import indexseq.animator.Animation;
import indexseq.animator.Animator;
import indexseq.enums.FileStatus;
import indexseq.enums.PropagationType;
import indexseq.supportclasses.AddResult;
import indexseq.supportclasses.Focuser;
import indexseq.supportclasses.SearchResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;


public class Block extends JComponent implements Comparable {
    protected BlockHeader header;
    private RecordPanel recordPanel;
    protected BlockPointer address;
    private BlockState blockState = BlockState.NORMAL;
    //    protected String name;
    protected ArrayList<Record> records;
    protected int blockFactor;
    protected int fieldNum;
    protected int filled;
    protected RecordOrientation recordsAxis;
    //Koristiti za pointer kod indirektne
    private BlockPointer specialPointer = null;

    private FileStatus fileStatus;

    private List<Block> popedBlocks;

    private List<Focuser> focusers = new ArrayList<>();
//    protected RecordOrientation recordOrientation = RecordOrientation.VERTICAL;
//    private HorizontalFocuser animListener = new HorizontalFocuser();

    //    private boolean added = true;
//    private boolean animated = false;
    protected List<Record> bufferRecords;


    private static Color headerColor = new Color(77, 131, 222);
    private static Color selectedColor = new Color(255, 255, 0, 120);
    private static Stroke divisionStroke = new BasicStroke(2);

    public void clear() {
        hasChanged();
        for (int i = blockFactor - 1; i >= 0; i--) {
            recordPanel.pop(i);
        }
        for (int i = 0; i < blockFactor; i++) {
            recordPanel.addEmpty();
        }
        filled = 0;
    }

    public boolean isFull() {
        return filled == blockFactor;
    }

    public int getFreeCount() {

        return blockFactor - filled;
    }


    public int free(int freeCount) {
        int i = 0;
        int count = blockFactor - filled;
        while (i < filled) {
            Record r = records.get(i);
            if (r.getStatus() == Record.Status.ACTIVE) {
                if (i >= freeCount) {
                    Animator.init().addAnimation(r.getSelectAnim());
                }

            } else if (r.getStatus() == Record.Status.INACTIVE) {
                count++;
                Animator.init().addAnimation(r.getDeleteAnim());
                Animator.init().addCallback(() -> {
                    int index = records.indexOf(r);
                    recordPanel.pop(index);
                    recordPanel.addEmpty();
                    return null;
                });

            }
            ++i;
        }
        return count;
    }

    public List<Record> selectActive() {
        bufferRecords = new ArrayList<>();
        for (Record r : records) {
            Animator.init().addAnimation(r.getSelectAnim());
            if (r.getStatus() == Record.Status.ACTIVE) {
                bufferRecords.add(r);
                Animator.init().addCallback(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        r.setStatus(Record.Status.CORRECT);
                        return null;
                    }
                });
            } else if (r.getStatus() == Record.Status.INACTIVE) {
                Animator.init().addCallback(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        r.setStatus(Record.Status.WRONG);
                        return null;
                    }
                });
            } else {
                break;
            }
        }
        return bufferRecords;
    }

    public int getFirstKey() {

        return records.get(0).getKey();
    }

    @Override
    public int compareTo(Object o) {
        Block b = (Block) o;
        return address.getAddress() - b.address.getAddress();
    }

    public Record popRecord() {
        Record r = recordPanel.pop(0);
        recordPanel.addEmpty();
        return r;
    }

    public boolean isEmpty() {
        return filled == 0;
    }


    enum RecordOrientation {
        VERTICAL(1), HORIZONTAL(0);
        int order;

        RecordOrientation(int i) {
            order = i;
        }

        public int toInt() {
            return order;
        }
    }

    enum BlockState {
        NORMAL, SELECTED
    }

    class BlockHeader extends JComponent {
        private Color headerColor = new Color(77, 131, 222);

        @Override
        public void paintComponent(Graphics g) {
            int x = 0;
            int y = 0;
            int w = getWidth();
            int hHeader = getHeight();
            int w1, w2;
            if (specialPointer == null) {
                w1 = w;
            } else {
                w1 = w * 2 / 3;
            }
            w2 = w - w1;
            Record.drawField(g, headerColor, x, y, w1, hHeader, fileStatus.isStatus());
            Record.drawText(g, Color.BLACK, x, y, w1, hHeader, address.toString());
//            x += w1;
//            Record.drawField(g, headerColor, x, y, w2, hHeader, fileStatus.isStatus());
//            Record.drawText(g, Color.BLACK, x, y, w2, hHeader, String.valueOf(address));
            if (specialPointer != null) {
                x += w1;
//                int w3 = w - w1 - w2;
                Record.drawField(g, headerColor, x, y, w2, hHeader, fileStatus.isStatus());
                Record.drawText(g, Color.BLACK, x, y, w2, hHeader, specialPointer.toString());
            }
        }

    }

    class RecordPanel extends JPanel {

        RecordPanel(ArrayList<Record> records) {
            setLayout(new BoxLayout(this, recordsAxis.toInt()));
            reset(records);
        }

        void addNew(Record r) {
            if (isFull()) {
                return;
            }
            r.setFileStatus(fileStatus);
            records.set(filled, r);
            remove(filled);
            add(r, filled);
            incFilled();
            updateUI();
        }

        public void addEmpty() {
            Record dummy = new Record(fieldNum);
            dummy.setFileStatus(fileStatus);
            add(dummy);
            records.add(dummy);
            updateUI();
        }

        void push(Record r, int address) {
            if (address >= blockFactor) {
                return;
            }
            r.setFileStatus(fileStatus);
            hasChanged();
//            Animator.init().addAnimation(r.getAddedAnim());
            records.add(address, r);
            add(r, address);
            incFilled();
            updateUI();
        }

        Record pop(int address) {
//            if (address >= blockFactor) {
//                return null;
//            }
            hasChanged();
            Record r = records.get(address);
            records.remove(address);
            remove(address);
            if (address < filled) {
                decFilled();
            }
            updateUI();
            return r;
        }

        private boolean removeRecordandShrinkBF(int address) {
            if (address >= blockFactor) {
                return false;
            }
            remove(address);
            records.remove(address);
            updateUI();
            blockFactor--;
            return true;
        }

        private boolean removeRecord(int address) {
            if (address >= blockFactor) {
                return false;
            }
            if (address < filled) {
                decFilled();
            }
            remove(address);
            records.remove(address);
            updateUI();
            return true;
        }

        void reset(ArrayList<Record> newRecords) {
            for (Record r : records) {
                remove(r);
            }
            for (Record r : newRecords) {
                r.setAlignmentX(Component.CENTER_ALIGNMENT);
                add(r);
            }
            for (int i = newRecords.size(); i < blockFactor; i++) {
                addEmpty();
//            records.addRecord(new Record());
            }
            records = newRecords;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2d = (Graphics2D) g;
            if (g2d != null) {
                g2d.setStroke(divisionStroke);
                if (recordsAxis == RecordOrientation.HORIZONTAL) {
                    int w = -1;
                    int h = getHeight();
                    for (int i = 0; i < blockFactor; i++) {
                        Record r = records.get(i);
                        w += r.getWidth();
                        g2d.setColor(r.getCurrentColor().darker());
                        g2d.drawLine(w, 0, w, h);
                    }
                }
            }
        }
    }


    public Block(BlockPointer address, int blockFactor, int fieldNum) {
        this(address, blockFactor, fieldNum, RecordOrientation.VERTICAL);
    }

    public Block(BlockPointer address, int blockFactor, int fieldNum, RecordOrientation recordOrientation) {
        this(address, blockFactor, fieldNum, new ArrayList<>(), recordOrientation);
    }

    public Block(BlockPointer address, int blockFactor, int fieldNum, ArrayList<Record> records) {
        this(address, blockFactor, fieldNum, records, RecordOrientation.VERTICAL);
    }

    public Block(BlockPointer address, int blockFactor, int fieldNum, ArrayList<Record> records, RecordOrientation recordOrientation) {
//        this.setOpaque(true);
        this.fileStatus = FileStatus.UNLOADED;
        this.address = address;
        this.blockFactor = blockFactor;
        this.fieldNum = fieldNum;
        this.recordsAxis = recordOrientation;
        this.records = records;
        filled = records.size();
        setup();
    }

    public Block(Block b) {
        fileStatus = b.fileStatus;
        fieldNum = b.fieldNum;

        blockFactor = b.blockFactor;
        address = b.address;
        records = new ArrayList<>();
        for (Record r : b.records) {
            records.add(new Record(r));
        }
        recordsAxis = b.recordsAxis;
        filled = b.filled;
        specialPointer = b.specialPointer;
        setup();
    }

    public void addRecordOnEnd(Record r) {
        if (isFull()) {
            return;
        }
        recordPanel.addNew(r);
//        recordPanel.updateUI();
    }


    public AddResult addRecord(Record r) {
        int i = -1;
        Record r1;
        do {
            i++;
            r1 = records.get(i);
            if (r1.getStatus() == Record.Status.EMPTY) {
                return insertRecord(r, i);
            }
            int key1 = r.getKey(), key2 = r1.getKey();
            if (key1 == key2) {
                break;
            }
            int j = i + 1;
            if (j >= (blockFactor)) {
                if (key1 < key2) {
                    return insertRecord(r, i);
                } else {
                    return new AddResult(r, null);
                }
            }
            int key3 = records.get(j).getKey();
            if (key1 > key2 && key1 < key3) {
                return insertRecord(r, j);
            }
        } while (r1.getStatus() != Record.Status.EMPTY && i < blockFactor);

        return new AddResult(null, null);
    }

    public AddResult insertRecord(Record record, Integer address) {
        Record r1, r = record;
        do {
            r1 = recordPanel.pop(address);
            recordPanel.push(r, address++);
            r = r1;
        }
        while ((address < blockFactor) && (r1.getStatus() != Record.Status.EMPTY) && r1.getStatus() != Record.Status.INACTIVE);
//        r1 = recordPanel.pop(address);
//        while ((address < blockFactor) && (r1.getStatus() != Record.Status.EMPTY)){
//            recordPanel.push(r, address++);
//            r = r1;
//            r1 = recordPanel.pop(address);
//        }

//        recordPanel.removeAll();

//        for (Record record :
//                records) {
//            recordPanel.addRecord(record);
//
//        }


//        recordPanel.updateUI();

//        Animator.init().addAnimation(getShineAnim());
        if (r == null) {
            return new AddResult(record, specialPointer);
        }
        if (r.getStatus() != Record.Status.EMPTY && r.getStatus() != Record.Status.INACTIVE && address == blockFactor) {
            return new AddResult(r, specialPointer);
        }
        return new AddResult(null, null);
    }


    public Record getRecord(int recordAddress) {
        return records.get(recordAddress);
    }

    public void refreshRecords() {
        for (Record r :
                records) {
            r.resetStatus();
        }
    }

    /*
    Dodaje listenere ucitavanja bloka
     */
    public void addFocuser(Focuser a) {
        if (focusers == null) {
            focusers = new ArrayList<>();
        }
        focusers.add(a);
    }


    private void setup() {
//        name = "Blok " + address;
        header = new BlockHeader();
        recordPanel = new RecordPanel(records);


        updateBlockSize();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(header);
        this.add(recordPanel);
        setupMouseListener();

    }

    private void resetRecordPanel() {
        remove(recordPanel);
        recordPanel = new RecordPanel(records);
        add(recordPanel);
        repaint();
        updateUI();
    }

    protected void updateBlockSize() {
        switch (address.getType()) {
            case SPECIAL:
            case OVERFLOW:
                setPreferredSize(new Dimension(60 + fieldNum * 20, 70 + blockFactor * 10));
                setMinimumSize(new Dimension(60 + fieldNum * 20, 70 + blockFactor * 10));
                setMaximumSize(new Dimension(60 + fieldNum * 20, 70 + blockFactor * 10));
                break;
            case PRIMARY:
                setPreferredSize(new Dimension(80 + fieldNum * 20, 70 + blockFactor * 10));
                setMinimumSize(new Dimension(80 + fieldNum * 20, 70 + blockFactor * 10));
                setMaximumSize(new Dimension(80 + fieldNum * 20, 70 + blockFactor * 10));
                break;
        }
        repaint();
    }

    private void setupMouseListener() {
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                if (animListener.states == BlockStates.SELECTED) {
//                    animListener.states = BlockStates.NORMAL;
//                } else {

//                    animListener.states = BlockStates.SELECTED;
//                }
                if (e.getButton() == MouseEvent.BUTTON3) {
//                    getGrowAnim().run();
//                    blockAnimator.setAnimation(Animations.ADDED);
                } else if (e.getButton() == MouseEvent.BUTTON2) {
//                    boolean right;
//                    if (e.getX()<getWidth()/2){
//                        right=false;
//                    }else{
//                        right=true;
//                    }
//                    getShineAnim().run();
                } else {
//                    getHighlightAnim().run();
//
//                    if (blockState == BlockState.SELECTED) {
//                        blockState = BlockState.NORMAL;
//                    } else {
//                        blockState = BlockState.SELECTED;
//                    }

                }
//                blockAnimator.animate(getGraphics());
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                blockState = BlockState.SELECTED;
                repaint();
//                getHighlightAnim().run();

            }

            @Override
            public void mouseExited(MouseEvent e) {
                blockState = BlockState.NORMAL;
                repaint();
            }
        });
    }

    public SearchResult find(int key) {
        int i;
        for (i = 0; i < blockFactor; ++i) {
            Record r = records.get(i);
            if (r.getStatus() != Record.Status.INACTIVE) {
                if (r.getStatus() == Record.Status.EMPTY) {
                    break;
                } else if (r.getKey() == key) {
                    Animator.init().addAnimation(r.getCorectAnim());
                    return new SearchResult(true, address, i, this);
                } else if (r.getKey() > key) {
                    Animator.init().addAnimation(r.getWrongAnim());
                    return new SearchResult(false, address, i, this);
                } else {
                    Animator.init().addAnimation(r.getSelectAnim());
                }
            } else {
                int j = i;
                while (j < blockFactor) {
                    Record r1 = records.get(j);
                    if (r1.getStatus() != Record.Status.INACTIVE ){
                        if(r1.getKey() > key) {
                            return new SearchResult(false, address, i, this);
                        }
                        break;
                    }
                    ++j;
                }
                i = j-1;
            }
        }
        if (specialPointer != null) {
            return new SearchResult(false, specialPointer, this, address.getAddress());
        }
        if (i==blockFactor){
            return new SearchResult(false, new BlockPointer("Over"), i, this);
        }
        return new SearchResult(false, address, i, this);


    }

    public void changeRecordSpecialStatus(Record.Status status, int index) {
        records.get(index).setSpecialStatus(status);
        repaint();
    }

    public void resetRecordStatus(Integer recordAddress) {
        records.get(recordAddress).resetStatus();

    }


    public void setSpecialPointer(BlockPointer specialPointer) {
        hasChanged();
        this.specialPointer = specialPointer;
    }

    public BlockPointer getSpecialPointer() {
        return specialPointer;
    }

    public Integer getSpecialPointerAddress() {
        if (specialPointer != null) {
            return specialPointer.getAddress();
        }
        return null;
    }

    /*
    Vraca broj adrese blok
     */
    public Integer getAddressNum() {
        return address.getAddress();
    }

    /*
    Vraca objekat koji sadrzi broj adrese bloka i koju zonu blok zauzima
     */
    public BlockPointer getAddress() {
        return address;
    }

    public void setAddress(BlockPointer address) {
        this.address = address;
    }

//    @Override
//    public String getName() {
//        return name;
//    }
//
//    @Override
//    public void setName(String name) {
//        this.name = name;
//    }

    public ArrayList<Record> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<Record> records) {
        hasChanged();
        this.records = records;
        filled = records.size();
        resetRecordPanel();
    }

    public int getBlockFactor() {
        return blockFactor;
    }

    public void setBlockFactor(int value) {
        if (value < blockFactor) {
            while (recordPanel.removeRecordandShrinkBF(value)) ;
        } else if (value > blockFactor) {
            for (int i = 0; i < value - blockFactor; i++) {
                recordPanel.addEmpty();
//                recordPanel.push(, i);
            }
        }
        blockFactor = value;
        updateBlockSize();
    }

    public int getFieldNum() {
        return this.fieldNum;
    }

    public void setFieldNum(int value) {
        for (Record r : records) {
            r.setFieldSize(value);
        }
        fieldNum = value;
        updateBlockSize();
        recordPanel.updateUI();
    }

    public void setRecord(Record record, int i) {
        recordPanel.push(record, i);
        recordPanel.pop(i + 1);
    }

    public int getFilled() {
        return filled;
    }

    public void setFilled(int filled) {
        this.filled = filled;
    }

    private void incFilled() {
        filled++;
    }

    private void decFilled() {
        filled--;
    }

    public void setFileStatus(FileStatus fileStatus) {
        this.fileStatus = fileStatus;
        for (Record r :
                records) {
            r.setFileStatus(fileStatus);
        }
    }

    private void drawSilouette(Graphics g, int x, int y, int w, int h) {
        int orgX = x;
        int orgY = y;
        int hHeader;
        if (recordsAxis == RecordOrientation.VERTICAL) {
            hHeader = h / 5;
        } else {
            hHeader = h / 3;
        }
        Record.drawField(g, headerColor, x, y, w, hHeader);
        y += hHeader;
        x = orgX;
        h -= hHeader;
        Record.drawField(g, Record.activeColor, x, y, w, h);
    }

    private void drawSelectGradient(Graphics2D g, Color c) {
        if (g == null) {
            return;
        }
        int h = getHeight();
        int w = getWidth();
        Color transparent = new Color(0, 0, 0, 0);
//        GradientPaint north = new GradientPaint(0, 0, c, 0, h / 4, transparent);
//        GradientPaint south = new GradientPaint(0, h * 3 / 4, transparent, 0, h, c);
//        GradientPaint west = new GradientPaint(0, 0, c, w / 4, 0, transparent);
//        GradientPaint east = new GradientPaint(w * 3 / 4, 0, transparent, w, 0, c);
        int division = 4;
        GradientPaint north = new GradientPaint(0, 0, c, 0, (float) h / division, transparent);
        GradientPaint south = new GradientPaint(0, (float) h * (division - 1) / division, transparent, 0, h, c);
        GradientPaint west = new GradientPaint(0, 0, c, (float) w / division, 0, transparent);
        GradientPaint east = new GradientPaint((float) w * (division - 1) / division, 0, transparent, w, 0, c);
        GradientPaint[] gradients = new GradientPaint[]{north, south, west, east};
        for (int i = 0; i < gradients.length; i++) {
            GradientPaint gradient = gradients[i];
            g.setPaint(gradient);
            g.fillRect(0, 0, w, h);
        }
    }

    public int getHighestKey() {
        return records.get(filled - 1).getKey();
    }

    public int getPrefferedKey(PropagationType type) {
    	/*
        switch (treeType){
            case HIGHEST: {
                if (filled > 0) {
                    return records.get(filled - 1).getFirstKey();
                }
                break;
            }
            case LOWEST: {
                if (filled > 0) {
                    return records.get(0).getFirstKey();
                }
                break;
            }
        }*/

        switch (type) {
            case HIGH: {
                if (filled > 0) {
                    return records.get(filled - 1).getKey();
                }
                break;
            }
            case LOW: {
                if (filled > 0) {
                    return records.get(0).getKey();
                }
                break;
            }
        }
        return -1;
    }

    public Animation getGrowAnim() {
        return getResizeAnim(true);
    }

    public Animation getShrinkAnim() {
        return getResizeAnim(false);
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
        if (blockState == BlockState.SELECTED) {
            drawSelectGradient((Graphics2D) g, selectedColor);
        }
    }

    public Animation getGrowthAnim() {
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
                int width = getWidth(), height = getHeight();
                int startW = width / 5;
                int growStep = (int) Math.ceil((width - startW) / (float) n);
                int w = startW + growStep * i;
//                if (w>Math.min(width,height)|| i == n) {
//                    a.end();
//                    return;
//                }

                Graphics2D g = (Graphics2D) getGraphics();
                if (i <= n / 5) {
                    g.clearRect(0, 0, width, height);
                    runAnimationListeners();
//                    g.clearRect(getX(),getY(),width,height);
                } else {
//                    drawBlock(g,(width - w) / 2, (height - w) / 2,w,w);
                    drawSilouette(g, (width - w) / 2, (height - w) / 2, w, w);
                }
                a.incStep();
            }
        });
        return a;
    }

    private Animation getResizeAnim(boolean growing){
        Animation a = new Animation(this);
        a.addActionListener(new ActionListener() {
            Dimension size, oldSize;
            int x_inc = 0, y_inc = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                int i = a.getStep();
                int n = a.getStepNum();
                if (i == n) {
                    setSize(oldSize);
                    a.end();
                    return;
                }

                if (i == 0) {
                    oldSize = getSize();
                    if (growing) {
                        size = new Dimension(oldSize.width / 3, oldSize.height / 3);
                    }else{
                        size = oldSize;
                    }
                    x_inc = 4*size.width / n;
                    y_inc = 4*size.height / n;
                    runAnimationListeners();
//                    g.clearRect(getX(),getY(),width,height);
                } else {
                    if (growing) {
                        size.width += x_inc;
                        size.height += y_inc;
                    }else{
                        size.width -= x_inc;
                        size.height -= y_inc;
                    }
                    setSize(size);
                }
                a.incStep();
            }
        });
        return a;
    }

    public Animation getShineAnim() {
        Animation a = new Animation(this);
        a.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = a.getStep();
                int n = a.getStepNum();

                Graphics2D g = (Graphics2D) getGraphics();
                int w = (int) Math.ceil((float) getWidth() / a.getStepNum());

                int x = w * i;
//                }
                if (x >= getWidth() || i == n) {
                    a.end();
                    return;
                }
                GradientPaint gradient = new GradientPaint(0, 0, new Color(255, 255, 0, 200), getWidth(), 0, new Color(0, 0, 0, 0));
                g.setPaint(gradient);
                g.fillRect(x, 0, w, getHeight());
                a.incStep();
            }
        });
        return a;
    }


    public Animation getHighlightAnim(boolean newBlock) {
        if (newBlock) {
            return getSelectAnim(Color.GREEN);
        }
        return getSelectAnim(Color.YELLOW);
    }

    public Animation getSelectAnim(Color c) {
        Animation a = new Animation(this);
        a.addActionListener(new ActionListener() {
            int alpha = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                int i = a.getStep();
                float stepNum = a.getStepNum();

                if (i == stepNum) {
                    a.end();
                    return;
                } else if (i == 0) {
                    runAnimationListeners();

//                if (step<=stepNum/3){
//                    c = new Color(255,255,0,alpha++);
//                }else if (step<=stepNum*2/3) {
//                    c = Color.YELLOW;
//                }else if (stepNum==step){
//                    c = Color.YELLOW;
//                    a.end();
//                    repaint();
//                    return;
//                }else{
//                    c = new Color(255,255,0,alpha--);
                }
//                drawBlock(getGraphics(),0,0,getWidth(),getHeight());
//                repaint();
//                if (i%10==0) {
                alpha = (int) (30 * i / stepNum);
//                System.out.println("step = "+step+" stepNum = "+stepNum+" alpha = "+alpha);
                Color color = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
                drawSelectGradient((Graphics2D) getGraphics(), color);
//                }
                a.incStep();
//                a.incStep();

//                blockAnimator.setBlockState(BlockStates.SELECTED);
//                blockAnimator.animate(getGraphics());
//                blockAnimator.setAnimation(Animations.DESELECTED, 10, 0);
//                blockAnimator.animate(getGraphics());
//                a.end();
            }
        });
        return a;
    }

    public Animation getDeleteAnim() {
        Animation a = new Animation(this);
        Stroke s = new BasicStroke(3);
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
                if (g == null) {
                    return;
                }
                g.setColor(Color.RED);
                g.setStroke(s);
                if (i <= n / 2) {
                    g.drawLine(0, getHeight(), getWidth(), 0);
                } else {
                    g.drawLine(0, 0, getWidth(), getHeight());
                }
                a.incStep();
            }
        });
        return a;
    }

    public Animation getLoadAnim() {
        return getLoadUnloadAnim(FileStatus.LOADED);
    }

    public Animation getUnloadAnim() {
        return getLoadUnloadAnim(FileStatus.UNLOADED);
    }

    private Animation getLoadUnloadAnim(FileStatus f) {

        Stroke s = new BasicStroke(3);
        Animation a = new Animation(this);
        a.addActionListener(new ActionListener() {
            int lastAngle = 0;

            @Override
            public void actionPerformed(ActionEvent e) {

                int pos = Math.min(getWidth(), getHeight()) / 4;
                int div = 2 * pos;
//                int x = getX();
//                int y = getY();
                int x = 0;
                int y = 0;
                int i = a.getStep();
                int n = a.getStepNum();
                int startAngle = lastAngle;
                int endAngle = 360 * i / n;
                Graphics2D g = (Graphics2D) getGraphics();
                if (g == null) {
                    return;
                }
                g.setColor(Color.BLUE);
                g.setStroke(s);
                g.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (i <= n / 10) {
                    runAnimationListeners();
                } else if (i <= n / 5) {
                    g.clearRect(0, 0, getWidth(), getHeight());
                } else {

                    g.drawArc(x + pos, y + pos, div, div, startAngle, endAngle);
                    lastAngle = endAngle;
                    if (i >= n * 9 / 10) {
                        setFileStatus(f);
                        a.end();
                    }
                }
                a.incStep();
            }
        });
        return a;
    }

    public void hasChanged() {
        UndoSystem.init().addBlock(this);
    }

    private void runAnimationListeners() {
        for (ActionListener a : focusers) {
            a.actionPerformed(new ActionEvent(Block.this, 0, null));
        }
    }


    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setupMouseListener();
    }


}


