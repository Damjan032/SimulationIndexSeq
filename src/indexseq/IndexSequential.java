package indexseq;

import indexseq.Zones.OverflowZone;
import indexseq.Zones.PrimaryZone;
import indexseq.Zones.TreePane;
import indexseq.enums.IndexFileStage;
import indexseq.enums.OverFlowType;
import indexseq.enums.PropagationType;
import indexseq.supportclasses.SearchResult;

import javax.swing.*;

public abstract class IndexSequential extends JPanel {
    protected  String name;
    protected int order;
    protected PropagationType propagationType;
    protected IndexFileStage fileStage = IndexFileStage.PRIMARY;


    /*
    Kreira JComponent koji sadrzi izgled indeks sekvencijalne datoteke
    i vraca ga kao rezultat da bi se nakacio na MainFrame
     */
    public abstract JComponent getViewComponent();

    public void form(){
        System.out.println("Formiram sa stanjem: "+fileStage);
        switch (fileStage){
            case PRIMARY: {
                fileStage = IndexFileStage.OVERFLOW;
                formOverflow();
                break;
            }
            case OVERFLOW:{
                fileStage = IndexFileStage.TREE;
                formTree();
                break;
            }
            default:
        }

    }

    protected abstract void formOverflow();

    protected abstract void formTree();


    public abstract void add(int key);

    public abstract void update(int key);

    public abstract SearchResult search(int key);

    public abstract void reorganisation();

    public void setTreeType(PropagationType type){
        propagationType = type;
    }
    public abstract PropagationType getTreeType();

    public boolean isFormed(){
        return fileStage == IndexFileStage.TREE;
    }

    public void deform() {
        switch (fileStage){
            case TREE: {
                fileStage = IndexFileStage.OVERFLOW;
                break;
            }
            case OVERFLOW:{
                fileStage = IndexFileStage.PRIMARY;
                break;
            }
        }
    }

    public int getOrder() {
        return order;
    }

    public abstract int getBlockFactor();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public abstract void setTree(TreePane t);

    private void setForm(IndexFileStage f){
        fileStage = f;
    }

    public abstract void setPrimaryZone(PrimaryZone newPrimary);

    public abstract void setOver(OverflowZone overflowBackup);

    public abstract OverFlowType getOverflowType();

    public IndexFileStage getFileStage() {
        return fileStage;
    }

    public abstract PrimaryZone getPrimary();

    public abstract OverflowZone getOverflow();

    public abstract TreePane getTree();
}

