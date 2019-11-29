package indexseq;

import indexseq.Zones.OverflowZone;
import indexseq.Zones.PrimaryZone;
import indexseq.Zones.TreePane;
import indexseq.blockandcomponents.Block;
import indexseq.blockandcomponents.NodeBlock;
import indexseq.enums.FileStatus;
import indexseq.enums.PointerType;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static indexseq.enums.PointerType.*;

public class UndoSystem {

    private Map<PointerType, Map<Integer, Block>> editedBlocks = new HashMap<>();

    private TreePane treePaneBackup;
    private PrimaryZone primaryBackup;
    private OverflowZone overflowBackup;

    private static UndoSystem instance = new UndoSystem();

    private boolean formUndo = true;
    private boolean buildUndo = false;

    private IndexSequential indexSequential;
    private PrimaryZone primaryZone;
    private TreePane treePane;
    private OverflowZone overflowZone;


    private UndoSystem() {
        addNew();
    }

    private void addNew() {
        editedBlocks.put(TREE, new HashMap<>());
        editedBlocks.put(OVERFLOW, new HashMap<>());
        editedBlocks.put(PRIMARY, new HashMap<>());
        editedBlocks.put(SPECIAL, new HashMap<>());
    }

    public static UndoSystem init() {
        return instance;
    }

    public void addBlock(Block b) {
        if (!formUndo) {
            try {
                int address = b.getAddressNum();
                PointerType type = b.getAddress().getType();
                Map<Integer, Block> map = editedBlocks.get(type);
                if (!map.containsKey(address)) {
                    Block copy;
                    if (b.getAddress().getType() == TREE) {
                        copy = new NodeBlock((NodeBlock) b);
                    } else {
                        copy = new Block(b);
                        copy.setFileStatus(FileStatus.UNLOADED);
                    }
                    map.put(address, copy);
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }

    public void setFormUndo(boolean formUndo) {
        this.formUndo = formUndo;
    }

    public void undo() {
        if (formUndo) {
            indexSequential.deform();
            formUndo = false;
        } else {
            if (buildUndo) {
                indexSequential.setTree(treePaneBackup);
                indexSequential.setPrimaryZone(primaryBackup);
                indexSequential.setOver(overflowBackup);
                treePaneBackup = null;
                primaryBackup = null;
                overflowBackup = null;
                buildUndo = false;
            }else {
                for (Block block : editedBlocks.get(TREE).values()) {
                    treePane.setBlock((NodeBlock) block, block.getAddressNum());
                }
                for (Block b : editedBlocks.get(OVERFLOW).values()) {
                    overflowZone.setBlock(b, b.getAddressNum());
                }
                for (Block b : editedBlocks.get(SPECIAL).values()) {
                    overflowZone.setBlock(b, b.getAddressNum());
                }
                for (Block b : editedBlocks.get(PRIMARY).values()) {
                    primaryZone.setBlock(b, b.getAddressNum());
                }
            }
        }
    }

    public void reset() {
        editedBlocks.clear();
        treePaneBackup = null;
        overflowBackup = null;
        primaryBackup = null;
        buildUndo = false;
        addNew();
    }



    public void backupTree(TreePane t) {
        if (treePaneBackup==null) {
            buildUndo = true;
            treePaneBackup = new TreePane(t);
        }
    }
    public void backupPrimary(PrimaryZone p){
        if (primaryBackup==null) {
            buildUndo = true;
            primaryBackup = new PrimaryZone(p);
        }
    }
    public void backupOverflow(OverflowZone o){
        if (overflowBackup==null) {
            buildUndo = true;
            overflowBackup = new OverflowZone(o);
        }
    }

    public void setTreePane(TreePane treePane) {
        this.treePane = treePane;
    }

    public void setPrimaryZone(PrimaryZone primaryZone) {
        this.primaryZone = primaryZone;
    }

    public void setOverflowZone(OverflowZone overflowZone) {
        this.overflowZone = overflowZone;
    }

    public void setIndexSequential(IndexSequential indexSequential) {
        this.indexSequential=indexSequential;
        setPrimaryZone(indexSequential.getPrimary());
        setOverflowZone(indexSequential.getOverflow());
        setTreePane(indexSequential.getTree());
    }
}
