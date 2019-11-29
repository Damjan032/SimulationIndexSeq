package indexseq.blockandcomponents;

import indexseq.animator.Animation;
import indexseq.enums.FileStatus;
import indexseq.enums.IndexFileStage;
import indexseq.enums.PointerType;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;

public class Record extends JComponent {


    public ArrayList<BlockPointer> getPointers() {
        return pointers;
    }

    public boolean areKeysSame() {
        int key = keys.get(0);
        for (int k:keys){
            if (k!=key){
                return false;
            }
        }
        return true;
    }

    public boolean hasMoreKeys(){
        return keys.size() > 1;
    }

    public void updateSpecialField(int prefferedKey, BlockPointer blockPointer) {
        keys.set(0,prefferedKey);
        pointers.set(0,blockPointer);
        repaint();
    }

    public void equaliseKeys() {
        int key = keys.get(0);
        BlockPointer bp = pointers.get(0);
        for (int i =1;i<keys.size();++i){
            keys.set(i,key);
            pointers.set(i,bp);
        }

    }

    public void setKeys(ArrayList<Integer> keys) {
        this.keys = keys;
    }

    public void setPointers(ArrayList<BlockPointer> pointers) {
        this.pointers = pointers;
    }


    public enum Status {
        ACTIVE, INACTIVE, EMPTY, SELECTED, WRONG, PASSED, CORRECT,ADDED,UPDATED;
    }


    //Za random boje
    //private Random record = new Random();
    //Ove boje
    //private Color c;
    public static final Color borderColor = new Color(0, 2, 85);


    public static final Color selectedColor = new Color(244, 245, 56);
    public static final Color activeColor = new Color(127, 203, 253);
    public static final Color inactColor = new Color(68, 107, 135, 255);
    public static final Color emptyColor = new Color(255, 234, 203);
    public static final Color passedColor = new Color(77, 157, 255);
    public static final Color wrongColor = new Color(255, 76, 65);
    public static final Color correctColor = new Color(45, 167, 83);
    public static final Color updatedColor = new Color(255, 124, 41);
    public static final Color addedColor = new Color(139, 255, 0);

    private Color textColor = Color.BLACK;

    private ArrayList<Integer> keys = new ArrayList<>();
    private ArrayList<BlockPointer> pointers = new ArrayList<>();
    //    private int key = 0;
    //    private String[] specialFields;
    private ArrayList<String> fields;
    //    private Integer pointer;
    private int fieldSize = 0;
    private int specialFieldsSize = 1;
    private Status status = Status.EMPTY;
    private Status specialStatus;
    private Status oldStatus;

    private Color currentColor;

    private float[] lengths;
    private int sumLength = 0;

    private FileStatus fileStatus = FileStatus.LOADED;

    public Record(int fieldSize) {
        fields = new ArrayList<>();
        for (int i = 0; i < fieldSize; i++) {
            fields.add("");
        }
        this.fieldSize = fieldSize;
        setupLengths();
    }

    //    public Record(int fieldSize) {
//        this.fieldSize = fieldSize;
//        this.fields = new ArrayList<String>(new String[fieldSize));
//        for (int i = 0; i < fieldSize; ++i) {
//            fields[i] = "None";
//        }
//    }
    public Record(String field) {
        fields = new ArrayList<>();
        fields.add(field);
        fieldSize = 1;
        setStatus(Status.ACTIVE);
        fileStatus = FileStatus.LOADED;
        setupLengths();
    }

    public Record(int key, ArrayList<String> fields, FileStatus fileStatus) {
        keys.add(key);
        this.fields = fields;
        this.fieldSize = fields.size();
        setStatus(Status.ACTIVE);
        this.fileStatus = fileStatus;
        setupLengths();
    }

    public Record(int key, ArrayList<String> strings) {
        this(key, strings, FileStatus.UNLOADED);
    }

    public Record(int key, BlockPointer pointer) {
        keys.add(key);
        pointers.add(pointer);
        specialFieldsSize = 2;
        setStatus(Status.ACTIVE);
    }

    public Record(int key1, int address1, int key2, int address2) {
        keys.add(key1);
        keys.add(key2);
        pointers.add(new BlockPointer(PointerType.PRIMARY, address1));
        pointers.add(new BlockPointer(PointerType.PRIMARY, address2));
        specialFieldsSize = 4;
        setStatus(Status.ACTIVE);
    }

    public Record(ArrayList<String> fields) {
        this.fields = fields;
        this.fieldSize = fields.size();
    }
    public Record(ArrayList<Integer> keys, ArrayList<BlockPointer> pointers){
        this.keys = keys;
        this.pointers = pointers;
        specialFieldsSize = keys.size()+pointers.size();
        setStatus(Status.ACTIVE);
    }


    public Record(Record r) {
        status = r.status;
        oldStatus = r.oldStatus;
        fileStatus = r.fileStatus;
        keys = new ArrayList<>();
        keys.addAll(r.keys);
        pointers = new ArrayList<>();
        for (BlockPointer bp:r.pointers){
            pointers.add(new BlockPointer(bp));
        }
        fieldSize = r.fieldSize;
        fields = new ArrayList<>();
        for (int i = 0; i < fieldSize; ++i) {
            String s = r.fields.get(i);
            fields.add(s);
        }
        setupLengths();
    }

    /*
    Namesta unapred odnose duzina polja
     */
    private void setupLengths() {
        setMinimumSize(new Dimension(100, 20));
        lengths = new float[fieldSize];
        for (String s : fields) {
            sumLength += s.length();
        }
        int i = 0;
        for (String s : fields) {
            lengths[i] = ((float) s.length()) / sumLength;
            ++i;
        }
    }

    private Animation getAnim(Status status) {
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
                if (i == n / 5) {
                    setSpecialStatus(status);
                    repaint();
                }
                if (i == n * 4 / 5) {
                    resetStatus();
                    repaint();
                }
//                b.repaint();
                a.incStep();

            }
        });
        return a;
    }




    public Animation getSelectAnim() {
        return getAnim(Status.SELECTED);
    }

    public Animation getCorectAnim() {
        return getAnim(Status.CORRECT);
    }

    public Animation getWrongAnim() {
        return getAnim(Status.WRONG);
    }
    public Animation getUpdateAnim() {
        return getAnim(Status.UPDATED);
    }
    public Animation getAddedAnim() {
        return getAnim(Status.ADDED);
    }

    public Animation getDeleteAnim(){
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
                if (g==null){
                    return;
                }
                g.setColor(wrongColor);
                g.setStroke(s);
                if (i <= n / 2) {
                    g.drawLine(0,getHeight(),getWidth(),0);
                }else{
                    g.drawLine(0,0,getWidth(),getHeight());
                }
                a.incStep();
            }
        });
        return a;
    }

    public void select() {
        oldStatus = status;
        status = Status.SELECTED;
        this.repaint();
    }

    public void deselect() {
        status = oldStatus;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawRecord(g, 0, 0, getWidth(), getHeight());
    }

    public static void drawText(Graphics g, Color textColor, int x, int y, int w, int h, String text) {
        g.setColor(textColor);
        int fontsize = 3 * h / 4;
        Font f = new Font("Arial", Font.PLAIN, fontsize);
        Dimension d = findFontD(g, text, w, h, f);
        int xstring = x + (w - d.width) / 2;
        int ystring = y + (h + d.height) / 2;
        g.drawString(text, xstring, ystring);
    }

    public static void drawField(Graphics g, Color c, int x, int y, int w, int h, boolean transparent) {
        if (transparent) {
            c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 80);
        }
        drawField(g, c, x, y, w, h);
    }

    public static void drawField(Graphics g, Color c, int x, int y, int w, int h) {
//        System.out.println(Integer.toBinaryString(c.getRGB())+" vs "+Integer.toBinaryString(c.getRGB()|(125<<24)));
//        if (unloaded){
//            c = new Color(c.getRGB()^(125<<24),true);
//            c = new Color(c.getRed(),c.getGreen(),c.getBlue(),125);
//        }
        g.setColor(c);
//        g.fill3DRect(x, y, w, h, true);
        g.fillRect(x, y, w, h);
        g.setColor(c.darker());

//        int length = h/3;
//        g.drawLine(x+w-1,y+length,x+w-1,y+h-length);

        g.drawLine(x + w - 1, y + h / 4, x + w - 1, y + h - 1);
        g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);

//        g.fillRoundRect(x, y, w, h,10,5);
//        g.setColor(Color.WHITE);
//        g.drawRoundRect(x, y, w, h,10,5);

//        g.draw3DRect(x, y, w, h,true);
    }

    public static void drawBorder(Graphics g, Color c, int x, int y, int w, int h, boolean transparent) {
        if (transparent) {
            c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 125);
        }
        drawBorder(g, c, x, y, w, h);
    }

    public static void drawBorder(Graphics g, Color c, int x, int y, int w, int h) {
        g.setColor(c);
        g.drawRect(x, y, w - 1, h - 1);
    }

    public void drawRecord(Graphics g, int x, int y, int w, int h) {
//        if (status==Status.EMPTY){
//            g.setColor(emptyColor);
//            g.fillRect(x,y,w,h);
//            return;
//        }
        int width;
        if (pointers.size() == 0) {
            width = w / (1 + fieldSize);
        } else {
            width = w / (2 + fieldSize);
        }
        int y2 = y;
        int orgx = x;
        int orgy = y;
        if (keys.isEmpty()){
            updateColor(g);
            drawField(g, g.getColor(), x, y2, width, h, fileStatus.isStatus());

            drawText(g, textColor, x, y2, width, h, "/");
            x+=width;
        }else {
            int height = h / keys.size();
            for (int i = 0; i < keys.size(); i++) {
                x = orgx;
                updateColor(g);

                drawField(g, g.getColor(), x, y2, width, height, fileStatus.isStatus());

                drawText(g, textColor, x, y2, width, height, Integer.toString(keys.get(i)));

                x += width;

                if (i < pointers.size()) {
                    updateColor(g);
                    drawField(g, g.getColor(), x, y2, width, height, fileStatus.isStatus());

                    drawText(g, textColor, x, y2, width, height, pointers.get(i).toString());
                    x += width;
                }
                y2 += height;
            }
        }

        for (int i = 0; i < fieldSize; i++) {
            String s = fields.get(i);
//            width = (int) (w*lengths[width]);

            updateColor(g);
            drawField(g, g.getColor(), x, y, width, h, fileStatus.isStatus());
            drawText(g, textColor, x, y, width, h, s);

            x += width;
        }
    }

    public static Dimension findFontD(Graphics g, String text, int w, int h, Font oldf) {
        //search up to 100
//        w -= 2;
        g.setFont(oldf);
        for (int i = 16; i >= 8; i -= 2) {
            Font f = new Font(oldf.getFontName(), oldf.getStyle(), i);

            FontMetrics fm = g.getFontMetrics(f);
            Dimension d = new Dimension(fm.stringWidth(text), fm.getHeight());

            if (h > d.height && w > d.width) {
                g.setFont(f);
                return d;
            }
        }
        g.setFont(new Font(oldf.getFontName(), oldf.getStyle(), 5));
        return new Dimension(0, 0);
    }

    private void updateColor(Graphics g) {
        Status s;
        if (specialStatus!=null){
            s = specialStatus;
        }else{
            s = status;
        }
        switch (s) {
            case ACTIVE: {
                g.setColor(activeColor);
                textColor = Color.BLACK;
                currentColor = activeColor;
                break;
            }
            case INACTIVE: {
                g.setColor(inactColor);
                textColor = Color.GRAY;
                currentColor = inactColor;
                break;
            }
            case EMPTY: {
                g.setColor(emptyColor);
                textColor = Color.BLACK;
                currentColor = emptyColor;

                break;
            }
            case SELECTED: {
                g.setColor(selectedColor);
                currentColor = selectedColor;
                textColor = Color.BLACK;
//                Graphics2D g2d = (Graphics2D) g;
//                GradientPaint gp = new GradientPaint(0, 0, selectedColor, 0, getHeight(), new Color(0, 0, 0, 0));
//                g2d.setPaint(gp);
//                textColor = new Color(255, 194, 0);
                break;
            }
            case WRONG:
                g.setColor(wrongColor);
                textColor = Color.BLACK;
                currentColor = wrongColor;
                break;
            case PASSED:
                g.setColor(passedColor);
                textColor = Color.BLACK;
                currentColor = passedColor;
                break;
            case CORRECT:
                g.setColor(correctColor);
                textColor = Color.BLACK;
                currentColor = correctColor;
                break;
            case ADDED:
                g.setColor(addedColor);
                currentColor = addedColor;
                break;
            case UPDATED:
                g.setColor(updatedColor);
                currentColor = updatedColor;
                break;
        }
    }

    public int getKey() {
        if (!keys.isEmpty()) {
            return keys.get(0);
        }
        return -1;
    }

    public int getPointer() {
        return pointers.get(0).getAddress();
    }
    public int getPointerAddress(int i) {
        return pointers.get(i).getAddress();
    }

    public int getKey(int i) {
        return keys.get(i);
    }

    public ArrayList<Integer> getKeys() {
        return keys;
    }

    public String getField(int i) {
        return fields.get(i);
    }

    public int getFieldSize() {
        return fieldSize;
    }

    public void setFieldSize(int value) {
        if (value < fieldSize) {
            do {
                fields.remove(0);
            }
            while (fields.size() > value);
        } else if (fieldSize < value) {
            do {
                fields.add("");
            } while (fields.size() < value);
        }
        updateUI();
        fieldSize = value;

    }


    public void setKey(int i, int key) {
        this.keys.set(i, key);
    }

    public ArrayList<String> getFields() {
        return fields;
    }

    public void setFields(ArrayList<String> fields) {
        this.fields = fields;
    }

    public void setField(int i, String text) {
        fields.set(i, text);
    }

    public BlockPointer getPointer(int i) {
        return pointers.get(i);
    }

    public void setPointer(BlockPointer blockPointer, int i) {
        pointers.set(i, blockPointer);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
//        if (oldStatus == null) {
//            oldStatus = status;
//        } else {
//            oldStatus = this.status;
//        }
        this.status = status;
    }

    public void setSpecialStatus(Status s){
        specialStatus = s;
    }

    public void resetStatus() {
//        if (oldStatus != null) {
//            status = oldStatus;
//
//            repaint();
//        }
        specialStatus = null;
    }


    public Color getCurrentColor(){
        return currentColor;
    }

    public void setFileStatus(FileStatus fileStatus) {
        this.fileStatus = fileStatus;
    }
}